package sagamo.app.actividad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorAct  extends FragmentPagerAdapter {

    private List<Fragment> listaFragments = new ArrayList<>();
    private List<String> listaTitulos = new ArrayList<>();

    public AdaptadorAct(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    //MÉTODO QUE OBTIENE LOS FRAGMENTOS Y SUS TITULOS
    public void addFragment(Fragment fragment, String titulo){
        listaFragments.add(fragment);
        listaTitulos.add(titulo);
    }

    //MÉTODOS HEREDADOS
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       return listaTitulos.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return listaFragments.get(position);
    }

    @Override
    public int getCount() {
        return listaFragments.size();
    }
}
