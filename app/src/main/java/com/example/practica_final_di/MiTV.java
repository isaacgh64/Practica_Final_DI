package com.example.practica_final_di;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MiTV extends AppCompatActivity {

    ListView listView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_tv);
        listView=findViewById(R.id.listView);

        DescargarXML descargarXML = new DescargarXML();
        descargarXML.execute("https://raw.githubusercontent.com/dracohe/CARLOS/master/guide_IPTV.xml");
    }
    //Clase que nos lee los datos por XML
    private class DescargarXML extends AsyncTask<String, Void, Void> {
        String todo="" ;
        ArrayAdapter<String> adapter;
        List<String> list;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiTV.this);
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
                url = new URL(script);
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
                                todo+=nietos.item(j).getTextContent();
                            }
                            list.add(todo);
                            todo="";
                        }

                    }
                } else {
                    Toast.makeText(MiTV.this, "No me pude conectar a la nube", Toast.LENGTH_SHORT).show();
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