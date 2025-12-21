import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

public class AnalizadorSemantico {
    private static final AnalizadorSemantico instancia = new AnalizadorSemantico();
    ArrayList<SimpleEntry<String, String>> pilaAux;
    TablaSimbolos tablaSimbolosGlobal;
    TablaSimbolos tablaSimbolosLocal;
    String etiquetaTablaActual;
    boolean zonaDeclaracion;
    int desplazamientoGlobal;
    int desplazamientoLocal;

    private AnalizadorSemantico() {
        pilaAux = new ArrayList<>();
    }

    public static AnalizadorSemantico obtenerInstancia() {
        return instancia;
    }

    public void accionSemantica(int codigoAccion) {
        int TOPE = pilaAux.size() - 1; // Índice del tope de la pila
        // Declaración de variables auxiliares para las reglas
        SimpleEntry<String, String> P1;
        SimpleEntry<String, String> P;
        SimpleEntry<String, String> Pa;
        SimpleEntry<String, String> B;
        SimpleEntry<String, String> F;
        SimpleEntry<String, String> S;
        SimpleEntry<String, String> S1;
        SimpleEntry<String, String> H;
        SimpleEntry<String, String> T;
        SimpleEntry<String, String> A;
        SimpleEntry<String, String> K;
        SimpleEntry<String, String> Ka;
        SimpleEntry<String, String> C;
        SimpleEntry<String, String> Ca;
        SimpleEntry<String, String> E;
        SimpleEntry<String, String> E1;
        SimpleEntry<String, String> E1a;
        SimpleEntry<String, String> R;
        SimpleEntry<String, String> R1;
        SimpleEntry<String, String> R1a;
        SimpleEntry<String, String> U;
        SimpleEntry<String, String> U1;
        SimpleEntry<String, String> U1a;
        SimpleEntry<String, String> V;
        SimpleEntry<String, String> V1;
        SimpleEntry<String, String> L;
        SimpleEntry<String, String> Q;
        SimpleEntry<String, String> Qa;
        SimpleEntry<String, String> id;

        switch (codigoAccion) {
            case 1:
                pilaAux.remove(TOPE);
            break;
            case 2:
                pilaAux.subList(TOPE - 1, TOPE + 1).clear();
            break;
            case 3:
                pilaAux.subList(TOPE - 2, TOPE + 1).clear();
            break;
            case 4:
                pilaAux.subList(TOPE - 3, TOPE + 1).clear();
            break;
            case 5:
                pilaAux.subList(TOPE - 4, TOPE + 1).clear();
            break;
            case 6:
                zonaDeclaracion = false;
            break;
            case 7:
                pilaAux.subList(TOPE - 6, TOPE + 1).clear();
            break;
            case 8:
                zonaDeclaracion = true;
            break;
            case 9:
                pilaAux.subList(TOPE - 8, TOPE + 1).clear();
            break;
            case 10:
                tablaSimbolosGlobal = new TablaSimbolos();
                desplazamientoGlobal = 1;
                etiquetaTablaActual = "global";
            break;
            case 11:
                tablaSimbolosLocal = new TablaSimbolos();
                desplazamientoLocal = -1;
                etiquetaTablaActual = "funcion_" + tablaSimbolosGlobal.getId(Integer.parseInt(pilaAux.get(TOPE).getValue()));
            break;
            case 12:
                tablaSimbolosLocal = null;
                etiquetaTablaActual = "global";
            break;
            case 13:
                // P1 -> P
                P1 = pilaAux.get(TOPE - 1);
                P = pilaAux.get(TOPE);   
                if(P.getValue().equals("tipo_ok"))
                    P1.setValue("tipo_ok");
                else
                    P1.setValue("tipo_error");
                tablaSimbolosGlobal = null;
            break;
            case 14:
                // P -> B P
                P = pilaAux.get(TOPE - 2);
                B = pilaAux.get(TOPE - 1);
                Pa = pilaAux.get(TOPE);
                if (B.getValue().equals("ret_logico") || B.getValue().equals("ret_entero") ||
                    B.getValue().equals("ret_real") || B.getValue().equals("ret_cadena")) {
                    P.setValue("tipo_error");
                } else if (Pa.getValue().equals("vacio")) {
                    P.setValue(B.getValue());
                } else if (B.getValue().equals(Pa.getValue()) &&
                        (B.getValue().equals("tipo_ok"))) {
                    P.setValue("tipo_ok");
                } else {
                    P.setValue("tipo_error");
                }
            break;
            case 15:
                // P -> F P
                P = pilaAux.get(TOPE - 2);
                F = pilaAux.get(TOPE - 1);
                Pa = pilaAux.get(TOPE);
                if (Pa.getValue().equals("vacio")) {
                    P.setValue(F.getValue());
                } else if (F.getValue().equals(Pa.getValue()) &&
                        (F.getValue().equals("tipo_ok"))) {
                    P.setValue("tipo_ok");
                } else {
                    P.setValue("tipo_error");
                }
            break;
            case 16:
                pilaAux.get(TOPE).setValue("vacio");
            break;
            case 17:
                pilaAux.get(TOPE - 1).setValue(pilaAux.get(TOPE).getValue());
            break;
            case 18:
                pilaAux.get(TOPE - 2).setValue(pilaAux.get(TOPE).getValue());
            break;
            case 19:
                pilaAux.get(TOPE - 1).setValue("ret_" + pilaAux.get(TOPE).getValue());
            break;
            case 20:
                pilaAux.get(TOPE - 1).setValue("logico");
            break;
            case 21:
                pilaAux.get(TOPE - 1).setValue("entero");
            break;
            case 22:
                pilaAux.get(TOPE - 1).setValue("real");
            break;
            case 23:
                pilaAux.get(TOPE - 1).setValue("cadena");
            break;
            case 24:
                pilaAux.get(TOPE - 1).setValue("void");
            break;
            case 25:
                // B -> while ( E ) { C
                B = pilaAux.get(TOPE - 6);
                E = pilaAux.get(TOPE - 3);
                C = pilaAux.get(TOPE);
                if (E.getValue().equals("logico") && !C.getValue().equals("tipo_error")) {
                    B.setValue(C.getValue());
                } else {
                    B.setValue("tipo_error");
                }
            break;
            case 26:
                // B -> if ( E ) S
                B = pilaAux.get(TOPE - 5);
                E = pilaAux.get(TOPE - 2);
                S = pilaAux.get(TOPE);
                if (E.getValue().equals("logico") && S.getValue().equals("tipo_ok")) {
                    B.setValue("tipo_ok");
                } else {
                    B.setValue("tipo_error");
                }
            break;
            case 27:
                // B -> let T id
                B = pilaAux.get(TOPE - 3);
                T = pilaAux.get(TOPE - 1);
                id = pilaAux.get(TOPE);
                actualizarVariableTS(obtenerTablaActual(), id.getValue(), T.getValue());
                incrementarDesplazamiento(desplazamiento(T.getValue()));
                B.setValue("tipo_ok");
            break;
            case 28:
                // S -> id S1
                S = pilaAux.get(TOPE - 2);
                id = pilaAux.get(TOPE - 1);
                S1 = pilaAux.get(TOPE);
                if (buscarTipo(id.getValue(),obtenerTablaActual()).equals(S1.getValue())) {
                    S.setValue("tipo_ok");
                } else if (buscarTipo(id.getValue(),obtenerTablaActual()).equals("funcion") &&
                            buscarParametros(id.getValue(),obtenerTablaActual()).equals(S1.getValue())) {
                    S.setValue("tipo_ok");
                } else if (buscarTipo(id.getValue(),obtenerTablaActual()) == null &&
                            S1.getValue().equals("entero")) {
                    S.setValue("tipo_ok");
                    // tablaSimbolosGlobal.addSimbolo() // ! si no se ha declarado se añade a  TSG, creo que vamos a tener que cambiar como se añade desde el lexico.
                    actualizarVariableTS(tablaSimbolosGlobal, etiquetaTablaActual, etiquetaTablaActual);
                } else {
                    S.setValue("tipo_error");
                }
            break;
            case 29:
                // S -> write E
                S = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE);
                if (E.getValue().equals("cadena") || E.getValue().equals("entero") || E.getValue().equals("real")) {
                    S.setValue(E.getValue());
                } else {
                    S.setValue("tipo_error");
                }
            break;
            case 30:
                // S -> read id
                S = pilaAux.get(TOPE - 2);
                id = pilaAux.get(TOPE);
                if (buscarTipo(id.getValue(),obtenerTablaActual()).equals("cadena") ||
                    buscarTipo(id.getValue(),obtenerTablaActual()).equals("entero") ||
                    buscarTipo(id.getValue(),obtenerTablaActual()).equals("real")) {
                    S.setValue("tipo_ok");
                } else {
                    S.setValue("tipo_error");
                }
            break;
            case 31:
                // S -> /= E
                S = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE);
                if (E.getValue().equals("entero") || E.getValue().equals("real")) {
                    S.setValue(E.getValue());
                } else {
                    S.setValue("tipo_error");
                }
            break;
            case 32:
                // F -> function H id ( A ) { C
                F = pilaAux.get(TOPE - 8);
                H = pilaAux.get(TOPE - 6);
                id = pilaAux.get(TOPE - 5);
                A = pilaAux.get(TOPE - 3);
                C = pilaAux.get(TOPE);
                if (C.getValue().equals("ret_"+H.getValue())) {
                    F.setValue("tipo_ok");
                    actualizarFuncionTS();
                } else if (H.getValue().equals("void") &&
                            (C.getValue().equals("vacio") || C.getValue().equals("tipo_ok"))) {
                    F.setValue("tipo_ok");
                    actualizarFuncionTS();
                } else {
                    F.setValue("tipo_error");
                }
            break;
            case 33:
                // A -> T id K
                A = pilaAux.get(TOPE - 3);
                T = pilaAux.get(TOPE - 2);
                K = pilaAux.get(TOPE);
                if (K.getValue().equals("vacio")) {
                    A.setValue(T.getValue());
                } else {
                    A.setValue(T.getValue() + "," + K.getValue());
                }
            break;
            case 34:
                // K -> , T id K
                K = pilaAux.get(TOPE - 4);
                T = pilaAux.get(TOPE - 2);
                Ka = pilaAux.get(TOPE);
                if (Ka.getValue().equals("vacio")) {
                    K.setValue(T.getValue());
                } else {
                    K.setValue(T.getValue() + "," + Ka.getValue());
                }
            break;
            case 35:
                // C -> B C
                C = pilaAux.get(TOPE - 2);
                B = pilaAux.get(TOPE - 1);
                Ca = pilaAux.get(TOPE);
                if (B.getValue().equals("tipo_error") || Ca.getValue().equals("tipo_error")) {
                    C.setValue("tipo_error");
                } else if (B.getValue().equals("ret_logico") || B.getValue().equals("ret_entero") ||
                            B.getValue().equals("ret_real") || B.getValue().equals("ret_cadena")) {
                    C.setValue(B.getValue());
                } else if (Ca.getValue().equals("vacio")) {
                    C.setValue(B.getValue());
                } else {
                    C.setValue(Ca.getValue());
                }
                break;
            case 36:
                // E -> R E1
                E = pilaAux.get(TOPE - 2);
                R = pilaAux.get(TOPE - 1);
                E1 = pilaAux.get(TOPE);
                if (E1.getValue().equals("vacio")) {
                    E.setValue(R.getValue());
                } else if (R.getValue().equals(E1.getValue()) && R.getValue().equals("logico")) {
                    E.setValue("logico");
                } else {
                    E.setValue("tipo_error");
                }
            break;
            case 37:
                // E1 -> && R E1
                E1 = pilaAux.get(TOPE - 3);
                R = pilaAux.get(TOPE - 1);
                E1a = pilaAux.get(TOPE);
                if (E1a.getValue().equals("vacio")) {
                    E1.setValue(R.getValue());
                } else if (R.getValue().equals(E1a.getValue()) &&
                            (R.getValue().equals("logico") || R.getValue().equals("entero") || R.getValue().equals("real"))) {
                    E1.setValue(R.getValue());
                } else {
                    E1.setValue("tipo_error");
                }
            break;
            case 38:
                // R -> U R1
                R = pilaAux.get(TOPE - 2);
                U = pilaAux.get(TOPE - 1);
                R1 = pilaAux.get(TOPE);
                if (R1.getValue().equals("vacio")) {
                    R.setValue(U.getValue());
                } else if (U.getValue().equals(R1.getValue()) &&
                            (U.getValue().equals("logico") || U.getValue().equals("entero") || U.getValue().equals("real"))) {
                    R.setValue(U.getValue());
                } else {
                    R.setValue("tipo_error");
                }
            break;
            case 39:
                // R1 -> == U R1
                R1 = pilaAux.get(TOPE - 3);
                U = pilaAux.get(TOPE - 1);
                R1a = pilaAux.get(TOPE);
                if (R1a.getValue().equals("vacio")) {
                    R1.setValue(U.getValue());
                } else if (U.getValue().equals(R1a.getValue()) &&
                            (U.getValue().equals("logico") || U.getValue().equals("entero") || U.getValue().equals("real"))) {
                    R1.setValue("logico");
                } else {
                    R1.setValue("tipo_error");
                }
            break;
            case 40:
                // U -> V U1
                U = pilaAux.get(TOPE - 2);
                V = pilaAux.get(TOPE - 1);
                U1 = pilaAux.get(TOPE);
                if (U1.getValue().equals("vacio")) {
                    U.setValue(V.getValue());
                } else if (V.getValue().equals(U1.getValue()) &&
                            (V.getValue().equals("entero") || V.getValue().equals("real"))) {
                    U.setValue(V.getValue());
                } else {
                    U.setValue("tipo_error");
                }
            break;
            case 41:
                // U1 -> / V U1
                U1 = pilaAux.get(TOPE - 3);
                V = pilaAux.get(TOPE - 1);
                U1a = pilaAux.get(TOPE);
                if (U1a.getValue().equals("vacio")) {
                    U1.setValue(V.getValue());
                } else if (V.getValue().equals(U1a.getValue()) &&
                            (V.getValue().equals("entero") || V.getValue().equals("real"))) {
                    U1.setValue(V.getValue());
                } else {
                    U1.setValue("tipo_error");
                }
            break;
            case 42:
                // V -> id V1
                V = pilaAux.get(TOPE - 2);
                id = pilaAux.get(TOPE - 1);
                V1 = pilaAux.get(TOPE);
                if (V1.getValue().equals("vacio")) {
                    V.setValue(buscarTipo(id.getValue(),obtenerTablaActual()));
                } else if (buscarTipo(id.getValue(),obtenerTablaActual()).equals("funcion") &&
                            buscarParametros(id.getValue(),obtenerTablaActual()).equals(V1.getValue())) {
                    V.setValue(buscarTipoRetorno(id.getValue(),obtenerTablaActual()));
                } else {
                    V.setValue("tipo_error");
                }
            break;
            case 43:
                // L -> E Q
                L = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE - 1);
                Q = pilaAux.get(TOPE);
                if (Q.getValue().equals("vacio")) {
                    L.setValue(E.getValue());
                } else {
                    L.setValue(E.getValue() + "," + Q.getValue());
                }
            break;
            case 44:
                // Q -> , E Q
                Q = pilaAux.get(TOPE - 3);
                E = pilaAux.get(TOPE - 1);
                Qa = pilaAux.get(TOPE);
                if (Qa.getValue().equals("vacio")) {
                    Q.setValue(E.getValue());
                } else {
                    Q.setValue(E.getValue() + "," + Qa.getValue());
                }
            break;
        }
    }

    private void actualizarVariableTS(TablaSimbolos tabla, String pos, String tipo) {
        // TODO
    }
    private void actualizarFuncionTS() {
        // TODO
    }
    private int desplazamiento(String tipo) {
        switch (tipo) {
            case "logico":
                return 4;
            case "entero":
                return 4;
            case "real":
                return 8;
            case "cadena":
                return 128;
            default:
                return 0;
        }
    }

    private void incrementarDesplazamiento(int incremento) {
        if (etiquetaTablaActual.equals("global")) {
            desplazamientoGlobal += incremento;
        } else {
            desplazamientoLocal -= incremento;
        }
    }

    private TablaSimbolos obtenerTablaActual() {
        if (etiquetaTablaActual.equals("global")) {
            return tablaSimbolosGlobal;
        } else {
            return tablaSimbolosLocal;
        }
    }

    private String buscarTipo(String pos, TablaSimbolos tabla) {
        TablaSimbolos.Simbolo s = tabla.getSimbolo(Integer.valueOf(pos));
        if (s == null) {
            return null;
        }
        return s.getTipo();
    }

    private String buscarParametros(String pos, TablaSimbolos tabla) {
        TablaSimbolos.Simbolo s = tabla.getSimbolo(Integer.valueOf(pos));
        if (s == null) {
            return null;
        }
        return s.getTiposParams();
    }

    private String buscarTipoRetorno(String pos, TablaSimbolos tabla) {
        TablaSimbolos.Simbolo s = tabla.getSimbolo(Integer.valueOf(pos));
        if (s == null) {
            return null;
        }
        return s.getTipoRetorno();
    }
    
    // Añade desde el Sintáctico los elementos a la pila auxiliar
    public void pushToAux(String simbolo, String atributo) {
        pilaAux.add(new SimpleEntry<>(simbolo, atributo));
    }

    // Condición de análisis semántico correcto
    public boolean auxEsP1() {
        return pilaAux.size() == 1 && pilaAux.get(0).getKey().equals("P1")
                && pilaAux.get(0).getValue().equals("tipo_ok");
    }
}