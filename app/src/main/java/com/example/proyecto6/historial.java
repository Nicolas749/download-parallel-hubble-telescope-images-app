package com.example.proyecto6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class historial extends AppCompatActivity {

    ListView listH;
    Context con = this;
    SingletonImages imagesInformationMain = SingletonImages.getInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);


        listH = findViewById(R.id.listH);


        String name = (getIntent().getStringExtra("name"));
        String id = getIntent().getStringExtra(Intent.EXTRA_TEXT);




        if((imagesInformationMain.getNames().equals(name))|| imagesInformationMain.getNames().contains(name)){
            Log.i("Ya en historial" ,name);
        }else{
            imagesInformationMain.saveName(name);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(con,android.R.layout.simple_list_item_1,imagesInformationMain.getNames());
        listH.setAdapter(adapter);

        listH.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://esahubble.org/images/" + id));
                startActivity(webIntent);

            }
        });


    }


}