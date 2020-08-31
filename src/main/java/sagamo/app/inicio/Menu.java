package sagamo.app.inicio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import sagamo.app.R;
import sagamo.app.actividad.ActActividad;
import sagamo.app.clases.Actividad;
import sagamo.app.clases.Clase;
import sagamo.app.clases.DialogoCargaActividad;
import sagamo.app.clases.Notificacion;
import sagamo.app.retroalimentacion.Retroalimentacion;

public class Menu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    private SharedPreferences preferences;
    private String tipoUsuario;
    private String usuario;
    private Toolbar toolbar;
    private List<Notificacion> lista;
    private MenuRecyclerViewAdapter adapter;
    private RecyclerView rcvNotificaciones;
    private Gson objGson = new Gson();
    private DialogoCargaActividad dialogoCargaActividad;
    private TextView txvVacio;

    private String idActividad;
    private Actividad objActividad = new Actividad();
    private Clase objClase = new Clase();
    private Notificacion objNotificacion = new Notificacion();
    private FrameLayout flMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sag_menu);

        //Asignaciones
        mAuth = FirebaseAuth.getInstance();
        txvVacio = findViewById(R.id.sms_lista_vacia);
        mfirestore = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("cache", Context.MODE_PRIVATE);
        tipoUsuario = preferences.getString("tipo", "null");
        usuario = preferences.getString("usuario", "null");
        System.out.println("RESPUESTA: "+tipoUsuario+", "+usuario);
        lista = new ArrayList<>();
        flMenu = findViewById(R.id.fl_Menu);

        toolbar = findViewById(R.id.bar_inicio);
        this.setSupportActionBar(toolbar);
        rcvNotificaciones = findViewById(R.id.recycler_view);
        rcvNotificaciones.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        rcvNotificaciones.setLayoutManager(gridLayoutManager);

        try{
            if (lista.size() == 0){
                txvVacio.setText("No existen actividades para visualizar");
                txvVacio.setVisibility(View.VISIBLE);
            } else {txvVacio.setVisibility(View.INVISIBLE);}

            dialogoCargaActividad = new DialogoCargaActividad(this);
            getNotificaciones(tipoUsuario, usuario);

        }catch(Throwable t){
            Log.e("MAIN", "onCreate: ", t);
            t.printStackTrace(System.out);
            Snackbar.make(flMenu, R.string.sag_error_inesperado, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
        }
    }

    private void getNotificaciones(String tipoUsuario, String usuario) {
        if(tipoUsuario.equals("docente")){
            mfirestore.collection("notificacion")
            .whereEqualTo("idDocente", usuario)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()){

                for(QueryDocumentSnapshot document: task.getResult()){
                    Notificacion objAux = document.toObject(Notificacion.class);
                    objAux.setId(document.getId());
                    lista.add(objAux);
                    txvVacio.setVisibility(View.INVISIBLE);
                }
                adapter = new MenuRecyclerViewAdapter(lista);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    dialogoCargaActividad.startProgressAlertDialog();
                    idActividad = lista.get(rcvNotificaciones.getChildAdapterPosition(v)).getIdActividad();
                    objNotificacion=lista.get(rcvNotificaciones.getChildAdapterPosition(v));
                    mfirestore.collection("actividad")
                        .document(idActividad)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                objActividad = task.getResult().toObject(Actividad.class);
                                objActividad.setId(idActividad);
                                mfirestore.collection("clase").document(objActividad.getIdClase().toString())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){

                                            objClase = task.getResult().toObject(Clase.class);
                                            objClase.setIdClase(objActividad.getIdClase());
                                            Intent intent = new Intent(Menu.this, Retroalimentacion.class);
                                            intent.putExtra("clase", objGson.toJson(objClase));
                                            intent.putExtra("actividad", objGson.toJson(objActividad));
                                            intent.putExtra("notificacion", objGson.toJson(objNotificacion));
                                            startActivity(intent);
                                            dialogoCargaActividad.stopProgressAlertDialog();
                                            finish();
                                        }
                                        else {Log.e("ERROR", "Ha ocurrido un error al obtener datos (Clase): " );
                                            dialogoCargaActividad.stopProgressAlertDialog();}
                                    }
                                });
                            }else {
                                Log.e("ERROR", "Ha ocurrido un error al obtener datos Actividad: " );
                                dialogoCargaActividad.stopProgressAlertDialog();
                            }
                            }
                        });
                    }
                });
                rcvNotificaciones.setAdapter(adapter);
            }
            else {
                Log.d("SAGAMO", "Error getting documents: ", task.getException());
            }
                }
            });


        }else if(tipoUsuario.equals("representante")){
            mfirestore.collection("notificacion")
            .whereEqualTo("idRepresentante", usuario)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Notificacion objAux = document.toObject(Notificacion.class);
                        objAux.setId(document.getId());
                        lista.add(objAux);
                        txvVacio.setVisibility(View.INVISIBLE);
                    }
                    adapter = new MenuRecyclerViewAdapter(lista);
                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogoCargaActividad.startProgressAlertDialog();
                            idActividad = lista.get(rcvNotificaciones.getChildAdapterPosition(v)).getIdActividad();
                            objNotificacion=lista.get(rcvNotificaciones.getChildAdapterPosition(v));
                            mfirestore.collection("actividad")
                            .document(idActividad)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        objActividad = task.getResult().toObject(Actividad.class);
                                        objActividad.setId(idActividad);
                                        mfirestore.collection("clase").document(objActividad.getIdClase().toString())
                                            .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            objClase = task.getResult().toObject(Clase.class);
                                                            objClase.setIdClase(objActividad.getIdClase());
                                                            Intent intent = new Intent(Menu.this, Retroalimentacion.class);
                                                            intent.putExtra("clase", objGson.toJson(objClase));
                                                            intent.putExtra("actividad", objGson.toJson(objActividad));
                                                            intent.putExtra("notificacion", objGson.toJson(objNotificacion));
                                                            startActivity(intent);
                                                            dialogoCargaActividad.stopProgressAlertDialog();
                                                            finish();
                                                        }
                                                        else {Log.e("ERROR", "Ha ocurrido un error al obtener datos (Clase): " );
                                                            dialogoCargaActividad.stopProgressAlertDialog();}
                                                    }
                                                });
                                    }else {Log.e("ERROR", "Ha ocurrido un error al obtener datos Actividad: " );
                                        dialogoCargaActividad.stopProgressAlertDialog();}
                                }
                            });

                        }

                    });
                    rcvNotificaciones.setAdapter(adapter);
                }
                else {
                    Log.d("SAGAMO", "Error getting documents: ", task.getException());
                }
                }
            });
        }else dialogoCargaActividad.stopProgressAlertDialog();
    }

    //INFLA EL TOOLBAR CON EL MENU
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();


        if (tipoUsuario.equals("docente")){
            inflater.inflate(R.menu.sag_toolbar_inicio, menu);

        }else
        {inflater.inflate(R.menu.sag_toolbar_prueba, menu);}

        return super.onCreateOptionsMenu(menu);
    }

    //ACCIONES POR LOS ICONOS DEL TOOLBAR
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.salir:
                new MaterialAlertDialogBuilder(Menu.this)
                        .setTitle("Cerrar sesión")
                    .setMessage("¿Desea salir de la aplicación y cerrar sesión?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();//Elimina la cache o datos de sesión
                            editor.commit();
                            Snackbar.make(flMenu, R.string.sag_salir, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_sucessful)).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 2500);

                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();


                return true;
            case R.id.next:
                Intent intent = new Intent(this, ActActividad.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.help:
                Intent intent1 = new Intent(this, Informacion.class);
                startActivity(intent1);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}