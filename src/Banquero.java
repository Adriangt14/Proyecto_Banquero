import java.util.ArrayList;
import java.util.List;

public class Banquero {

    private String[] tareas = {
            "Compilar proyecto", "Maquina virtual", "Procesar video",
            "Imprimir informe", "Ejecutar IDE"
    };

    private int[][] maximo = {
            {7, 5, 3}, {3, 2, 2}, {9, 0, 2}, {2, 2, 2}, {4, 3, 3}
    };

    private int[][] asignadosIniciales = {
            {0, 1, 0}, {2, 0, 0}, {3, 0, 2}, {2, 1, 1}, {0, 0, 2}
    };

    private int[][] asignados;
    private int[] disponibles;

    public Banquero() {
        reiniciar();
    }

    public String[] getTareas() {
        return tareas;
    }

    public int[] getDisponibles() {
        return disponibles;
    }

    public int[] getAsignados(int proceso) {
        return asignados[proceso];
    }

    public int[] getMaximo(int proceso) {
        return maximo[proceso];
    }

    public int[] getNecesidad(int proceso) {
        int[] necesidad = new int[3];

        for (int i = 0; i < 3; i++) {
            necesidad[i] = maximo[proceso][i] - asignados[proceso][i];
        }

        return necesidad;
    }

    public List<Integer> evaluar() {
        int[] trabajo = disponibles.clone();
        boolean[] terminado = new boolean[5];
        List<Integer> orden = new ArrayList<>();

        while (orden.size() < 5) {
            boolean encontrado = false;

            for (int i = 0; i < 5; i++) {
                if (terminado[i]) {
                    continue;
                }

                int[] necesidad = getNecesidad(i);
                boolean puedeTerminar = true;

                for (int j = 0; j < 3; j++) {
                    if (necesidad[j] > trabajo[j]) {
                        puedeTerminar = false;
                    }
                }

                if (puedeTerminar) {
                    for (int j = 0; j < 3; j++) {
                        trabajo[j] += asignados[i][j];
                    }

                    terminado[i] = true;
                    orden.add(i);
                    encontrado = true;
                }
            }

            if (!encontrado) {
                return new ArrayList<>();
            }
        }

        return orden;
    }

    public String solicitar(int proceso, int[] solicitud) {
        int[] necesidad = getNecesidad(proceso);

        for (int i = 0; i < 3; i++) {
            if (solicitud[i] < 0) {
                return "No se permiten numeros negativos.";
            }

            if (solicitud[i] > necesidad[i]) {
                return "La solicitud supera la necesidad.";
            }

            if (solicitud[i] > disponibles[i]) {
                return "No hay recursos suficientes.";
            }
        }

        boolean hayRecursos = false;

        for (int cantidad : solicitud) {
            if (cantidad > 0) {
                hayRecursos = true;
            }
        }

        if (!hayRecursos) {
            return "Solicita al menos una unidad.";
        }

        for (int i = 0; i < 3; i++) {
            disponibles[i] -= solicitud[i];
            asignados[proceso][i] += solicitud[i];
        }

        List<Integer> orden = evaluar();

        if (orden.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                disponibles[i] += solicitud[i];
                asignados[proceso][i] -= solicitud[i];
            }

            return "Solicitud rechazada: estado inseguro.";
        }

        return "Solicitud aprobada. Secuencia: " + secuencia(orden);
    }

    public String secuencia(List<Integer> orden) {
        String resultado = "";

        for (int i = 0; i < orden.size(); i++) {
            resultado += "P" + orden.get(i);

            if (i < orden.size() - 1) {
                resultado += " -> ";
            }
        }

        return resultado;
    }

    public void reiniciar() {
        asignados = new int[5][3];

        for (int i = 0; i < 5; i++) {
            asignados[i] = asignadosIniciales[i].clone();
        }

        disponibles = new int[]{3, 3, 2};
    }
}
