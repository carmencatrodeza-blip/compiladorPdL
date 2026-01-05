public class GestorErrores {

	private final String ROJO = "\033[31m";
	private final String BLANCO = "\033[0m";
	private String output = "";

	// TODO: intentar mejorar el control de la linea (hay veces que se indica que el error está en la línea siguiente a dónde realmente está).

	// Mensajes de error genéricos
	public void mostrarError(int codigo){
		output += ROJO;
		switch(codigo){
		case 1:
			output += "Error al abrir el fichero.";
			break;
		case 2:
			output += "Error al leer el fichero.";
			break;
		case 3:
			output += "Error al escribir los tokens en tokens.txt.";
			break;
		case 4:
			output += "Error al escribir la tabla de símbolos en tablaSimbolos.txt.";
			break;
		case 5:
			output += "Error al escribir el parse en parse.txt.";
			break;
		case 6:
			output += "Error al cerrar el fichero.";
			break;
		default:
			output += "Error desconocido.";
		}
		output += BLANCO;
		System.err.println(output);
	}

	// Mensajes de error léxico
    public void mostrarError(int codigo, int linea, char caracter, String lexema) {
		output += ROJO + "Error Léxico " + codigo + " [Línea " + linea + "]: ";
		switch (codigo) {
		case 101:
			output += "Caracter " + caracter + " no reconocido.";
			break;
		case 102:
			output += "Constante real sin parte entera.";
			break;
		case 103:
			output += "Constante real sin parte decimal: \"" + lexema + "\".";
			break;
		case 104:
			output += "Sentencia de escape formada sin estar creando una cadena.";
			break;
		case 105:
			output += "Operador & no seguido de otro &.";
			break;
		case 106:
			output += "Constante entera fuera de rango (>32767): \"" + lexema + "\".";
			break;
		case 107:
			output += "Constante real fuera de rango (>117549436.0): \"" + lexema + "\".";
			break;
		case 108:
			output += "Constante cadena demasiado larga (>64 caracteres): " + lexema + ".";
			break;
		default:
			output += "Error desconocido.";
		}
		output += BLANCO;
		System.err.println(output);
	}

	// Mensajes de error sintáctico
	public void mostrarError(int codigo, int linea, String topePila, String lexemaActual, String ultimoLexema, String posibles) {
		output += ROJO + "Error Sintáctico " + codigo + " [Línea " + linea + "]: tras '" + ultimoLexema + "' ";
		switch(codigo){
		case 201:
			output += "se esperaba '" + topePila + "' pero se encontró '" + lexemaActual + "'.";
			break;
		case 202:
			output += "se esperaba uno de los siguientes: " + posibles + ", pero se encontró '" + lexemaActual + "'.";
			break;
		default:
			output += "Error desconocido.";
		}
		output += BLANCO;
		System.err.println(output);
	}

	// Mensajes de error semántico
	public void mostrarError(int codigo, int linea, String lexema) {
		output += ROJO + "Error Semántico " + codigo + " [Línea " + linea + "]: ";
		switch (codigo) {
		case 301:
			output += "Los parámetros usados en la función '" + lexema + "' no coinciden con los de su declaración.";
		break;
		case 302:
			output += "Operación aritmética '/' realizada con tipos de datos diferentes. Ambos datos utilizados en la operación deben ser enteros o reales.";
		break;
		case 303:
			output += "Operación relacional '==' realizada con tipos de datos diferentes. Ambos datos utilizados en la operación deben ser enteros, reales o lógicos.";
		break;
		case 304:
			output += "Operación lógica '&&' realizada con tipos de datos diferentes. Ambos datos utilizados en la operación deben ser lógicos.";
		break;
		case 305:
			output += "El tipo de la sentencia 'return' de la función '" + lexema + "' no coincide con el de su cabecera.";
		break;
		case 306:
			output += "Asignación '/=' realizada con una variable de tipo distinto a entero o real.";
		break;
		case 307:
			output += "Sentencia 'read' con variable de tipo distinto de entero, real o cadena.";
		break;
		case 308:
			output += "Sentencia 'write' con variable de tipo distinto de entero, real o cadena.";
		break;
		case 309:
			output += "El tipo de '" + lexema + "' no coincide con el de su valor asignado.";
		break;
		case 310:
			output += "La variable '" + lexema + "' se ha declarado anteriormente.";
		break;
		case 311:
			output += "El tipo de la expresión evaluada en la estructura 'if' debe ser lógico.";
		break;
		case 312:
			output += "El tipo de la expresión evaluada en la estructura 'while' debe ser lógico.";
		break;
		case 313:
			output += "Sentencia 'return' fuera de estructura de función.";
		break;
		default:
			output += "Error desconocido.";
		}
		output += BLANCO;
		System.err.println(output);
	}
}