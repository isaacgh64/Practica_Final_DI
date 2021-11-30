package com.example.practica_final_di;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MiBD extends AppCompatActivity {

    Button buttonCSV, buttonJSON, buttonXML;
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
                dato += " \t " + objeto.get("Año_Estreno").getAsString();
                dato += " \t " + objeto.get("Cadena").getAsString();
                dato += " \t " + objeto.get("Número_Temporadas").getAsString();
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
            /*progressDialog = new ProgressDialog(MiBD.this);
            progressDialog.setTitle("Descargando datos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
            progressDialog.show();*/
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, list);
            listView.setAdapter(adapter);
            //progressDialog.dismiss();
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