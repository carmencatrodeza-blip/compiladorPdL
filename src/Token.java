
public class Token {
	private String codigo;
	private String atributo;
	private int linea;

	public Token(String codigo, String atributo, int linea) {
		this.codigo = codigo;
		this.atributo = atributo;
		this.linea = linea;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getAtributo() {
		return atributo;
	}
	
	public int getLinea() {
		return linea;
	}
	
	public String toFormatoTS() {
		if(atributo != null && !atributo.isEmpty()) {
			return "<" + codigo + ", " + atributo + ">";
		}
		else {
			return "<" + codigo + ", " + ">";
		}
	}
}
