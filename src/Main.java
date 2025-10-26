import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

public class Main {
    public static void main(String[] args) {
        String dirPrueba = "src/PIdG33.txt"; // dir/ficheroDePrueba.txt
        AnalizadorLexico lexico = new AnalizadorLexico(dirPrueba);
        GestorErrores gestorErrores = new GestorErrores();

        /*
        TODO: Las tablas de simbolos son independientes de los analizadores
        mapTablas = new HashMap<String, TablaSimbolos>();
        empareja: etiqueta -> tablaSimbolos
         */

        boolean fin = false;

        try (BufferedWriter out = new BufferedWriter(new FileWriter("tokens.txt"))) {
            while (!fin) {
                SimpleEntry<String, Object> par = lexico.sigToken();

                if (par == null) {
                    System.err.println("Se detiene el análisis por error léxico previo.");
                    fin = true;
                } else {
                    Token tok = Token.fromEntry(par);
                    out.write(tok.toString());
                    out.flush();

                    if ("EOF".equals(par.getKey())) {
                        fin = true;
                    }
                }
            }
            try (BufferedWriter ts = new BufferedWriter(new FileWriter("tablaSimbolos.txt"))) {
                ts.write(lexico.getTablaSimbolos().toString());
            } catch (IOException e) {
                System.err.println("Error al escribir la tabla de símbolos: " + e.getMessage());
                gestorErrores.mostrarError(112, 0, ' ', null);
            }

            System.out.println("Lectura de fichero terminada.");
        } catch (IOException e) {
            System.err.println("Error al escribir tokens: " + e.getMessage());
            gestorErrores.mostrarError(111, 0, ' ', null);
        }
    }
}