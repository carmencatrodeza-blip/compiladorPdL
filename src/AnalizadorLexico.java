import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

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
	private final Compilador compilador;
	private int estado; // Estado del autómata.
	private String lexema; // Variable para construir el lexema.
	private int contador; // Contador de caracteres leídos.
	private long numero; // Variable para calcular números.
	private int dec; // Variable para la parte decimal de números reales.
	private int caracter; // Caracter guardado como byte.
	private CaracterEspecial ce; // Caracter especial actual.
	private FileReader fr; // Lector de archivos.
	private Set<String> palabrasReservadas;

	public AnalizadorLexico(Compilador compilador, FileReader fr) {
		this.compilador = compilador;
		this.fr = fr;
		inicializarSetSimbolosReservados();
		try{
			caracter = fr.read();
			ce = CaracterEspecial.fromAscii(caracter);
		} catch (IOException ioe){
			compilador.lanzarError();
			compilador.getGestorErrores().mostrarError(2);
		}
	}

	public SimpleEntry<String, Object> sigToken() {
		estado = 0;
		lexema = "";
		numero = 0;
		dec = 0;
		contador = 0;
		while (true){
			switch(estado){
			case 0: // Estado inicial
				if(ce == CaracterEspecial.ESPACIO || ce == CaracterEspecial.TAB || ce == CaracterEspecial.RETORNO_CARRO){
					leerCaracter();
				}
				else if(ce == CaracterEspecial.SALTO_LINEA){
					compilador.incrementarLinea();
					leerCaracter();
				}
				else if(esLetra(caracter)){
					lexema += (char)caracter;
					estado = 1;
					leerCaracter();
				}
				else if(esDigito(caracter)){
					construirNumero(caracter);
					lexema += (char)caracter;
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
				compilador.lanzarError();
				compilador.getGestorErrores().mostrarError(104, compilador.getLinea(), (char)caracter, null);
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
					compilador.getWriter().write("< parIzq , >", "tokens");
					return new SimpleEntry<>("parIzq", null);
				}
				else if(ce == CaracterEspecial.PAR_DER){
					leerCaracter();
					compilador.getWriter().write("< parDer , >", "tokens");
					return new SimpleEntry<>("parDer", null);
				}
				else if(ce == CaracterEspecial.LLAVE_IZQ){
					leerCaracter();
					compilador.getWriter().write("< llaveIzq , >", "tokens");
					return new SimpleEntry<>("llaveIzq", null);
				}
				else if(ce == CaracterEspecial.LLAVE_DER){
					leerCaracter();
					compilador.getWriter().write("< llaveDer , >", "tokens");
					return new SimpleEntry<>("llaveDer", null);
				}
				else if(ce == CaracterEspecial.PUNTO_Y_COMA){
					leerCaracter();
					compilador.getWriter().write("< puntoComa , >", "tokens");
					return new SimpleEntry<>("puntoComa", null);
				}
				else if(ce == CaracterEspecial.COMA){
					leerCaracter();
					compilador.getWriter().write("< coma , >", "tokens");
					return new SimpleEntry<>("coma", null);
				}
				else if(ce == CaracterEspecial.PUNTO){
					// ! Necesito imprimir " .55 " y corto al leer el punto
					compilador.lanzarError();
					compilador.getGestorErrores().mostrarError(102, compilador.getLinea(), (char)caracter, null);
					return null;
				}
				else if(caracter == -1){
					compilador.getWriter().write("< EOF , >", "tokens");
					return new SimpleEntry<>("EOF", null);
				}
				else{
					compilador.lanzarError();
					compilador.getGestorErrores().mostrarError(101, compilador.getLinea(), (char)caracter, null);
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
					if(!esReservada(lexema).equals("noEsReservada")){
						compilador.getWriter().write("< " + lexema + " , >", "tokens");
						return new SimpleEntry<>(lexema, null);
					} else {
						int pos = -1;
						// Si se está declarando, se añade a la tabla activa.
						if (compilador.getZonaDeclaracion())
							pos = compilador.getTablaActual().addSimbolo(lexema);
						// Si existe tabla local activa, se busca primero allí.
						if(!compilador.getEtiquetaActual().equals("GLOBAL"))
							pos = compilador.getTablaActual().contieneId(lexema);
						// Si la tabla activa es la global o no se encontró en la local, se busca en la global.
						if(pos == -1)
							pos = compilador.getTablaGlobal().contieneId(lexema);
						// Declaración implícita.
						if(pos == -1){ 
							pos = compilador.getTablaGlobal().addSimbolo(lexema);
							compilador.getTablaGlobal().actualizarVariable(pos, "entero", compilador.getDesplazamientoGlobal());
							compilador.incrementarDesplazamientoGlobal(4);
						}
						compilador.getWriter().write("< id , " + pos + " >", "tokens");
						return new SimpleEntry<>("id", pos);
					}
				}
				break;
			case 2:
				if(esLetra(caracter) || esDigito(caracter) || ce == CaracterEspecial.GUION_BAJO){
					lexema += (char)caracter;
					leerCaracter();
				} else {
					int pos = -1;
						// Si existe tabla local activa se busca primero allí.
						if(!compilador.getEtiquetaActual().equals("GLOBAL"))
							pos = compilador.getTablaActual().contieneId(lexema);
						// Si la tabla activa es la global o no se encontró en la local se busca en la global.
						if(pos == -1)
							pos = compilador.getTablaGlobal().contieneId(lexema);
						// Si no se encontró en ninguna es un nuevo id.
						if(pos == -1){
							if (compilador.getZonaDeclaracion()) // Se añade a la tabla activa.
								pos = compilador.getTablaActual().addSimbolo(lexema);
							else { // Declaración implícita.
								pos = compilador.getTablaGlobal().addSimbolo(lexema);
								compilador.getTablaGlobal().actualizarVariable(pos, "entero", compilador.getDesplazamientoGlobal());
								compilador.incrementarDesplazamientoGlobal(4);
							}
						}
					compilador.getWriter().write("< id , " + pos + " >", "tokens");
					return new SimpleEntry<>("id", pos);
				}
				break;
			case 3:
				if(esDigito(caracter)){
					lexema += (char)caracter;
					construirNumero(caracter);
					leerCaracter();
				}
				else if(ce == CaracterEspecial.PUNTO){
					estado =  4;
					lexema += (char)caracter;
					leerCaracter();
				}
				else{
				if (numero > 32767){
					compilador.lanzarError();
					compilador.getGestorErrores().mostrarError(106, compilador.getLinea(), (char)caracter, lexema);
					return null;
					}
					compilador.getWriter().write("< entero , " + (int)numero + " >", "tokens");
					return new SimpleEntry<>("entero", (int)numero);
				}
				break;
			case 4:
				if(esDigito(caracter)){
					estado = 5;
					lexema += (char)caracter;
					dec--;
					construirNumero(caracter);
					leerCaracter();
				}
				else{
					compilador.lanzarError();
					compilador.getGestorErrores().mostrarError(103, compilador.getLinea(), (char)caracter, lexema);
					return null;
				}
				break;
			case 5:
				if(esDigito(caracter)){
					lexema += (char)caracter;
					construirNumero(caracter);
					dec--;
					leerCaracter();
				}
				else{
					double output = calcularValor();
					if (output > 117549436.0f){
						compilador.lanzarError();
						compilador.getGestorErrores().mostrarError(107, compilador.getLinea(), (char)caracter, lexema);
						return null;
					}
					compilador.getWriter().write("< real , " + output + " >", "tokens");
					return new SimpleEntry<>("real", output);
				}
				break;
			case 6:
				if (ce == CaracterEspecial.COMILLAS) {
					lexema += (char)caracter;
					leerCaracter();
					if (contador > 64) {
						compilador.lanzarError();
						compilador.getGestorErrores().mostrarError(108, compilador.getLinea(), (char)caracter, lexema);
						return null;
					}
					compilador.getWriter().write("< cadena , " + lexema + " >", "tokens");
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
					compilador.getWriter().write("< asigDiv , >", "tokens");
					return new SimpleEntry<>("asigDiv", null);
				}
				else {
					compilador.getWriter().write("< div , >", "tokens");
					return new SimpleEntry<>("div", null);
				}
				break;
			case 9:
				if (ce == CaracterEspecial.SALTO_LINEA) {
					compilador.incrementarLinea();
					estado = 0;
					leerCaracter();
				}
				else if (caracter == -1) {
					compilador.getWriter().write("< EOF , >", "tokens");
					return new SimpleEntry<>("EOF", null);
				}
				else {
					leerCaracter();
				}
				break;
			case 10:
				if (ce == CaracterEspecial.IGUAL) {
					leerCaracter();
					compilador.getWriter().write("< igual , >", "tokens");
					return new SimpleEntry<>("igual", null);
				}
				else {
					compilador.getWriter().write("< asig , >", "tokens");
					return new SimpleEntry<>("asig", null);
				}
			case 11:
				if(ce == CaracterEspecial.Y) {
					leerCaracter();
					compilador.getWriter().write("< y , >", "tokens");
					return new SimpleEntry<>("y", null);
				}
				else {
					compilador.lanzarError();
					compilador.getGestorErrores().mostrarError(105, compilador.getLinea(), (char)caracter, null);
					return null;
				}
			default:
				return null;
			}
		}
	}

	private void leerCaracter() {
		try {
			caracter = fr.read();
			ce = CaracterEspecial.fromAscii(caracter);
		} catch (IOException ioe) {
			compilador.lanzarError();
			compilador.getGestorErrores().mostrarError(2);
		}
	}

	private void construirNumero(int caracter){
		int num = caracter - 48; // Transformar de ASCII a dígito.
		numero = numero * 10 + num; // Construir el número.
	}

	private double calcularValor(){ return (double)numero * Math.pow(10, dec); }

	private boolean esLetra(int c) { return (c >= 65 && c <= 90) || (c >= 97 && c <= 122); }

	private boolean esDigito(int c) { return (c >= 48 && c <= 57); }

	private void inicializarSetSimbolosReservados() {
		palabrasReservadas = Set.of(
			"boolean","float","function","if","int","let","read",
			"return","string","void","while","write"
		);
	}

	private String esReservada(String s) { return (palabrasReservadas.contains(s)) ? s : "noEsReservada"; }
}