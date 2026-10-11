import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private final Banquero banquero = new Banquero();
    private final Color verde = new Color(48, 83, 63);
    private final Color fondo = new Color(245, 247, 244);

    private final JComboBox<String> tareas = new JComboBox<>(banquero.getTareas());
    private final JSpinner ram = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private final JSpinner cpu = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private final JSpinner impresoras = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));

    private final JLabel recursos = new JLabel();
    private final JLabel detalle = new JLabel();
    private final JLabel mensaje = new JLabel("Sistema listo.");

    public VentanaPrincipal() {
        setTitle("Gestor de recursos");
        setSize(740, 530);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBackground(fondo);
        principal.setBorder(new EmptyBorder(18, 20, 18, 20));
        setContentPane(principal);

        JLabel titulo = new JLabel("Gestor de recursos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));
        titulo.setForeground(verde);
        principal.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(3, 1, 10, 10));
        centro.setBackground(fondo);

        JPanel p1 = seccion("RECURSOS DISPONIBLES");
        recursos.setFont(new Font("SansSerif", Font.BOLD, 16));
        recursos.setForeground(verde);
        p1.add(recursos, BorderLayout.CENTER);

        JPanel p2 = seccion("TAREA SELECCIONADA");
        JPanel info = new JPanel(new GridLayout(2, 1, 5, 5));
        info.setOpaque(false);
        info.add(tareas);
        info.add(detalle);
        p2.add(info, BorderLayout.CENTER);

        JPanel p3 = seccion("SOLICITAR RECURSOS");
        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        campos.setOpaque(false);
        campos.add(new JLabel("RAM:"));
        campos.add(ram);
        campos.add(new JLabel("CPU:"));
        campos.add(cpu);
        campos.add(new JLabel("Impresoras:"));
        campos.add(impresoras);
        p3.add(campos, BorderLayout.CENTER);

        centro.add(p1);
        centro.add(p2);
        centro.add(p3);
        principal.add(centro, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(8, 10));
        inferior.setOpaque(false);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);

        JButton solicitar = boton("Solicitar", verde, Color.WHITE);
        JButton evaluar = boton("Evaluar seguridad", new Color(225, 233, 225), verde);
        JButton reiniciar = boton("Reiniciar", new Color(235, 237, 234), Color.DARK_GRAY);

        solicitar.addActionListener(e -> solicitar());
        evaluar.addActionListener(e -> evaluar());
        reiniciar.addActionListener(e -> reiniciar());

        botones.add(solicitar);
        botones.add(evaluar);
        botones.add(reiniciar);

        mensaje.setForeground(verde);
        inferior.add(botones, BorderLayout.NORTH);
        inferior.add(mensaje, BorderLayout.SOUTH);
        principal.add(inferior, BorderLayout.SOUTH);

        tareas.addActionListener(e -> actualizar());
        actualizar();
    }

    private JPanel seccion(String titulo) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 225, 220)),
                new EmptyBorder(10, 12, 10, 12)));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 11));
        etiqueta.setForeground(verde);
        p.add(etiqueta, BorderLayout.NORTH);
        return p;
    }

    private JButton boton(String texto, Color fondo, Color letra) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(letra);
        b.setFocusPainted(false);
        return b;
    }

    private void actualizar() {
        int[] r = banquero.getDisponibles();
        recursos.setText("RAM: " + r[0] + "     CPU: " + r[1]
                + "     Impresoras: " + r[2]);

        int p = tareas.getSelectedIndex();
        if (p >= 0) {
            detalle.setText("Asignados: " + texto(banquero.getAsignados(p))
                    + "   |   Máximo: " + texto(banquero.getMaximo(p))
                    + "   |   Necesita: " + texto(banquero.getNecesidad(p)));
        }
    }

    private String texto(int[] v) {
        return v[0] + " / " + v[1] + " / " + v[2];
    }

    private void solicitar() {
        int[] peticion = {(int) ram.getValue(), (int) cpu.getValue(),
                (int) impresoras.getValue()};

        String resultado = banquero.solicitar(tareas.getSelectedIndex(), peticion);
        actualizar();
        mensaje.setText(resultado);
        mensaje.setForeground(resultado.startsWith("Aprobada")
                ? verde : new Color(165, 55, 55));
    }

    private void evaluar() {
        List<Integer> orden = banquero.evaluar();
        mensaje.setText(orden.isEmpty()
                ? "Estado inseguro: no hay secuencia segura."
                : "Sistema seguro: " + banquero.secuencia(orden));
        mensaje.setForeground(orden.isEmpty()
                ? new Color(165, 55, 55) : verde);
    }

    private void reiniciar() {
        banquero.reiniciar();
        ram.setValue(0);
        cpu.setValue(0);
        impresoras.setValue(0);
        actualizar();
        mensaje.setText("Sistema reiniciado.");
        mensaje.setForeground(verde);
    }
}
