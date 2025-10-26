public class GestorErrores {

    public GestorErrores() {
    }

    public void mostrarError(int codigo, int linea, char caracter, String lexema) {
		switch (codigo) {
		case 101:
			System.err.println("Error Léxico [Línea " + linea + "]: Caracter " + caracter + " no reconocido.");
			break;
		case 102:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante real sin parte entera: \"" + lexema + "\".");
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
			System.err.println("Error Léxico [Línea " + linea +
								"]: Constante entera fuera de rango (>32767): \"" + lexema + "\".");
			break;
		case 107:
			System.err.println("Error Léxico [Línea " + linea +
								"]: Constante real fuera de rango (>117549436.0): \"" + lexema + "\".");
			break;
		case 108:
			System.err.println("Error Léxico [Línea " + linea +
								"]: Constante cadena demasiado larga (>64 caracteres): \"" + lexema + "\".");
			break;
		case 109:
			System.err.println("Error al abrir el fichero.");
			break;
		case 110:
			System.err.println("Error al leer el fichero.");
			break;
		case 111:
			System.err.println("Error al escribir los tokens en tokens.txt.");
			break;
		case 112:
			System.err.println("Error al escribir la tabla de símbolos en tablaSimbolos.txt.");
			break;
		}
	}
}