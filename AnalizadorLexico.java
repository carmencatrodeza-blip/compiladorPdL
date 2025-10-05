import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;

// Enum para los caracteres especiales
enum CaracterEspecial {
	ESPACIO(' ', 32),
	TAB('\t', 9),
	SALTO_LINEA('\n', 10),
	GUION_BAJO('_', 95),
	COMILLAS('"', 34),
	BARRA('/', 47),
	BARRA_INV('\\', 92),
	IGUAL('=', 61),
	Y('&', 38),
	PAR_IZQ('(', 40),
	PAR_DER(')', 41),
	LLAVE_IZQ('{', 123),
	LLAVE_DER('}', 125),
	PUNTO_Y_COMA(';', 59),
	COMA(',', 44),
	PUNTO('.', 46);

	public final char caracter;
	public final int ascii;
	CaracterEspecial(char caracter, int ascii) {
		this.caracter = caracter;
		this.ascii = ascii;
	}

	public static CaracterEspecial fromAscii(int ascii) {
		for (CaracterEspecial ce : values()) {
			if (ce.ascii == ascii) return ce;
		}
		return null;
	}
}

public class AnalizadorLexico {

	// TODO: Añadir tabla de símbolos. 
	/*
    ¿HashMap: id -> (líneaTS, tipo)?
    ¿LinkedHashMap: id -> tipo?
    Si solo necesitamos lineaTS para el token, HashMap: id -> tipo y variable lineaTS aparte.
	 */

	private int linea; // Número de línea del documento.
	private int estado; // Estado del autómata.
	private String lexema; // Variable para construir el lexema.
	private int numero; // Variable para calcular números.
	private int dec; // Variable para la parte decimal de números reales.
	private int caracter; // Caracter guardado como byte.
	private FileReader fr; // Lector de archivos.

	public AnalizadorLexico() {
		linea = 1;
		try{
			fr = new FileReader("entrada.txt");
			caracter = fr.read();
		} catch (FileNotFoundException fnf){
			System.err.println("Archivo de entrada no encontrado.");
		} catch (IOException ioe){
			System.err.println("Error al abrir el archivo de entrada.");
		}
	}

	public SimpleEntry<String, Object> sigToken() {
		estado = 0;
		lexema = "";
		numero = 0;
		while (true){
			switch(estado){
			case 0: // Estado inicial
				CaracterEspecial ce = CaracterEspecial.fromAscii(caracter);
				if(ce == CaracterEspecial.ESPACIO || ce == CaracterEspecial.TAB){
					leerCaracter();
				}
				else if(ce == CaracterEspecial.SALTO_LINEA){
					linea++;
					leerCaracter();
				}
				else if(esLetra(caracter)){
					lexema += (char)caracter;
					estado = 1;
					leerCaracter();
				}
				else if(esDigito(caracter)){
					int num = caracter - 48; // Transformar de ASCII a dígito.
					numero = numero * 10 + num; // Construir el número.
					estado = 3;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.GUION_BAJO){
					lexema += (char)caracter;
					estado = 2;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.COMILLAS){
					estado = 6;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.BARRA){
					estado = 8;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.BARRA_INV){
					mostrarError(104, linea, (char)caracter);
					// ? Al mostrar un error, ¿seguimos con normalidad o paramos el programa?
				}
				else if(ce == CaracterEspecial.IGUAL){
					estado = 10;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.Y){
					estado = 11;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.PAR_IZQ){
					estado = 22;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.PAR_DER){
					estado = 23;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.LLAVE_IZQ){
					estado = 24;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.LLAVE_DER){
					estado = 25;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.PUNTO_Y_COMA){
					estado = 26;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.COMA){
					estado = 27;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.PUNTO){
					mostrarError(102, linea, (char)caracter);
					// ? Al mostrar un error, ¿seguimos con normalidad o paramos el programa?
				}
				else if(caracter == -1){
					estado = 99; // Estado de fin de archivo.
				}
				else{
					mostrarError(101, linea, (char)caracter);
					// ? Al mostrar un error, ¿seguimos con normalidad o paramos el programa?
				}
				break;
			case 1:
				ce = CaracterEspecial.fromAscii(caracter);
				if(esLetra(caracter)){
					lexema += (char)caracter;
					leerCaracter();
				}
				else if(esDigito(caracter) || ce == CaracterEspecial.GUION_BAJO){
					lexema += (char)caracter;
					estado = 2;
					leerCaracter();
				}
				else{
					if(esReservada(lexema) != "noEsReservada"){
						return new SimpleEntry<>(lexema, null);
					}
					else{
						int pos = buscarTS(lexema);
						if(pos == -1){
							pos = insertarTS(lexema, "id");
						}
						return new SimpleEntry<>("id", pos);
					}
				}
				break;
			case 2:
				ce = CaracterEspecial.fromAscii(caracter);
				if(esLetra(caracter) || esDigito(caracter) || ce == CaracterEspecial.GUION_BAJO){
					lexema += (char)caracter;
					leerCaracter();
				}
				else{
					int pos = buscarTS(lexema);
					if(pos == -1){
						pos = insertarTS(lexema, "id");
					}
					return new SimpleEntry<>("id", pos);
				}
				break;
			case 3:
				ce = CaracterEspecial.fromAscii(caracter);
				if(esDigito(caracter)){
					construirNumero(caracter);
					leerCaracter();
				}
				else if(ce == CaracterEspecial.PUNTO){
					estado =  4;
					leerCaracter();
				}
				else{
					return new SimpleEntry<>("entero", calcularValor());
				}
				break;
			case 4:
				ce = CaracterEspecial.fromAscii(caracter);
				if(esDigito(caracter)){
					estado = 5;
					construirNumero(caracter);
					leerCaracter();
				}
				else{
					mostrarError(103, linea, (char)caracter);
				}
				break;
			case 5:
				ce = CaracterEspecial.fromAscii(caracter);
				if(esDigito(caracter)){
					construirNumero(caracter);
					leerCaracter();
				}
				else{
					return new SimpleEntry<>("real", calcularValor());
				}
				break;
			case 6:
				if (caracter == '"') {
					leerCaracter();
					return new SimpleEntry<>("cadena", lexema);
				}
				else if (caracter == '\\') { //no se si me he equivocado
					estado = 7;
					leerCaracter();
				}
				else {
					lexema += (char)caracter;
					leerCaracter();
				}
				break;
			case 7:
				lexema += (char)caracter;
				estado = 6;
				leerCaracter();
				break;
			case 8:
				if (caracter == '/') {
					estado = 9;
					leerCaracter();
				}
				else if (caracter == '=') {
					leerCaracter();
					return new SimpleEntry<>("/=", null);
				}
				else {
					return new SimpleEntry<>("/", null);
				}
				break;
			case 9:
				if (caracter == '\n') {
					linea++;
					estado = 0;
					leerCaracter();
				}
				else if (caracter == -1) {
					return new SimpleEntry<>("EOF", null);
				}
				else {
					leerCaracter();
				}
				break;
			case 10: //si pongo break de error
				if (caracter == '=') {
					leerCaracter();
					return new SimpleEntry<>("==", null);
				}
				else {
					return new SimpleEntry<>("=", null);
				}
			case 11:
				if(caracter == '&') {
					leerCaracter();
					return new SimpleEntry<>("&&", null);
				}
				else {
					mostrarError(101, linea, '&');  //no hay token & suelto en la tabla
					estado = 0;
				}
				break;
			case 12:
				return new SimpleEntry<>("id", null);
			case 13:
				return new SimpleEntry<>("entero", null);
			case 14:
				return new SimpleEntry<>("real", null);
			case 15:
				return new SimpleEntry<>("cadena", null);
			case 16:
				return new SimpleEntry<>("/=", null);
			case 17:
				return new SimpleEntry<>("/", null);
			case 18:
				return new SimpleEntry<>("==", null);
			case 19:
				return new SimpleEntry<>("=", null);
			case 20:
				return new SimpleEntry<>("&&", null);
			case 21:
				return new SimpleEntry<>(lexema, null);
			case 22:
				return new SimpleEntry<>("(", null);
				case 23:
					return new SimpleEntry<>(")", null);
				case 24:
					return new SimpleEntry<>("{", null);	
				case 25:
					return new SimpleEntry<>("}", null);
				case 26:
					return new SimpleEntry<>(";", null);
				case 27:
					return new SimpleEntry<>(",", null);
				case 99:
					return new SimpleEntry<>("EOF", null);
			}
		}
	}

	private void leerCaracter() {
		try {
			caracter = fr.read();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void construirNumero(int caracter){
		int num = caracter - 48; // Transformar de ASCII a dígito.
		numero = numero * 10 + num; // Construir el número.
		if(estado > 3) dec++;
	}

	private int calcularValor(){
		return numero * (int)Math.pow(10, -dec);
	}

	private boolean esLetra(int c) {
		return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
	}

	private boolean esDigito(int c) {
		return (c >= 48 && c <= 57);
	}

	private String esReservada(String s) {
		String[] reservadas = {"boolean","float","function","if","int","let","read","return","string","void","while","write"};
		for (String r : reservadas) {
			if (s.equals(r)) return s;
		}
		return "noEsReservada";
	}

	// Busca el identificador s en la tabla de símbolos y devuelve su posición (-1 si no está).
	private int buscarTS(String s){
		return -1; // TODO: Implementar.
	}

	// Inserta el identificador s en la tabla de símbolos con el tipo dado y devuelve su posición.
	private int insertarTS(String s, String tipo){
		return -1;// TODO: Implementar.
	}

	private void mostrarError(int codigo, int linea, char caracter) {
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
		}
	}
