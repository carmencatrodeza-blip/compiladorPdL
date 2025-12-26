import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    private final GestorErrores gestorErrores;
    private final String rutaTokens = "tokens.txt";
    private final String rutaTabla = "tablaSimbolos.txt";
    private final String rutaParse = "parse.txt";

    public Writer(GestorErrores gestorErrores) {
        this.gestorErrores = gestorErrores;
    }

    public void write(String contenido, String archivo) {
        String ruta = null;
        int codigo = 0;
        boolean append = contenido.isEmpty() ? false : archivo.equals("tokens");
        if (!contenido.isEmpty() && archivo.equals("tokens")) contenido += "\n";
        switch (archivo) {
            case "tokens": ruta = rutaTokens; codigo = 3; break;
            case "tabla": ruta = rutaTabla; codigo = 4; break;
            case "parse": ruta = rutaParse; codigo = 5; break;
        }
        try (FileWriter fw = new FileWriter(ruta, append)) {
            fw.write(contenido);
            fw.flush();
        } catch (IOException e) {
            gestorErrores.mostrarError(codigo);
        }
    }
}
