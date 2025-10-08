public class GestorErrores {

    public GestorErrores() {
    }

    public void mostrarError(int codigo, int linea, char caracter) {
		switch (codigo) {
		case 101:
			System.err.println("Error Léxico [Línea " + linea + "]: Caracter " + caracter + " no reconocido.");
			break;
		case 102:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante real sin parte entera.");
			break;
		case 103:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante real sin parte decimal.");
			break;
		case 104:
			System.err.println("Error Léxico [Línea " + linea + "]: Sentencia de escape formada sin estar creando una cadena.");
			break;
		case 105:
			System.err.println("Error Léxico [Línea " + linea + "]: Operador & no seguido de otro &.");
			break;
		case 106:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante entera fuera de rango.");
			break;
		case 107:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante real fuera de rango.");
			break;
		case 108:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante cadena demasiado larga.");
			break;
		}
	}
}
