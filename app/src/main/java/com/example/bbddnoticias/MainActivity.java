package com.example.bbddnoticias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    // Método para que al pulasr en la imagen nos lleve a la pantalla del Listado de Noticias.
    public void Listado(View view) {
        Intent listado = new Intent(this, ListadoDeNoticias.class);
        startActivity(listado);
    }

    /**
     * Método para mostrar el menú.
     */
    public boolean onCreateOptionsMenu(Menu menuApp) {
        getMenuInflater().inflate(R.menu.menu, menuApp);

        // Ocultar elemento Principal del menú.
        menuApp.findItem(R.id.mnuPrincipal).setVisible(false);
        menuApp.findItem(R.id.mnuPrincipalM).setVisible(false);
        //MenuItem mnuTextGroup = menu.findItem(R.id.groupPrincipal);
        //mnuTextGroup.setVisible(false);

        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu, menu);
        // Selecciona el ítem del menú principal.
        //MenuItem muestraMenuPrinc = menuApp.findItem(R.id.mnuPrincipal);
        // Oculta el ítem.
        //muestraMenuPrinc.setVisible(false);
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
}