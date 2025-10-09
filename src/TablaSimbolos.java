import java.util.LinkedHashMap;

public class TablaSimbolos {

    // Estructura interna de cada símbolo
    public static class Simbolo {
        private final int pos;     // posición en la TS (empieza en 1)
        private final String tipo; // "id", "int", "float", etc. (si lo necesitas más tarde)

        public Simbolo(int pos, String tipo) {
            this.pos = pos;
            this.tipo = tipo;
        }

        public int getPos() {
            return pos;
        }

        public String getTipo() {
            return tipo;
        }
    }

    // Usamos LinkedHashMap para conservar orden de inserción (posiciones deterministas)
    private final LinkedHashMap<String, Simbolo> tabla = new LinkedHashMap<>();
    private int siguientePos = 1;

    // Devuelve la posición del identificador si existe; -1 si no está.
    public int contieneId(String id) {
        Simbolo s = tabla.get(id);
        return (s == null) ? -1 : s.getPos();
    }

    // Inserta (si no existe) y devuelve la posición del identificador.
    public int addSimbolo(String id, String tipo) {
        Simbolo s = tabla.get(id);
        if (s != null) return s.getPos();
        Simbolo nuevo = new Simbolo(siguientePos++, tipo);
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
}
