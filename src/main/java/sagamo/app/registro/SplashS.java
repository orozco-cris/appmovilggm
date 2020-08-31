package sagamo.app.registro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sagamo.app.MainActivity;
import sagamo.app.R;
import sagamo.app.inicio.Menu;

public class SplashS extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sag_splash_2);
        relativeLayout = findViewById(R.id.rl_splash);
        try{
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            if(currentUser!=null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("SAGAMO", "usuario:" + currentUser.getEmail());
                        Intent intent = new Intent(SplashS.this, Menu.class);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);

            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashS.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);
            }
        }catch(Throwable t){
            Snackbar.make(relativeLayout, R.string.sag_error_inesperado, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
            Log.e("SPLASH", "onCreate: ", t);
            t.printStackTrace(System.out);
        }
    }

}