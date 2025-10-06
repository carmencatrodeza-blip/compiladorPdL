import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

public class Main {
    public static void main(String[] args) {
        String dirPrueba = "./codigoMal.txt"; // dir/ficheroDePrueba.txt
        AnalizadorLexico lexico = new AnalizadorLexico(dirPrueba);
        try {
            FileWriter ficheroTokens = new FileWriter("./tokens.txt");
            SimpleEntry<String,Object> token;
            do {
                token = lexico.sigToken();
            } while (token != null);
            ficheroTokens.close();
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de tokens: " + e.getMessage());
        }
    }
}
