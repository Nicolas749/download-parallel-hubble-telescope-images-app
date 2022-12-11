package com.example.proyecto6;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class AdaptadorHistorial extends RecyclerView.Adapter <AdaptadorHistorial.ViewHolder> {
    SingletonImages imagesInformationMain = SingletonImages.getInformation();
    private Map<String, Bitmap> cache = new HashMap<>();
    ArrayList<String> name = new ArrayList<>();
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageHist ;
        TextView textHist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageHist = (ImageView) itemView.findViewById(R.id.imageHist);
            textHist = itemView.findViewById(R.id.textHist);
        }
    }


    public ArrayList <Integer> intCards = new ArrayList<>();
    String tipo;

    public AdaptadorHistorial( ArrayList<String> name){
        this.name = name;
        //this.tipo = tipo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_historial,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        //holder.imageHist.setImageResource(intCards.get(i));
        holder.imageHist.setImageBitmap(cache.get(i));
        holder.textHist.setText(name.get(i));
    }

    @Override
    public int getItemCount() {
        return name.size();
    }
}

