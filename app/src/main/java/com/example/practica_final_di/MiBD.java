package com.example.practica_final_di;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
        //Botón que lee los datos por JSON
        buttonJSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescargarJSON descargarJSON = new DescargarJSON();
                descargarJSON.execute("/PF/listadoJSON.php");
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
            progressDialog.setTitle("Borrando datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
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
            progressDialog.setProgress(progressDialog.getProgress() + 10);
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
                    Toast.makeText(MiBD.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
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
            progressDialog.setTitle("Actualizando datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
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
            progressDialog.setProgress(progressDialog.getProgress() + 10);

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
                    Toast.makeText(MiBD.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
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
            progressDialog.setTitle("Subiendo datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
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
            progressDialog.setProgress(progressDialog.getProgress() + 10);
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
                    Toast.makeText(MiBD.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
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
            progressDialog.setTitle("Descargando datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            ArrayAdapter<String> adapter;
            List<String> list = new ArrayList<String>();
            String[] lineas = todo.split("\n");
            for (String linea : lineas) {
                String[] campos = linea.split(";");
                String dato =" "+ campos[0]+"\t";
                dato += campos[1]+"\t";
                dato += campos[2]+"\t";
                dato += campos[3]+"\t";
                dato += campos[4];
                list.add(dato);
            }
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(progressDialog.getProgress() + 10);

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
                    Toast.makeText(MiBD.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
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
            progressDialog.setTitle("Descargando datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
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
            progressDialog.setProgress(progressDialog.getVolumeControlStream() + 10);

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
                    Toast.makeText(MiBD.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
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
            progressDialog.setTitle("Descargando datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
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
            progressDialog.setProgress(progressDialog.getVolumeControlStream() + 10);

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
                    Toast.makeText(MiBD.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
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

}