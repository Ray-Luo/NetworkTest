package com.example.networktest;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity implements View.OnClickListener {

    public static final int SHOW_RESPONSE = 0;
    private Button sendRequest;
    private TextView responseText;
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_RESPONSE:
                    String response = (String)msg.obj;
                    responseText.setText(response);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendRequest = (Button)findViewById(R.id.send_request);
        responseText = (TextView)findViewById(R.id.response);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void View.OnClickListener(View v)
    {
        if(v.getId() == R.id.send_request)
            sendRequestWithHttpUrlConnection();
    }

    private void sendRequestWithHttpUrlConnection()
    {
        new Thread(new Runnable()
        {
            @Override
        public void run()
            {
                try
                {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://10.0.2.2/get_data.xml");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");
                        parseXMLWithPull(response);
                    }
                }catch (Exception e)
                {e.printStackTrace();}
                }
         }).start();

    }

    private void parseXMLWithPull(String xmlData)
    {
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while(eventType != xmlPullParser.getEventType())
            {
                String nodeName = xmlPullParser.getName();
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                    {
                        if("id".equals(nodeName))
                            id = xmlPullParser.nextText();
                        else if("name".equals(nodeName))
                            name = xmlPullParser.nextText();
                        else if("version".equals(nodeName))
                            version = xmlPullParser.nextText();
                        break;
                    }
                    case XmlPullParser.END_TAG:
                        if("app".equals(nodeName))
                        {
                            Log.d("MainActivity", "id is " + id);
                            Log.d("MainActivity", "name is " + name);
                            Log.d("MainActivity", "version is " + version);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();

            }
        }catch (Exception e)
        {e.printStackTrace();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
