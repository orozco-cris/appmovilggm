package sagamo.app.inicio;

import java.util.ArrayList;
import java.util.List;

public class objetoPrueba {
    private static final String TAG = objetoPrueba.class.getSimpleName();

    public String titulo;
    public String subtitulo;

    objetoPrueba(String tit, String sub){
        this.titulo = tit;
        this.subtitulo = sub;
    }

    public static List<objetoPrueba> listafn(){
        List<objetoPrueba> lista = new ArrayList<>();
        lista.add(new objetoPrueba("Jhon", "Andra"));
        lista.add(new objetoPrueba("Luis", "Barr"));
        lista.add(new objetoPrueba("Cris", "Oro"));
        return lista;
    }

}
