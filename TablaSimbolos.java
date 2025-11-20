import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;

public class TablaSimbolos {

    // Estructura interna de cada símbolo
    public static class Simbolo {
        // private final int id; // Hará falta para cuando hagamos una TS por función.
        private final Integer pos; // posición en la TS (empieza en 1)
        private final String tipo; // "id", "int", "float", etc. (si lo necesitas más tarde)
        private final Integer desplazamiento; 
        private final String[] tiposParams;
        private final int[] modosPasoParams; // 1: por valor, 2: por referencia
        private final Integer numParams;
        private final String tipoRetorno;
        private final String etiqueta;

        public Simbolo(int pos) { // Constructor para insertar nuevos ids, la info la introduce el semantico
            this.pos = pos;
            this.tipo = null;
            this.desplazamiento = null;
            this.tiposParams = null;
            this.modosPasoParams = null;
            this.numParams = null;
            this.tipoRetorno = null;
            this.etiqueta = null;
        }

        public Simbolo(int pos, String tipo, int desplazamiento){ // Constructor para identificadores de variables
            this.pos = pos;
            this.tipo = tipo;
            this.desplazamiento = desplazamiento;
            this.tiposParams = null;
            this.modosPasoParams = null;
            this.numParams = null;
            this.tipoRetorno = null;
            this.etiqueta = null;
        }

        public Simbolo(int pos, String tipo, String[] tiposParams, int[] modosPasoParams,
                        String tipoRetorno, String etiqueta){ // Constructor para identificadores de funciones
            this.pos = pos;
            this.tipo = tipo;
            this.desplazamiento = null;
            this.tiposParams = Arrays.copyOf(tiposParams, tiposParams.length);
            this.modosPasoParams = Arrays.copyOf(modosPasoParams, modosPasoParams.length);
            this.numParams = tiposParams.length;
            this.tipoRetorno = tipoRetorno;
            this.etiqueta = etiqueta;
        }

        public int getPos() { return pos; }
        public String getTipo() { return tipo; }
        public Integer getDesplazamiento() { return desplazamiento; }
        public Integer getNumParams() { return numParams; }
        public String[] getTiposParams() { return tiposParams; }
        public int[] getModosPasoParams() { return modosPasoParams; }
        public String getTipoRetorno() { return tipoRetorno; }
        public String getEtiqueta() { return etiqueta; }
    }

    // Usamos LinkedHashMap para conservar orden de inserción (posiciones deterministas)
    private final LinkedHashMap<String, Simbolo> tabla = new LinkedHashMap<>(); // identificador -> simbolo
    private int siguientePos = 1; // ? Realmente es util?
    private int sumDesplazamiento = 0; 

    /** Devuelve la posición del identificador si existe; -1 si no está. */
    public int contieneId(String id) {
        Simbolo s = tabla.get(id);
        return (s == null) ? -1 : s.getPos();
    }

    /** Inserta (si no existe) y devuelve la posición del identificador. */
    public int addSimbolo(String id) {
        Simbolo s = tabla.get(id);
        if (s != null) return s.getPos();
        Simbolo nuevo = new Simbolo(siguientePos++);
        tabla.put(id, nuevo);
        return nuevo.getPos();
    }

    // (Opcional) getters si luego quieres inspeccionar la TS
    public int size() { 
        return tabla.size(); 
    }

    public Simbolo get(String id) { 
        return tabla.get(id); 
    }

    private int updateSumDesplazamiento(String tipo) {
        int incremento = 0;
        switch (tipo) {
            case "entero":
                incremento = 4;
                break;
            case "real":
                incremento = 8;
                break;
            case "cadena":
                incremento = 128;
                break;
            case "logico":
                incremento = 1;
                break;
        }
        return sumDesplazamiento + incremento;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("CONTENIDOS DE LA TABLA #0: \n");

        for(Map.Entry<String,Simbolo> entry : tabla.entrySet()){
            String lexema = entry.getKey();
            imprimirSimbolo(lexema, entry.getValue(), sb);
            sb.append("--------- ---------\n");
        }
        sb.append("---------------------------------------------------\n");
        return sb.toString();
    }

    private void imprimirSimbolo(String id, Simbolo s, StringBuilder sb) {
        sb.append("* LEXEMA: '").append(id).append("'\n");
        sb.append("  Atributos:\n");
        if (s.getTipo() != null)
            sb.append("  + tipo: ").append(s.getTipo()).append("\n");
        if (s.getDesplazamiento() != null)
            sb.append("  + despl: ").append(s.getDesplazamiento()).append("\n");
        if (s.getNumParams() != null) {
            sb.append("  + numParam: ").append(s.getNumParams()).append("\n");
            for (int i = 0; i < s.getNumParams(); i++) {
                sb.append("    + tipoParam").append(i + 1).append(": ").append(s.getTiposParams()[i]).append("\n");
                sb.append("    modoParam").append(i + 1).append(": ").append(s.getModosPasoParams()[i]).append("\n");
            }
            sb.append("    + tipoRetorno: ").append(s.getTipoRetorno()).append("\n");
            sb.append("  + etiqueta: ").append(s.getEtiqueta()).append("\n");
        }
    }
}
