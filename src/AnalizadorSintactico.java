import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class AnalizadorSintactico {
    
    private final AnalizadorLexico lexico;
    private Stack<String> pila;
    private HashMap<String, HashMap<String, String>> tabla; // No terminal -> (Terminal -> Regla)
    private Set<String> terminales;
    private Map<String, String> nombresTerminales; // nombre token -> nombre terminal
    private String parse;
    private String tokenActual; // Código del token actual traducido a terminal
    private String lexemaActual; // Valor del token leído
    private String lexemaAnterior; // Valor del token anterior

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
                    GestorErrores.obtenerInstancia().mostrarError(201,
                        lexico.getLinea(), topePila, tokenActual, lexemaAnterior, null);
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
                    // Obtener las reglas posibles para el no terminal
                    Map<String,String> posibles = tabla.get(topePila);
                    // Obtener los terminales posibles
                    String esperados = "";
                    for (String s : posibles.keySet()) esperados += "'" + s + "', ";
                    esperados = esperados.substring(0, esperados.length()-2); // Quitar la última coma y espacio
                    GestorErrores.obtenerInstancia().mostrarError(202,
                        lexico.getLinea(), topePila, tokenActual, lexemaAnterior, esperados);
                    return "0";
                }
            }
        }

        if (tokenActual.equals("$")) {
            System.out.println("\033[32mAnálisis sintáctico completado con éxito.\033[0m");
            return parse.substring(0, parse.length()-1); // Eliminar el espacio final.
        } else {
            GestorErrores.obtenerInstancia().mostrarError(201,
                lexico.getLinea(), "$", tokenActual, lexemaAnterior, null);
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

    // Inicializa la tabla descendente de análisis sintáctico.
    // Guarda los precedentes de las reglas ya que es lo único que se usará.
    private void inicializarTabla() {
        tabla = new HashMap<>();

        HashMap<String, String> reglasP1 = new HashMap<>();
        reglasP1.put("function", "1.P");
        reglasP1.put("if", "1.P");
        reglasP1.put("let", "1.P");
        reglasP1.put("read", "1.P");
        reglasP1.put("return", "1.P");
        reglasP1.put("while", "1.P");
        reglasP1.put("write", "1.P");
        reglasP1.put("id", "1.P");
        reglasP1.put("$", "1.P");
        tabla.put("P1", reglasP1);

        HashMap<String, String> reglasP = new HashMap<>();
        reglasP.put("function", "3.F P");
        reglasP.put("if", "2.B P");
        reglasP.put("let", "2.B P");
        reglasP.put("read", "2.B P");
        reglasP.put("return", "2.B P");
        reglasP.put("while", "2.B P");
        reglasP.put("write", "2.B P");
        reglasP.put("id", "2.B P");
        reglasP.put("$", "4.lambda");
        tabla.put("P", reglasP);

        HashMap<String, String> reglasB = new HashMap<>();
        reglasB.put("if", "7.if ( E ) S");
        reglasB.put("let", "8.let T id ;");
        reglasB.put("read", "5.S");
        reglasB.put("return", "5.S");
        reglasB.put("while", "6.while ( E ) { C }");
        reglasB.put("write", "5.S");
        reglasB.put("id", "5.S");
        tabla.put("B", reglasB);

        HashMap<String, String> reglasS = new HashMap<>();
        reglasS.put("read", "11.read id ;");
        reglasS.put("return", "12.return X ;");
        reglasS.put("write", "10.write E ;");
        reglasS.put("id","9.id S1");
        tabla.put("S", reglasS);

        HashMap<String, String> reglasS1 = new HashMap<>();
        reglasS1.put("=", "13.= E ;");
        reglasS1.put("/=", "14./= E ;");
        reglasS1.put("(", "15.( L ) ;");
        tabla.put("S1", reglasS1);

        HashMap<String, String> reglasF = new HashMap<>();
        reglasF.put("function", "16.function H id ( A ) { C }");
        tabla.put("F", reglasF);

        HashMap<String, String> reglasH = new HashMap<>();
        reglasH.put("boolean", "17.T");
        reglasH.put("float", "17.T");
        reglasH.put("int", "17.T");
        reglasH.put("string", "17.T");
        reglasH.put("void", "18.void");
        tabla.put("H", reglasH);

        HashMap<String, String> reglasT = new HashMap<>();
        reglasT.put("boolean", "19.boolean");
        reglasT.put("float", "20.float");
        reglasT.put("int", "22.int");
        reglasT.put("string", "21.string");
        tabla.put("T", reglasT);

        HashMap<String, String> reglasA = new HashMap<>();
        reglasA.put("boolean", "23.T id K");
        reglasA.put("float", "23.T id K");
        reglasA.put("int", "23.T id K");
        reglasA.put("string", "23.T id K");
        reglasA.put("void", "24.void");
        tabla.put("A", reglasA);

        HashMap<String, String> reglasK = new HashMap<>();
        reglasK.put(")", "26.lambda");
        reglasK.put(",", "25., T id K");
        tabla.put("K", reglasK);

        HashMap<String, String> reglasC = new HashMap<>();
        reglasC.put("if", "27.B C");
        reglasC.put("let", "27.B C");
        reglasC.put("read", "27.B C");
        reglasC.put("return", "27.B C");
        reglasC.put("while", "27.B C");
        reglasC.put("write", "27.B C");
        reglasC.put("}", "28.lambda");
        reglasC.put("id", "27.B C");
        tabla.put("C", reglasC);

        HashMap<String, String> reglasE = new HashMap<>();
        reglasE.put("(", "29.R E1");
        reglasE.put("id", "29.R E1");
        reglasE.put("entero", "29.R E1");
        reglasE.put("real",  "29.R E1");
        reglasE.put("cadena", "29.R E1");
        tabla.put("E", reglasE);

        HashMap<String, String> reglasE1 = new HashMap<>();
        reglasE1.put("&&", "30.&& R E1");
        reglasE1.put(")", "31.lambda");
        reglasE1.put(",", "31.lambda");
        reglasE1.put(";", "31.lambda");
        tabla.put("E1", reglasE1);

        HashMap<String, String> reglasR = new HashMap<>();
        reglasR.put("(", "32.U R1");
        reglasR.put("id", "32.U R1");
        reglasR.put("entero", "32.U R1");
        reglasR.put("real", "32.U R1");
        reglasR.put("cadena", "32.U R1");
        tabla.put("R", reglasR);

        HashMap<String, String> reglasR1 = new HashMap<>();
        reglasR1.put("==", "33.== U R1");
        reglasR1.put("&&", "34.lambda");
        reglasR1.put(")", "34.lambda");
        reglasR1.put(",", "34.lambda");
        reglasR1.put(";", "34.lambda");
        tabla.put("R1", reglasR1);
        
        HashMap<String, String> reglasU = new HashMap<>();
        reglasU.put("(", "35.V U1");
        reglasU.put("id", "35.V U1");
        reglasU.put("entero", "35.V U1");
        reglasU.put("real", "35.V U1");
        reglasU.put("cadena", "35.V U1");
        tabla.put("U", reglasU);

        HashMap<String, String> reglasU1 = new HashMap<>();
        reglasU1.put("/", "36./ V U1");
        reglasU1.put("==", "37.lambda"); 
        reglasU1.put("&&", "37.lambda");
        reglasU1.put(")", "37.lambda");
        reglasU1.put(",", "37.lambda");
        reglasU1.put(";", "37.lambda");
        tabla.put("U1", reglasU1);

        HashMap<String, String> reglasV = new HashMap<>();
        reglasV.put("(", "39.( E )");
        reglasV.put("id", "38.id V1");
        reglasV.put("entero", "40.entero");
        reglasV.put("real", "41.real");
        reglasV.put("cadena", "42.cadena");
        tabla.put("V", reglasV);

        HashMap<String, String> reglasV1 = new HashMap<>();
        reglasV1.put("(", "43.( L )");
        reglasV1.put(")", "44.lambda");
        reglasV1.put("/", "44.lambda");
        reglasV1.put("==", "44.lambda");
        reglasV1.put("&&", "44.lambda");
        reglasV1.put(",", "44.lambda");
        reglasV1.put(";", "44.lambda");
        tabla.put("V1", reglasV1);

        HashMap<String, String> reglasX = new HashMap<>();
        reglasX.put("(", "45.E");
        reglasX.put(";", "46.lambda");
        reglasX.put("id", "45.E");
        reglasX.put("entero", "45.E");
        reglasX.put("real", "45.E");
        reglasX.put("cadena", "45.E");
        tabla.put("X", reglasX);

        HashMap<String, String> reglasL = new HashMap<>();
        reglasL.put("(", "47.E Q");
        reglasL.put(")", "48.lambda");
        reglasL.put("id", "47.E Q");
        reglasL.put("entero", "47.E Q");
        reglasL.put("real", "47.E Q");
        reglasL.put("cadena", "47.E Q");
        tabla.put("L", reglasL);

        HashMap<String, String> reglasQ = new HashMap<>();
        reglasQ.put(")", "50.lambda");
        reglasQ.put(",", "49., E Q");
        tabla.put("Q", reglasQ);
    }

    private String leerSiguienteToken() {
        Map.Entry<String,Object> t = lexico.sigToken();
        String nombreToken = t.getKey();
        Object lexema = t.getValue();
        String terminal = traducirTokenATerminal(nombreToken);
        // Guardar la palabra real leída o el símbolo terminal si no hay lexema
        lexemaActual = obtenerLexema(lexema, nombreToken);

        return terminal;
    }

    private String obtenerLexema(Object lexema, String token) {
        if ("id".equals(token) && lexema instanceof Integer) {
            return lexico.getTablaSimbolos().getId((Integer)lexema);
        }  
        return (lexema != null) ? lexema.toString() : traducirTokenATerminal(token);
    }


}
