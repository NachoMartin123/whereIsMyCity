package com.example.nacho.actividad3;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // para que no se gire

        inicializarSpinner();
        inicializarPuntuacion();
        ((Button)findViewById(R.id.btnEmpezar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString().equals("NBA"))
                    empezar();
                else
                    Toast.makeText(InicioActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();

            }
        });

        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.logo));
    }

    private void inicializarSpinner(){
        String[] spinnerArray =  {"NBA","NFL"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);


    }

    public void empezar(){
        Intent intento = new Intent(InicioActivity.this, MapsActivity.class);
        intento.putExtra("liga", ((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString());
        startActivity(intento);
    }

    private void inicializarPuntuacion(){
        try {
            ((TextView) findViewById(R.id.txtPuntos)).setText("Puntuación máxima: "+getIntent().getExtras().getString("puntos"));
        }catch(Exception e) {
            ((TextView) findViewById(R.id.txtPuntos)).setText("Puntuación máxima: 0");
        }
    }
}
