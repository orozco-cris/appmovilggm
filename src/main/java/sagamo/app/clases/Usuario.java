package sagamo.app.clases;

public class Usuario {
    private String Nombre;
    private String Apellido;
    private String Celular;
    private String Cedula;
    private String correo;
    private String token;
    private String tipo;

    public Usuario(){}

    public Usuario(String nombre, String apellido, String celular, String cedula, String correo, String token, String tipo) {
        this.Nombre = nombre;
        this.Apellido = apellido;
        this.Celular = celular;
        this.Cedula = cedula;
        this.correo = correo;
        this.token = token;
        this.tipo = tipo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String celular) {
        Celular = celular;
    }

    public String getCedula() {
        return Cedula;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "Nombre='" + Nombre + '\'' +
                ", Apellido='" + Apellido + '\'' +
                ", Celular='" + Celular + '\'' +
                ", Cedula='" + Cedula + '\'' +
                ", correo='" + correo + '\'' +
                ", token='" + token + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
