package com.example.proyecto6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    //Attributes from view
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImagesRecyclerViewAdapter adapter;
    Context contex = this;
    TextView textView;

    //Attributes
    SingletonImages imagesInformationMain = SingletonImages.getInformation(); //Singleton

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(findViewById(R.id.linearLayout).getContext());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);
        //Item divider in recycler view
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), 1);
        recyclerView.addItemDecoration(mDividerItemDecoration);
        textView = findViewById(R.id.textView);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(contex, activity_historial.class);
                //startActivity(intent);
            }
        });


        //URL from which to extract information
        URL url = null;
        try
        {
            url = new URL("https://esahubble.org/images/");
        }
        catch (MalformedURLException e)
        {
            Toast.makeText(this, "Invalid URL1", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //Check url content
        if (url != null)
        {
            //Task to extract information from the url
            GetImagesTask getImagesTask = new GetImagesTask();
            getImagesTask.execute(url);
        }
        else
        {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
        }
    }


    //Task that saves image id and title in a map
    public class GetImagesTask extends AsyncTask <URL, Void, Map<String,String>>
    {
        @Override
        protected Map<String, String> doInBackground(URL... urls)
        {
            HttpURLConnection connection = null; //Connection
            Map <String, String> map = new HashMap<>(); //Map

            try
            {
                //Connection and stream reader
                connection = (HttpURLConnection) urls[0].openConnection();
                connection.setInstanceFollowRedirects(true);

                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK)
                {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
                    {
                        //Read line and when identifies id, puts id and title in the map
                        String line;

                        while ((line = reader.readLine()) != null)
                        {
                            if(line.contains("id: "))
                            {
                                //Cleans id and title
                                String id = line;
                                id = id.replaceAll("id", "");
                                id = id.replaceAll(" ", "");
                                id = id.replaceAll("\\p{Punct}", "");


                                line = reader.readLine();
                                String title = line;
                                title = title.replaceAll("        title: ", "");
                                title = title.replaceAll("\\p{Punct}", "");

                                map.put(id, title);
                            }
                        }

                        return map;
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Snackbar.make(findViewById(R.id.linearLayout), "Unable to connect", Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                Snackbar.make(findViewById(R.id.linearLayout), "Unable to connect", Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally
            {
                connection.disconnect(); // close the HttpURLConnection
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> map) {
            //Information in singleton
            imagesInformationMain.setImageInformation(map);

            //Adapter in recycler view
            adapter = new ImagesRecyclerViewAdapter( imagesInformationMain.getInformationMap());
            recyclerView.setAdapter(adapter);



        }
    }
}