package di.wordle;

public class Palabra {
    private int id;
    private String palabra;
    private int vecesUsada;
    private int vecesAcertada;
    private String fechaAgregada;

    public Palabra(int id, String palabra, int vecesUsada, int vecesAcertada, String fechaAgregada) {
        this.id = id;
        this.palabra = palabra;
        this.vecesUsada = vecesUsada;
        this.vecesAcertada = vecesAcertada;
        this.fechaAgregada = fechaAgregada;
    }

    public int getId() { return id; }
    public String getPalabra() { return palabra; }
    public int getVecesUsada() { return vecesUsada; }
    public int getVecesAcertada() { return vecesAcertada; }
    public String getFechaAgregada() { return fechaAgregada; }
}
