package com.example.practica_final_di;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SeriesFav extends AppCompatActivity {

    ListView listView;
    ProgressDialog progressDialog;
    ArrayList<String> Titulo=new ArrayList();
    ArrayList<Integer> Foto=new ArrayList();
    ArrayList<String> Descripcion=new ArrayList();
    ArrayList<String> Popularidad=new ArrayList();
    ArrayList<String> FechaSalida=new ArrayList();
    ArrayList<String> Ejecutar=new ArrayList();
    public int posicion=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_fav);
        listView=findViewById(R.id.listView);
        Ejecutar.add("https://api.themoviedb.org/3/search/tv?api_key=612e085c9c82a64498c74fac7cfbadd4&language=es-es&query=La+Casa+de+Papel&page=1");
        Ejecutar.add("https://api.themoviedb.org/3/search/tv?api_key=612e085c9c82a64498c74fac7cfbadd4&language=es-es&query=Breaking+Bad&page=1");
        DescargarJSON descargarJSON = new DescargarJSON();
        descargarJSON.execute(Ejecutar.get(posicion));
        posicion++;

        Foto.add(R.drawable.papel);
        Foto.add(R.drawable.brk);



    }
    void RellenarListView(){
        AdaptadorParaSeries1 adaptadorParaSeries1= new AdaptadorParaSeries1(this,R.layout.series,Titulo);
        listView.setAdapter(adaptadorParaSeries1);
        if(posicion<Ejecutar.size()){
            DescargarJSON descargarJSON = new DescargarJSON();
            descargarJSON.execute(Ejecutar.get(posicion));
            posicion++;
        }

    }
    //Clase que usaremos para leer nuestros datos con JSON
    private class DescargarJSON extends AsyncTask<String, Void, Void> {
        String todo = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SeriesFav.this);
            progressDialog.setTitle("Descargando datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            Boolean flag=true;
            super.onPostExecute(unused);
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(todo).getAsJsonObject().getAsJsonArray("results");
            for (JsonElement elemento : jsonArray) {
                if(flag){
                    JsonObject objeto = elemento.getAsJsonObject();
                    Titulo.add(objeto.get("original_name").getAsString());
                    Descripcion.add(objeto.get("overview").getAsString());
                    Popularidad.add(objeto.get("popularity").getAsString());
                    FechaSalida.add(objeto.get("first_air_date").getAsString());
                    flag=false;
                }
            }
            RellenarListView();
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getSecondaryProgress() + 10);

        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(script);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo += linea;
                        Thread.sleep(100);
                        publishProgress();
                    }
                    br.close();
                    inputStream.close();
                } else {
                    Toast.makeText(SeriesFav.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
                }
                Thread.sleep(2000);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //Clase que usaremos para poder adaptar al Layout y que se vea más bonito
    private class AdaptadorParaSeries1 extends ArrayAdapter<String> {

        public AdaptadorParaSeries1(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
            super(context, resource, objects);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return rellenarFila(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return rellenarFila(position, convertView, parent);
        }

        public View rellenarFila(int posicion, View view, ViewGroup padre) {

            LayoutInflater inflater = getLayoutInflater();
            View mifila = inflater.inflate(R.layout.series, padre, false);

            TextView titulo = mifila.findViewById(R.id.textViewTituloS);
            titulo.setText(Titulo.get(posicion));

            ImageView logo = mifila.findViewById(R.id.imageView);
            logo.setImageResource(Foto.get(posicion));

            return mifila;
        }


    }
}