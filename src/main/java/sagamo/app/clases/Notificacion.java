package sagamo.app.clases;

public class Notificacion {
    private String id;
    private String idDocente;
    private String idRepresentante;
    private String idActividad;
    private String estudiante;
    private String nombre;
    private String observacion;
    private String calificacion;
    private String fechaplazo;

    public Notificacion(){}

    public Notificacion(String idDocente, String idRepresentante, String idActividad, String estudiante, String nombre, String observacion, String calificacion, String fechaplazo) {
        this.idDocente = idDocente;
        this.idRepresentante = idRepresentante;
        this.idActividad = idActividad;
        this.estudiante = estudiante;
        this.nombre = nombre;
        this.observacion = observacion;
        this.calificacion = calificacion;
        this.fechaplazo = fechaplazo;
    }

    public String getFechaplazo() {
        return fechaplazo;
    }

    public void setFechaplazo(String fechaplazo) {
        this.fechaplazo = fechaplazo;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(String idDocente) {
        this.idDocente = idDocente;
    }

    public String getIdRepresentante() {
        return idRepresentante;
    }

    public void setIdRepresentante(String idRepresentante) {
        this.idRepresentante = idRepresentante;
    }

    public String getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(String idActividad) {
        this.idActividad = idActividad;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "id='" + id + '\'' +
                ", idDocente='" + idDocente + '\'' +
                ", idRepresentante='" + idRepresentante + '\'' +
                ", idActividad='" + idActividad + '\'' +
                ", estudiante='" + estudiante + '\'' +
                ", nombre='" + nombre + '\'' +
                ", observacion='" + observacion + '\'' +
                ", calificacion='" + calificacion + '\'' +
                ", fechaplazo='" + fechaplazo + '\'' +
                '}';
    }
}
