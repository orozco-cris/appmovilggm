package sagamo.app.adaptador;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sagamo.app.R;
import sagamo.app.clases.Estudiante;

public class FrgEstudianteRecyclerVA extends RecyclerView.Adapter<FrgEstudianteRecyclerVA.clViewHolderE> {
    private List<Estudiante> listaEstudiante;
    private Context context;
    private List<String> selectCedulas = new ArrayList<>();


    public FrgEstudianteRecyclerVA(List<Estudiante> listaEst){
        this.listaEstudiante = listaEst;
    }

    @NonNull
    @Override
    public FrgEstudianteRecyclerVA.clViewHolderE onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sag_recycler_estudiantes, parent, false);
        return new FrgEstudianteRecyclerVA.clViewHolderE(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull clViewHolderE holder, int position) {
        if (listaEstudiante != null && position < listaEstudiante.size()){
            Estudiante objEstudiante = listaEstudiante.get(position);
            holder.txvNombre.setText(objEstudiante.getNombre());
            holder.txvApellido.setText(objEstudiante.getApellido());
            String id = objEstudiante.getRepresentante();
            if (selectCedulas.contains(id)){
                    holder.cbEstudiante.setChecked(true);
            }
            else {
                    holder.cbEstudiante.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaEstudiante.size();
    }

    public Estudiante getItem(int position){
        return listaEstudiante.get(position);
    }

    public void setSelectCedulas(List<String> selectCedula){
        this.selectCedulas = selectCedula;
        notifyDataSetChanged();
    }


    public class clViewHolderE extends RecyclerView.ViewHolder{
        public TextView txvNombre;
        public TextView txvApellido;
        public CheckBox cbEstudiante;

        //FrameLayout rootView;


        public clViewHolderE(@NonNull View itemView) {
            super(itemView);

            txvNombre = itemView.findViewById(R.id.txv_rcv_nombre_est);
            txvApellido = itemView.findViewById(R.id.txv_rcv_apellido_est);
            cbEstudiante = itemView.findViewById(R.id.cb_select_estudiante);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cbEstudiante.isChecked()){
                        cbEstudiante.setChecked(false);

                    }else {
                        cbEstudiante.setChecked(true);

                    }
                }
            });

            //rootView = itemView.findViewById(R.id.cont_estudiantes);
        }
    }

}
