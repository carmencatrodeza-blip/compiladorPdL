import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Lee README.txt para conocer los detalles de uso del procesador.");
            return;
        }
        String ruta = args[0];
        Compilador compilador = new Compilador();
        // Limpiar archivos con resultados de otras ejecuciones.
        compilador.getWriter().write("", "tokens");
        compilador.getWriter().write("", "tabla");
        compilador.getWriter().write("", "parse");
        FileReader fr = null;

        try {
            fr = new FileReader(ruta);
        } catch (FileNotFoundException fnf) {
            compilador.getGestorErrores().mostrarError(1);
        }

        AnalizadorLexico lexico = new AnalizadorLexico(compilador, fr);
        AnalizadorSemantico semantico = new AnalizadorSemantico(compilador);
        AnalizadorSintactico sintactico = new AnalizadorSintactico(compilador, lexico, semantico);

        String resultadoParse = sintactico.parse();
        compilador.getWriter().write("descendente " + resultadoParse, "parse");
        if(!compilador.getErrorDetectado())
            System.out.println("\033[32mAnálisis completado, el fichero fuente es correcto.\033[0m");

        try {
            if (fr != null) {
                fr.close();
            }
        } catch (IOException e) {
            compilador.getGestorErrores().mostrarError(6);
        }
    }
}