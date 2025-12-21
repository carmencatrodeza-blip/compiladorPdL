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
        tablaSimbolosGlobal = null;
        tablaSimbolosLocal = null;
        etiquetaTablaActual = "";
        zonaDeclaracion = false;
        desplazamientoGlobal = 0;
        desplazamientoLocal = 0;
    }

    public static AnalizadorSemantico obtenerInstancia() {
        return instancia;
    }

    public TablaSimbolos getTablaSimbolosGlobal() { return tablaSimbolosGlobal; }
    public TablaSimbolos getTablaSimbolosLocal() { return tablaSimbolosLocal; }
    public boolean getZonaDeclaracion() { return zonaDeclaracion; }
    public void setZonaDeclaracion(boolean zona) { zonaDeclaracion = zona; }
    public String getEtiquetaTablaActual() { return etiquetaTablaActual; }
    public void setEtiquetaTablaActual(String etiqueta) { etiquetaTablaActual = etiqueta; }
    public int getDesplazamientoGlobal() { return desplazamientoGlobal; }
    public void incrementarDesplazamientoGlobal(int incremento) { desplazamientoGlobal += incremento; }
    public int getDesplazamientoLocal() { return desplazamientoLocal; }
    public void incrementarDesplazamientoLocal(int incremento) { desplazamientoLocal += incremento; }

    public void accionSemantica(int codigoAccion) {
        int TOPE = pilaAux.size() - 1;
        SimpleEntry<String, String> P1 = null;
        SimpleEntry<String, String> P = null;
        SimpleEntry<String, String> Pa = null;
        SimpleEntry<String, String> B = null;
        SimpleEntry<String, String> F = null;
        SimpleEntry<String, String> S = null;
        SimpleEntry<String, String> S1 = null;
        SimpleEntry<String, String> H = null;
        SimpleEntry<String, String> T = null;
        SimpleEntry<String, String> A = null;
        SimpleEntry<String, String> K = null;
        SimpleEntry<String, String> Ka = null;
        SimpleEntry<String, String> C = null;
        SimpleEntry<String, String> Ca = null;
        SimpleEntry<String, String> E = null;
        SimpleEntry<String, String> E1 = null;
        SimpleEntry<String, String> E1a = null;
        SimpleEntry<String, String> R = null;
        SimpleEntry<String, String> R1 = null;
        SimpleEntry<String, String> R1a = null;
        SimpleEntry<String, String> U = null;
        SimpleEntry<String, String> U1 = null;
        SimpleEntry<String, String> U1a = null;
        SimpleEntry<String, String> V = null;
        SimpleEntry<String, String> V1 = null;
        SimpleEntry<String, String> X = null;
        SimpleEntry<String, String> L = null;
        SimpleEntry<String, String> Q = null;

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
                // aniadirATS(AUX[tope].pos, AUX[tope-1].despl, AUX[tope-1].tipo); AUX[tope-3].tipo:=tipo_ok; accDespl()
            break;
            case 28:
            break;
            case 29:
            break;
            case 30:
            break;
            case 31:
            break;
            case 32:
            break;
            case 33:
            break;
            case 34:
            break;
            case 35:
            break;
            case 36:
            break;
            case 37:
            break;
            case 38:
            break;
            case 39:
            break;
            case 40:
            break;
            case 41:
            break;
            case 42:
            break;
            case 43:
            break;
            case 44:
            break;
        }
    }

    private void actualizarVariableTS(TablaSimbolos tabla, Integer pos, String tipo, int desplazamiento) {
        // TODO
    }
    private void actualizarFuncionTS(){
        //TODO
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
    
}