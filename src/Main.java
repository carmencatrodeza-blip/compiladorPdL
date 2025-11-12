import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

public class Main {
    public static void main(String[] args) {
        String dirPrueba = "src\\PIdG33.txt";
        
        // Prueba del Analizador Léxico
        FileReader fr = abrirArchivo(dirPrueba);
        if (fr != null) {
            AnalizadorLexico lexico = new AnalizadorLexico(fr);
            pruebaLexico(lexico);
            cerrarArchivo(fr);
        }
        
        // Prueba del Analizador Sintáctico
        fr = abrirArchivo(dirPrueba);
        if (fr != null) {
            AnalizadorLexico lexico = new AnalizadorLexico(fr);
            AnalizadorSintactico sintactico = new AnalizadorSintactico(lexico);
            pruebaSintactico(sintactico);
            cerrarArchivo(fr);
        }
    }

    private static FileReader abrirArchivo(String ruta) {
        try {
            return new FileReader(ruta);
        } catch (FileNotFoundException fnf) {
            GestorErrores.obtenerInstancia().mostrarError(1);
            return null;
        }
    }

    private static void cerrarArchivo(FileReader fr) {
        try {
            if (fr != null) {
                fr.close();
            }
        } catch (IOException e) {
            GestorErrores.obtenerInstancia().mostrarError(6);
        }
    }

    private static void pruebaLexico(AnalizadorLexico lexico) {
        boolean fin = false;

        try (BufferedWriter out = new BufferedWriter(new FileWriter("tokens.txt"))) {
            while (!fin) {
                SimpleEntry<String, Object> par = lexico.sigToken();

                if (par == null) {
                    System.err.println("Se detiene el análisis por error léxico previo.");
                    escribirTabla(lexico);
                    fin = true;
                } else {
                    try {
                        Token tok = Token.fromEntry(par);
                        out.write(tok.toString());
                        out.flush();
                    } catch (IOException e) {
                        GestorErrores.obtenerInstancia().mostrarError(3);
                        escribirTabla(lexico);
                        break;
                    }
                    escribirTabla(lexico);

                    if ("EOF".equals(par.getKey())) {
                        System.out.println("Lectura de fichero terminada.");
                        fin = true;
                    }
                }
            }
        } catch (IOException e) {
            GestorErrores.obtenerInstancia().mostrarError(3);
            escribirTabla(lexico);
        }
    }

    private static void pruebaSintactico(AnalizadorSintactico sintactico) {
        String resultadoParse = sintactico.parse();

        try (BufferedWriter out = new BufferedWriter(new FileWriter("parse.txt"))) {
            out.write("descendente " + resultadoParse);
            out.flush();
            System.out.println("Parse completado. Resultado guardado en parse.txt");
        } catch (IOException e) {
            GestorErrores.obtenerInstancia().mostrarError(5);
        }
    }

    private static void escribirTabla(AnalizadorLexico lexico) {
        try (BufferedWriter ts = new BufferedWriter(new FileWriter("tablaSimbolos.txt"))) {
            ts.write(lexico.getTablaSimbolos().toString());
            ts.flush();
        } catch (IOException e) {
            GestorErrores.obtenerInstancia().mostrarError(4);
        }
    }
}