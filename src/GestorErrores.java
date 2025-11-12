public class GestorErrores {
    
    private final static GestorErrores instancia = new GestorErrores();
    
    private GestorErrores() {
    }
    
    public static GestorErrores obtenerInstancia() {
        return instancia;
    }

	public void mostrarError(int codigo){
		switch(codigo){
		case 1:
			System.err.println("Error al abrir el fichero.");
			break;
		case 2:
			System.err.println("Error al leer el fichero.");
			break;
		case 3:
			System.err.println("Error al escribir los tokens en tokens.txt.");
			break;
		case 4:
			System.err.println("Error al escribir la tabla de símbolos en tablaSimbolos.txt.");
			break;
		case 5:
			System.err.println("Error al escribir el parse en parse.txt.");
			break;
		case 6:
			System.err.println("Error al cerrar el fichero.");
			break;
		}
	}

    public void mostrarError(int codigo, int linea, char caracter, String lexema) {
		switch (codigo) {
		case 101:
			System.err.println("Error Léxico [Línea " + linea + "]: Caracter " + caracter + " no reconocido.");
			break;
		case 102:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante real sin parte entera.");
			break;
		case 103:
			System.err.println("Error Léxico [Línea " + linea + "]: Constante real sin parte decimal: \"" + lexema + "\".");
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
								"]: Constante cadena demasiado larga (>64 caracteres): " + lexema + ".");
			break;
		}
	}

	public void mostrarError(int codigo, int linea, String topePila, String tokenActual){
		switch(codigo){
		case 201:
			System.err.println("Error sintáctico [Línea " + linea + "]: Se esperaba '" + topePila + "' pero se encontró '" + tokenActual + "'");
			break;
		case 202:
			System.err.println("Error sintáctico [Línea " + linea + "]: No hay regla para el no terminal '" + topePila + "' con el token actual '" + tokenActual + "'");
			break;
		case 203:
			System.err.println("Error sintáctico: Entrada no consumida después del análisis."); // ? Mejorar mensaje
			break;
		}
	}
}