package sagamo.app.retroalimentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sagamo.app.R;
import sagamo.app.clases.Actividad;
import sagamo.app.clases.Clase;
import sagamo.app.clases.DialogoCargaActividad;
import sagamo.app.clases.Notificacion;

public class Retroalimentacion extends AppCompatActivity {

    private TextInputEditText edtNombre;
    private TextInputEditText edtFechaCreacion;
    private TextInputEditText edtFechaPlazo;
    private TextInputEditText edtTipo;
    private TextInputEditText edtDescripcion;
    private TextInputEditText edtParalelo;
    private TextInputEditText edtMateria;
    private TextInputEditText edtEstudiante;
    private TextInputEditText edtDialogo;
    private AutoCompleteTextView spiCalificacion;
    private FloatingActionButton fabRespuesta;
    private CoordinatorLayout clRetroalimentacion;
    private DialogoCargaActividad dialogoCargaActividad;

    private ArrayAdapter<String> adapterSpi;
    private Gson objGson;
    private String jsonClase = "";
    private String jsonActividad = "";
    private String jsonNotificacion = "";
    private String tipoActividad = "";
    private String tipoUsuario;
    private String tokenDestinatario = "";

    private Clase objClase = new Clase();
    private Actividad objActividad = new Actividad();
    private Notificacion objNotificaciones = new Notificacion();
    private FirebaseFirestore mfirestore;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sag_act_retroalimentacion);

        //Asignaciones
        edtNombre = findViewById(R.id.edt_nombre_act);
        edtFechaCreacion = findViewById(R.id.edt_fechacreado_act);
        edtFechaPlazo = findViewById(R.id.edt_fechaplazo_act);
        edtTipo = findViewById(R.id.edt_tipo_act);
        edtDescripcion = findViewById(R.id.edt_descripcion_act);
        edtParalelo = findViewById(R.id.edt_paralelo_act);
        edtMateria = findViewById(R.id.edt_materia_act);
        edtEstudiante = findViewById(R.id.edt_estudiante_act);
        edtDialogo = findViewById(R.id.edt_dialogo_act);
        spiCalificacion = findViewById(R.id.spi_calificaciones_act);
        fabRespuesta = findViewById(R.id.fab_to_responder_data);
        clRetroalimentacion = findViewById(R.id.cl_retroalimentacion);
        mfirestore = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("cache", Context.MODE_PRIVATE);
        dialogoCargaActividad = new DialogoCargaActividad(this);


        tipoUsuario = preferences.getString("tipo", "null");
        if(!tipoUsuario.equals("null")){
            Log.e("TIPO USUARIO", "posi: "+tipoUsuario);
        }else Log.e("TIPO USUARIO", "GG nomas: "+tipoUsuario);

        ArrayList<String> listaSpi = new ArrayList<>();
        listaSpi.add("Realizado");
        listaSpi.add("No realizado");
        listaSpi.add("Incompleto");
        adapterSpi = new ArrayAdapter<String>(this, R.layout.list_item, listaSpi);
        spiCalificacion.setAdapter(adapterSpi);

        objGson = new Gson();

        //Recibe elementos de una anterior actividad
        if(getIntent().getExtras() != null){
            jsonClase = getIntent().getExtras().getString("clase");
            jsonActividad = getIntent().getExtras().getString("actividad");
            jsonNotificacion = getIntent().getExtras().getString("notificacion");

            try{
                objClase = objGson.fromJson(jsonClase, Clase.class);
                edtParalelo.setText(objClase.getParalelo());
                edtMateria.setText(objClase.getMateria());

                objActividad = objGson.fromJson(jsonActividad, Actividad.class);
                edtNombre.setText(objActividad.getNombre());
                edtFechaCreacion.setText(objActividad.getFechaRealizado());
                edtFechaPlazo.setText(objActividad.getFechaPlazo());
                edtTipo.setText(objActividad.getTipo());
                edtDescripcion.setText(objActividad.getDescripcion());

                objNotificaciones  = objGson.fromJson(jsonNotificacion, Notificacion.class);
                edtEstudiante.setText(objNotificaciones.getEstudiante());
                edtDialogo.setText(objNotificaciones.getObservacion());
                spiCalificacion.setText(objNotificaciones.getCalificacion());

                spiCalificacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spiCalificacion.setText("");
                    }
                });

                getToken();

                if (tipoUsuario.equals("docente")){
                    edtNombre.setEnabled(true);
                    edtFechaPlazo.setEnabled(true);
                    edtDescripcion.setEnabled(true);
                    spiCalificacion.setEnabled(true);
                }
            }
            catch(Exception e){
                Log.d("TAG", "onCreate: "+e);
            }
        }


        fabRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(getIntent().getExtras() != null){



                new MaterialAlertDialogBuilder(Retroalimentacion.this)
                        .setTitle("Confirmación")
                        .setMessage("¿Desea guardar los cambios?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialogoCargaActividad.startProgressAlertDialog();

                                if(!edtNombre.getText().toString().equals("") && !edtFechaPlazo.getText().toString().equals("") &&
                                        !edtDescripcion.getText().toString().equals("") && !spiCalificacion.getText().toString().equals("")){

                                    objActividad.setNombre(edtNombre.getText().toString());
                                    objActividad.setFechaPlazo(edtFechaPlazo.getText().toString());
                                    objActividad.setDescripcion(edtDescripcion.getText().toString());
                                    actualizarActividad();

                                    objNotificaciones.setCalificacion(spiCalificacion.getText().toString());
                                    objNotificaciones.setObservacion(edtDialogo.getText().toString());
                                    actualizarNotificacion();


                                    respuestaActividad();
                                }else{
                                    dialogoCargaActividad.stopProgressAlertDialog();
                                    Snackbar.make(clRetroalimentacion, R.string.sag_error_campos, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_warning)).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
            }
        });
        spiCalificacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tipoActividad = parent.getItemAtPosition(position).toString();
            }
        });

    }

    public void actualizarActividad(){
        mfirestore.collection("actividad").document(objActividad.getId())
        .update("nombre", objActividad.getNombre(), "fechaPlazo",
                objActividad.getFechaPlazo(), "descripcion", objActividad.getDescripcion())
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
                dialogoCargaActividad.stopProgressAlertDialog();
                Snackbar.make(clRetroalimentacion, R.string.sag_error_guardar, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_warning)).show();
            }
        });
    }

    public void actualizarNotificacion(){
        mfirestore.collection("notificacion").document(objNotificaciones.getId())
        .update("calificacion", objNotificaciones.getCalificacion(), "observacion",
                objNotificaciones.getObservacion(), "nombre", objActividad.getTipo()+": "+objActividad.getNombre()+", ha sido modificado")
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
                dialogoCargaActividad.stopProgressAlertDialog();
                Snackbar.make(clRetroalimentacion, R.string.sag_error_guardar, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_warning)).show();
            }
        });
    }


    public void respuestaActividad(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fcm.googleapis.com/fcm/send";

        JSONObject objNotificacion = new JSONObject();
        JSONObject objData = new JSONObject();
        JSONObject todo = new JSONObject();

        try{
            objNotificacion.put("title", "Sagamo");
            objNotificacion.put("body",  objActividad.getTipo()+": "+objActividad.getNombre()+", ha sido modificado");

            objData.put("clase", objGson.toJson(objClase));
            objData.put("actividad", objGson.toJson(objActividad));
            objNotificaciones.setObservacion(edtDialogo.getText().toString());
            objData.put("notificacion", objGson.toJson(objNotificaciones));

            todo.put("to", tokenDestinatario);
            todo.put("notification", objNotificacion);
            todo.put("data", objData);

            System.out.println("Datos a enviar: "+todo.toString());


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, todo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("TAG", response.toString());
                    dialogoCargaActividad.stopProgressAlertDialog();
                    Snackbar.make(clRetroalimentacion, R.string.sag_sucess_guardar, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_sucessful)).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("TAG", "Error: " + error.getMessage());
                    dialogoCargaActividad.stopProgressAlertDialog();
                    Snackbar.make(clRetroalimentacion, R.string.sag_error_guardar, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
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

        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    // Obtener token para respuesta
    public void getToken(){

        Log.d("TOKEN", "INICIO:"+tipoUsuario);

        if(tipoUsuario.equals("docente")){
            Log.d("TOKEN", "DOCENTE:"+tipoUsuario);
            mfirestore.collection("usuario")
            .whereEqualTo("cedula", objNotificaciones.getIdRepresentante())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("ACTUALIZACION TOKEN", document.getId() + " => " + document.getData());
                            tokenDestinatario = document.getData().get("token").toString();
                            System.out.println("TOKEN: "+tokenDestinatario);
                        }
                    }else {
                        Log.d("ACTUALIZACION TOKEN", "Error getting documents: ", task.getException());
                    }

                }
            });
        }else{
            Log.d("TOKEN", "REPRESENTANTE:"+tipoUsuario);
            mfirestore.collection("usuario")
                    .whereEqualTo("cedula", objNotificaciones.getIdDocente())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("ACTUALIZACION TOKEN", document.getId() + " => " + document.getData());
                                    tokenDestinatario = document.getData().get("token").toString();
                                    System.out.println("TOKEN: "+tokenDestinatario);
                                }
                            }else {
                                Log.d("ACTUALIZACION TOKEN", "Error getting documents: ", task.getException());
                            }

                        }
                    });
        }
    }



}