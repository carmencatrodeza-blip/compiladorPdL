import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

public class Main {
    public static void main(String[] args) {
        String dirPrueba = "tx\\anexo\\AL-TS-27_10\\programa-fuente-valido-3.txt"; // dir/ficheroDePrueba.txt
        AnalizadorLexico lexico = new AnalizadorLexico(dirPrueba);

        boolean fin = false;

        try (BufferedWriter out = new BufferedWriter(new FileWriter("tokens.txt"))) {
            while (!fin) {
                SimpleEntry<String, Object> par = lexico.sigToken();

                if (par == null) {
                    System.err.println("Se detiene el análisis por error léxico previo.");
                    // Siempre intentamos volcar la tabla actual antes de salir
                    escribirTabla(lexico);
                    fin = true;
                } else {
                    try {
                        Token tok = Token.fromEntry(par);
                        out.write(tok.toString());
                        out.flush();
                    } catch (IOException e) {
                        // Error al escribir tokens.txt
                        System.err.println("Error al escribir tokens: " + e.getMessage());
                        GestorErrores.obtenerInstancia().mostrarError(111);
                        // Intentar escribir la tabla aunque fallara tokens.txt
                        escribirTabla(lexico);
                        fin = true;
                        break;
                    }

                    // Actualizamos el fichero de la tabla tras cada token para que exista si se corta la ejecución
                    escribirTabla(lexico);

                    if ("EOF".equals(par.getKey())) {
                        System.out.println("Lectura de fichero terminada.");
                        fin = true;
                    }
                }
            }
        } catch (IOException e) {
            // Error al abrir/escribir tokens.txt (catch del try-with-resources)
            System.err.println("Error al escribir tokens: " + e.getMessage());
            GestorErrores.obtenerInstancia().mostrarError(111);
            // Intentar escribir la tabla aunque el writer de tokens fallara
            escribirTabla(lexico);
        }
    }

    private static void escribirTabla(AnalizadorLexico lexico) {
        try (BufferedWriter ts = new BufferedWriter(new FileWriter("tablaSimbolos.txt"))) {
            ts.write(lexico.getTablaSimbolos().toString());
            ts.flush();
        } catch (IOException e) {
            System.err.println("Error al escribir la tabla de símbolos: " + e.getMessage());
            GestorErrores.obtenerInstancia().mostrarError(112);
        }
    }
}