package sagamo.app.actividad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import sagamo.app.R;
import sagamo.app.adaptador.FrgParaleloRecyclerVA;
import sagamo.app.clases.Clase;


public class FrgparaleloA extends Fragment {
    private RecyclerView rcvClases;
    private FirebaseFirestore mfirestore;
    private List<Clase> lista;
    private FloatingActionButton fabAgregar;
    private FrgParaleloRecyclerVA adapter;
    private Clase objRespuesta;
    private SharedPreferences preferences;
    private String tipoUsuario = "";
    private String usuario = "";

    public FrgparaleloA() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sag_frg_paralelo_a, container, false);

        //Asignaciones
        view.findViewById(R.id.linear_paralelo).setBackgroundResource(R.drawable.sag_bg_actividad);
        rcvClases = view.findViewById(R.id.rcv_paralelos);
        mfirestore = FirebaseFirestore.getInstance();
        lista = new ArrayList<>();
        fabAgregar = view.findViewById(R.id.fab_to_list_estudiantes);
        objRespuesta = new Clase();

        try{
            preferences = this.getActivity().getSharedPreferences("cache", Context.MODE_PRIVATE);
            tipoUsuario = preferences.getString("tipo", "null");
            usuario = preferences.getString("usuario", "null");

            //Recycler
            rcvClases.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
            rcvClases.setLayoutManager(gridLayoutManager);

            //Carga de clases
            getClases();

            //EVENTO Floating Action button
            rcvClases.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                        fabAgregar.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if(dy > 0 && fabAgregar.isShown() ){
                        fabAgregar.hide();
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
            fabAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(objRespuesta.getIdparalelo() != null && !objRespuesta.getMateria().equals("")){
                        System.out.println("Paralelo: "+objRespuesta.getIdparalelo());
                        Bundle result = new Bundle();
                        result.putInt("idparalelo", objRespuesta.getIdparalelo());
                        result.putInt("idclase", objRespuesta.getIdClase());
                        result.putString("materia", objRespuesta.getMateria());
                        result.putString("paralelo", objRespuesta.getParalelo());
                        result.putString("docente", objRespuesta.getUsuario());
                        getParentFragmentManager().setFragmentResult("clase", result);
                        Snackbar.make(v, R.string.sag_sucess_operacion, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_information)).show();
                    }else{
                        Snackbar.make(v, R.string.sag_error_seleccion, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_warning)).show();
                    }
                }
            });

            //Evento del recycler
            rcvClases.addOnItemTouchListener(new
                    RecyclerItemClickListener(FrgparaleloA.this.getContext(), rcvClases,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            objRespuesta = adapter.getItem(position);
                        }
                        @Override
                        public void onItemLongClick(View view, int position) {
                        }
                    }));
        }catch(Throwable t){
            Log.e("MAIN", "onCreate: ", t);
            t.printStackTrace(System.out);
            Snackbar.make(view, R.string.sag_error_inesperado, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
        }
        return view;
    }

    //Método para mostrar las clases
    public void getClases(){
        mfirestore.collection("clase")
        .whereEqualTo("usuario", usuario)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Clase objClase = document.toObject(Clase.class);
                    objClase.setIdClase(Integer.parseInt(document.getId()));
                    lista.add(objClase);
                }
                adapter = new FrgParaleloRecyclerVA(lista);
                rcvClases.setAdapter(adapter);
            }
            else{
                Log.d("SAGAMO", "Error: "+task.getException());
            }
            }
        });
    }
}