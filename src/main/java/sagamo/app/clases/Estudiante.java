package sagamo.app.clases;

public class Estudiante {
    private Integer idEstudiante;
    private Integer idParalelo;
    private String representante;
    private String nombre;
    private String apellido;
    private String token;

    public Estudiante() {
    }

    public Estudiante(Integer idEstudiante, Integer idParalelo, String representante, String nombre, String apellido, String token) {
        this.idEstudiante = idEstudiante;
        this.idParalelo = idParalelo;
        this.representante = representante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Integer getIdParalelo() {
        return idParalelo;
    }

    public void setIdParalelo(Integer idParalelo) {
        this.idParalelo = idParalelo;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "idEstudiante=" + idEstudiante +
                ", idParalelo=" + idParalelo +
                ", representante='" + representante + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
