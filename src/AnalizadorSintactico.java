import java.util.HashMap;
import java.util.Stack;
import java.util.Set;
import java.util.Map;

public class AnalizadorSintactico {
    
    private final AnalizadorLexico lexico;
    private Stack<String> pila;
    private HashMap<String, HashMap<String, String>> tabla; // No terminal -> (Terminal -> Regla)
    private Set<String> terminales;
    private Map<String, String> nombresTerminales; // nombre token -> nombre terminal
    private String parse;
    private String tokenActual;
    private String lexemaActual;
    private String lexemaAnterior;

    public AnalizadorSintactico(AnalizadorLexico lexico) {
        this.lexico = lexico;
        inicializarPila();
        inicializarTabla();
        inicializarSetTerminales();
        inicializarMapaNombres();
    }

    public String parse(){
        parse = "";
        tokenActual = leerSiguienteToken();
        
        while (!pila.isEmpty()) {
            String topePila = pila.peek();

            if (terminales.contains(topePila)) {
                if (topePila.equals(tokenActual)) {
                    pila.pop();
                    lexemaAnterior = lexemaActual;
                    tokenActual = leerSiguienteToken();
                } else {
                    GestorErrores.obtenerInstancia().mostrarError(201, lexico.getLinea(), topePila, tokenActual, lexemaAnterior, null);
                    return "0";
                }
            } else {
                HashMap<String, String> reglas = tabla.get(topePila);
                if (reglas != null && reglas.containsKey(tokenActual)) {
                    pila.pop();
                    String regla = reglas.get(tokenActual);
                    String numero = regla.substring(0, regla.indexOf('.'));
                    parse += numero + " ";
                    String produccion = regla.substring(regla.indexOf('.') + 1);
                    if (!produccion.equals("lambda")) {
                        String[] simbolos = produccion.split(" ");
                        for (int i = simbolos.length - 1; i >= 0; i--) {
                            pila.push(simbolos[i]);
                        }
                    }
                } else {
                    // No hay regla para el no terminal con el token actual
                    Map<String,String> posibles = tabla.get(topePila);
                    String esperados = "";
                    for (String s : posibles.keySet()) esperados += "'" + s + "', ";
                    esperados = esperados.substring(0, esperados.length()-2); // Quitar la última coma y espacio
                    GestorErrores.obtenerInstancia().mostrarError(202, lexico.getLinea(), topePila, tokenActual, lexemaAnterior, esperados);
                    return "0";
                }
            }
        }

        if (tokenActual.equals("$")) {
            System.out.println("\033[32mAnálisis sintáctico completado con éxito.\033[0m");
            return parse.substring(0, parse.length()-1); // Eliminar el espacio final.
        } else {
            GestorErrores.obtenerInstancia().mostrarError(201, lexico.getLinea(), "$", tokenActual, lexemaAnterior, null);
            return "0";
        }
    }

    // Inicializa la pila con el símbolo de inicio y el marcador de fin de cadena.
    private void inicializarPila() {
        pila = new Stack<>();
        pila.push("$");
        pila.push("P"); // Símbolo de inicio
    }

    // Inicializa el conjunto de terminales del lenguaje.
    private void inicializarSetTerminales() {
        terminales = Set.of(
            "function", "if", "let", "read", "return", "while", "write", "id",
            "$", "(", ")", ";", "=", "/=", "/", ",", "{", "}", "boolean", "float",
            "int", "string", "void", "entero", "real", "cadena", "&&", "=="
        );
    }

    // Inicializa el mapa que traduce los nombres de los tokens a los nombres de los terminales usados en la tabla.
    private void inicializarMapaNombres() {
        nombresTerminales = new HashMap<>();
        nombresTerminales.put("asigDiv", "/=");
        nombresTerminales.put("asig", "=");
        nombresTerminales.put("coma", ",");
        nombresTerminales.put("puntoComa", ";");
        nombresTerminales.put("parIzq", "(");
        nombresTerminales.put("parDer", ")");
        nombresTerminales.put("llaveIzq", "{");
        nombresTerminales.put("llaveDer", "}");
        nombresTerminales.put("div", "/");
        nombresTerminales.put("y", "&&");
        nombresTerminales.put("igual", "==");
        nombresTerminales.put("EOF", "$");
    }

    // Traduce el nombre del token al nombre del terminal usado en la tabla.
    private String traducirTokenATerminal(String token) {
        return nombresTerminales.containsKey(token) ? nombresTerminales.get(token) : token;
    }

    // Inicializa la tabla descendente de análisis sintáctico. Guarda los precedentes de las reglas ya que es lo único que se usará.
    private void inicializarTabla() {
        tabla = new HashMap<>();

        HashMap<String, String> reglasP = new HashMap<>();
        reglasP.put("function", "2.F P");
        reglasP.put("if", "1.B P");
        reglasP.put("let", "1.B P");
        reglasP.put("read", "1.B P");
        reglasP.put("return", "1.B P");
        reglasP.put("while", "1.B P");
        reglasP.put("write", "1.B P");
        reglasP.put("id", "1.B P");
        reglasP.put("$", "3.lambda");
        tabla.put("P", reglasP);

        HashMap<String, String> reglasB = new HashMap<>();
        reglasB.put("if", "6.if ( E ) S");
        reglasB.put("let", "7.let T id ;");
        reglasB.put("read", "4.S");
        reglasB.put("return", "4.S");
        reglasB.put("while", "5.while ( E ) { C }");
        reglasB.put("write", "4.S");
        reglasB.put("id", "4.S");
        tabla.put("B", reglasB);

        HashMap<String, String> reglasS = new HashMap<>();
        reglasS.put("read", "10.read id ;");
        reglasS.put("return", "11.return X ;");
        reglasS.put("write", "9.write E ;");
        reglasS.put("id","8.id S1");
        tabla.put("S", reglasS);

        HashMap<String, String> reglasS1 = new HashMap<>();
        reglasS1.put("=", "12.= E ;");
        reglasS1.put("/=", "13./= E ;");
        reglasS1.put("(", "14.( L ) ;");
        tabla.put("S1", reglasS1);

        HashMap<String, String> reglasF = new HashMap<>();
        reglasF.put("function", "15.function H id ( A ) { C }");
        tabla.put("F", reglasF);

        HashMap<String, String> reglasH = new HashMap<>();
        reglasH.put("boolean", "16.T");
        reglasH.put("float", "16.T");
        reglasH.put("int", "16.T");
        reglasH.put("string", "16.T");
        reglasH.put("void", "17.void");
        tabla.put("H", reglasH);

        HashMap<String, String> reglasT = new HashMap<>();
        reglasT.put("boolean", "18.boolean");
        reglasT.put("float", "19.float");
        reglasT.put("int", "21.int");
        reglasT.put("string", "20.string");
        tabla.put("T", reglasT);

        HashMap<String, String> reglasA = new HashMap<>();
        reglasA.put("boolean", "22.T id K");
        reglasA.put("float", "22.T id K");
        reglasA.put("int", "22.T id K");
        reglasA.put("string", "22.T id K");
        reglasA.put("void", "23.void");
        tabla.put("A", reglasA);

        HashMap<String, String> reglasK = new HashMap<>();
        reglasK.put(")", "25.lambda");
        reglasK.put(",", "24., T id K");
        tabla.put("K", reglasK);

        HashMap<String, String> reglasC = new HashMap<>();
        reglasC.put("if", "26.B C");
        reglasC.put("let", "26.B C");
        reglasC.put("read", "26.B C");
        reglasC.put("return", "26.B C");
        reglasC.put("while", "26.B C");
        reglasC.put("write", "26.B C");
        reglasC.put("}", "27.lambda");
        reglasC.put("id", "26.B C");
        tabla.put("C", reglasC);

        HashMap<String, String> reglasE = new HashMap<>();
        reglasE.put("(", "28.R E1");
        reglasE.put("id", "28.R E1");
        reglasE.put("entero", "28.R E1");
        reglasE.put("real",  "28.R E1");
        reglasE.put("cadena", "28.R E1");
        tabla.put("E", reglasE);

        HashMap<String, String> reglasE1 = new HashMap<>();
        reglasE1.put("&&", "29.&& R E1");
        reglasE1.put(")", "30.lambda");
        reglasE1.put(",", "30.lambda");
        reglasE1.put(";", "30.lambda");
        tabla.put("E1", reglasE1);

        HashMap<String, String> reglasR = new HashMap<>();
        reglasR.put("(", "31.U R1");
        reglasR.put("id", "31.U R1");
        reglasR.put("entero", "31.U R1");
        reglasR.put("real", "31.U R1");
        reglasR.put("cadena", "31.U R1");
        tabla.put("R", reglasR);

        HashMap<String, String> reglasR1 = new HashMap<>();
        reglasR1.put("==", "32.== U R1");
        reglasR1.put("&&", "33.lambda");
        reglasR1.put(")", "33.lambda");
        reglasR1.put(",", "33.lambda");
        reglasR1.put(";", "33.lambda");
        tabla.put("R1", reglasR1);
        
        HashMap<String, String> reglasU = new HashMap<>();
        reglasU.put("(", "34.V U1");
        reglasU.put("id", "34.V U1");
        reglasU.put("entero", "34.V U1");
        reglasU.put("real", "34.V U1");
        reglasU.put("cadena", "34.V U1");
        tabla.put("U", reglasU);

        HashMap<String, String> reglasU1 = new HashMap<>();
        reglasU1.put("/", "35./ V U1");
        reglasU1.put("==", "36.lambda"); 
        reglasU1.put("&&", "36.lambda");
        reglasU1.put(")", "36.lambda");
        reglasU1.put(",", "36.lambda");
        reglasU1.put(";", "36.lambda");
        tabla.put("U1", reglasU1);

        HashMap<String, String> reglasV = new HashMap<>();
        reglasV.put("(", "38.( E )");
        reglasV.put("id", "37.id V1");
        reglasV.put("entero", "39.entero");
        reglasV.put("real", "40.real");
        reglasV.put("cadena", "41.cadena");
        tabla.put("V", reglasV);

        HashMap<String, String> reglasV1 = new HashMap<>();
        reglasV1.put("(", "42.( L )");
        reglasV1.put(")", "43.lambda");
        reglasV1.put("/", "43.lambda");
        reglasV1.put("==", "43.lambda");
        reglasV1.put("&&", "43.lambda");
        reglasV1.put(",", "43.lambda");
        reglasV1.put(";", "43.lambda");
        tabla.put("V1", reglasV1);

        HashMap<String, String> reglasX = new HashMap<>();
        reglasX.put("(", "44.E");
        reglasX.put(";", "45.lambda");
        reglasX.put("id", "44.E");
        reglasX.put("entero", "44.E");
        reglasX.put("real", "44.E");
        reglasX.put("cadena", "44.E");
        tabla.put("X", reglasX);

        HashMap<String, String> reglasL = new HashMap<>();
        reglasL.put("(", "46.E Q");
        reglasL.put(")", "47.lambda");
        reglasL.put("id", "46.E Q");
        reglasL.put("entero", "46.E Q");
        reglasL.put("real", "46.E Q");
        reglasL.put("cadena", "46.E Q");
        tabla.put("L", reglasL);

        HashMap<String, String> reglasQ = new HashMap<>();
        reglasQ.put(")", "49.lambda");
        reglasQ.put(",", "48., E Q");
        tabla.put("Q", reglasQ);
    }

    private String leerSiguienteToken() {
        Map.Entry<String,Object> t = lexico.sigToken();
        String nombreToken = t.getKey();
        Object lexema = t.getValue();
        String terminal = traducirTokenATerminal(nombreToken);
        // Guardar la palabra real leída o el símbolo terminal si no hay lexema
        // si es un id, guardar su lexema (nombre real)
        lexemaActual = obtenerLexemaReal(lexema, nombreToken);

        return terminal;
    }

    private String obtenerLexemaReal(Object lexema, String token) {
        if ("id".equals(token) && lexema instanceof Integer) {
            return lexico.getTablaSimbolos().getId((Integer)lexema); // nombre real
        }  
        return (lexema != null) ? lexema.toString() : traducirTokenATerminal(token);
    }


}
