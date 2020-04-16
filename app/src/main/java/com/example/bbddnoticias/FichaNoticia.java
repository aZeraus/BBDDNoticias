package com.example.bbddnoticias;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class FichaNoticia extends AppCompatActivity {

    // Instanciamos los objetos
    private TextView tvDatoTitulo, tvDatoFecha, tvDatoEnlace;
    private CheckBox cbLeido, cbFavorita;
    private RadioGroup rgFiable;
    private RadioButton rbFiable, rbNoFiable;
    private Button btnVolver;

    // Creamos el componente ProgressDialog que muestra una ventana de carga por si tarda demasiado la petición.
    ProgressDialog progreso;

    // Creamos la cola de peticiones RequestQueue de Volley para la conexión con los WebServices.
    RequestQueue requestQueue;
    // Creamos la petición StringRequest que permite solicitar un recurso con formato de texto plano.
    StringRequest stringRequest;

    int id, leidoInt, favoritaInt, fuenteInt;
    String titulo, fecha, enlace;
    boolean leido, favorita, fuente;
    private boolean marcaFuente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_noticia);

        // Recogemos las variables con los datos de la actividad anterior.
        Intent intentRecibirID = getIntent();
        id = (int)intentRecibirID.getExtras().get("id");
        titulo = (String) intentRecibirID.getExtras().get("titulo");
        fecha = (String) intentRecibirID.getExtras().get("fecha");
        enlace = (String) intentRecibirID.getExtras().get("enlace");
        leido = (boolean)intentRecibirID.getExtras().get("leido");
        favorita = (boolean)intentRecibirID.getExtras().get("favorita");
        fuente = (boolean)intentRecibirID.getExtras().get("fuente");
        // Cadena para el Log.
        String datosRecibidos = "id:" + id +
                " titulo:" + titulo +
                " fecha:" + fecha +
                " enlace:"+ enlace +
                " leido:" + leido +
                " favorita:"+ favorita +
                " fuente:" + fuente;
        Log.d("Noticia", datosRecibidos);
        // Obtenemos las referencias de los TextView los Radios y el botón.
        tvDatoTitulo = (TextView) findViewById(R.id.tvDatoTitulo);
        tvDatoFecha = (TextView) findViewById(R.id.tvDatoFecha);
        tvDatoEnlace = (TextView) findViewById(R.id.tvDatoEnlace);
        cbLeido = (CheckBox) findViewById(R.id.cbLeido);
        cbFavorita = (CheckBox) findViewById(R.id.cbFavorita);

        rgFiable = (RadioGroup) findViewById(R.id.rgFiable);
        rbFiable = (RadioButton) findViewById(R.id.rbFiable);
        rbNoFiable = (RadioButton) findViewById(R.id.rbNoFiable);

        btnVolver = (Button) findViewById(R.id.btnVolver);

        // Volcamos el contenido proveniente de la actividad anterior.
        tvDatoTitulo.setText(titulo);
        tvDatoFecha.setText(fecha);
        tvDatoEnlace.setText(enlace);
        if (leido){
            cbLeido.setChecked(true);
        } else cbLeido.setChecked(false);
        if (favorita){
            cbFavorita.setChecked(true);
        } else cbFavorita.setChecked(false);
        if (fuente){
            rbFiable.setChecked(true);
            rbNoFiable.setChecked(false);
        } else {
            rbFiable.setChecked(false);
            rbNoFiable.setChecked(true);
        }
        // Creamos el Listener para el botón Volver.
        btnVolver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                volver();
            }
        });

        // Creamos el Listener para el enlace.
        tvDatoEnlace.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
				// Creamos un objeto Uri para que nos abra en enlace en un navegador.
                Uri uri = Uri.parse(enlace);
				// Crea el Intent para que nos abra en enlace en un navegador.
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
					// Lanza el Intent.
                    startActivity(intent);
                    Log.i("Página abierta", enlace);

                }catch(ActivityNotFoundException anfe){
                    Log.e("Página NO abierta", enlace);
                }
            }
        });

        // Instanciamos la cola de peticiones RequestQueue de Volley.
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Método para mostrar el menú.
     */
    public boolean onCreateOptionsMenu(Menu menuApp) {
        getMenuInflater().inflate(R.menu.menu, menuApp);
        return true;
    }

    /**
     * Método que gestiona las acciones de los items del menú.
     */
    public boolean onOptionsItemSelected(MenuItem itemSeleccion) {
        switch (itemSeleccion.getItemId()) {
            // Si se pulsa Insertar en el menú.
            case R.id.mnuInsertar:
            case R.id.mnuInsertarM:
                Intent intentInsertar = new Intent(this, InsertarNoticia.class);
                startActivity(intentInsertar);
                return true;
            // Si se pulsa Eliminar en el menú.
            case R.id.mnuEliminar:
            case R.id.mnuEliminarM:
                Intent intentEliminar = new Intent(this, BorrarNoticia.class);
                startActivity(intentEliminar);
                return true;
            // Si se pulsa Página Principal en el menú o la flecha hacia atrás en la Barra de herramientas.
            case R.id.mnuPrincipal:
            case R.id.mnuPrincipalM:
                Intent intentPrincipal = new Intent(this, MainActivity.class);
                startActivity(intentPrincipal);
                return true;
            default:
                return super.onOptionsItemSelected(itemSeleccion);
        }
    }

    /**
     * Método que comprueba que alguno de los RadioButton esté seleccionado y si es así nos devuelve
     * su estado.
     */
    private boolean compruebaFiable() {

        // Si queremos comprobar si alguno de los RadioButton de un RadioGroup ha sido seleccionado:
        // Si el indice de seleccionados vale -1 es que no se ha seleccionado ninguno.
        if (rgFiable.getCheckedRadioButtonId() == -1) {
            marcaFuente = false;
        } else if (rbFiable.isChecked()){// Si no asignamos el resultado en función del seleccionado.
            rbFiable.setChecked(true);
            rbNoFiable.setChecked(false);
            marcaFuente = true;
        } else if (rbNoFiable.isChecked()){
            rbFiable.setChecked(false);
            rbNoFiable.setChecked(true);
            marcaFuente = true;
        }
        return marcaFuente;
    }

    /**
     * Método para que al pulsar el botón btnVolver obtenga los valores de selección que tienen los
     * CheckBox y RadioButton.
     * Llama al método compruebaFiable para obtener el estado de los RadioButton y si hay alguno
     * seleccionado llama al método webServiceActualizar y si no lanza un mensaje indicándolo.
     */
    public void volver() {
        // Llamamos al método para comprobar que la fuente esté seleccionada.
        if (compruebaFiable()) {
            // Cambiamos los valores booleanos por int para pasarlos a PHP
            if (cbLeido.isChecked()) leidoInt = 1; else leidoInt = 0;
            if (cbFavorita.isChecked()) favoritaInt = 1; else favoritaInt = 0;
            if (rbFiable.isChecked()) fuenteInt = 1; else fuenteInt = 0;
            // Cadena para el Log.
            String leidoFavFu = "Leido: " + leidoInt +
                    " Favorita: " + favoritaInt +
                    " Fuente: " + fuenteInt;
            Log.d("leidoFavFu ", leidoFavFu);

            webServiceActualizar();
        } else { // Si no lanza un mensaje.
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.errFuenteNoSeleccionada),
                    Toast.LENGTH_LONG).show();
            marcaFuente = false;
        }
    }

    /**
     * Método que carga el WebService para actualizar las posibles modificaciones en Leído, Favorita
     * y Fiable de la noticia mostrada en la BBDD remota.
     */
    private void webServiceActualizar() {
        // Creamos el ProgressDialog.
        progreso=new ProgressDialog(this);
        progreso.setMessage("Cargando...");
        // Mostramos el ProgressDialog.
        progreso.show();

        String ip=getString(R.string.Url);
        String url = ip + "ActualizaNoticia.php?id=" + id +
                "&leido=" + leidoInt +
                "&favorita=" + favoritaInt +
                "&fuente="+ fuenteInt;
        Log.d("URL ", url);

        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("Actualizado")){
                    Toast.makeText(getApplicationContext(),"La valoración de la noticia ha sido guardada.",Toast.LENGTH_SHORT).show();

                    // Volvemos a la pantalla principal.
                    Intent intentPrincipal = new Intent(getApplicationContext(), ListadoDeNoticias.class);
                    startActivity(intentPrincipal);
                }else{
                    if (response.trim().equalsIgnoreCase("DatoNoRecibido")){
                        Toast.makeText(getApplicationContext(),"No se recibieron los datos.",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA ",""+response);
                    }else{
                        Toast.makeText(getApplicationContext(),"No se ha actualizado la noticia.",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA ",""+response);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        });
        requestQueue.add(stringRequest);
    }
}
