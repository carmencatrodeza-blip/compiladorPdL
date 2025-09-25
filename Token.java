
public class Token {
	private String codigo;
	private String atributo;
	private int linea;
	private int columna;

	public Token(String codigo, String atributo, int linea, int columna) {
		this.codigo = codigo;
		this.atributo = atributo;
		this.linea = linea;
		this.columna = columna;
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
	
	public int getColumna() {
		return columna;
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
