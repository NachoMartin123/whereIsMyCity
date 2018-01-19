package com.example.nacho.actividad3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private CameraUpdate mCamera;
    String[] latitudes,longitudes ,ciudades, aficion;
    Boolean[] posVisitadas;
    int puntuacion =0;
    int tam = 0;
    TextToSpeech tts;
    int ronda=0;

    String resultadoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // para que no se gire


        inicializarLiga(getIntent().getExtras().getString("liga"));

        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        nuevaRonda(mMap);
        mMap.animateCamera(mCamera);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    private void nuevaRonda(GoogleMap nMap){
        mMap.clear();//remove all markers
        // Add a marker in Sydney and move the camera
        final int pos = getPositionNotVisited();
        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latitudes[pos]), Double.valueOf(longitudes[pos]))).title(ciudades[pos]));
        //resultadoActual = aficion[pos];
        ((TextView) findViewById(R.id.txtAficion)).setText(aficion[pos].toUpperCase()+" ronda: "+ronda);//poner nombre de aficion
        ((ImageButton) findViewById(R.id.btnAficion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reproducir audio aficion
                String toSpeak = aficion[pos];
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        int pos2 = getDistinctRandomPos(pos, pos);
        int pos3 = getDistinctRandomPos(pos, pos2);
        Log.d("posicion visitada: "+pos, "posicion otra2: "+pos3);
        //poner otro 2 markers
        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latitudes[pos2]), Double.valueOf(longitudes[pos2]))).title(ciudades[pos2]));
        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latitudes[pos3]), Double.valueOf(longitudes[pos3]))).title(ciudades[pos3]));
        mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(40.071346, -99.18677), 3);//centro de USA

        asignarBotones(pos, pos2, pos3);


        posVisitadas[pos]=true;
        ronda++;
    }


    //asigna valores de texto a los botones de forma aleatoria
    private void asignarBotones(final int pos, final int pos2, final int pos3){
        Random rand = new Random();
        int n = rand.nextInt(3); // Gives n such that 0 <= n < tam
        int n2, n3;
        do{
            n2 = rand.nextInt(3);
        }while(n==n2);
        do{
            n3 = rand.nextInt(3);
        }while(n3==n2 || n3==n);

        //asignar textos en los 3 botones
        asignarPosicionBoton(n, pos, true);
        asignarPosicionBoton(n2, pos2 , false);
        asignarPosicionBoton(n3, pos3,  false);
        final Integer[] ordenados = ordenMenorMayor(n, n2, n3, pos, pos2, pos3);

        //asignar audio
        ((ImageButton) findViewById(R.id.btnOpciones)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                // reproducir audio opciones de botones;
                tts.speak(ciudades[ordenados[0]], TextToSpeech.QUEUE_ADD, null);
                tts.speak(ciudades[ordenados[1]], TextToSpeech.QUEUE_ADD, null);
                tts.speak(ciudades[ordenados[2]], TextToSpeech.QUEUE_ADD, null);

            }
        });
    }

    private Integer[] ordenMenorMayor(int n, int n2, int n3, int pos, int pos2, int pos3){
        Integer[] orden = new Integer[3];
        if(n < n2 && n < n3) {
            orden[0] = pos;
            if(n2<n3) {
                orden[1] = pos2;
                orden[2] = pos3;
            }else{
                orden[1] = pos3;
                orden[2] = pos2;
            }
        }else if(n2 < n && n2 <n3 ){
            orden[0] = pos2;
            if(n<n3){
                orden[1] = pos;
                orden[2] = pos3;
            }else{
                orden[1] = pos3;
                orden[2] = pos;
            }
        }else{
            orden[0] = pos3;
            if(n<n2){
                orden[1] = pos;
                orden[2] = pos2;
            }else{
                orden[1] = pos2;
                orden[2] = pos;
            }
        }
        return orden;
    }

    /**
     *
     * @param n, 0, 1 o 2, para asignar valores a los 3 botones de respuesta
     * @param posicion, para obtener la informacion de los arrays
     * @param correcto
     */
    private void asignarPosicionBoton(int n, final int posicion, final boolean correcto){
        if(n==0){
            ((Button) findViewById(R.id.btn1)).setText(ciudades[posicion]);
            ((Button) findViewById(R.id.btn1)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(correcto) {
                        puntuacion += 20;
                        Toast.makeText(MapsActivity.this, "CORRECTO", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MapsActivity.this, "RESPUESTA INCORRECTA ", Toast.LENGTH_SHORT).show();
                    }
                    siguienteRonda();
                }
            });
        }else if(n==1){
            ((Button) findViewById(R.id.btn2)).setText(ciudades[posicion]);
            ((Button) findViewById(R.id.btn2)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(correcto) {
                        puntuacion += 20;
                        Toast.makeText(MapsActivity.this, "CORRECTO", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MapsActivity.this, "RESPUESTA INCORRECTA ", Toast.LENGTH_SHORT).show();
                    }
                    siguienteRonda();

                }
            });

        }else if(n==2){
            ((Button) findViewById(R.id.btn3)).setText(ciudades[posicion]);
            ((Button) findViewById(R.id.btn3)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(correcto) {
                        puntuacion += 20;
                        Toast.makeText(MapsActivity.this, "CORRECTO", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MapsActivity.this, "RESPUESTA INCORRECTA ", Toast.LENGTH_SHORT).show();
                    }
                    siguienteRonda();
                }
            });
        }
    }


    private void siguienteRonda(){
        if(estaTodoVisitado()==false){
            nuevaRonda(mMap);
        }else{
            Intent intento = new Intent(MapsActivity.this, InicioActivity.class);
            intento.putExtra("puntos", String.valueOf(puntuacion));
            startActivity(intento);
        }

    }

    private boolean estaTodoVisitado(){
        for(int i=0;i<posVisitadas.length;i++){
            if(posVisitadas[i]==false)
                return false;
        }
        return true;
    }


    private int getPositionNotVisited(){
        Random rand = new Random();
        int n = rand.nextInt(tam); // Gives n such that 0 <= n < tam
        do{
            n = rand.nextInt(tam);
        }while(posVisitadas[n]==true);
        return n;
    }

    private int getDistinctRandomPos(int pos, int pos2){
        Random rand = new Random();
        int n = rand.nextInt(tam); // Gives n such that 0 <= n < tam
        do{
            n = rand.nextInt(tam);
        }while(pos ==n || pos2==n|| ciudades[pos2]==ciudades[n] || ciudades[pos]==ciudades[n]);
        return n;
    }





    private void inicializarLiga(String nombreLiga){
        int contador =0;
        if(nombreLiga.toUpperCase().equals("NBA")) {
            tam = 30;
            latitudes = new String[tam];
            longitudes = new String[tam];
            ciudades = new String[tam];
            aficion = new String[tam];
            posVisitadas = new Boolean[tam];
        }
        else if(nombreLiga.toUpperCase().equals("NFL"))
            ;


        try
        {
            InputStream fraw=null;
            if(nombreLiga.toUpperCase().equals("NBA"))
                fraw = getResources().openRawResource(R.raw.nba);
            else if(nombreLiga.toUpperCase().equals("NFL"))
                fraw = getResources().openRawResource(R.raw.nfl);

            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));

            String linea;
            while((linea = brin.readLine()) != null) {
                String[] trozos = linea.split(";");
                latitudes[contador] = trozos[0];
                longitudes[contador] = trozos[1];
                ciudades[contador] = trozos[2];
                aficion[contador] = trozos[3];
                contador++;
            }
            fraw.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde recurso raw");
        }
        resetPosVisitadas();
    }

    private void resetPosVisitadas(){
        for(int i=0;i<tam;i++)
            posVisitadas[i]=false;
    }


    /*private void setMarker(LatLng position, String title, String info, float opacity,
                           float dimension1, float dimension2, int icon){
        mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(info)
                .alpha(opacity)
                .anchor(dimension1, dimension2)
                .icon(BitmapDescriptorFactory.fromResource(icon)));
    }*/
}


