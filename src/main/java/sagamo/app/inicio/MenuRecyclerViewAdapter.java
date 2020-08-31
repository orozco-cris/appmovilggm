package sagamo.app.inicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import sagamo.app.R;
import sagamo.app.clases.Notificacion;

import java.util.List;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.clViewHolder>
implements View.OnClickListener{

    private List<Notificacion> listaNot;
    private View.OnClickListener listener;


    public MenuRecyclerViewAdapter(List<Notificacion> lista){
        this.listaNot = lista;
    }


    @NonNull
    @Override
    public MenuRecyclerViewAdapter.clViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sag_rcv_notificaciones, parent, false);
        //notifyDataSetChanged();
        layoutView.setOnClickListener(this);
        return new MenuRecyclerViewAdapter.clViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull clViewHolder holder, int position) {
        if(listaNot != null && position < listaNot.size()){
            Notificacion objNotificacion = listaNot.get(position);

            holder.txvNombreAct.setText(objNotificacion.getNombre());
            holder.txvFechaPlazo.setText(objNotificacion.getEstudiante());
        }
    }

    @Override
    public int getItemCount() {
        return listaNot.size();
    }

    public void setOnClickListener(View.OnClickListener listenerM){
        this.listener = listenerM;
    }


    @Override
    public void onClick(View v) {
        if(listener!= null){
            listener.onClick(v);
        }
    }


    public class clViewHolder extends RecyclerView.ViewHolder{
        public TextView txvFechaPlazo;
        public TextView txvNombreAct;


        public clViewHolder(@NonNull View itemView) {
            super(itemView);

            txvNombreAct = itemView.findViewById(R.id.txv_rcv_nombre_act);
            txvFechaPlazo  = itemView.findViewById(R.id.txv_rcv_fechap_act);


        }
    }

}
