import java.util.AbstractMap;

public class Token {
    private final String tipo;
    private final Object valor;

    public Token(String tipo, Object valor) {
        this.tipo = (tipo == null) ? "" : tipo;
        this.valor = (valor == null) ? "" : valor;
    }

    public static Token fromEntry(AbstractMap.SimpleEntry<String,Object> entry) {
        if (entry == null) {
            return new Token("ERROR", "");
        }
        return new Token(entry.getKey(), entry.getValue());
    }

    @Override
    public String toString() {
        return "<" + tipo + ", " + valor + ">\n";
    }

    public String getTipo() {
		return tipo; 
	}

    public Object getValor() {
		return valor;
	}
}
