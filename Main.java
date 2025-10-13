import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

public class Main {
    public static void main(String[] args) {
        String dirPrueba = "src/prueba55.txt"; // dir/ficheroDePrueba.txt
        AnalizadorLexico lexico = new AnalizadorLexico(dirPrueba);
        GestorErrores gestorErrores = new GestorErrores();

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
            }

            System.out.println("Lectura de fichero terminada.");
        } catch (IOException e) {
            System.err.println("Error al escribir tokens: " + e.getMessage());
            gestorErrores.mostrarError(111, 0, ' ');
        }
    }
}
