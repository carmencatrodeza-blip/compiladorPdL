import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;

// Enum para los caracteres especiales
enum CaracterEspecial {
	ESPACIO(' ', 32),
	TAB('\t', 9),
	SALTO_LINEA('\n', 10),
	RETORNO_CARRO('\r', 13),
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

	private int linea; // Número de línea del documento.
	private int estado; // Estado del autómata.
	private String lexema; // Variable para construir el lexema.
	private int contador; // Contador de caracteres leídos.
	private int numero; // Variable para calcular números.
	private int dec; // Variable para la parte decimal de números reales.
	private int caracter; // Caracter guardado como byte.
	private CaracterEspecial ce; // Caracter especial actual.
	private FileReader fr; // Lector de archivos.
	private GestorErrores gestor;
	private TablaSimbolos tablaSimbolos;

	public AnalizadorLexico(String nombreFichero) {
		try{
			fr = new FileReader(nombreFichero);
			caracter = fr.read();
			ce = CaracterEspecial.fromAscii(caracter);
		} catch (FileNotFoundException fnf){
			System.err.println("Archivo de entrada no encontrado.");
		} catch (IOException ioe){
			System.err.println("Error al abrir el archivo de entrada.");
		}
		gestor = new GestorErrores();
		linea = 1;
		tablaSimbolos = new TablaSimbolos();
		inicializarTablaSimbolosReservados();
	}

	public SimpleEntry<String, Object> sigToken() {
		estado = 0;
		lexema = "";
		numero = 0;
		while (true){
			switch(estado){
			case 0: // Estado inicial
				if(ce == CaracterEspecial.ESPACIO || ce == CaracterEspecial.TAB || ce == CaracterEspecial.RETORNO_CARRO){
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
					construirNumero(caracter);
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
					lexema += (char)caracter;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.BARRA){
					estado = 8;
					leerCaracter();
				}
				else if(ce == CaracterEspecial.BARRA_INV){
					gestor.mostrarError(104, linea, (char)caracter);
					return null;
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
					leerCaracter();
					return new SimpleEntry<>("parIzq", null);
				}
				else if(ce == CaracterEspecial.PAR_DER){
					leerCaracter();
					return new SimpleEntry<>("parDer", null);
				}
				else if(ce == CaracterEspecial.LLAVE_IZQ){
					leerCaracter();
					return new SimpleEntry<>("llaveIzq", null);
				}
				else if(ce == CaracterEspecial.LLAVE_DER){
					leerCaracter();
					return new SimpleEntry<>("llaveDer", null);
				}
				else if(ce == CaracterEspecial.PUNTO_Y_COMA){
					leerCaracter();
					return new SimpleEntry<>("puntoComa", null);
				}
				else if(ce == CaracterEspecial.COMA){
					leerCaracter();
					return new SimpleEntry<>("coma", null);
				}
				else if(ce == CaracterEspecial.PUNTO){
					gestor.mostrarError(102, linea, (char)caracter);
					return null;
				}
				else if(caracter == -1){
					return new SimpleEntry<>("EOF", null);
				}
				else{
					gestor.mostrarError(101, linea, (char)caracter);
					return null;
				}
				break;
			case 1:
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
						int pos = tablaSimbolos.contieneId(lexema);
						if(pos == -1){
							pos = tablaSimbolos.addSimbolo(lexema, "id");
						}
						return new SimpleEntry<>("id", pos);
					}
				}
				break;
			case 2:
				if(esLetra(caracter) || esDigito(caracter) || ce == CaracterEspecial.GUION_BAJO){
					lexema += (char)caracter;
					leerCaracter();
				}
				else{
					int pos = tablaSimbolos.contieneId(lexema);
					if(pos == -1){
						pos = tablaSimbolos.addSimbolo(lexema, "id");
					}
					return new SimpleEntry<>("id", pos);
				}
				break;
			case 3:
				if(esDigito(caracter)){
					construirNumero(caracter);
					leerCaracter();
				}
				else if(ce == CaracterEspecial.PUNTO){
					estado =  4;
					leerCaracter();
				}
				else{
					if (numero > 32767){
						gestor.mostrarError(106, linea, (char)caracter);
						return null;
					}
					return new SimpleEntry<>("entero", numero);
				}
				break;
			case 4:
				if(esDigito(caracter)){
					estado = 5;
					construirNumero(caracter);
					leerCaracter();
				}
				else{
					gestor.mostrarError(103, linea, (char)caracter);
					return null;
				}
				break;
			case 5:
				if(esDigito(caracter)){
					construirNumero(caracter);
					leerCaracter();
				}
				else{
					double output = calcularValor();
					if (output > 117549436.0f){
						gestor.mostrarError(107, linea, (char)caracter);
						return null;
					}
					return new SimpleEntry<>("real", calcularValor());
				}
				break;
			case 6:
				if (ce == CaracterEspecial.COMILLAS) {
					lexema += (char)caracter;
					leerCaracter();
					if (contador > 64) {
						gestor.mostrarError(108, linea, (char)caracter);
						return null;
					}
					return new SimpleEntry<>("cadena", lexema);
				}
				else if (ce == CaracterEspecial.BARRA_INV) {
					estado = 7;
					contador++;
					lexema += (char)caracter;
					leerCaracter();
				}
				else {
					contador++;
					lexema += (char)caracter;
					leerCaracter();
				}
				break;
			case 7:
				// De momento se asume que el carácter siguiente es válido (n, t, ", \, etc.)
				estado = 6;
				lexema += (char)caracter;
				leerCaracter();
				break;
			case 8:
				if (ce == CaracterEspecial.BARRA) {
					estado = 9;
					leerCaracter();
				}
				else if (ce == CaracterEspecial.IGUAL) {
					leerCaracter();
					return new SimpleEntry<>("asigDiv", null);
				}
				else {
					return new SimpleEntry<>("div", null);
				}
				break;
			case 9:
				if (ce == CaracterEspecial.SALTO_LINEA) {
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
			case 10:
				if (ce == CaracterEspecial.IGUAL) {
					leerCaracter();
					return new SimpleEntry<>("igual", null);
				}
				else {
					return new SimpleEntry<>("asig", null);
				}
			case 11:
				if(ce == CaracterEspecial.Y) {
					leerCaracter();
					return new SimpleEntry<>("y", null);
				}
				else {
					gestor.mostrarError(105, linea, (char)caracter);
					return null;
				}
			}
		}
	}

	private void leerCaracter() {
		try {
			caracter = fr.read();
			ce = CaracterEspecial.fromAscii(caracter);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void construirNumero(int caracter){
		int num = caracter - 48; // Transformar de ASCII a dígito.
		numero = numero * 10 + num; // Construir el número.
		if(estado > 3) dec++;
	}

	private double calcularValor(){
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
	
	public void inicializarTablaSimbolosReservados() {
		String[] reservadas = {"boolean","float","function","if","int","let","read","return","string","void","while","write"};
		for (String s : reservadas) {
			this.tablaSimbolos.addSimbolo(s,"id");
		}
	}

	public TablaSimbolos getTablaSimbolos() {
		return tablaSimbolos;
	}
	
}
