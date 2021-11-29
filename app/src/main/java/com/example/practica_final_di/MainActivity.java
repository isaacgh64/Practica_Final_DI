package com.example.practica_final_di;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //Botones que vamos a usar para permitir la navegación entre datos
    Button buttonMiBD, buttonTV, buttonSeries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Los buscamosa en nuestro activity Main
        buttonMiBD=findViewById(R.id.buttonMiBD);
        buttonSeries=findViewById(R.id.buttonSeries);
        buttonTV=findViewById(R.id.buttonTV);
        //Escuchamos los botones y que nos lleven a diferentes activitys para poder realizar las funciones
        buttonMiBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MiBD.class);
                startActivity(intent);
            }
        });
        buttonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        buttonSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}