import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

public class AnalizadorSemantico {
    private final Compilador compilador;
    ArrayList<SimpleEntry<String, String>> pilaAux;
    int desplazamientoGlobal;
    int desplazamientoLocal;

    public AnalizadorSemantico(Compilador compilador) {
        this.compilador = compilador;
        pilaAux = new ArrayList<>();
    }

    public void accionSemantica(int codigoAccion) {
        // TODO: Implementar lanzamiento de errores semanticos
        System.out.println("DEBUG: Antes de accion semantica " + codigoAccion + ", pilaAux: " + pilaAux);
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
                compilador.setZonaDeclaracion(false);
            break;
            case 7:
                pilaAux.subList(TOPE - 6, TOPE + 1).clear();
            break;
            case 8:
                compilador.setZonaDeclaracion(true);
            break;
            case 9:
                pilaAux.subList(TOPE - 8, TOPE + 1).clear();
            break;
            case 10:
                desplazamientoGlobal = 1;
            break;
            case 11:
                compilador.setTablaLocal(new TablaSimbolos());
                desplazamientoLocal = -1;
                String etiqueta = "FUNCION_" + compilador.getTablaGlobal().getId(Integer.parseInt(pilaAux.get(TOPE).getValue()));
                compilador.setEtiquetaActual(etiqueta);
            break;
            case 12:
                compilador.getWriter().write(compilador.getTablaLocal().toString(), "tabla");
                compilador.setTablaLocal(null);
                compilador.setEtiquetaActual("GLOBAL");
            break;
            case 13:
                // P1 -> P
                P1 = pilaAux.get(TOPE - 1);
                P = pilaAux.get(TOPE);   
                if("tipo_ok".equals(P.getValue()))
                    P1.setValue("tipo_ok");
                else
                    P1.setValue("tipo_error");
                compilador.getWriter().write(compilador.getTablaGlobal().toString(), "tabla");
            break;
            case 14:
                // P -> B P
                P = pilaAux.get(TOPE - 2);
                B = pilaAux.get(TOPE - 1);
                Pa = pilaAux.get(TOPE);
                if ("ret_logico".equals(B.getValue()) || "ret_entero".equals(B.getValue()) ||
                    "ret_real".equals(B.getValue()) || "ret_cadena".equals(B.getValue())) {
                    P.setValue("tipo_error");
                } else if ("vacio".equals(Pa.getValue())) {
                    P.setValue(B.getValue());
                } else if (B.getValue().equals(Pa.getValue()) &&
                        ("tipo_ok".equals(B.getValue()))) {
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
                if ("vacio".equals(Pa.getValue())) {
                    P.setValue(F.getValue());
                } else if (F.getValue().equals(Pa.getValue()) &&
                        ("tipo_ok".equals(F.getValue()))) {
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
                pilaAux.get(TOPE - 2).setValue("ret_" + pilaAux.get(TOPE).getValue());
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
                if ("logico".equals(E.getValue()) && !"tipo_error".equals(C.getValue())) {
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
                if ("logico".equals(E.getValue()) && "tipo_ok".equals(S.getValue())) {
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
                if (buscarTipo(id.getValue()).equals(S1.getValue())) {
                    S.setValue("tipo_ok");
                } else if ("funcion".equals(buscarTipo(id.getValue())) &&
                            buscarParametros(id.getValue()).equals(S1.getValue())) {
                    S.setValue("tipo_ok");
                } else if (buscarTipo(id.getValue()) == null &&
                            "entero".equals(S1.getValue())) {
                    S.setValue("tipo_ok");
                    // tablaSimbolosGlobal.addSimbolo() // ! si no se ha declarado se añade a  TSG, creo que vamos a tener que cambiar como se añade desde el lexico.
                    // actualizarVariableTS();
                    // incrementarDesplazamiento();
                } else {
                    S.setValue("tipo_error");
                }
            break;
            case 29:
                // S -> write E
                S = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE);
                if ("cadena".equals(E.getValue()) || "entero".equals(E.getValue()) || "real".equals(E.getValue())) {
                    S.setValue("tipo_ok");
                } else {
                    S.setValue("tipo_error");
                }
            break;
            case 30:
                // S -> read id
                S = pilaAux.get(TOPE - 2);
                id = pilaAux.get(TOPE);
                if ("cadena".equals(buscarTipo(id.getValue())) ||
                    "entero".equals(buscarTipo(id.getValue())) ||
                    "real".equals(buscarTipo(id.getValue()))) {
                    S.setValue("tipo_ok");
                } else {
                    S.setValue("tipo_error");
                }
            break;
            case 31:
                // S -> /= E
                S = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE);
                if ("entero".equals(E.getValue()) || "real".equals(E.getValue())) {
                    S.setValue("tipo_ok");
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
                    actualizarFuncionTS(compilador.getTablaGlobal(),id.getValue(),A.getValue(),H.getValue(),compilador.getEtiquetaActual());
                } else if ("void".equals(H.getValue()) &&
                            ("vacio".equals(C.getValue()) || "tipo_ok".equals(C.getValue()))) {
                    F.setValue("tipo_ok");
                    actualizarFuncionTS(compilador.getTablaGlobal(),id.getValue(),A.getValue(),H.getValue(),compilador.getEtiquetaActual());
                } else {
                    F.setValue("tipo_error");
                }
            break;
            case 33:
                // A -> T id K
                A = pilaAux.get(TOPE - 3);
                T = pilaAux.get(TOPE - 2);
                K = pilaAux.get(TOPE);
                if ("vacio".equals(K.getValue())) {
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
                if ("vacio".equals(Ka.getValue())) {
                    K.setValue(T.getValue());
                } else {
                    K.setValue(T.getValue() + "," + Ka.getValue());
                }
            break;
            case 35:
                // C -> B Ca
                C = pilaAux.get(TOPE - 2);
                B = pilaAux.get(TOPE - 1);
                Ca = pilaAux.get(TOPE);
                if ("tipo_error".equals(B.getValue()) || "tipo_error".equals(Ca.getValue())) {
                    C.setValue("tipo_error");
                } else if ("ret_logico".equals(B.getValue()) || "ret_entero".equals(B.getValue()) ||
                            "ret_real".equals(B.getValue()) || "ret_cadena".equals(B.getValue())) {
                    C.setValue(B.getValue());
                } else if ("vacio".equals(Ca.getValue())) {
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
                if ("vacio".equals(E1.getValue())) {
                    E.setValue(R.getValue());
                } else if ("logico".equals(R.getValue()) && R.getValue().equals(E1.getValue())) {
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
                if ("vacio".equals(E1a.getValue())) {
                    E1.setValue(R.getValue());
                } else if (("logico".equals(R.getValue()) || "entero".equals(R.getValue()) ||
                            "real".equals(R.getValue())) && R.getValue().equals(E1a.getValue())) {
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
                if ("vacio".equals(R1.getValue())) {
                    R.setValue(U.getValue());
                } else if (("logico".equals(U.getValue()) || "entero".equals(U.getValue()) ||
                            "real".equals(U.getValue())) && U.getValue().equals(R1.getValue())) {
                    R.setValue("logico");
                } else {
                    R.setValue("tipo_error");
                }
            break;
            case 39:
                // R1 -> == U R1
                R1 = pilaAux.get(TOPE - 3);
                U = pilaAux.get(TOPE - 1);
                R1a = pilaAux.get(TOPE);
                if ("vacio".equals(R1a.getValue())) {
                    R1.setValue(U.getValue());
                } else if (("logico".equals(U.getValue()) || "entero".equals(U.getValue()) ||
                            "real".equals(U.getValue())) && U.getValue().equals(R1a.getValue())) {
                    R1.setValue(U.getValue());
                } else {
                    R1.setValue("tipo_error");
                }
            break;
            case 40:
                // U -> V U1
                U = pilaAux.get(TOPE - 2);
                V = pilaAux.get(TOPE - 1);
                U1 = pilaAux.get(TOPE);
                if ("vacio".equals(U1.getValue())) {
                    U.setValue(V.getValue());
                } else if (("entero".equals(V.getValue()) || "real".equals(V.getValue()))
                            && V.getValue().equals(U1.getValue())) {
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
                } else if (("entero".equals(V.getValue()) || "real".equals(V.getValue()))
                            && V.getValue().equals(U1a.getValue())) {
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
                if ("vacio".equals(V1.getValue())) {
                    V.setValue(buscarTipo(id.getValue()));
                } else if ("funcion".equals(buscarTipo(id.getValue())) &&
                            buscarParametros(id.getValue()).equals(V1.getValue())) {
                    V.setValue(buscarTipoRetorno(id.getValue()));
                } else {
                    V.setValue("tipo_error");
                }
            break;
            case 43:
                // L -> E Q
                L = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE - 1);
                Q = pilaAux.get(TOPE);
                if ("vacio".equals(Q.getValue())) {
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
                if ("vacio".equals(Qa.getValue())) {
                    Q.setValue(E.getValue());
                } else {
                    Q.setValue(E.getValue() + "," + Qa.getValue());
                }
            break;
            case 45:
                // A -> T id ; K -> , T id
                id = pilaAux.get(TOPE);
                T = pilaAux.get(TOPE - 1);
                actualizarVariableTS(obtenerTablaActual(), id.getValue(), T.getValue());
                incrementarDesplazamiento(desplazamiento(T.getValue()));
            break;
            case 46:
                // L -> lambda ; Se utiliza para llamar a funciones sin parametros, por eso debe ser tipo void y no vacio.
                pilaAux.get(TOPE).setValue("void");
            break;
        }
        System.out.println("DEBUG: Despues de accion semantica " + codigoAccion + ", pilaAux: " + pilaAux);
    }

    private void actualizarVariableTS(TablaSimbolos tabla, String pos, String tipo) {
        int desplazamiento = compilador.getEtiquetaActual().equals("GLOBAL") ? desplazamientoGlobal : desplazamientoLocal;
        tabla.actualizarVariable(Integer.parseInt(pos), tipo, desplazamiento);
    }
    private void actualizarFuncionTS(TablaSimbolos tabla, String pos, String tiposP, String tipoR, String etiqueta) {
        tabla.actualizarFuncion(Integer.parseInt(pos), tiposP, tipoR, etiqueta);
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
        if (compilador.getEtiquetaActual().equals("GLOBAL")) {
            desplazamientoGlobal += incremento;
        } else {
            desplazamientoLocal -= incremento;
        }
    }

    private TablaSimbolos obtenerTablaActual() {
        if (compilador.getEtiquetaActual().equals("GLOBAL")) {
            return compilador.getTablaGlobal();
        } else {
            return compilador.getTablaLocal();
        }
    }

    private String buscarTipo(String pos) {
        TablaSimbolos.Simbolo s = null;
        if(!compilador.getEtiquetaActual().equals("GLOBAL"))
            s = obtenerTablaActual().getSimbolo(Integer.parseInt(pos));
        if (s == null)
            s = compilador.getTablaGlobal().getSimbolo(Integer.parseInt(pos));
        return s.getTipo();
    }

    private String buscarParametros(String pos) {
        TablaSimbolos.Simbolo s = null;
        if(!compilador.getEtiquetaActual().equals("GLOBAL"))
            s = obtenerTablaActual().getSimbolo(Integer.parseInt(pos));
        if (s == null)
            s = compilador.getTablaGlobal().getSimbolo(Integer.parseInt(pos));
        return s.getTiposParams();
    }

    private String buscarTipoRetorno(String pos) {
        TablaSimbolos.Simbolo s = null;
        if(!compilador.getEtiquetaActual().equals("GLOBAL"))
            s = obtenerTablaActual().getSimbolo(Integer.parseInt(pos));
        if (s == null)
            s = compilador.getTablaGlobal().getSimbolo(Integer.parseInt(pos));
        return s.getTipoRetorno();
    }
    
    // Añade desde el Sintáctico los elementos a la pila auxiliar
    public void pushToAux(String simbolo, String atributo) {
        pilaAux.add(new SimpleEntry<>(simbolo, atributo));
    }

    // Condición de análisis semántico correcto
    public boolean auxEsP1() {
        System.out.println("DEBUG: auxEsP1 "+ pilaAux.toString());
        return pilaAux.size() == 1 && "P1".equals(pilaAux.get(0).getKey())
                && "tipo_ok".equals(pilaAux.get(0).getValue());
    }
}