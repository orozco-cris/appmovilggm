package sagamo.app.actividad;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sagamo.app.MainActivity;
import sagamo.app.R;
import sagamo.app.adaptador.FrgEstudianteRecyclerVA;

import sagamo.app.clases.Actividad;
import sagamo.app.clases.Clase;
import sagamo.app.clases.DialogoCargaFragment;
import sagamo.app.clases.Estudiante;
import sagamo.app.clases.Notificacion;
import sagamo.app.inicio.Menu;
import sagamo.app.registro.Registro;

public class FrgEstudianteA extends Fragment {
    private RecyclerView rcvEstudiantes;
    private FloatingActionButton fabEnviarDatos;
    private FirebaseFirestore mfirestore;
    private CheckBox cbSelectAll;
    private List<Estudiante> listaEstudiantes;
    private FrgEstudianteRecyclerVA adapter;
    private List<String> selectCed = new ArrayList<>();
    private List<String> selectToken = new ArrayList<>();
    private List<String> selectEst = new ArrayList<>();
    private DialogoCargaFragment dialogoCargaFragment;

    private Integer idparaleloC;
    private Integer idclaseC;
    private String materiaC;
    private String paraleloC;
    private String docenteC;
    private String nombreA;
    private String tipoA;
    private String fecharA;
    private String fechapA;
    private String descripcionA;
    private Actividad objActividad = new Actividad();
    private Gson objGson = new Gson();
    private RequestQueue queue;
    private String url;
    private Clase objClase;
    private JSONObject objNotificacion;

    private LinearLayout lyEstudiante;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("clase", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                idparaleloC = result.getInt("idparalelo", 0);
                idclaseC = result.getInt("idclase", 0);
                materiaC = result.getString("materia", "");
                paraleloC = result.getString("paralelo", "");
                docenteC = result.getString("docente", "");
                System.out.println("Clase: "+ idparaleloC + ", "+idclaseC+", "+materiaC+", "+paraleloC+", "+docenteC);
                getEstudiantes();
            }
        });

        getParentFragmentManager().setFragmentResultListener("actividad", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                nombreA = result.getString("nombre", "");
                tipoA = result.getString("tipo", "");
                fecharA = result.getString("fecharealizado", "");
                fechapA = result.getString("fechaplazo", "");
                descripcionA = result.getString("descripcion", "");
                System.out.println("ActActividad: "+nombreA+ ", "+tipoA+ ", " +fecharA+", "+fechapA+", "+descripcionA);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.sag_frg_estudiante_a, container, false);

        //Asignaciones
        view.findViewById(R.id.linear_estudiante).setBackgroundResource(R.drawable.sag_bg_actividad);
        rcvEstudiantes = view.findViewById(R.id.rcv_estudiantes);
        mfirestore = FirebaseFirestore.getInstance();
        listaEstudiantes = new ArrayList<>();
        cbSelectAll = view.findViewById(R.id.cb_select_estudiante_all);
        fabEnviarDatos = view.findViewById(R.id.fab_to_send_data);
        dialogoCargaFragment = new DialogoCargaFragment(this);
        queue = Volley.newRequestQueue(FrgEstudianteA.this.getContext());
        url = "https://fcm.googleapis.com/fcm/send";
        objNotificacion = new JSONObject();
        lyEstudiante = view.findViewById(R.id.linear_estudiante);

        try{
            //Recycler
            rcvEstudiantes.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),1, GridLayoutManager.VERTICAL, false);
            rcvEstudiantes.setLayoutManager(gridLayoutManager);
            //Eventos del recycler
            rcvEstudiantes.addOnItemTouchListener(new
                    RecyclerItemClickListener(FrgEstudianteA.this.getContext(),
                    rcvEstudiantes, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    multiselect(position);
                    cbSelectAll.setChecked(false);
                    if(selectCed.size() == listaEstudiantes.size()){
                        cbSelectAll.setChecked(true);
                    }
                }
                @Override
                public void onItemLongClick(View view, int position) {
                }
            }));

            //Seleccionar todos los elementos
            cbSelectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setGetAll();
                }
            });

            //Boton flotante que llama a creac y enviar actividad / notificacion
            fabEnviarDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MaterialAlertDialogBuilder(FrgEstudianteA.this.getContext())
                            .setTitle("Confirmación")
                            .setMessage("¿Desea guardar los cambios?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println("Tamaño: "+selectCed.size()+" "+nombreA);
                                    if(nombreA != null  && materiaC!= null && selectCed.size() > 0){
                                        dialogoCargaFragment.startProgressAlertDialog();
                                        crearActividad();
                                    }else{
                                        Snackbar.make(lyEstudiante, R.string.sag_error_general, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_warning)).show();
                                    }
                                }
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            });
        }catch(Throwable t){
            Log.e("MAIN", "onCreate: ", t);
            t.printStackTrace(System.out);
            Snackbar.make(view, R.string.sag_error_inesperado, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
        }
        return view;
    }

    //Método para consultar estudiantes
    public void getEstudiantes() {
        mfirestore.collection("estudiante")
                .whereEqualTo("idparalelo", idparaleloC)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        listaEstudiantes.clear();
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Estudiante objEstudiante = document.toObject(Estudiante.class);
                                listaEstudiantes.add(objEstudiante);
                            }
                            adapter = new FrgEstudianteRecyclerVA(listaEstudiantes);

                            rcvEstudiantes.setAdapter(adapter);
                        }
                        else{
                            Log.d("SAGAMO", "Error: "+task.getException());}
                    }
                });
    }

    //Metodo seleccionar todos los estudiantes
    private void setGetAll() {
        if (cbSelectAll.isChecked()){
            System.out.println("DATOS: "+selectCed.size());
            if(adapter!= null && selectCed.size()!=0 && selectToken.size()!= 0 && selectEst.size()!=0){
                selectCed.clear();
                selectToken.clear();
                selectEst.clear();
                for(Estudiante aux: listaEstudiantes){
                    selectCed.add(aux.getRepresentante());
                    selectToken.add(aux.getToken());
                    selectEst.add(aux.getNombre()+" "+aux.getApellido());
                }
                adapter.setSelectCedulas(selectCed);
                System.out.println("Lista: "+selectCed);
            }else{
                System.out.println("Error");
            }
        }
        else{
            selectCed.clear();
            selectToken.clear();
            selectEst.clear();
            System.out.println("Lista: "+selectCed);
        }

    }

    //Llena las listas de estudiantes
    private void multiselect(int position){
        Estudiante data = adapter.getItem(position);
        if(data != null){
            if(selectCed.contains(data.getRepresentante())){
                selectCed.remove(data.getRepresentante());
                selectToken.remove(data.getToken());
                selectEst.remove(data.getNombre()+" "+data.getApellido());
            }else{
                selectCed.add(data.getRepresentante());
                selectToken.add(data.getToken());
                selectEst.add(data.getNombre()+" "+data.getApellido());
            }

            if(selectCed.size()>0){
                Log.d("Estudiantes", "seleccionados: "+selectCed.size());
            }

        }
    }

    //Crea la nueva actividad
    private void crearActividad(){
        objActividad = new Actividad(idclaseC, nombreA, fecharA, fechapA, descripcionA, tipoA);
        mfirestore.collection("actividad")
        .add(objActividad)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("CREACION", "onSuccess: "+documentReference.getId());
                objActividad.setId(documentReference.getId());
                try {
                    //crearNotificaciones();
                    actualizaActividad();

                } catch (Exception e) {
                    e.printStackTrace();
                    dialogoCargaFragment.stopProgressAlertDialog();
                    Snackbar.make(lyEstudiante, R.string.sag_error_inesperado, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("CREACION", "onFailure: ", e);
                dialogoCargaFragment.stopProgressAlertDialog();
                Snackbar.make(lyEstudiante, R.string.sag_error_guardar, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
            }
        });

    }

    //Actualizar Actividad
    public void actualizaActividad(){
        mfirestore.collection("actividad").document(objActividad.getId())
        .update("id", objActividad.getId())
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
                try {
                    crearNotificaciones();
                    dialogoCargaFragment.stopProgressAlertDialog();
                    Snackbar.make(lyEstudiante, R.string.sag_sucess_guardar, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_sucessful)).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(FrgEstudianteA.this.getContext(), Menu.class);
                            startActivity(intent);
                            getActivity().onBackPressed();
                            //FrgEstudianteA.finish();
                        }
                    }, 4000);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error updating document", e);
                dialogoCargaFragment.stopProgressAlertDialog();
                Snackbar.make(lyEstudiante, R.string.sag_error_guardar, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
            }
        });
    }

    //Crea las notificaciones
    private void crearNotificaciones() throws JSONException {
        for ( int i = 0; i < selectCed.size(); i++){
            dialogoCargaFragment.startProgressAlertDialog();
            final String token = selectToken.get(i);
            final Notificacion objNotificaciones = new Notificacion(docenteC, selectCed.get(i),
            objActividad.getId(), selectEst.get(i), materiaC+" creó una nueva actividad ("+tipoA+")", "", "No realizado", fechapA);

            mfirestore.collection("notificacion")
            .add(objNotificaciones)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("CREACION", "onSuccess: "+documentReference.getId());
                    objNotificaciones.setId(documentReference.getId());
                    actualizarNotificacion(objNotificaciones.getId());
                    crearMensaje(objNotificaciones, token);

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("CREACION", "onFailure: ", e);
                    dialogoCargaFragment.stopProgressAlertDialog();
                    Snackbar.make(lyEstudiante, R.string.sag_error_guardar, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
                }
            });
        }

    }

    //Envia mensaje Cloud Messaging
    public void crearMensaje(Notificacion objNotificaciones, String token){

        try{
            final JSONObject objData = new JSONObject();
            final JSONObject todo = new JSONObject();
            objClase = new Clase(idclaseC, idparaleloC, materiaC, paraleloC, docenteC);

            objNotificacion.put("title", "Sagamo");
            objNotificacion.put("body", materiaC+" creó una nueva actividad ("+tipoA+")");
            objData.put("clase", objGson.toJson(objClase));
            objData.put("actividad", objGson.toJson(objActividad));
            objData.put("notificacion", objGson.toJson(objNotificaciones));
            todo.put("to", token);
            todo.put("notification", objNotificacion);
            todo.put("data", objData);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, todo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("TAG", response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("TAG", "Error: " + error.getMessage());
                    dialogoCargaFragment.stopProgressAlertDialog();
                    Snackbar.make(lyEstudiante, R.string.sag_error_guardar, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "key=AAAA8_qoDdo:APA91bEbGoMPohDoHxZb8L2Tv-KN9XCWBfLWX_aJReUAsGE-691KtgbJnXnrF8DZrK4qmQiQlP6JvgFG3Km-W3__nGMdxx2uTxqK5W425B8cs24NEcOrw1CxIiqIU-xZvB_gR_Vq592O");
                    System.out.println(headers);
                    return headers;
                }
            };
            queue.add(jsonObjectRequest);
        }catch (Exception e){
            e.printStackTrace(System.out);
        }
    }

    //Actualiza notificacion
    public void actualizarNotificacion(String id){
        mfirestore.collection("notificacion").document(id)
        .update("id", id)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully updated!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error updating document", e);
                dialogoCargaFragment.stopProgressAlertDialog();
                Snackbar.make(lyEstudiante, R.string.sag_error_guardar, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
            }
        });
    }
}