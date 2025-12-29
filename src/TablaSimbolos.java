import java.util.LinkedHashMap;
import java.util.Map;

public class TablaSimbolos {

    // Estructura interna de cada símbolo
    public static class Simbolo {
        private final Integer pos;
        private String tipo;
        private Integer desplazamiento; 
        private String tiposParams;
        private int[] modosPasoParams; // 1: por valor, 2: por referencia
        private Integer numParams;
        private String tipoRetorno;
        private String etiqueta;

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

        public int getPos() { return pos; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public Integer getDesplazamiento() { return desplazamiento; }
        public void setDesplazamiento(Integer desplazamiento) { this.desplazamiento = desplazamiento; }
        public Integer getNumParams() { return numParams; }

        public String getTiposParams() { return tiposParams; }
        public int[] getModosPasoParams() { return modosPasoParams; }
        public String getTipoRetorno() { return tipoRetorno; }
        public void setParametros(Integer numParams, String tiposParams, int[] modosPasoParams, String tipoRetorno) {
            this.numParams = numParams;
            this.tiposParams = tiposParams;
            this.modosPasoParams = modosPasoParams;
            this.tipoRetorno = tipoRetorno;
        }
        public String getEtiqueta() { return etiqueta; }
        public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
    }

    // Usamos LinkedHashMap para conservar orden de inserción
    private final LinkedHashMap<String, Simbolo> tabla = new LinkedHashMap<>(); // identificador -> simbolo
    private final int id;
    private int siguientePos = 1; 
    private String etiqueta;

    public TablaSimbolos(int id) {
        this.id = id;
    }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    // Devuelve la posición del identificador si existe; -1 si no está.
    public int contieneId(String id) {
        Simbolo s = tabla.get(id);
        return (s == null) ? -1 : s.getPos();
    }

    // Inserta (si no existe) y devuelve la posición del identificador.
    public int addSimbolo(String id) {
        Simbolo s = tabla.get(id);
        if (s != null)
            return s.getPos();
        Simbolo nuevo = new Simbolo(siguientePos++);
        tabla.put(id, nuevo);
        return nuevo.getPos();
    }

    // Devuelve el id asociado a una posición dada.
    public String getId (int pos){
        for(Map.Entry<String,Simbolo> entry : tabla.entrySet()){
            if(entry.getValue().getPos() == pos){
                return entry.getKey();
            }
        }
        return null;
    }

    // Devuelve el simbolo asociado a una posición dada.
    public Simbolo getSimbolo (int pos){
        for(Map.Entry<String,Simbolo> entry : tabla.entrySet()){
            if(entry.getValue().getPos() == pos){
                return entry.getValue();
            }
        }
        return null;
    }

    public void actualizarVariable(int pos, String tipo, int desplazamiento) {
        Simbolo s = getSimbolo(pos);
        s.setTipo(tipo);
        s.setDesplazamiento(desplazamiento);
    }

    public void actualizarFuncion(int pos, String tiposP, String tipoRetorno, String etiqueta) {
        Simbolo s = getSimbolo(pos);
        int n = tiposP.split(",").length;
        s.setTipo("funcion");
        s.setParametros(n, tiposP, null, tipoRetorno);
        s.setEtiqueta(etiqueta);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("CONTENIDOS DE LA TABLA ").append(etiqueta == null ? "(sin etiqueta)" : etiqueta).append(" #").append(this.id).append(" : \n");

        for(Map.Entry<String,Simbolo> entry : tabla.entrySet()){
            String lexema = entry.getKey();
            imprimirSimbolo(lexema, entry.getValue(), sb);
            sb.append("--------- ---------\n");
        }
        sb.append("---------------------------------------------------");
        return sb.toString();
    }

    private void imprimirSimbolo(String id, Simbolo s, StringBuilder sb) {
        sb.append("* LEXEMA : '").append(id).append("'\n");
        sb.append("  Atributos :\n");
        if (s.getTipo() != null)
            sb.append("  + Tipo : '").append(s.getTipo()).append("'\n");
        if (s.getDesplazamiento() != null)
            sb.append("  + Despl : ").append(Math.abs(s.getDesplazamiento()) - 1).append("\n");
        if (s.getNumParams() != null) {
            sb.append("  + numParam : ").append(s.getNumParams()).append("\n");
            String[] tipos = s.getTiposParams().split(",");
            for (int i = 0; i < s.getNumParams(); i++) {
                sb.append("    ---------\n");
                sb.append("    + TipoParam").append(i + 1).append(" : '").append(tipos[i]).append("'\n");
                sb.append("    + ModoParam").append(i + 1).append(": 'valor'\n");
            }
            sb.append("    ---------\n");
            sb.append("  + TipoRetorno : '").append(s.getTipoRetorno()).append("'\n");
            sb.append("  + EtiqFuncion : '").append(s.getEtiqueta()).append("'\n");
        }
    }
}
