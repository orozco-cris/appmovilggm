package sagamo.app.clases;

public class Actividad {
    private String id;
    private Integer idClase;
    private String nombre;
    private String fechaRealizado;
    private String fechaPlazo;
    private String descripcion;
    private String tipo;

    public Actividad(){}

    public Actividad(Integer idClase, String nombre, String fechaRealizado, String fechaPlazo, String descripcion, String tipo) {
        this.idClase = idClase;
        this.nombre = nombre;
        this.fechaRealizado = fechaRealizado;
        this.fechaPlazo = fechaPlazo;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIdClase() {
        return idClase;
    }

    public void setIdClase(Integer idClase) {
        this.idClase = idClase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaRealizado() {
        return fechaRealizado;
    }

    public void setFechaRealizado(String fechaRealizado) {
        this.fechaRealizado = fechaRealizado;
    }

    public String getFechaPlazo() {
        return fechaPlazo;
    }

    public void setFechaPlazo(String fechaPlazo) {
        this.fechaPlazo = fechaPlazo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Actividad{" +
                "id='" + id + '\'' +
                ", idClase=" + idClase +
                ", nombre='" + nombre + '\'' +
                ", fechaRealizado='" + fechaRealizado + '\'' +
                ", fechaPlazo='" + fechaPlazo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
