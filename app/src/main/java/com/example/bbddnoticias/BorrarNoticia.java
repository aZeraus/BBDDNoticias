package com.example.bbddnoticias;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class BorrarNoticia extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{
    // Instanciamos los objetos.
    private ListView lvLista;
    // Creamos un ArrayList de tipo Noticias para guardar la lista de noticias.
    final ArrayList<Noticias> listaNoticias = new ArrayList<>();
    // Creamos un ArrayList de tipo Noticias para la lista de noticias a borrar.
    // final ArrayList<Noticias> listaBorrar = new ArrayList<>();
    // Creamos un ArrayAdapter para el ListView.
    ArrayAdapter adaptador;
    private int idInfo;

    // Creamos el componente ProgressDialog que muestra una ventana de carga por si tarda demasiado la petición.
    ProgressDialog progreso;

    // Creamos la cola de peticiones RequestQueue de Volley para la conexión con los WebServices.
    RequestQueue requestQueue;
    // Creamos la petición StringRequest que permite solicitar un recurso con formato de texto plano.
    StringRequest stringRequest;

    // Creamos el objeto JsonObjectRequest para obtener la información de WebServices en formato JSON.
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_noticia);

        lvLista = (ListView) findViewById(R.id.lvLista);
        // Establecemos un Listener en el ListView para obtener la id de la noticia seleccionada.
        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtenemos la posición en en arrayList de la noticia seleccionada.
                Noticias noticiasInfo = listaNoticias.get(position);
                // Obtenemos la id de la noticia seleccionada.
                idInfo = noticiasInfo.getId();
                // Toast.makeText(getApplicationContext(), "Id de la noticia a borrar:\n" + idInfo, Toast.LENGTH_LONG).show();

                mostrarAlerta();
            }
        });
        // Instanciamos la cola de peticiones RequestQueue de Volley.
        requestQueue = Volley.newRequestQueue(this);

        // Llamamos al método para que obtenga los datos con los que cargar el listado.
        webServiceListar();
    }

    /**
     * Método que crea una ventana de tipo Alerta.
     * Si se acepta se eliminará la noticia llamando al método borrarWebService.
     */
    public void mostrarAlerta() {
        // Creamos el objeto AlertDialog.Builder
        new AlertDialog.Builder(this)
                // Asignamos las propiedades que se mostrarán.
                .setTitle("Aviso")
                .setMessage("¿Está seguro de borrar la noticia "+idInfo+"?")
                .setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acciones a realizar si se acepta.
                                webServiceBorrar();
                                // Lanzamos la pantalla principal.
                                // Usamos getBaseContext() para que tome el contexto de la Activity y no el de la alerta.
                                Intent volverPrincipal = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(volverPrincipal);
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acciones a realizar si no se acepta.
                                // Cancelamos la ventana de alerta.
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "No se ha borrado ninguna noticia.",
                                        Toast.LENGTH_SHORT).show();
                                Log.i("Borrado cancelado", String.valueOf(idInfo));
                                // Lanzamos la pantalla principal.
                                Intent volverPrincipal = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(volverPrincipal);
                            }
                        }).show();
    }

    /**
     * Método para mostrar el menú.
     */
    public boolean onCreateOptionsMenu(Menu menuApp) {
        getMenuInflater().inflate(R.menu.menu, menuApp);
        // Ocultar elemento Eliminar del menú.
        menuApp.findItem(R.id.mnuEliminar).setVisible(false);
        menuApp.findItem(R.id.mnuEliminarM).setVisible(false);

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

        // Enviamos a Volley la url para que procese la información.
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,this,this);
        // Añadimos la petición jsonObjectRequest a la cola requestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    /**
     * Método que se procesa si el envío a Volley fue correcto.
     */
    public void onResponse(JSONObject response) {
        String Titulo;
        // Creamos un objeto de la clase Noticias.
        Noticias noticia = null;
        // Usamos un objeto JSONArray que recibe como parámetro el JSON obtenido de la conexión Volley.
        JSONArray json = response.optJSONArray("noticia");
        try {
            // Bucle para recorrer el array JSON con los resultados obtenidos y asignar sus valores a instancias de la clase Noticias.
            for(int i=0;i<json.length();i++) {
                noticia = new Noticias();
                // Convertimos los datos del JSON en objetos.
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);

                // Extraemos los atributos del JSON y rellenamos los atributos de los objetos noticia.
                noticia.setId(jsonObject.optInt("id"));
                // noticia.setTitulo(jsonObject.optString("Titulo"));

                try{ // Debido a que la BBDD externa nos envía los caracteres con una codificación incorrecta.
                    Titulo = new String(jsonObject.optString("Titulo").getBytes("ISO-8859-1"),"UTF-8");
                    noticia.setTitulo(Titulo);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                noticia.setEnlace(jsonObject.optString("Enlace"));
                noticia.setFecha(jsonObject.optString("Fecha"));
                noticia.setLeido(jsonObject.optBoolean("Leido"));
                noticia.setFavorita(jsonObject.optBoolean("Favorita"));
                noticia.setFuente(jsonObject.optBoolean("Fuente"));
                listaNoticias.add(noticia);
                // listaBorrar.add(noticia.getId());
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
     * Método que se procesa si el envío a Volley tuvo errores o el JSON no tiene contendo.
     */
    public void onErrorResponse(VolleyError error) {
        // Ocultamos el ProgressDialog.
        progreso.hide();
        Toast.makeText(getApplicationContext(), "No se pudo realizar la consulta.\nLa base de datos está vacía\no se produjo un error.", Toast.LENGTH_LONG).show();
        // Toast.makeText(getApplicationContext(), "No se pudo realizar la consulta.\nonErrorResponse\n" + error.toString(), Toast.LENGTH_LONG).show();
        // Mostramos el error por la consola.
        Log.i("ERROR", error.toString());
    }

    /**
     * Método que carga el WebService para borrar los datos de la Noticia seleccionada en la BBDD remota.
     */
    private void webServiceBorrar() {
        // Creamos el ProgressDialog.
        progreso=new ProgressDialog(this);
        progreso.setMessage("Cargando...");
        // Mostramos el ProgressDialog.
        progreso.show();

        String ip=getString(R.string.Url);
        String url = ip + "BorrarNoticia.php?id="+idInfo;
		// Instanciamos el objeto stringRequest.
        stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("Eliminado")){
                    Toast.makeText(getApplicationContext(),"Noticia " + idInfo +
                            " eliminada con éxito.",Toast.LENGTH_SHORT).show();
                    Log.d("Noticia borrada", String.valueOf(idInfo));
                }else{
                    if (response.trim().equalsIgnoreCase("DatoNoRecibido")){
                        Toast.makeText(getApplicationContext(),"No se recibieron los datos.",
                                Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }else{
                        Toast.makeText(getApplicationContext(),"No se ha eliminado el registro.",
                                Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"No se ha podido conectar",
                        Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        });
        requestQueue.add(stringRequest);
    }
}