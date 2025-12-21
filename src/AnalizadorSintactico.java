import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class AnalizadorSintactico {
    
    private final AnalizadorLexico lexico;
    private final AnalizadorSemantico semantico;
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
        this.semantico = AnalizadorSemantico.obtenerInstancia();
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

            if (topePila.startsWith("{") && topePila.endsWith("}")) { // Acción semántica
                int accion = Integer.parseInt(topePila.substring(1, topePila.length()-1));
                semantico.accionSemantica(accion);
                pila.pop();
            } else if (terminales.contains(topePila)) {
                if (topePila.equals(tokenActual)) {
                    String simbolo = pila.pop();
                    semantico.pushToAux(simbolo, lexemaActual); // Asumir método en semántico
                    lexemaAnterior = lexemaActual;
                    tokenActual = leerSiguienteToken();
                } else {
                    GestorErrores.obtenerInstancia().mostrarError(201,
                        lexico.getLinea(), topePila, tokenActual, lexemaAnterior, null);
                    return "0";
                }
            } else {
                // No terminal
                HashMap<String, String> reglas = tabla.get(topePila);
                if (reglas != null && reglas.containsKey(tokenActual)) {
                    String simbolo = pila.pop();
                    semantico.pushToAux(simbolo, null);
                    String regla = reglas.get(tokenActual);
                    String numero = regla.substring(0, regla.indexOf('.'));
                    parse += numero + " ";
                    String produccion = regla.substring(regla.indexOf('.') + 1);
                    String[] simbolos = produccion.split(" ");
                    for (int i = simbolos.length - 1; i >= 0; i--) {
                        if(!simbolos[i].equals("lambda"))
                            pila.push(simbolos[i]);
                    }
                } else {
                    Map<String,String> posibles = tabla.get(topePila);
                    String esperados = "";
                    for (String s : posibles.keySet()) esperados += "'" + s + "', ";
                    esperados = esperados.substring(0, esperados.length()-2);
                    GestorErrores.obtenerInstancia().mostrarError(202,
                        lexico.getLinea(), topePila, tokenActual, lexemaAnterior, esperados);
                    return "0";
                }
            }
        }

        if (tokenActual.equals("$") && semantico.auxEsP1()) {
            System.out.println("\033[32mAnálisis sintáctico y semántico completado con éxito.\033[0m");
            return parse.substring(0, parse.length()-1);
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
        reglasP1.put("function", "1.{10} P {13} {1}");
        reglasP1.put("if", "1.{10} P {13} {1}");
        reglasP1.put("let", "1.{10} P {13} {1}");
        reglasP1.put("read", "1.{10} P {13} {1}");
        reglasP1.put("return", "1.{10} P {13} {1}");
        reglasP1.put("while", "1.{10} P {13} {1}");
        reglasP1.put("write", "1.{10} P {13} {1}");
        reglasP1.put("id", "1.{10} P {13} {1}");
        reglasP1.put("$", "1.{10} P {13} {1}");
        tabla.put("P1", reglasP1);

        HashMap<String, String> reglasP = new HashMap<>();
        reglasP.put("function", "3.F P {15} {2}");
        reglasP.put("if", "2.B P {14} {2}");
        reglasP.put("let", "2.B P {14} {2}");
        reglasP.put("read", "2.B P {14} {2}");
        reglasP.put("return", "2.B P {14} {2}");
        reglasP.put("while", "2.B P {14} {2}");
        reglasP.put("write", "2.B P {14} {2}");
        reglasP.put("id", "2.B P {14} {2}");
        reglasP.put("$", "4.lambda {16}");
        tabla.put("P", reglasP);

        HashMap<String, String> reglasB = new HashMap<>();
        reglasB.put("if", "7.if ( E ) S  {26} {5}");
        reglasB.put("let", "8.let {8} T id {6} {27} ; {4}");
        reglasB.put("read", "5.S {17} {1}");
        reglasB.put("return", "5.S {17} {1}");
        reglasB.put("while", "6.while ( E ) { C {25} } {7}");
        reglasB.put("write", "5.S {17} {1}");
        reglasB.put("id", "5.S {17} {1}");
        tabla.put("B", reglasB);

        HashMap<String, String> reglasS = new HashMap<>();
        reglasS.put("read", "11.read id {30} ; {3}");
        reglasS.put("return", "12.return X {19} ; {3}");
        reglasS.put("write", "10.write E {29} ; {3}");
        reglasS.put("id","9.id S1 {28} {2}");
        tabla.put("S", reglasS);

        HashMap<String, String> reglasS1 = new HashMap<>();
        reglasS1.put("=", "13.= E {18} ; {3}");
        reglasS1.put("/=", "14./= E {31} ; {3}");
        reglasS1.put("(", "15.( L {18} ) ; {4}");
        tabla.put("S1", reglasS1);

        HashMap<String, String> reglasF = new HashMap<>();
        reglasF.put("function", "16.function {8} H id {11} ( A {6} ) { C {32} {12} } {9}");
        tabla.put("F", reglasF);

        HashMap<String, String> reglasH = new HashMap<>();
        reglasH.put("boolean", "17.T {17} {1}");
        reglasH.put("float", "17.T {17} {1}");
        reglasH.put("int", "17.T {17} {1}");
        reglasH.put("string", "17.T {17} {1}");
        reglasH.put("void", "18.void {24} {1}");
        tabla.put("H", reglasH);

        HashMap<String, String> reglasT = new HashMap<>();
        reglasT.put("boolean", "19.boolean");
        reglasT.put("float", "20.float");
        reglasT.put("int", "22.int");
        reglasT.put("string", "21.string");
        tabla.put("T", reglasT);

        HashMap<String, String> reglasA = new HashMap<>();
        reglasA.put("boolean", "23.T id K {33} {3}");
        reglasA.put("float", "23.T id K {33} {3}");
        reglasA.put("int", "23.T id K {33} {3}");
        reglasA.put("string", "23.T id K {33} {3}");
        reglasA.put("void", "24.void {24} {1}");
        tabla.put("A", reglasA);

        HashMap<String, String> reglasK = new HashMap<>();
        reglasK.put(")", "26.lambda {16}");
        reglasK.put(",", "25., T id K {34} {4}");
        tabla.put("K", reglasK);

        HashMap<String, String> reglasC = new HashMap<>();
        reglasC.put("if", "27.B C {35} {2}");
        reglasC.put("let", "27.B C {35} {2}");
        reglasC.put("read", "27.B C {35} {2}");
        reglasC.put("return", "27.B C {35} {2}");
        reglasC.put("while", "27.B C {35} {2}");
        reglasC.put("write", "27.B C {35} {2}");
        reglasC.put("}", "28.lambda {16}");
        reglasC.put("id", "27.B C {35} {2}");
        tabla.put("C", reglasC);

        HashMap<String, String> reglasE = new HashMap<>();
        reglasE.put("(", "29.R E1 {36} {2}");
        reglasE.put("id", "29.R E1 {36} {2}");
        reglasE.put("entero", "29.R E1 {36} {2}");
        reglasE.put("real",  "29.R E1 {36} {2}");
        reglasE.put("cadena", "29.R E1 {36} {2}");
        tabla.put("E", reglasE);

        HashMap<String, String> reglasE1 = new HashMap<>();
        reglasE1.put("&&", "30.&& R E1 {37} {3}");
        reglasE1.put(")", "31.lambda {16}");
        reglasE1.put(",", "31.lambda {16}");
        reglasE1.put(";", "31.lambda {16}");
        tabla.put("E1", reglasE1);

        HashMap<String, String> reglasR = new HashMap<>();
        reglasR.put("(", "32.U R1 {38} {2}");
        reglasR.put("id", "32.U R1 {38} {2}");
        reglasR.put("entero", "32.U R1 {38} {2}");
        reglasR.put("real", "32.U R1 {38} {2}");
        reglasR.put("cadena", "32.U R1 {38} {2}");
        tabla.put("R", reglasR);

        HashMap<String, String> reglasR1 = new HashMap<>();
        reglasR1.put("==", "33.== U R1 {39} {3}");
        reglasR1.put("&&", "34.lambda {16}");
        reglasR1.put(")", "34.lambda {16}");
        reglasR1.put(",", "34.lambda {16}");
        reglasR1.put(";", "34.lambda {16}");
        tabla.put("R1", reglasR1);
        
        HashMap<String, String> reglasU = new HashMap<>();
        reglasU.put("(", "35.V U1 {40} {2}");
        reglasU.put("id", "35.V U1 {40} {2}");
        reglasU.put("entero", "35.V U1 {40} {2}");
        reglasU.put("real", "35.V U1 {40} {2}");
        reglasU.put("cadena", "35.V U1 {40} {2}");
        tabla.put("U", reglasU);

        HashMap<String, String> reglasU1 = new HashMap<>();
        reglasU1.put("/", "36./ V U1 {41} {3}");
        reglasU1.put("==", "37.lambda {16}"); 
        reglasU1.put("&&", "37.lambda {16}");
        reglasU1.put(")", "37.lambda {16}");
        reglasU1.put(",", "37.lambda {16}");
        reglasU1.put(";", "37.lambda {16}");
        tabla.put("U1", reglasU1);

        HashMap<String, String> reglasV = new HashMap<>();
        reglasV.put("(", "39.( E {18} ) {3}");
        reglasV.put("id", "38.id V1 {42} {2}");
        reglasV.put("entero", "40.entero {21} {1}");
        reglasV.put("real", "41.real {22} {1}");
        reglasV.put("cadena", "42.cadena {23} {1}");
        tabla.put("V", reglasV);

        HashMap<String, String> reglasV1 = new HashMap<>();
        reglasV1.put("(", "43.( L {18} ) {3}");
        reglasV1.put(")", "44.lambda {16}");
        reglasV1.put("/", "44.lambda {16}");
        reglasV1.put("==", "44.lambda {16}");
        reglasV1.put("&&", "44.lambda {16}");
        reglasV1.put(",", "44.lambda {16}");
        reglasV1.put(";", "44.lambda {16}");
        tabla.put("V1", reglasV1);

        HashMap<String, String> reglasX = new HashMap<>();
        reglasX.put("(", "45.E {17} {1}");
        reglasX.put(";", "46.lambda {16}");
        reglasX.put("id", "45.E {17} {1}");
        reglasX.put("entero", "45.E {17} {1}");
        reglasX.put("real", "45.E {17} {1}");
        reglasX.put("cadena", "45.E {17} {1}");
        tabla.put("X", reglasX);

        HashMap<String, String> reglasL = new HashMap<>();
        reglasL.put("(", "47.E Q {43} {2}");
        reglasL.put(")", "48.lambda {16}");
        reglasL.put("id", "47.E Q {43} {2}");
        reglasL.put("entero", "47.E Q {43} {2}");
        reglasL.put("real", "47.E Q {43} {2}");
        reglasL.put("cadena", "47.E Q {43} {2}");
        tabla.put("L", reglasL);

        HashMap<String, String> reglasQ = new HashMap<>();
        reglasQ.put(")", "50.lambda {16}");
        reglasQ.put(",", "49., E Q {44} {3}");
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
