import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

public class Main {
    public static void main(String[] args) {
        String dirPrueba = "tx\\pruebas_draco\\PIdG33(AL1).txt"; // dir/ficheroDePrueba.txt
        AnalizadorLexico lexico = new AnalizadorLexico(dirPrueba);
        GestorErrores gestorErrores = new GestorErrores();

        boolean fin = false;

        try (BufferedWriter out = new BufferedWriter(new FileWriter("tokens.txt"))) {
            while (!fin) {
                SimpleEntry<String, Object> par = lexico.sigToken();

                if (par == null) {
                    System.err.println("Se detiene el análisis por error léxico previo.");
                    // Siempre intentamos volcar la tabla actual antes de salir
                    escribirTabla(lexico, gestorErrores);
                    fin = true;
                } else {
                    try {
                        Token tok = Token.fromEntry(par);
                        out.write(tok.toString());
                        out.flush();
                    } catch (IOException e) {
                        // Error al escribir tokens.txt
                        System.err.println("Error al escribir tokens: " + e.getMessage());
                        gestorErrores.mostrarError(111, 0, ' ', null);
                        // Intentar escribir la tabla aunque fallara tokens.txt
                        escribirTabla(lexico, gestorErrores);
                        fin = true;
                        break;
                    }

                    // Actualizamos el fichero de la tabla tras cada token para que exista si se corta la ejecución
                    escribirTabla(lexico, gestorErrores);

                    if ("EOF".equals(par.getKey())) {
                        fin = true;
                    }
                }
            }
            System.out.println("Lectura de fichero terminada.");
        } catch (IOException e) {
            // Error al abrir/escribir tokens.txt (catch del try-with-resources)
            System.err.println("Error al escribir tokens: " + e.getMessage());
            gestorErrores.mostrarError(111, 0, ' ', null);
            // Intentar escribir la tabla aunque el writer de tokens fallara
            escribirTabla(lexico, gestorErrores);
        }
    }

    private static void escribirTabla(AnalizadorLexico lexico, GestorErrores gestorErrores) {
        try (BufferedWriter ts = new BufferedWriter(new FileWriter("tablaSimbolos.txt"))) {
            ts.write(lexico.getTablaSimbolos().toString());
            ts.flush();
        } catch (IOException e) {
            System.err.println("Error al escribir la tabla de símbolos: " + e.getMessage());
            gestorErrores.mostrarError(112, 0, ' ', null);
        }
    }
}
