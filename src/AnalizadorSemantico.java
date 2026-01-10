import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

public class AnalizadorSemantico {
    private final Compilador compilador;
    // Tanto la posicion de los ids como el tipo leído se guarda como String para poder usar una pila de parejas de Strings.
    ArrayList<SimpleEntry<String, String>> pilaAux;
    int desplazamientoLocal;

    public AnalizadorSemantico(Compilador compilador) {
        this.compilador = compilador;
        pilaAux = new ArrayList<>();
    }

    public boolean accionSemantica(int codigoAccion, String lexemaLeido) {

        int TOPE = pilaAux.size() - 1; // Índice del tope de la pila
        // Declaración de variables auxiliares para las reglas
        SimpleEntry<String, String> P1, P, Pa, B, F, S, S1, H, T, A, K, Ka, C, Ca, E, E1,
                                    E1a, R, R1, R1a, U, U1, U1a, V, V1, X,  L, Q, Qa, id;
        String atrP, atrPa, atrB, atrF, atrS, atrS1, atrH, atrT, atrA, atrK, atrKa,
                atrC, atrCa, atrE, atrE1, atrE1a, atrR, atrR1, atrR1a, atrU, atrU1,
                atrU1a, atrV, atrV1, atrX, atrQ, atrQa, atrId, lexemaId;

        switch (codigoAccion) {
            case 1:
                pilaAux.remove(TOPE);
                return true;
            case 2:
                pilaAux.subList(TOPE - 1, TOPE + 1).clear();
                return true;
            case 3:
                pilaAux.subList(TOPE - 2, TOPE + 1).clear();
                return true;
            case 4:
                pilaAux.subList(TOPE - 3, TOPE + 1).clear();
                return true;
            case 5:
                pilaAux.subList(TOPE - 4, TOPE + 1).clear();
                return true;
            case 6:
                compilador.setZonaDeclaracion(false);
                return true;
            case 7:
                pilaAux.subList(TOPE - 6, TOPE + 1).clear();
                return true;
            case 8:
                compilador.setZonaDeclaracion(true);
                return true;
            case 9:
                pilaAux.subList(TOPE - 8, TOPE + 1).clear();
                return true;
            case 10:
                // L -> lambda ; X -> lambda ; Se utiliza para llamar a funciones sin parametros, por eso debe ser tipo void y no vacio.
                pilaAux.get(TOPE).setValue("void");
                return true;
            case 11:
                compilador.setTablaLocal(new TablaSimbolos(compilador.getIdTablaSig()));
                desplazamientoLocal = 0;
                String etiqueta = "FUNCION-" + compilador.getTablaGlobal().getId(Integer.parseInt(pilaAux.get(TOPE).getValue()));
                compilador.setEtiquetaActual(etiqueta);
                compilador.getTablaLocal().setEtiqueta(etiqueta);
                compilador.setDentroDeFuncion(true);
                return true;
            case 12:
                // Solo se verifica que el tipo de retorno sea correcto. Los datos de la función se guardan en la TS en la acción 32.
                // F -> function H id ( A ) { C
                F = pilaAux.get(TOPE - 8);
                H = pilaAux.get(TOPE - 6);
                atrH = H.getValue();
                id = pilaAux.get(TOPE - 5);
                atrId = id.getValue();
                C = pilaAux.get(TOPE);
                atrC = C.getValue();
                lexemaId = compilador.getTablaGlobal().getId(Integer.parseInt(atrId));

                compilador.getWriter().write(compilador.getTablaLocal().toString(), "tabla");
                compilador.setTablaLocal(null);
                compilador.setEtiquetaActual("GLOBAL");
                compilador.setDentroDeFuncion(false);
                if (atrC.equals("ret_" + atrH)) {
                    F.setValue("tipo_ok");
                    return true;
                } else if ("void".equals(atrH) &&
                            ("vacio".equals(atrC) || "tipo_ok".equals(atrC))) {
                    F.setValue("tipo_ok");
                    return true;
                } else {
                    F.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(305, compilador.getLinea(), lexemaId);
                    return false;
                }
            case 13:
                // P1 -> P
                P1 = pilaAux.get(TOPE - 1);
                P = pilaAux.get(TOPE);   
                atrP = P.getValue();

                compilador.getWriter().writeTablaGlobal(compilador.getTablaGlobal().toString());
                compilador.setTablaGlobal(null);
                compilador.setEtiquetaActual(null);
                if("tipo_ok".equals(atrP)){
                    P1.setValue("tipo_ok");
                    return true;
                } else {
                    P1.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(300, compilador.getLinea(), null);
                    return false;
                }
            case 14:
                // P -> B P
                P = pilaAux.get(TOPE - 2);
                B = pilaAux.get(TOPE - 1);
                atrB = B.getValue();
                Pa = pilaAux.get(TOPE);
                atrPa = Pa.getValue();

                if ("ret_logico".equals(atrB) || "ret_entero".equals(atrB) ||
                    "ret_real".equals(atrB) || "ret_cadena".equals(atrB)) {
                    P.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(313, compilador.getLinea(), null);
                    return false;
                } else if ("vacio".equals(atrPa)) {
                    P.setValue(atrB);
                    return true;
                } else if ("tipo_ok".equals(atrB) &&
                        atrB.equals(atrPa)) {
                    P.setValue("tipo_ok");
                    return true;
                } else {
                    P.setValue("tipo_error");
                    return false;
                }
            case 15:
                // P -> F P
                P = pilaAux.get(TOPE - 2);
                F = pilaAux.get(TOPE - 1);
                atrF = F.getValue();
                Pa = pilaAux.get(TOPE);
                atrPa = Pa.getValue();

                if ("vacio".equals(atrPa)) {
                    P.setValue(atrF);
                    return true;
                } else if ("tipo_ok".equals(atrF) &&
                        atrF.equals(atrPa)) {
                    P.setValue("tipo_ok");
                    return true;
                } else {
                    P.setValue("tipo_error");
                    return false;
                }
            case 16:
                pilaAux.get(TOPE).setValue("vacio");
                return true;
            case 17:
                pilaAux.get(TOPE - 1).setValue(pilaAux.get(TOPE).getValue());
                return true;
            case 18:
                pilaAux.get(TOPE - 2).setValue(pilaAux.get(TOPE).getValue());
                return true;
            case 19:
                // S -> return X
                S = pilaAux.get(TOPE - 2);
                X = pilaAux.get(TOPE);
                atrX = X.getValue();
                if (compilador.getDentroDeFuncion()){
                    S.setValue("ret_" + atrX);
                    return true;
                } else {
                    S.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(313, compilador.getLinea(), null);
                }
                
            case 20:
                pilaAux.get(TOPE - 1).setValue("logico");
                return true;
            case 21:
                pilaAux.get(TOPE - 1).setValue("entero");
                return true;
            case 22:
                pilaAux.get(TOPE - 1).setValue("real");
                return true;
            case 23:
                pilaAux.get(TOPE - 1).setValue("cadena");
                return true;
            case 24:
                pilaAux.get(TOPE - 1).setValue("void");
                return true;
            case 25:
                // B -> while ( E
                B = pilaAux.get(TOPE - 3);
                E = pilaAux.get(TOPE);
                atrE = E.getValue();

                // En esta acción solo se verifica que el tipo de E sea lógico.
                // Se asigna el tipo de B en la acción 46 una vez se ha comprobado el cuerpo del while.
                if ("logico".equals(atrE)) {
                    return true;
                } else {
                    B.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(312, compilador.getLinea(), null);
                    return false;
                }
            case 26:
                // B -> if ( E
                B = pilaAux.get(TOPE - 3);
                E = pilaAux.get(TOPE);
                atrE = E.getValue();

                if ("logico".equals(atrE)) {
                    B.setValue("tipo_ok");
                    return true;
                } else {
                    B.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(311, compilador.getLinea(), null);
                    return false;
                }
            case 27:
                // B -> let T id
                B = pilaAux.get(TOPE - 3);
                T = pilaAux.get(TOPE - 1);
                atrT = T.getValue();
                id = pilaAux.get(TOPE);
                atrId = id.getValue();
                lexemaId = compilador.getTablaGlobal().getId(Integer.parseInt(atrId));

                if (buscarTipo(atrId) == null){
                    actualizarVariableTS(obtenerTablaActual(), atrId, atrT);
                    incrementarDesplazamiento(desplazamiento(atrT));
                    B.setValue("tipo_ok");
                    return true;
                } else {
                    B.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(310, compilador.getLinea(), lexemaId);
                    return false;
                }
            case 28:
                // S -> id S1
                S = pilaAux.get(TOPE - 2);
                id = pilaAux.get(TOPE - 1);
                atrId = id.getValue();
                S1 = pilaAux.get(TOPE);
                atrS1 = S1.getValue();
                lexemaId = compilador.getTablaGlobal().getId(Integer.parseInt(atrId));

                if (!"funcion".equals(buscarTipo(atrId)) && !atrS1.equals(buscarTipo(atrId))) {
                    S.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(309, compilador.getLinea(), lexemaId);
                    return false;
                } else if ("funcion".equals(buscarTipo(atrId)) &&
                            !buscarParametros(atrId).equals(atrS1)) {
                    S.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(301, compilador.getLinea(), lexemaId);
                    return false;
                } else {
                    S.setValue("tipo_ok");
                    return true;
                }
            case 29:
                // S -> write E
                S = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE);
                atrE = E.getValue();

                if ("cadena".equals(atrE) ||
                    "entero".equals(atrE) ||
                    "real".equals(atrE)) {
                    S.setValue("tipo_ok");
                    return true;
                } else {
                    S.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(308, compilador.getLinea(), null);
                    return false;
                }
            case 30:
                // S -> read id
                S = pilaAux.get(TOPE - 2);
                id = pilaAux.get(TOPE);
                atrId = id.getValue();
                lexemaId = compilador.getTablaGlobal().getId(Integer.parseInt(atrId));

                if ("cadena".equals(buscarTipo(atrId)) ||
                    "entero".equals(buscarTipo(atrId)) ||
                    "real".equals(buscarTipo(atrId))) {
                    S.setValue("tipo_ok");
                    return true;
                } else {
                    S.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(307, compilador.getLinea(), lexemaId);
                    return false;
                }
            case 31:
                // S1 -> /= E
                S1 = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE);
                atrE = E.getValue();

                if ("entero".equals(atrE) || "real".equals(atrE)) {
                    S1.setValue(atrE);
                    return true;
                } else {
                    S1.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(306, compilador.getLinea(), null);
                    return false;
                }
            case 32:
                // Se guardan los datos al acabar de leer la cabecera para poder llamar recursivamente.
                // F -> function H id ( A 
                H = pilaAux.get(TOPE - 3);
                atrH = H.getValue();
                id = pilaAux.get(TOPE - 2);
                atrId = id.getValue();
                A = pilaAux.get(TOPE);
                atrA = A.getValue();
                actualizarFuncionTS(atrId, atrA, atrH, compilador.getEtiquetaActual());
                return true;
            case 33:
                // A -> T id K
                A = pilaAux.get(TOPE - 3);
                T = pilaAux.get(TOPE - 2);
                atrT = T.getValue();
                K = pilaAux.get(TOPE);
                atrK = K.getValue();

                if ("vacio".equals(atrK)) {
                    A.setValue(atrT);
                } else {
                    A.setValue(atrT + "," + atrK);
                }
                return true;
            case 34:
                // K -> , T id K
                K = pilaAux.get(TOPE - 4);
                T = pilaAux.get(TOPE - 2);
                atrT = T.getValue();
                Ka = pilaAux.get(TOPE);
                atrKa = Ka.getValue();

                if ("vacio".equals(atrKa)) {
                    K.setValue(atrT);
                } else {
                    K.setValue(atrT + "," + atrKa);
                }
                return true;
            case 35:
                // C -> B Ca
                C = pilaAux.get(TOPE - 2);
                B = pilaAux.get(TOPE - 1);
                atrB = B.getValue();
                Ca = pilaAux.get(TOPE);
                atrCa = Ca.getValue();

                if ("tipo_error".equals(atrB) || "tipo_error".equals(atrCa)) {
                    C.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(300, compilador.getLinea(), null);
                    return false;
                } else if ("ret_logico".equals(atrB) || "ret_entero".equals(atrB) ||
                            "ret_real".equals(atrB) || "ret_cadena".equals(atrB)) {
                    C.setValue(atrB);
                } else if ("vacio".equals(atrCa)) {
                    C.setValue(atrB);
                } else {
                    C.setValue(atrCa);
                }
                return true;
            case 36:
                // E -> R E1
                E = pilaAux.get(TOPE - 2);
                R = pilaAux.get(TOPE - 1);
                atrR = R.getValue();
                E1 = pilaAux.get(TOPE);
                atrE1 = E1.getValue();

                if ("vacio".equals(atrE1)) {
                    E.setValue(atrR);
                    return true;
                }
                if ("logico".equals(atrR) && atrR.equals(atrE1)) {
                    E.setValue("logico");
                    return true;
                } else {
                    E.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(304, compilador.getLinea(), null);
                    return false;
                }
            case 37:
                // E1 -> && R E1
                E1 = pilaAux.get(TOPE - 3);
                R = pilaAux.get(TOPE - 1);
                atrR = R.getValue();
                E1a = pilaAux.get(TOPE);
                atrE1a = E1a.getValue();

                if ("vacio".equals(atrE1a)) {
                    E1.setValue(atrR);
                    return true;
                }
                if ("logico".equals(atrR) && atrR.equals(atrE1a)) {
                    E1.setValue("logico");
                    return true;
                } else {
                    E1.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(304, compilador.getLinea(), null);
                    return false;
                }
            case 38:
                // R -> U R1
                R = pilaAux.get(TOPE - 2);
                U = pilaAux.get(TOPE - 1);
                atrU = U.getValue();
                R1 = pilaAux.get(TOPE);
                atrR1 = R1.getValue();

                if ("vacio".equals(atrR1)) {
                    R.setValue(atrU);
                    return true;
                }
                if (("logico".equals(atrU) || "entero".equals(atrU) ||
                            "real".equals(atrU)) && atrU.equals(atrR1)) {
                    R.setValue("logico");
                    return true;
                } else {
                    R.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(303, compilador.getLinea(), null);
                    return false;
                }
            case 39:
                // R1 -> == U R1
                R1 = pilaAux.get(TOPE - 3);
                U = pilaAux.get(TOPE - 1);
                atrU = U.getValue();
                R1a = pilaAux.get(TOPE);
                atrR1a = R1a.getValue();

                if ("vacio".equals(atrR1a)) {
                    R1.setValue(atrU);
                    return true;
                }
                if (("logico".equals(atrU) || "entero".equals(atrU) ||
                            "real".equals(atrU)) && atrU.equals(atrR1a)) {
                    R1.setValue(atrU);
                    return true;
                } else {
                    R1.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(303, compilador.getLinea(), null);
                    return false;
                }
            case 40:
                // U -> V U1
                U = pilaAux.get(TOPE - 2);
                V = pilaAux.get(TOPE - 1);
                atrV = V.getValue();
                U1 = pilaAux.get(TOPE);
                atrU1 = U1.getValue();

                if ("vacio".equals(atrU1)) {
                    U.setValue(atrV);
                    return true;
                }
                if (("entero".equals(atrV) || "real".equals(atrV))
                            && atrV.equals(atrU1)) {
                    U.setValue(atrV);
                    return true;
                } else {
                    U.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(302, compilador.getLinea(), null);
                    return false;
                }
            case 41:
                // U1 -> / V U1
                U1 = pilaAux.get(TOPE - 3);
                V = pilaAux.get(TOPE - 1);
                atrV = V.getValue();
                U1a = pilaAux.get(TOPE);
                atrU1a= U1a.getValue();

                if (atrU1a.equals("vacio")) {
                    U1.setValue(atrV);
                    return true;
                } else if (("entero".equals(atrV) || "real".equals(atrV))
                            && atrV.equals(atrU1a)) {
                    U1.setValue(atrV);
                    return true;
                } else {
                    U1.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(302, compilador.getLinea(), null);
                    return false;
                }
            case 42:
                // V -> id V1
                V = pilaAux.get(TOPE - 2);
                id = pilaAux.get(TOPE - 1);
                atrId = id.getValue();
                V1 = pilaAux.get(TOPE);
                atrV1 = V1.getValue();
                lexemaId = compilador.getTablaGlobal().getId(Integer.parseInt(atrId));

                if ("vacio".equals(atrV1)) {
                    V.setValue(buscarTipo(atrId));
                    return true;
                }
                if ("funcion".equals(buscarTipo(atrId)) &&
                            buscarParametros(atrId).equals(atrV1)) {
                    V.setValue(buscarTipoRetorno(atrId));
                    return true;
                } else {
                    V.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(301, compilador.getLinea(), lexemaId);
                    return false;
                }
            case 43:
                // L -> E Q
                L = pilaAux.get(TOPE - 2);
                E = pilaAux.get(TOPE - 1);
                atrE = E.getValue();
                Q = pilaAux.get(TOPE);
                atrQ = Q.getValue();

                if ("vacio".equals(atrQ)) {
                    L.setValue(atrE);
                } else {
                    L.setValue(atrE + "," + atrQ);
                }
                return true;
            case 44:
                // Q -> , E Q
                Q = pilaAux.get(TOPE - 3);
                E = pilaAux.get(TOPE - 1);
                atrE = E.getValue();
                Qa = pilaAux.get(TOPE);
                atrQa = Qa.getValue();

                if ("vacio".equals(atrQa)) {
                    Q.setValue(atrE);
                } else {
                    Q.setValue(atrE + "," + atrQa);
                }
                return true;
            case 45:
                // A -> T id ; K -> , T id
                id = pilaAux.get(TOPE);
                atrId = id.getValue();
                T = pilaAux.get(TOPE - 1);
                atrT = T.getValue();

                actualizarVariableTS(obtenerTablaActual(), atrId, atrT);
                incrementarDesplazamiento(desplazamiento(atrT));
                return true;
            case 46:
                // B -> while ( E ) { C
                B = pilaAux.get(TOPE - 6);
                C = pilaAux.get(TOPE);
                atrC = C.getValue();

                // Se asume que la condición ya ha sido verificada en la acción 25
                if (!"tipo_error".equals(atrC)) {
                    B.setValue(atrC);
                    return true;
                } else {
                    B.setValue("tipo_error");
                    compilador.lanzarError();
                    compilador.getGestorErrores().mostrarError(300, compilador.getLinea(), null);
                    return false;
                }
            default:
                return false;
        }
    }

    private void actualizarVariableTS(TablaSimbolos tabla, String pos, String tipo) {
        int desplazamiento = compilador.getEtiquetaActual().equals("GLOBAL") ?
                            compilador.getDesplazamientoGlobal() : desplazamientoLocal;
        tabla.actualizarVariable(Integer.parseInt(pos), tipo, desplazamiento);
    }
    private void actualizarFuncionTS(String pos, String tiposP, String tipoR, String etiqueta) {
        compilador.getTablaGlobal().actualizarFuncion(Integer.parseInt(pos), tiposP, tipoR, etiqueta);
    }
    private int desplazamiento(String tipo) {
        switch (tipo) {
            case "logico":
                return 1;
            case "entero":
                return 1;
            case "real":
                return 2;
            case "cadena":
                return 32;
            default:
                return 0;
        }
    }

    private void incrementarDesplazamiento(int incremento) {
        if (compilador.getEtiquetaActual().equals("GLOBAL")) {
            compilador.incrementarDesplazamientoGlobal(incremento);
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
        return s != null ? s.getTipo() : null;
    }

    private String buscarParametros(String pos) {
        return compilador.getTablaGlobal().getSimbolo(Integer.parseInt(pos)).getTiposParams();
    }

    private String buscarTipoRetorno(String pos) {
        return compilador.getTablaGlobal().getSimbolo(Integer.parseInt(pos)).getTipoRetorno();
    }
    
    // Añade desde el Sintáctico los elementos a la pila auxiliar
    public void pushToAux(String simbolo, String atributo) {
        pilaAux.add(new SimpleEntry<>(simbolo, atributo));
    }

    // Condición de análisis semántico correcto
    public boolean pilaAuxCorrecta() {
        return pilaAux.size() == 1 && "P1".equals(pilaAux.get(0).getKey())
                && "tipo_ok".equals(pilaAux.get(0).getValue());
    }
}