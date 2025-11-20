public class GestorErrores {
    
    private final static GestorErrores instancia = new GestorErrores();
	private static final String ROJO = "\033[31m";
	private static final String BLANCO = "\033[0m";
	private static String output;
    
    private GestorErrores() {
    }
    
    public static GestorErrores obtenerInstancia() {
        return instancia;
    }

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
		}
		output += BLANCO;
		System.err.println(output);
	}

    public void mostrarError(int codigo, int linea, char caracter, String lexema) {
		output += ROJO;
		switch (codigo) {
		case 101:
			output += "Error Léxico [Línea " + linea + "]: Caracter " + caracter + " no reconocido.";
			break;
		case 102:
			output += "Error Léxico [Línea " + linea + "]: Constante real sin parte entera.";
			break;
		case 103:
			output += "Error Léxico [Línea " + linea + "]: Constante real sin parte decimal: \"" + lexema + "\".";
			break;
		case 104:
			output += "Error Léxico [Línea " + linea + "]: Sentencia de escape formada sin estar creando una cadena.";
			break;
		case 105:
			output += "Error Léxico [Línea " + linea + "]: Operador & no seguido de otro &.";
			break;
		case 106:
			output += "Error Léxico [Línea " + linea +
								"]: Constante entera fuera de rango (>32767): \"" + lexema + "\".";
			break;
		case 107:
			output += "Error Léxico [Línea " + linea +
								"]: Constante real fuera de rango (>117549436.0): \"" + lexema + "\".";
			break;
		case 108:
			output += "Error Léxico [Línea " + linea +
								"]: Constante cadena demasiado larga (>64 caracteres): " + lexema + ".";
			break;
		}
		output += BLANCO;
		System.err.println(output);
	}

	public void mostrarError(int codigo, int linea, String topePila, String tokenActual){
		output += ROJO;
		switch(codigo){
		case 201:
			output += "Error sintáctico [Línea " + linea + "]: Se esperaba '" + topePila + "' pero se encontró '" + tokenActual + "'";
			break;
		case 202:
			output += "Error sintáctico [Línea " + linea + "]: No hay regla para el no terminal '" + topePila + "' con el token actual '" + tokenActual + "'";
			break;
		case 203:
			output += "Error sintáctico: Entrada no consumida después del análisis."; // ? Mejorar mensaje
			break;
		}
		output += BLANCO;
		System.err.println(output);
	}
}