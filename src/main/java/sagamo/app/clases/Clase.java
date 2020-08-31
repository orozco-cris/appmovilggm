package sagamo.app.clases;

public class Clase {
    private Integer idClase;
    private Integer idparalelo;
    private String materia;
    private String paralelo;
    private String usuario;

    public Clase(){

    }

    public Clase(Integer idClase, Integer idparalelo, String materia, String paralelo, String usuario) {
        this.idClase = idClase;
        this.idparalelo = idparalelo;
        this.materia = materia;
        this.paralelo = paralelo;
        this.usuario = usuario;
    }

    public Integer getIdClase() {
        return idClase;
    }

    public void setIdClase(Integer idClase) {
        this.idClase = idClase;
    }

    public Integer getIdparalelo() {
        return idparalelo;
    }

    public void setIdparalelo(Integer idparalelo) {
        this.idparalelo = idparalelo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getParalelo() {
        return paralelo;
    }

    public void setParalelo(String paralelo) {
        this.paralelo = paralelo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Clase{" +
                "idClase=" + idClase +
                ", idparalelo=" + idparalelo +
                ", materia='" + materia + '\'' +
                ", paralelo='" + paralelo + '\'' +
                ", usuario='" + usuario + '\'' +
                '}';
    }
}
