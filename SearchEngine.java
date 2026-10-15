import java.io.*;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;

public class SearchEngine {

    // índice invertido: termino -> (nombreDoc -> frecuencia)
    private final AVLTree<String, AVLTree<String, Integer>> indice = new AVLTree<>();

    // norma precalculada por documento: nombreDoc -> sqrt(sum(TF*IDF)^2)
    private final AVLTree<String, Double> normas = new AVLTree<>();

    private int totalDocs = 0;

    public void cargarDirectorio(String ruta) throws IOException {
        File directorio = new File(ruta);
        if (!directorio.isDirectory()) {
            throw new IllegalArgumentException("La ruta no es un directorio válido: " + ruta);
        }

        File[] archivos = directorio.listFiles((d, nombre) -> nombre.toLowerCase().endsWith(".txt"));
        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron archivos .txt en el directorio.");
            return;
        }

        for (File archivo : archivos) {
            indexarDocumento(archivo);
        }

        calcularNormas();
        System.out.println("Documentos cargados: " + totalDocs);
    }

    private void indexarDocumento(File archivo) throws IOException {
        String nombreDoc = archivo.getName();
        String contenido = new String(Files.readAllBytes(archivo.toPath()));
        String[] tokens = tokenizar(contenido);

        AVLTree<String, Integer> frecuencias = new AVLTree<>();
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            Integer conteo = frecuencias.obtener(token);
            frecuencias.insertar(token, conteo == null ? 1 : conteo + 1);
        }

        frecuencias.enOrden((termino, frecuencia) -> {
            AVLTree<String, Integer> lista = indice.obtener(termino);
            if (lista == null) {
                lista = new AVLTree<>();
                indice.insertar(termino, lista);
            }
            lista.insertar(nombreDoc, frecuencia);
        });

        totalDocs++;
    }

    private void calcularNormas() {
        // acumulamos sum(peso^2) por documento
        indice.enOrden((termino, lista) -> {
            double idf = calcularIDF(lista.tamanio());
            lista.enOrden((nombreDoc, frecuencia) -> {
                double peso = calcularTF(frecuencia) * idf;
                Double actual = normas.obtener(nombreDoc);
                normas.insertar(nombreDoc, actual == null ? peso * peso : actual + peso * peso);
            });
        });
        // aplicamos sqrt a cada entrada
        normas.enOrden((nombreDoc, sumaCuadrados) -> normas.insertar(nombreDoc, Math.sqrt(sumaCuadrados)));
    }

    public String[] buscar(String query) {
        String[] terminos = tokenizar(query);
        if (terminos.length == 0) return new String[0];

        AVLTree<String, Double> numeradores = new AVLTree<>();

        for (String termino : terminos) {
            if (termino.isEmpty()) continue;
            AVLTree<String, Integer> lista = indice.obtener(termino);
            if (lista == null) continue;

            double idf = calcularIDF(lista.tamanio());
            if (idf == 0) continue;

            lista.enOrden((nombreDoc, frecuencia) -> {
                double tf = calcularTF(frecuencia);
                double aporte = tf * idf;
                Double actual = numeradores.obtener(nombreDoc);
                numeradores.insertar(nombreDoc, actual == null ? aporte : actual + aporte);
            });
        }

        if (numeradores.tamanio() == 0) return new String[0];

        int totalCandidatos  = numeradores.tamanio();
        String[] nombres     = new String[totalCandidatos];
        double[] puntajes    = new double[totalCandidatos];
        int[]    conteo      = {0};

        numeradores.enOrden((nombreDoc, numerador) -> {
            Double denominador = normas.obtener(nombreDoc);
            double sim = (denominador == null || denominador == 0) ? 0 : numerador / denominador;
            if (sim > 0) {
                nombres[conteo[0]]  = nombreDoc;
                puntajes[conteo[0]] = sim;
                conteo[0]++;
            }
        });

        int validos      = conteo[0];
        int tamResultado = Math.min(10, validos);
        String[] resultado = new String[tamResultado];

        boolean[] usado = new boolean[validos];
        for (int r = 0; r < tamResultado; r++) {
            int mejor = -1;
            for (int i = 0; i < validos; i++) {
                if (!usado[i] && (mejor == -1 || puntajes[i] > puntajes[mejor])) {
                    mejor = i;
                }
            }
            resultado[r] = nombres[mejor];
            usado[mejor] = true;
        }

        return resultado;
    }

    private double calcularTF(int frecuencia) {
        if (frecuencia <= 0) return 0;
        return Math.log(frecuencia) / Math.log(2) + 1;
    }

    private double calcularIDF(int nt) {
        if (totalDocs <= 0 || nt <= 0) return 0;
        return Math.log((totalDocs + 1.0) / nt) / Math.log(2);
    }

    private String[] tokenizar(String texto) {
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        normalizado = normalizado.toLowerCase();
        normalizado = normalizado.replaceAll("[^a-z0-9]", " ");
        return normalizado.split("\\s+");
    }
}
