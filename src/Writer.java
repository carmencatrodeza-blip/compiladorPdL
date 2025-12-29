import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        boolean append = contenido.isEmpty() ? false : (!archivo.equals("parse"));
        if (!contenido.isEmpty() && !archivo.equals("parse")) contenido += "\n";
        switch (archivo) {
            case "tokens": ruta = rutaTokens; codigo = 3; break;
            case "tabla": ruta = rutaTabla; codigo = 4; break;
            case "parse": ruta = rutaParse; codigo = 5; break;
            default:
        }
        try (FileWriter fw = new FileWriter(ruta, append)) {
            fw.write(contenido);
            fw.flush();
        } catch (IOException e) {
            gestorErrores.mostrarError(codigo);
        }
    }

    public void writeTablaGlobal(String tablaGlobal) {
        String contenidoAnterior = "";

        File f = new File(rutaTabla);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String linea;
                StringBuilder sb = new StringBuilder();
                while ((linea = br.readLine()) != null) {
                    sb.append(linea).append("\n");
                }
                contenidoAnterior = sb.toString();
            } catch (IOException e) {
                gestorErrores.mostrarError(4);
                return;
            }
        }
        try (FileWriter fw = new FileWriter(rutaTabla, false)) {
            fw.write(tablaGlobal);
            if (!contenidoAnterior.isEmpty())
                fw.write("\n" + contenidoAnterior.substring(0, contenidoAnterior.length() - 1));
            fw.flush();
        } catch (IOException e) {
            gestorErrores.mostrarError(4);
        }
    }
}
