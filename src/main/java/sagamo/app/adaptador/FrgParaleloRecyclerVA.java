package sagamo.app.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.List;


import sagamo.app.R;
import sagamo.app.clases.Clase;

public class FrgParaleloRecyclerVA
        extends RecyclerView.Adapter<FrgParaleloRecyclerVA.clViewHolder>
        {
    private List<Clase> listaClase;
    private int lastSelectedPosition = -1;

    public FrgParaleloRecyclerVA(List<Clase> listaObj) {
        this.listaClase = listaObj;
    }

    @NonNull
    @Override
    public FrgParaleloRecyclerVA.clViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sag_recycler_paralelos, parent, false);
        return new FrgParaleloRecyclerVA.clViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull clViewHolder holder, int position) {
        if(listaClase != null &&  position < listaClase.size()){
            Clase objClase = listaClase.get(position);
            holder.txvParalelo.setText(objClase.getParalelo());
            holder.txvMateria.setText(objClase.getMateria());
            holder.txvIdParalelo.setText(objClase.getIdparalelo().toString());
        }
        holder.rbPersona.setChecked(lastSelectedPosition == position);
    }

    public Clase getItem(int position){
        return listaClase.get(position);
    }

    @Override
    public int getItemCount() {
        return listaClase.size();
    }

    public class clViewHolder extends RecyclerView.ViewHolder{
        public TextView txvParalelo;
        public TextView txvMateria;
        public TextView txvIdParalelo;
        public MaterialRadioButton rbPersona;

        public clViewHolder(@NonNull View itemView) {
            super(itemView);
            txvParalelo = itemView.findViewById(R.id.txvRcvParalelo);
            txvMateria = itemView.findViewById(R.id.txvRcvMateria);
            txvIdParalelo = itemView.findViewById(R.id.txvRcvId);
            rbPersona = itemView.findViewById(R.id.rb_select_paralelo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
                }
            });

            rbPersona.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
