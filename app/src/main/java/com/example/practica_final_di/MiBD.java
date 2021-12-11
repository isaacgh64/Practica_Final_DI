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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MiBD extends AppCompatActivity {

    Button buttonCSV, buttonJSON, buttonXML,buttonModificar,buttonInsertar,buttonBorrar;
    EditText editTextTitulo, editTextAnio, editTextCadena, editTextID,editTextNumero;
    ListView listView;
    ProgressDialog progressDialog;
    static final String SERVIDOR = "http://192.168.3.2";

    //ArrayList que usareamos para mostrar los datos más Bonitos
    ArrayList<String> ID=new ArrayList();
    ArrayList<String> Titulo=new ArrayList();
    ArrayList<String> Anio=new ArrayList();
    ArrayList<String> Cadena=new ArrayList();
    ArrayList<String> Temporadas=new ArrayList();

    AdaptadorParaSeries adaptadorParaSeries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_bd);
        //Botones que vamos a usar en nuestra aplicación
        buttonCSV=findViewById(R.id.buttonCSV);
        buttonJSON=findViewById(R.id.buttonJSON);
        buttonXML=findViewById(R.id.buttonXML);
        listView=findViewById(R.id.listView);
        buttonModificar=findViewById(R.id.buttonModificar);
        buttonInsertar=findViewById(R.id.buttonInsertar);
        buttonBorrar=findViewById(R.id.buttonBorrar);

        //EditText que vamos a usar en nuestra aplicacion
        editTextTitulo=findViewById(R.id.editTextTitulo);
        editTextCadena=findViewById(R.id.editTextCadena);
        editTextAnio=findViewById(R.id.editTextAnio);
        editTextID=findViewById(R.id.editTextID);
        editTextNumero=findViewById(R.id.editTextNumero);

        //Botón que nos lee por CSV
        buttonCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescargarCSV descargarCSV = new DescargarCSV();
                descargarCSV.execute("/PF/listadoCSV.php");

            }
        });


        //Botón que nos lee los datos por XML
        buttonXML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescargarXML descargarXML = new DescargarXML();
                descargarXML.execute("/PF/listadoXML.php");
            }
        });
        //Butón que nos inserta los datos en nuestra BD por POST
        buttonInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo=editTextTitulo.getText().toString().trim();
                String anio=editTextAnio.getText().toString().trim();
                String cadena=editTextCadena.getText().toString().trim();
                String numero=editTextNumero.getText().toString().trim();
                InsertarPOST insertarPOST = new InsertarPOST (titulo,anio,cadena,numero);
                insertarPOST.execute("/PF/insertarPOST.php");
            }
        });
        //Botón que nos modifica los datos en nuestra BD
        buttonModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=editTextID.getText().toString();
                String titulo=editTextTitulo.getText().toString().trim();
                String anio=editTextAnio.getText().toString().trim();
                String cadena=editTextCadena.getText().toString().trim();
                String numero=editTextNumero.getText().toString().trim();
                Modificar modificar = new Modificar (id,titulo,anio,cadena,numero);
                modificar.execute("/PF/modificar.php?id="+id+"&nombre="+titulo+"&anio="+anio+"&cadena="+cadena+"&n_temporadas="+numero);
            }
        });
        //Botón que nos borra un dato de nuestra BD
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=editTextID.getText().toString();
                Borrar borrar = new Borrar (id);
                borrar.execute("/PF/borrarGET.php?id="+id);
            }
        });

        //Hacemos que al pulsar el ListView nos lo muestre en los editText
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               editTextID.setText(ID.get(i));
               editTextTitulo.setText(Titulo.get(i));
               editTextAnio.setText(Anio.get(i));
               editTextCadena.setText(Cadena.get(i));
               editTextNumero.setText(Temporadas.get(i));
            }
        });

    }
    //Función que me rellena el ListView
    void RellenarListView(){
        adaptadorParaSeries = new AdaptadorParaSeries(this,R.layout.adaptador,ID);
        listView.setAdapter(adaptadorParaSeries);
    }
    //Clase que usaremos para borrar los datos
    private class Borrar extends AsyncTask<String, Void, Void> {
        String id;
        public Borrar(String id){
            this.id=id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiBD.this);
            progressDialog.setTitle(getString(R.string.BorrarD));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getProgress() + 99);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(SERVIDOR + script);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    id= URLEncoder.encode(id,"UTF-8");

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String salida="";
                    String linea="";
                    while((linea=br.readLine())!=null){
                        salida+=linea+"\n";
                    }
                    br.close();

                    System.out.println(salida);
                } else {
                    Toast.makeText(MiBD.this, getString(R.string.NoConectar), Toast.LENGTH_SHORT).show();
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
    //Clase que usaremos para poder Modificar
    private class Modificar extends AsyncTask<String, Void, Void> {
        String id,titulo,anio,cadena,numero;
        public Modificar(String id,String titulo, String anio, String cadena,String numero){
            this.id=id;
            this.titulo=titulo;
            this.anio=anio;
            this.cadena=cadena;
            this.numero=numero;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiBD.this);
            progressDialog.setTitle(getString(R.string.Actualizar));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getProgress() + 99);

        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(SERVIDOR + script);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    id= URLEncoder.encode(id,"UTF-8");
                    titulo= URLEncoder.encode(titulo,"UTF-8");
                    anio= URLEncoder.encode(anio,"UTF-8");
                    cadena= URLEncoder.encode(cadena,"UTF-8");
                    numero=URLEncoder.encode(numero,"UTF-8");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String salida="";
                    String linea="";
                    while((linea=br.readLine())!=null){
                        salida+=linea+"\n";
                    }
                    br.close();

                    System.out.println(salida);
                } else {
                    Toast.makeText(MiBD.this,getString(R.string.NoConectar), Toast.LENGTH_SHORT).show();
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
    //Clase que usaremos para poder meter los Datos por POST
    private class InsertarPOST extends AsyncTask<String, Void, Void> {
        String titulo,anio,cadena,numero;
        public InsertarPOST(String titulo, String anio, String cadena,String numero){
            this.titulo=titulo;
            this.anio=anio;
            this.cadena=cadena;
            this.numero=numero;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiBD.this);
            progressDialog.setTitle(getString(R.string.Subiendo));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getProgress() + 99);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(SERVIDOR + script);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                PrintStream ps= new PrintStream(httpURLConnection.getOutputStream());

                ps.print("nombre="+titulo);
                ps.print("&anio="+anio);
                ps.print("&cadena="+cadena);
                ps.print("&n_temporadas="+numero);
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String salida="";
                    String linea="";
                    while((linea=br.readLine())!=null){
                        salida+=linea+"\n";
                    }
                    br.close();
                    ps.close();
                    System.out.println(salida);
                } else {
                    Toast.makeText(MiBD.this,getString(R.string.NoConectar), Toast.LENGTH_SHORT).show();
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
    //Clase que usaremos para poder leer nuestros datos por CSV y mostrarlos en nuestro ListView
    private class DescargarCSV extends AsyncTask<String, Void, Void> {
        String todo = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiBD.this);
            progressDialog.setTitle(getString(R.string.DescargarD));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ArrayAdapter<String> adapter;

            String[] lineas = todo.split("\n");
            for (String linea : lineas) {
                String[] campos = linea.split(";");
                ID.add(campos[0]);
                Titulo.add(campos[1]);
                Anio.add(campos[2]);
                Cadena.add(campos[3]);
                Temporadas.add(campos[4]);

            }
            RellenarListView();
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getProgress() + 99);

        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(SERVIDOR + script);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    String linea = "";
                    while ((linea = br.readLine()) != null) {
                        todo += linea + "\n";
                        Thread.sleep(100);
                        publishProgress();
                    }
                    br.close();
                    inputStream.close();
                } else {
                    Toast.makeText(MiBD.this, getString(R.string.NoConectar), Toast.LENGTH_SHORT).show();
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
    //Clase que usaremos para leer nuestros datos con JSON
    private class DescargarJSON extends AsyncTask<String, Void, Void> {
        String todo = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiBD.this);
            progressDialog.setTitle(getString(R.string.DescargarD));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ArrayAdapter<String> adapter;
            List<String> list = new ArrayList<String>();
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(todo).getAsJsonArray();
            String[] lineas = todo.split("\n");
            for (JsonElement elemento : jsonArray) {
                JsonObject objeto = elemento.getAsJsonObject();
                String dato = " \t " + objeto.get("id").getAsString();
                dato += " \t " + objeto.get("Nombre").getAsString();
                dato += " \t " + objeto.get("Ano_Estreno").getAsString();
                dato += " \t " + objeto.get("Cadena").getAsString();
                dato += " \t " + objeto.get("Numero_Temporadas").getAsString();
                list.add(dato);
            }
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getProgress() + 99);

        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(SERVIDOR + script);
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
                    Toast.makeText(MiBD.this, getString(R.string.NoConectar), Toast.LENGTH_SHORT).show();
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
    //Clase que nos lee los datos por XML
    private class DescargarXML extends AsyncTask<String, Void, Void> {
        String todo="" ;
        ArrayAdapter<String> adapter;
        List<String> list;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiBD.this);
            progressDialog.setTitle(getString(R.string.DescargarD));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getProgress() + 99);

        }

        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(SERVIDOR + script);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                list= new ArrayList<String>();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(new URL(url.toString()).openStream());
                    Element raiz = doc.getDocumentElement();
                    NodeList hijos = raiz.getChildNodes();

                    for (int i = 0; i < hijos.getLength(); i++) {
                        Node nodo = hijos.item(i);

                        if (nodo instanceof Element) {
                            NodeList nietos = nodo.getChildNodes();
                            String dato;
                            for (int j = 0; j < nietos.getLength(); j++) {
                                todo+=  nietos.item(j).getTextContent();
                            }
                            list.add(todo);
                            todo="";
                        }

                    }
                } else {
                    Toast.makeText(MiBD.this, getString(R.string.NoConectar), Toast.LENGTH_SHORT).show();
                }
                Thread.sleep(2000);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //Clase con la que adaptaremos nuestras preguntas para que se vean mejor
    private class AdaptadorParaSeries extends ArrayAdapter<String> {

        public AdaptadorParaSeries(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
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
            View mifila = inflater.inflate(R.layout.adaptador, padre, false);

            TextView id = mifila.findViewById(R.id.textViewID);
            id.setText(ID.get(posicion));

            TextView titulo = mifila.findViewById(R.id.textViewTitulo);
            titulo.setText(Titulo.get(posicion));

            TextView anio = mifila.findViewById(R.id.textViewAnio);
            anio.setText(Anio.get(posicion));

            TextView cadena = mifila.findViewById(R.id.textViewCadena);
            cadena.setText(Cadena.get(posicion));

            TextView temporada = mifila.findViewById(R.id.textViewTemporadas);
            temporada.setText(Temporadas.get(posicion));


            return mifila;
        }
    }

}