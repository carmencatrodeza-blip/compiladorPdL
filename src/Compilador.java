public class Compilador {

    private final GestorErrores gestorErrores;
    private final Writer writer;
    private int linea;
    private final TablaSimbolos tablaGlobal;
    private int desplazamientoGlobal;
    private TablaSimbolos tablaLocal; // null si no estamos en función
    private String etiquetaActual;
    private boolean zonaDeclaracion;
    private boolean errorDetectado;

    public Compilador() {
        gestorErrores = new GestorErrores();
        linea = 0;
        tablaGlobal = new TablaSimbolos();
        etiquetaActual = "GLOBAL";
        tablaGlobal.setEtiqueta(etiquetaActual);
        desplazamientoGlobal = 1;
        zonaDeclaracion = false;
        writer = new Writer(gestorErrores);
        errorDetectado = false;
    }

    // Getters y setters
    public GestorErrores getGestorErrores() { return gestorErrores; }
    public Writer getWriter() { return writer; }
    public int getLinea() { return linea ; }
    public TablaSimbolos getTablaGlobal() { return tablaGlobal; }
    public TablaSimbolos getTablaLocal() { return tablaLocal; }
    public TablaSimbolos getTablaActual() { return etiquetaActual.equals("GLOBAL") ? tablaGlobal : tablaLocal ;}
    public int getDesplazamientoGlobal() { return desplazamientoGlobal; }
    public boolean getZonaDeclaracion() { return zonaDeclaracion; }
    public String getEtiquetaActual() { return etiquetaActual; }
    public boolean getErrorDetectado() { return errorDetectado; }
    
    public void incrementarLinea() { linea++; }
    public void incrementarDesplazamientoGlobal(int incremento) { desplazamientoGlobal += incremento; }
    public void setTablaLocal(TablaSimbolos t) { tablaLocal = t; }
    public void setZonaDeclaracion(boolean b) { zonaDeclaracion = b; }
    public void setEtiquetaActual(String e) { etiquetaActual = e; }
    public void lanzarError() { 
        if (!errorDetectado) {
            writer.write(tablaGlobal.toString(), "tabla");
            if (tablaLocal != null) {
                writer.write(tablaLocal.toString(), "tabla");
            }
        }
        errorDetectado = true; 
    }
}
