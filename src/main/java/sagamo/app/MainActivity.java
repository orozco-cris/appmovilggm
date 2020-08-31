package sagamo.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

//PARA PASAR ENTRE FRAGMENTOS

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import sagamo.app.clases.DialogoCargaActividad;
import sagamo.app.clases.Estudiante;
import sagamo.app.clases.Usuario;
import sagamo.app.inicio.Menu;
import sagamo.app.registro.Registro;

/*PRESENTA LA INTERFAZ DE LOGIN*/

public class MainActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private TextInputLayout constrasenaLayout;
    private TextInputEditText contrasenaEdit;
    private TextInputEditText correo;
    private FirebaseFirestore mfirestore;
    private Usuario objUsuario;
    private Estudiante objEstudiante;
    private String token = "";

    private MaterialButton siguienteBoton;
    private MaterialButton btnRegistro;

    private DialogoCargaActividad dialogoCargaActividad;
    private LinearLayout llLogeo;

    MaterialButton btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //EMPIEZA EL CODIGO / Asignaciones
        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();
        objUsuario = new Usuario();
        objEstudiante = new Estudiante();
        dialogoCargaActividad = new DialogoCargaActividad(this);
        btnCancelar = findViewById(R.id.cancel_button_l);

        constrasenaLayout = findViewById(R.id.contrasena_login_input);
        contrasenaEdit = findViewById(R.id.contrasena_login_edit);
        correo = findViewById(R.id.correo_login);
        siguienteBoton = findViewById(R.id.siguiente_button);
        btnRegistro = findViewById(R.id.registro_button_l);
        llLogeo = findViewById(R.id.ll_login);


        try{
            btnRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Registro.class);
                    startActivity(intent);
                    finish();
                }
            });

            siguienteBoton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!esContrasenaValida(contrasenaEdit.getText())){
                        constrasenaLayout.setError(getString(R.string.sag_error_contrasena));
                    }
                    else{
                        constrasenaLayout.setError(null);
                        dialogoCargaActividad.startProgressAlertDialog();
                        signIn(correo.getText().toString(), contrasenaEdit.getText().toString()); //Llama el logueo
                    }
                }
            });

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    correo.setText("");
                    contrasenaEdit.setText("");
                }
            });

            contrasenaEdit.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(esContrasenaValida(contrasenaEdit.getText())){
                        constrasenaLayout.setError(null);
                    }
                    return false;
                }
            });

        }catch(Throwable t){
            t.printStackTrace(System.out);
            Log.e("LOGUEO", "onCreate: ", t);
            Snackbar.make(llLogeo, R.string.sag_error_inesperado, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
        }

    }

    private boolean esContrasenaValida(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }


    //LOGEO FIREBASE
    private void signIn(String email, String password) {
        Log.d("SAGAMO", "signIn:" + email);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("SAGAMO", "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                setToken();

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 2500);
                dialogoCargaActividad.stopProgressAlertDialog();
                Snackbar.make(llLogeo, R.string.sag_sucess_operacion, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_sucessful)).show();

                 //Almacena el token
            } else {
                // If sign in fails, display a message to the user.
                Log.w("SAGAMO", "signInWithEmail:failure", task.getException());
                dialogoCargaActividad.stopProgressAlertDialog();
                Snackbar.make(llLogeo, R.string.sag_error_logueo, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();


            }

            // [START_EXCLUDE]
//            if (!task.isSuccessful()) {
//                Toast.makeText(MainActivity.this, "Logeo incorrecto", Toast.LENGTH_SHORT).show();
//            }

            }
        });
        // [END sign_in_with_email]
    }


    //Almacena en cache y obtiene token
    public void setToken(){
        mfirestore.collection("usuario")
        .whereEqualTo("correo", correo.getText().toString())
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        objUsuario = document.toObject(Usuario.class);
                    }
                    SharedPreferences preferences = getSharedPreferences("cache", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("usuario", objUsuario.getCedula());
                    editor.putString("tipo", objUsuario.getTipo());
                    Log.e("TIPO USUARIO", "onComplete: "+objUsuario.getTipo());
                    editor.commit();


                    Intent intent = new Intent(MainActivity.this, Menu.class);
                    startActivity(intent);
                    finish();

                    FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(!task.isSuccessful()){
                                Log.d("TOKEN", "onComplete: ", task.getException());
                                return;
                            }
                            token = task.getResult().getToken();
                            editarUsuario();
                            Log.d("TOKEN", "onComplete: "+token);
                        }
                    });

                }
            }
        });
    }

    //Actualiza Token en usuario y sus representados
    public void editarUsuario(){
        mfirestore.collection("usuario")
                .document(objUsuario.getCedula())
                .update("token", token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("USUARIO", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("USUARIO", "Error updating document", e);
                        dialogoCargaActividad.stopProgressAlertDialog();
                        Snackbar.make(llLogeo, R.string.sag_error_logueo, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
                    }
                });



        mfirestore.collection("estudiante")
        .whereEqualTo("representante", objUsuario.getCedula())
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        mfirestore.collection("estudiante")
                        .document(document.getId())
                        .update("token", token)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("ESTUDIANTE", "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ESTUDIANTE", "Error updating document", e);
                                dialogoCargaActividad.stopProgressAlertDialog();
                                Snackbar.make(llLogeo, R.string.sag_error_logueo, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
                            }
                        });
                    }
                }
            }
        });
    }

    //COMPRUEBA SI YA TIENE INICIADA LA SESION
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser!=null){
//            Log.d("SAGAMO", "usuario:" + currentUser.getEmail());
//            Intent intent = new Intent(MainActivity.this, Menu.class);
//            startActivity(intent);
//            finish();
//        }else {
//            Toast.makeText(MainActivity.this, "No tiene cuenta", Toast.LENGTH_SHORT).show();
//        }
//
//    }


}