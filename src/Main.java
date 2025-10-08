import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

// Todavía no sé si va correctamente jeje

public class Main {
    public static void main(String[] args) {
        String dirPrueba = "src/codigoMal.txt";
        AnalizadorLexico lexico = new AnalizadorLexico(dirPrueba);
        try {
            FileWriter ficheroTokens = new FileWriter("./tokens.txt");
            SimpleEntry<String,Object> token;
            do {
                token = lexico.sigToken();
                System.out.println(token);
            } while (token != null && token.getKey() != "EOF");
            ficheroTokens.close();
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de tokens: " + e.getMessage());
        }
    }
}
