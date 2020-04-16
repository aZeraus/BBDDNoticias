package com.example.bbddnoticias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Se usa como adaptador para personalizar el ListView Lista de Noticias.
 */
public class AdaptadorListView extends BaseAdapter {
    private Context contexto;
    private ArrayList<Noticias> arrayListNoticias;

    /**
     * Método constructor de la clase.
     * @param context Contexto del adaptador
     * @param arrayList de tipo Noticias para almacenar las mismas.
     */
    public AdaptadorListView(Context context, ArrayList<Noticias> arrayList) {
        // Contexto de la aplicación.
        this.contexto = context;
        // Lista de datos a generar.
        this.arrayListNoticias = arrayList;
    }

    /**
     * Método que devuelve la cantidad de objetos que hay en la lista.
     * @return Tamaño del Adaptador.
     */
    @Override
    public int getCount() {
        return arrayListNoticias.size();
    }

    /**
     * Devuelve el objeto del ArrayList que se encuentre en la posición que se le indica con position.
     * @param position dentro del ArrayList del objeto.
     * @return el objeto.
     */
    @Override
    public Object getItem(int position) {
        return arrayListNoticias.get(position);
    }

    /**
     * Devuelve el id de la posición que le pasamos.
     * @param position dentro del ArrayList del objeto.
     * @return la id de la noticia.
     */
    @Override
    public long getItemId(int position) {
        //return position;
        return arrayListNoticias.get(position).getId();
    }

    /**
     * Método con el que devolvemos la vista de cada uno de los ítems (objetos).
     * Se ejecuta en cada secuencia en la cual se quiera cargar el contenido de cada ítem del ListView.
     * Permite referenciar al objeto inflate que se utiliza para vincular el Layout con los ítems del ListView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int idNoticia;
        String tituloNoticia;
        String tituloNoticiaCor;
        // Si no está creadada la vista la crea.
        /*if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_list_view,null);
        }*/
        View vista = convertView;
        LayoutInflater layoutInflater = LayoutInflater.from(contexto);
        /* Obtenemos la referencia al objeto inflate que permite poder inyectar el Layout definido
         en item_list_view y colocarlo dentro de la vista del ListView para  así lograr la
          referencia a la vista en si. */

        vista = layoutInflater.inflate(R.layout.item_list_view,null);

        // Instanciamos los componentes.
        ImageView imageView = (ImageView) vista.findViewById(R.id.imgFuente);
        TextView tvNumero = (TextView) vista.findViewById(R.id.tvNumero);
        TextView tvTitulo = (TextView) vista.findViewById(R.id.tvTitulo);

        // Obtenemos los valores de los componentes obtenidos del arrayList en función de la posición.
        idNoticia = (Integer) arrayListNoticias.get(position).getId();
        tituloNoticia = (String)arrayListNoticias.get(position).getTitulo();
        tituloNoticiaCor = tituloNoticia;
        // Acortamos la longitud del título a un máximo de 44 caracteres y le añadimos ... para indicar que el título continúa.
        if(tituloNoticia.length()>46){
            tituloNoticiaCor = tituloNoticia.substring(0,43) + " ...";
        }

        // Establecemos los valores en los TextView.
        tvNumero.setText(String.valueOf(idNoticia));
        tvTitulo.setText(tituloNoticiaCor);

        // Establecemos la imagen en función del valor del campo Fuente.
        if (arrayListNoticias.get(position).isFuente()){
            imageView.setImageResource(R.drawable.vale);
        } else imageView.setImageResource(R.drawable.no_vale);

        // Devolvemos la vista.
        //return convertView;
        return vista;
    }
}
