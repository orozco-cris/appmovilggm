package sagamo.app.registro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import sagamo.app.MainActivity;
import sagamo.app.R;
import sagamo.app.clases.DialogoCargaActividad;
import sagamo.app.clases.Usuario;

public class Registro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;

    private TextInputEditText edtCorreo;
    private TextInputEditText edtContrasena;
    private TextInputLayout layContrasena;
    private TextInputEditText edtNombre;
    private TextInputEditText edtApellido;
    private TextInputEditText edtCedula;
    private TextInputEditText edtCelular;
    private MaterialButton btnSiguiente;
    private DialogoCargaActividad dialogoCargaActividad;
    private LinearLayout lyRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sag_registro);

        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mfirestore.setFirestoreSettings(settings);

        edtCorreo  = findViewById(R.id.correo_login);
        edtContrasena = findViewById(R.id.contrasena_login_edit);
        layContrasena = findViewById(R.id.contrasena_login_input);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtCedula = findViewById(R.id.edtCedula);
        edtCelular = findViewById(R.id.edtCelular);
        btnSiguiente = findViewById(R.id.siguiente_button);
        lyRegistro = findViewById(R.id.ly_registro);

       dialogoCargaActividad = new DialogoCargaActividad(Registro.this);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!esContrasenaValida(edtContrasena.getText())){
                    layContrasena.setError(getString(R.string.sag_error_contrasena));
                }
                else if(!edtNombre.getText().toString().equals("") && !edtApellido.getText().toString().equals("")
                        && !edtCedula.getText().toString().equals("") && !edtCorreo.getText().toString().equals("") && !edtCelular.getText().toString().equals("")){
                    layContrasena.setError(null);
                    createAccount(edtCorreo.getText().toString(), edtContrasena.getText().toString()); //Llama el logueo
                } else {
                    Snackbar.make(v, R.string.sag_error_campos, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_warning)).show();
                }
            }
        });

        edtContrasena.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(esContrasenaValida(edtContrasena.getText())){
                    layContrasena.setError(null);
                }

                return false;
            }
        });
    }

    private boolean esContrasenaValida(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }

    //METODOS DE LA CLASE
    //REGISTRAR USUARIO CON CORREO Y CONTRASEÑA
    private void createAccount(String email, String password) {
        lyRegistro = findViewById(R.id.ly_registro);

        dialogoCargaActividad.startProgressAlertDialog();
        Log.d("SAGAMO", "createAccount:" + email);
        // [START create_user_with_email]
        if(!email.equals("")){
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SAGAMO", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        dialogoCargaActividad.stopProgressAlertDialog();
                        //-------------
                        registrarBD();
                        //-------------
                        Snackbar.make(lyRegistro, "Usuario registrado correctamente", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_sucessful)).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Registro.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 2500);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SAGAMO", "createUserWithEmail:failure", task.getException());
                        dialogoCargaActividad.stopProgressAlertDialog();
                        Snackbar.make(lyRegistro, "Favor asegurése de ingresar información válida", Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
                    }
                }
                });
        }else {Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();}

        // [END create_user_with_email]
    }

    //ALMACENAR EL NUEVO USUARIO EN LA BD
    private void registrarBD(){
        System.out.println("LLEGO");
        String Nombre = edtNombre.getText().toString();
        String Apellido = edtApellido.getText().toString();
        String Celular = edtCelular.getText().toString();
        String Cedula = edtCedula.getText().toString();
        String Correo = edtCorreo.getText().toString();
        System.out.println("LLEGO 2");
        Usuario objUsuario = new Usuario(Nombre, Apellido, Celular, Cedula, Correo, "", "representante");
        //Usuario objUsuario = new Usuario(Nombre, Apellido, "", "", "", "", "");

        System.out.println("LLEGO "+objUsuario+ "Cedula: "+Cedula);
        mfirestore.collection("usuario").document(Cedula)
                .set(objUsuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        System.out.println("LLEGO SI");
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                        System.out.println("LLEGO NO");
                        mAuth.signOut();
                    }
                });
    }
}