package com.example.bbddnoticias;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ListadoDeNoticiasTexto extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{
    // Instanciamos los objetos
    private ListView lvLista;
    // Creamos un ArrayList de tipo Noticias para guardar la lista de noticias.
    final ArrayList<Noticias> listaNoticias = new ArrayList<>();
    // Creamos un ArrayAdapter para el ListView.
    ArrayAdapter adaptador;
    private int idInfo;
    String titulo, enlace, fecha;
    boolean leido, favorita, fuente;

    // Componente ProgressDialog que muestra una ventana de carga por si tarda demasiado la petición.
    ProgressDialog progreso;

    // Creamos la cola de peticiones RequestQueue de Volley para la conexión con los WebServices.
    RequestQueue requestQueue;

    // Creamos el objeto JsonObjectRequest para obtener la información de WebServices en formato JSON.
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_de_noticias);

        lvLista = (ListView) findViewById(R.id.lvLista);
        // Establecemos un Listener en el ListView para obtener la id de la noticia seleccionada.
        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtenemos la posición en en arrayList de la noticia seleccionada.
                Noticias noticiasInfo = listaNoticias.get(position);
                // Obtenemos los datos de la noticia seleccionada.
                idInfo = noticiasInfo.getId();
                titulo = noticiasInfo.getTitulo();
                enlace = noticiasInfo.getEnlace();
                fecha = noticiasInfo.getFecha();
                leido = noticiasInfo.isLeido();
                favorita = noticiasInfo.isFavorita();
                fuente = noticiasInfo.isFuente();

                // Cadena para el Log.
                String datosNoticia = "id:"+id+" titulo:"+titulo+" fecha:"+fecha+" enlace:"+enlace+" leido:"+leido+" favorita:"+favorita+" fuente:"+fuente;
                Log.d("Noticia", datosNoticia);
                // Toast.makeText(getApplicationContext(), "Id de la noticia:\n" + idInfo, Toast.LENGTH_LONG).show();

                // Creamos el Intent para lanzar la pantalla de FichaNoticia.
                Intent infoNoticia = new Intent(view.getContext(), FichaNoticia.class);
                // Enviamos las variables con la información a la actividad FichaNoticia.
                infoNoticia.putExtra("id", idInfo);
                infoNoticia.putExtra("titulo", titulo);
                infoNoticia.putExtra("enlace", enlace);
                infoNoticia.putExtra("fecha", fecha);
                infoNoticia.putExtra("leido", leido);
                infoNoticia.putExtra("favorita", favorita);
                infoNoticia.putExtra("fuente", fuente);
                startActivity(infoNoticia);
            }
        });

        // Instanciamos la cola de peticiones RequestQueue de Volley.
        requestQueue = Volley.newRequestQueue(this);
        // Llamamos al método para que obtenga los datos con los que cargar el listado.
        webServiceListar();
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
     * Método que carga el WebService para obtener los datos de la BBDD remota.
     */
    private void webServiceListar(){
        // Creamos el ProgressDialog.
        progreso =  new ProgressDialog(this);
        progreso.setMessage("Cargando...");
        // Mostramos el ProgressDialog.
        progreso.show();

        String ip=getString(R.string.Url);
        String url = ip + "ListarNoticias.php";

        //Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        // Enviamos a Volley la url para que procese la información
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,this,this);

        // Aumentamos el tiempo del los reintentos para que no de error en la búsqueda.
        /*
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        */
        // new DefaultRetryPolicy(500000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        // Nos conecta el jsonObjectRequest con el requestQueue
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    /**
     * Método que se procesa si el envío a Volley fue correcto.
     */
    public void onResponse(JSONObject response) {

        int leidoJi, favoritaJi, fuenteJi;
        boolean leidoJ, favoritaJ, fuenteJ;
        String Titulo;

        // Creamos un objeto de la clase noticia.
        Noticias noticia = null;
        // Usamos un objeto JSONArray que recibe como parámetro el json obtenido de la conexión.
        JSONArray json = response.optJSONArray("noticia");
        // if (json.length()>0){ // Si hay datos recibidos.
        // Bucle para recorrer todos los resultados de la búsqueda.
        try {
            // Vamos a recorrer el array json para asignar sus valores.
            for(int i=0;i<json.length();i++) {
                noticia = new Noticias();

                // Convertimos los datos del JSON en objetos
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);

                // Extraemos los atributos del JSON y rellenamos los atributos de los objetos noticia.
                noticia.setId(jsonObject.optInt("id"));
                // noticia.setTitulo(jsonObject.optString("Titulo"));

                try{// Debido a que la BBDD externa nos envía los carateres con una codificación incorrecta.
                    Titulo = new String(jsonObject.optString("Titulo").getBytes("ISO-8859-1"),"UTF-8");
                    noticia.setTitulo(Titulo);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                noticia.setEnlace(jsonObject.optString("Enlace"));
                noticia.setFecha(jsonObject.optString("Fecha"));
                // Leemos valores int (0 o 1) y los convertimos en boolean(true o false).
                leidoJi = jsonObject.optInt("Leido");
                leidoJ = (leidoJi==1)?true:false;
                favoritaJi = jsonObject.optInt("Favorita");
                favoritaJ = (favoritaJi==1)?true:false;
                fuenteJi = jsonObject.optInt("Fuente");
                fuenteJ = (fuenteJi==1)?true:false;
                noticia.setLeido(leidoJ);
                noticia.setFavorita(favoritaJ);
                noticia.setFuente(fuenteJ);
                // Cadena para el Log.
                String datosRecibidos = " leido:" + leidoJ +
                        " favorita:" + favoritaJ +
                        " fuente:" + fuenteJ;
                Log.i("Noticia", datosRecibidos);

                listaNoticias.add(noticia);
            }
            // Ocultamos el ProgressDialog.
            progreso.hide();

            // Cargamos el adaptador con el ArrayList listaNoticias.
            adaptador = new ArrayAdapter<Noticias>(this, android.R.layout.simple_list_item_1, listaNoticias);
            // Establecemos el ListView con el adaptador.
            lvLista.setAdapter(adaptador);

            //ListAdapter adaptadorLa = new ArrayAdapter<Noticias>(this, android.R.layout.simple_list_item_1, listaNoticias);
            //lvLista.setAdapter(adaptadorLa);
            Toast.makeText(getApplicationContext(), "Listado con éxito", Toast.LENGTH_SHORT).show();
        } catch (JSONException error) {
            // Ocultamos el ProgressDialog.
            progreso.hide();
            // Mostramos el error.
            Toast.makeText(getApplicationContext(), "No se pudo realizar la consulta." +
                    "\nLa base de datos está vacía." +
                    "\nPrimero debe cargar los datos." +
                    "\nO se produjo un error.", Toast.LENGTH_LONG).show();
            // Toast.makeText(getApplicationContext(), "No se pudo realizar la consulta.\nonErrorResponse\n" + error.toString(), Toast.LENGTH_LONG).show();
            // error.printStackTrace();
            // Mostramos el error por la consola.
            Log.d("ERROR", error.toString());
        }
    }

    @Override
    /**
     * Método que se procesa si el envío a Volley tuvo errores.
     */
    public void onErrorResponse(VolleyError error) {
        // Ocultamos el ProgressDialog.
        progreso.hide();
        Toast.makeText(getApplicationContext(), "No se pudo realizar la consulta.\nLa base de datos está vacía\no se produjo un error.", Toast.LENGTH_LONG).show();
        // Toast.makeText(getApplicationContext(), "No se pudo realizar la consulta.\nonErrorResponse\n" + error.toString(), Toast.LENGTH_LONG).show();
        // Mostramos el error por la consola.
        Log.i("ERROR", error.toString());
    }
}
