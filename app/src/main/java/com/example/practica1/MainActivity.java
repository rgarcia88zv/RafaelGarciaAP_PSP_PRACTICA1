package com.example.practica1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.practica1.Settings.SettingsActivity;
import com.example.practica1.receptores.IncomingCallsReceiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getName() + "xyzyx";

    private SharedPreferences sharedPreferences;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    String pref;



    public static TextView tvRegistros;


    private static final int PERMISO_VARIOS = 123;



   public static ArrayList<Contacto> LISTA_CONTACTOS = new ArrayList<>();
    String[] projection = new String[]{ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

    String selectionClause = ContactsContract.Data.MIMETYPE + "='" +
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND "
            + ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";

    String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRegistros = (TextView) findViewById(R.id.tvRegistros);

        init();

    }

    private void init() {
        listener = this;
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        };
        pref = sharedPreferences.getString("list","llamadas.csv");


        obtenerPermisos();
        comprobarPermisoContactos();

        tvRegistros.setText("");
        leerLlamadas();


    }

    private void obtenerPermisos() {

        int permisoContactos = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS);
        int permisoLog = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG);
        int permisoLlamadas = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);

        if (permisoContactos != PackageManager.PERMISSION_GRANTED || permisoLog != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE}, PERMISO_VARIOS);
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permisosConcedidos = 0;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                permisosConcedidos++;

            }
        }

        if (permisosConcedidos == grantResults.length) {
            obtenerContactos();
        } else {
            obtenerPermisos();
        }

    }

    private void obtenerContactos() {
        Cursor c = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selectionClause,
                null,
                sortOrder);

        while (c.moveToNext()) {
            int id = Integer.parseInt(c.getString(0));
            String nombre = c.getString(1);
            String numero = c.getString(2);

            Contacto contacto = new Contacto(id, nombre, numero);
            LISTA_CONTACTOS.add(contacto);

        }
        c.close();

    }

    private void comprobarPermisoContactos() {
        int result = PackageManager.PERMISSION_GRANTED;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            result = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        }
        if (result == PackageManager.PERMISSION_GRANTED) {
            obtenerContactos();
        }
    }

    public void leerLlamadas() {
        File f = new File(getExternalFilesDir(null),"llamadas.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuilder texto = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                texto.append(linea);
                texto.append('\n');
            }
            tvRegistros.setText(texto);
            br.close();
        } catch(IOException e) {
        }
    }

    public void leerHistorial() {
        File f = new File(getFilesDir(),"historial.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuilder texto = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                texto.append(linea);
                texto.append('\n');
            }
            tvRegistros.setText(tvRegistros.getText().toString() + texto);
            br.close();
        } catch(IOException e) {
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //ciclo de vida de la actividad
        int id = item.getItemId();
        switch (id) {
            case R.id.mnSettings:
                return viewSettingsActivity();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      if(pref.equals("llamadas.csv")){
            leerLlamadas();
        }else if(pref.equals("historial.csv")){
            leerHistorial();
        }

    }

    private boolean viewSettingsActivity() {
        //intención
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }


}