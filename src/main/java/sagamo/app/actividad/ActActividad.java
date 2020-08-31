package sagamo.app.actividad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.graphics.LinearGradient;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import sagamo.app.R;
import sagamo.app.modelo.MActividad;

public class ActActividad extends AppCompatActivity {

    private TabLayout tabTitulo;
    private ViewPager viewPager;
    private MActividad mActividad;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sag_actividad);

        //Asignaciones
        tabTitulo = findViewById(R.id.tab_actividad);
        viewPager = findViewById(R.id.vp_actividad);
        mActividad = new MActividad();
        linearLayout = findViewById(R.id.ll_actividad_p);

        //Creación y asignación de Fragments

        try{
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FrginformacionA frgInformacionA = new FrginformacionA();
            fragmentTransaction.add(R.id.vp_actividad, frgInformacionA);
            FrgparaleloA frgParaleloA = new FrgparaleloA();
            fragmentTransaction.add(R.id.vp_actividad, frgParaleloA);
            FrgEstudianteA frgEstudianteA = new FrgEstudianteA();
            fragmentTransaction.add(R.id.vp_actividad, frgEstudianteA);
            fragmentTransaction.commit();
            llenarViewPager();
        }catch(Throwable t){
            Log.e("MAIN", "onCreate: ", t);
            t.printStackTrace(System.out);
            Snackbar.make(linearLayout, R.string.sag_error_inesperado, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
        }
    }

    //Llena el viewPager con los fragmentos
    private void llenarViewPager() {
        AdaptadorAct objAdaptador = new AdaptadorAct(getSupportFragmentManager(), 1);
        objAdaptador.addFragment(new FrginformacionA(), "Actividad");
        objAdaptador.addFragment(new FrgparaleloA(), "Clases");
        objAdaptador.addFragment(new FrgEstudianteA(), "Estudiantes");
        viewPager.setOffscreenPageLimit(3);// Para no perder el contexto
        viewPager.setAdapter(objAdaptador);

        tabTitulo.setupWithViewPager(viewPager);
        tabTitulo.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.sag_icon_looks_one));
        tabTitulo.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.sag_icon_looks_two));
        tabTitulo.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.sag_icon_looks_three));
    }

    //MÉTODOS PARA INTERACTUAR CON LA ACTIVIDAD PARALELO
}