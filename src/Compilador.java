public class Compilador {

    private final GestorErrores gestorErrores;
    private final Writer writer;
    private int linea;
    private TablaSimbolos tablaGlobal;
    private int desplazamientoGlobal;
    private TablaSimbolos tablaLocal; // null si no estamos en función
    private String etiquetaActual;
    private int idTablaSig;
    private boolean zonaDeclaracion;
    private boolean errorDetectado;
    private boolean dentroDeFuncion;

    public Compilador() {
        gestorErrores = new GestorErrores();
        linea = 1;
        idTablaSig = 1;
        tablaGlobal = new TablaSimbolos(idTablaSig);
        idTablaSig++;
        etiquetaActual = "GLOBAL";
        tablaGlobal.setEtiqueta(etiquetaActual);
        desplazamientoGlobal = 0;
        zonaDeclaracion = false;
        writer = new Writer(gestorErrores);
        errorDetectado = false;
        dentroDeFuncion = false;
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
    public int getIdTablaSig() { return idTablaSig++; }
    public boolean getDentroDeFuncion() { return dentroDeFuncion; }
    
    public void incrementarLinea() { linea++; }
    public void incrementarDesplazamientoGlobal(int incremento) { desplazamientoGlobal += incremento; }
    public void setTablaGlobal(TablaSimbolos t) { tablaGlobal = t; }
    public void setTablaLocal(TablaSimbolos t) { tablaLocal = t; }
    public void setZonaDeclaracion(boolean b) { zonaDeclaracion = b; }
    public void setEtiquetaActual(String e) { etiquetaActual = e; }
    public void setDentroDeFuncion(boolean b) { dentroDeFuncion = b; }
    public void lanzarError() {
        if (!errorDetectado) {
            if (tablaLocal != null)
                writer.write(tablaLocal.toString(), "tabla");
            writer.writeTablaGlobal(tablaGlobal.toString());
        }
        errorDetectado = true; 
    }
}
