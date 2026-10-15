import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        SearchEngine motor = new SearchEngine();

        System.out.print("Ingresa la ruta del directorio con los archivos .txt: ");
        String ruta = teclado.nextLine().trim();

        try {
            System.out.println("Indexando documentos, por favor espera...");
            motor.cargarDirectorio(ruta);
        } catch (IOException e) {
            System.err.println("Error al cargar el directorio: " + e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("\nMotor listo. Escribe 'salir' para terminar.");
        while (true) {
            System.out.print("\nConsulta: ");
            String query = teclado.nextLine().trim();

            if (query.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta luego!");
                break;
            }

            if (query.isEmpty()) {
                System.out.println("Consulta vacía, intenta de nuevo.");
                continue;
            }

            if (query.length() > 200) {
                query = query.substring(0, 200);
                System.out.println("(Consulta truncada a 200 caracteres)");
            }

            String[] resultados = motor.buscar(query);

            if (resultados.length == 0) {
                System.out.println("No se encontraron documentos relevantes para esa consulta.");
            } else {
                System.out.println("Documentos más relevantes:");
                for (int i = 0; i < resultados.length; i++) {
                    System.out.printf("  %2d. %s%n", i + 1, resultados[i]);
                }
            }
        }

        teclado.close();
    }
}
