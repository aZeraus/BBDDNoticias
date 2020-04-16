package com.example.bbddnoticias;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Calendar;
import java.util.Locale;

public class InsertarNoticia extends AppCompatActivity{

    // Instanciamos los objetos.
    private EditText etTitInsertar, etEnlInsertar, etFechInsertar;
    private Button btnInsertar;

    // Guardamos el último año, mes y día del mes.
    private int ultimoAnio, ultimoMes, ultimoDiaDelMes;

    // Creamos el componente ProgressDialog que muestra una ventana de carga por si tarda demasiado la petición.
    ProgressDialog progreso;

    // Creamos la cola de peticiones RequestQueue de Volley para la conexión con los WebServices.
    RequestQueue requestQueue;

    // Creamos la petición StringRequest que permite solicitar un recurso con formato de texto plano.
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar_noticia);
        // Obtenemos las referencias de los EditText y el botón.
        etTitInsertar = (EditText) findViewById(R.id.etTitInsertar);
        etEnlInsertar = (EditText) findViewById(R.id.etEnlInsertar);
        etFechInsertar = (EditText) findViewById(R.id.etFechInsertar);
        btnInsertar = (Button) findViewById(R.id.btnInsertar);

        // Establecemos último año, mes y día con la fecha de hoy.
        final Calendar calendario = Calendar.getInstance();
        ultimoAnio = calendario.get(Calendar.YEAR);
        ultimoMes = calendario.get(Calendar.MONTH);
        ultimoDiaDelMes = calendario.get(Calendar.DAY_OF_MONTH);

        // Refrescamos la fecha en el EditText.
        refrescarFechaEnEditText();

        /**
         * Asociamos un evento de clic al EditText de la fecha.
         * Hace que el DatePickerDialog se muestre cuando se toque el EditText.
         */
        etFechInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            // Cuando se hace clic mostramos el DatePicker.
            public void onClick(View v) {
                // Le pasamos lo que haya en las variables globales de fecha, normalmente el día actual.
                DatePickerDialog dialogoFecha = new DatePickerDialog(InsertarNoticia.this,
                        listenerDeDatePicker, ultimoAnio, ultimoMes, ultimoDiaDelMes);
                //Mostramos el dialogo de la Fecha
                dialogoFecha.show();
            }
        });
        // Instanciamos la cola de peticiones RequestQueue de Volley.
        requestQueue = Volley.newRequestQueue(this);

        // Creamos el Listener para el botón Enviar.
        btnInsertar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Si los campos están rellenos y la URL es válida llamamos al WebService.
                if (etTitInsertar.getText().toString().trim().length()>0 &&
                        validaURL(etEnlInsertar.getText().toString()) &&
                        etFechInsertar.getText().toString().trim().length()>0){
                    webServiceInsertar();
                } else{
                    Toast.makeText(getApplicationContext(), "Los datos introducidos no son correctos.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Método para mostrar el menú.
     */
    public boolean onCreateOptionsMenu(Menu menuApp) {
        getMenuInflater().inflate(R.menu.menu, menuApp);
        // Ocultar elemento Eliminar del menú.
        menuApp.findItem(R.id.mnuInsertar).setVisible(false);
        menuApp.findItem(R.id.mnuInsertarM).setVisible(false);
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
            // Si se pulsa Página Principal en el menú.
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
     * Método para validar si es correcta la URL.
     * @param url que vamos a validar.
     */
    private boolean validaURL(String url) {
        boolean esURL = false;
        if (url.trim().length() > 0) {
            esURL = Patterns.WEB_URL.matcher(url).matches();
        }
        return esURL;
    }

    /**
     * Creamos un listener para el DatePickerDialog.
     * Cuando es llamado nos pasa la vista y el año, mes y día del mes que fueron seleccionados.
     * https://parzibyte.me/blog/2019/03/18/mostrar-datepicker-android-ejemplo-obtener-fecha/#Codigo_fuente_y_descarga_de_app
     */
    private DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anio, int mes, int diaDelMes) {
            // Actualizamos las vaiables globales de fecha.
            ultimoAnio = anio;
            ultimoMes = mes;
            ultimoDiaDelMes = diaDelMes;
            // Y refrescamos la fecha
            refrescarFechaEnEditText();
        }
    };

    /**
     * Método para refrescar la fecha en el EditText.
     */
    public void refrescarFechaEnEditText() {
        // Formateamos la fecha.
        String fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d",
                ultimoAnio, ultimoMes+1, ultimoDiaDelMes);

        // La incluímos en el editText.
        etFechInsertar.setText(fecha);
    }

    /**
     * Método que carga el WebService para insertar los datos en la BBDD remota.
     */
    private void webServiceInsertar() {
        // Creamos el ProgressDialog.
        progreso = new ProgressDialog(this);
        progreso.setMessage("Cargando...");
        // Mostramos el ProgressDialog.
        progreso.show();

        String ip=getString(R.string.Url);
        String url = ip + "InsertarNoticia.php?titulo=" + etTitInsertar.getText().toString() +
                "&enlace=" + etEnlInsertar.getText().toString() +
                "&fecha=" + etFechInsertar.getText().toString();
        // Sustituimos los espacios por %20
        url = url.replace(" ", "%20");

        //Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
        // Generamos  la petición de tipo String ya que solicita un recurso con formato de texto plano.
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.trim().equalsIgnoreCase("Insertada")) {
                    Toast.makeText(getApplicationContext(), "Noticia insertada con éxito.",
                            Toast.LENGTH_LONG).show();
                    Log.d("Noticia insertada", "Correcta");
                    // Volvemos a la pantalla principal.
                    Intent intentPrincipal = new Intent(getApplicationContext(), ListadoDeNoticias.class);
                    startActivity(intentPrincipal);
                } else {
                    if (response.trim().equalsIgnoreCase("DatoNoRecibido")) {
                        Toast.makeText(getApplicationContext(), "No se recibieron los datos.",
                                Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA ", "" + response);
                    } else {
                        Toast.makeText(getApplicationContext(), "Noticia no insertada.",
                                Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA ", "" + response);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se ha podido conectar",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Ocultamos el ProgressDialog.
        progreso.hide();
        // Añadimos la petición stringRequest a la cola requestQueue.
        requestQueue.add(stringRequest);
    }
}
