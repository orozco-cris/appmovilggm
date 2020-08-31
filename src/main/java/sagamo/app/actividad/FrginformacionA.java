package sagamo.app.actividad;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import sagamo.app.R;
import sagamo.app.clases.DialogoCargaFragment;

public class FrginformacionA extends Fragment {
    private AutoCompleteTextView spiTipo;
    private TextInputEditText edtNombre;
    private TextInputEditText edtFecha;
    private TextInputEditText edtDescripcion;
    private Calendar myCalendar;
    private ArrayAdapter<String> adaptador;
    private String formatoFecha;
    private FloatingActionButton fabDatosActividad;
    private Date fechaActual;
    private DateFormat dateFormat;
    private String tipoA = "";
    private FirebaseFirestore mfirestore;
    private DialogoCargaFragment dialogoCargaFragment;

    public FrginformacionA() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.sag_frg_informacion_a, container, false);
        view.findViewById(R.id.linear_actividad).setBackgroundResource(R.drawable.sag_bg_actividad);

        //Asignaciones
        spiTipo = view.findViewById(R.id.spi_tipo);
        edtNombre = view.findViewById(R.id.tiet_nombre_actividad);
        edtFecha = view.findViewById(R.id.tiet_fecha_actividad);
        edtDescripcion = view.findViewById(R.id.tiet_descripcion_actividad);
        fabDatosActividad = view.findViewById(R.id.fab_to_list_paralelos);
        mfirestore = FirebaseFirestore.getInstance();
        dialogoCargaFragment = new DialogoCargaFragment(this);

        try{
            //SELECT
            ArrayList<String> listas = new ArrayList<>();
            listas.add("Tarea");
            listas.add("Lección");
            listas.add("Evento");
            listas.add("Otro");
            adaptador = new ArrayAdapter<String>(view.getContext(), R.layout.list_item, listas);
            spiTipo.setAdapter(adaptador);

            spiTipo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tipoA = parent.getItemAtPosition(position).toString();
                }
            });

            //CALENDARIO
            fechaActual = new Date();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("FEcha actuaL: "+dateFormat.format(fechaActual));


            myCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    formatoFecha = "yyyy-MM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(formatoFecha, Locale.ENGLISH);

                    edtFecha.setText(sdf.format(myCalendar.getTime()));
                }
            };

            edtFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(view.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            fabDatosActividad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!edtNombre.getText().toString().equals("") && !tipoA.equals("") && !edtFecha.getText().toString().equals("")){
                        Bundle result = new Bundle();
                        result.putString("nombre", edtNombre.getText().toString());
                        result.putString("tipo", tipoA);
                        result.putString("fecharealizado", dateFormat.format(fechaActual));
                        result.putString("fechaplazo", edtFecha.getText().toString());
                        result.putString("descripcion", edtDescripcion.getText().toString());
                        getParentFragmentManager().setFragmentResult("actividad", result);
                        Snackbar.make(v, R.string.sag_sucess_operacion, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_information)).show();
                    }else{
                        Snackbar.make(v, R.string.sag_error_campos, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.not_warning)).show();
                    }


                }
            });
        }catch(Throwable t){
            Log.e("MAIN", "onCreate: ", t);
            t.printStackTrace(System.out);
            Snackbar.make(view, R.string.sag_error_inesperado, Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.not_danger)).show();
        }
        return view;
    }
}