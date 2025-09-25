
public class Token {
	private String codigo;
	private String atributo;

	public Token(String codigo, String atributo) {
		this.codigo = codigo;
		this.atributo = atributo;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getAtributo() {
		return atributo;
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

