package com.example.practica1.receptores;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.PendingIntent.getActivity;
import static com.example.practica1.MainActivity.LISTA_CONTACTOS;
import static com.example.practica1.MainActivity.tvRegistros;

public class IncomingCallsReceiver extends BroadcastReceiver {

    /*
    * Por alguna razón que desconozco, cuando recibe una llamada, lee la llamada anterior, que se mostrará en la interfaz, y en caso
    * de ser la primera que recibe, no muestra nada, sino que muestra la primera llamada cuando recibe la segunda, muestra la segunda cuando recibe la
    * tercera, y así consecutivamente.
    *
    * Detecta si es un contacto el número que llama si el número agregado y el que toma de la llamada son estrictamente iguales,
    * de forma que si el contacto tiene un número agregado con espacios, lo percibirá como desconocido.
    *
    * */

    String format = "yyyy; MM; dd; HH; mm; ss";
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat(format);
    String now = df.format(calendar.getTime());

    Context context;

    String numero;
    String guardarEnHistorial;
    String guardarEnLlamadas;
    String persona;

    public static boolean SWITCH = false;


    @Override
    public void onReceive(Context context, Intent intent) {

        ContentResolver resolver = context.getContentResolver();

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {

            numero = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(numero));

            Cursor c = resolver.query(uri, new String[]{
                    ContactsContract.PhoneLookup.STARRED}, null, null, null);
                persona = "Desconocido";

            //Usamos una hebra para comparar el numero con el de los contactos y escribir en los archivos
            Thread hebra = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < LISTA_CONTACTOS.size(); i++) {
                        if (LISTA_CONTACTOS.get(i).getNumTelf().equals(numero)) {
                            persona = LISTA_CONTACTOS.get(i).getNombre();
                        }
                    }
                    guardarEnHistorial = now + "; " + numero + "; " + persona;
                    guardarEnLlamadas = persona + "; " + now + "; " + numero;

                    escribeLlamada(guardarEnLlamadas, context);
                    escribeHistorial(guardarEnHistorial, context);

                }
            };

            hebra.start();
            leerLlamadas(context);

        }

    }

        //Escribir en llamadas.csv(Almacenamiento Externo)

    private boolean escribeLlamada(String string, Context context) {

        boolean result = true;
        File f = new File(context.getExternalFilesDir(null), "llamadas.csv");
        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);
            fw.write(string + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }
        //Leer en llamadas.csv(Almacenamiento Externo)
    public void leerLlamadas(Context context) {
        File f = new File(context.getExternalFilesDir(null), "llamadas.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuilder texto = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                texto.append(linea);
                texto.append("\n");
            }
            tvRegistros.setText(texto);
            br.close();
        } catch (IOException e) {
        }
    }
        //Escribir en historial.csv (Almacenamiento interno)

    private boolean escribeHistorial(String string, Context context) {

        boolean result = true;
        File f = new File(context.getFilesDir(), "historial.csv");
        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);

            fw.write(string + "\n\n");

            fw.flush();
            fw.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

        //Leer historial.csv (Almacenamiento interno)

    public void leerHistorial(Context context) {
        File f = new File(context.getFilesDir(), "historial.csv");
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
        } catch (IOException e) {
        }
    }


}