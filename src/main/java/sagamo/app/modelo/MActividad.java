package sagamo.app.modelo;



import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import sagamo.app.clases.Clase;

public class MActividad  {
    private FirebaseFirestore mfirestore = FirebaseFirestore.getInstance();
    Clase clase = new Clase();
    List<Clase> lista = new ArrayList<>();

//    public List<Clase> funcion (){
//
//
//        List<Clase> aux;
//
//         readData(new FirestoreCallback() {
//            @Override
//            public List<Clase> onCallBack(List<Clase> list) {
//                Log.d("SAGAMO", list.toString());
//                return list;
//            }
//        });
//
//        System.out.println("LISTA F: "+lista);
//    }




    private List<Clase> readData(final FirestoreCallback firestoreCallback){
        mfirestore.collection("clase")
            .whereEqualTo("usuario", "0602")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Clase objClase = document.toObject(Clase.class);
                            lista.add(objClase);
                        }
                        firestoreCallback.onCallBack(lista);


                    }
                    else{Log.d("SAGAMO", "Error: "+task.getException());}
                }
            });

        return lista;
    }

    private interface FirestoreCallback{
        List<Clase> onCallBack(List<Clase> list);
    }

}
