import java.io.FileReader;

public class AnalizadorLexico {

    private int linea; // Número de línea del documento.
    private int estado; // Estado del autómata.
    private String lexema; // Variable para construir el lexema.
    private int numero; // Variable para calcular números.
    private int caracter; // Caracter guardado como byte.
    private FileReader fr; // Lector de archivos.

    public AnalizadorLexico() {
        linea = 1;
        // fr = new FileReader("entrada.txt");
    }

    public Token sigToken() {
        estado = 0;
        lexema = "";
        numero = 0;
        while (estado <= 11){
            try {
                caracter = fr.read();
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch(estado){
                case 0: // Estado inicial
                    if(esLetra(caracter)){
                        lexema += (char)caracter;
                        estado = 1;
                    }
                    else if(esDigito(caracter)){
                        int num = caracter - 48; // Transformar de ASCII  a digito.
                        numero = numero * 10 + num; // Construir el número.
                        estado = 3;
                    }
                    else if(caracter == 95){ // _
                        lexema += (char)caracter;
                        estado = 2;
                    }
                    else if(caracter == 34){ // "
                        estado = 6;
                    }
                    else if(caracter == 47){ // /
                        estado = 8;
                    }
                    else if(caracter == 61){ // =
                        estado = 10;
                    }
                    else if(caracter == 10){ // Nueva línea
                        linea++;
                    }
                    else if(caracter == 38){ // &
                        estado = 11;
                    }
                    else if(caracter == 40){ // (
                        estado = 22;
                    }
                    else if(caracter == 41){ // )
                        estado = 23;
                    }
                    else if(caracter == 123){ // {
                        estado = 24;
                    }
                    else if(caracter == 125){ // }
                        estado = 25;
                    }
                    else if(caracter == 59){ // ;
                        estado = 26;
                    }
                    else if(caracter == 44){ // ,
                        estado = 27;
                    }
                    else {
                        //Error
                    }
                    break;
                case 1:
                    if(esLetra(caracter)){
                        lexema += (char)caracter;
                    }
                    else if(esDigito(caracter) || caracter == 95){
                        lexema += (char)caracter;
                        estado = 2;
                    }
                    else{
                        // TODO: En teoría no se puede retroceder al leer el archivo, pensar en otra manera de hacerlo.
                        // fr.skip(-1); // Regresar el caracter leído.
                        if(esReservada(lexema)){
                            // Generar token de palabra reservada.
                        }
                        else{
                            // Ver si está en la tabla de símbolos, añadir si es necesario y generar token de identificador.
                        }
                    }
            }
        }
        return null; // Placeholder para que no de error
    }

    private boolean esLetra(int c) {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }
    private boolean esDigito(int c) {
        return (c >= 48 && c <= 57);
    }
    private boolean esReservada(String s) {
        String[] reservadas = {"if", "else", "while", "return", "int", "float", "char", "void", "main"};
        for (String r : reservadas) {
            if (s.equals(r)) {
                return true;
            }
        }
        return false;
    }
}
