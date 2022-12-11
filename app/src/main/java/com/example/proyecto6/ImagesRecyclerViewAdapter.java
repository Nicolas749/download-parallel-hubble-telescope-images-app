package com.example.proyecto6;

import static android.app.PendingIntent.getActivity;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesRecyclerViewAdapter extends RecyclerView.Adapter <ImagesRecyclerViewAdapter.ImageRecyclerViewHolder>
{
    //Attribute

    private final List <Map.Entry<String, String>> data;

    private Map<String, Bitmap> cache = new HashMap<>(); //Cache
    SingletonImages imagesInformationMain = SingletonImages.getInformation();

    //Constructor
    public ImagesRecyclerViewAdapter(List <Map.Entry<String, String>> data)
    {
        this.data=data;
    }

    @NonNull
    @Override
    public ImageRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //Layout from recycler view recyclerviewitem_layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewitem_layout,parent,false);
        ImageRecyclerViewHolder imageRecyclerViewHolder = new ImageRecyclerViewHolder(view);

        return imageRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerViewHolder holder, int position)
    {
        //Put name and url in TextViews
        Map.Entry <String,String > imageinfo = data.get(position);

        holder.nameTextView.setText(String.valueOf(position+1)+". " + imageinfo.getValue());
        holder.urlTextView.setText("https://esahubble.org/images/" + imageinfo.getKey());

        //Image in ImageView if it has to download or obtain it from the cache
        if (cache.containsKey(imageinfo.getKey()))
        {
            holder.imageThumb.setImageBitmap(cache.get(imageinfo.getKey()));

        }
        else
        {
            new LoadThumbImageTask(holder.imageThumb).execute(imageinfo.getKey());
        }

        //Open second activity when an item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(holder.itemView.getContext(), ImageActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, imageinfo.getKey());
                intent.putExtra("name",imageinfo.getValue());
                holder.itemView.getContext().startActivity(intent);


/*
                Intent intent2 = new Intent(holder.itemView.getContext(), historial.class);
                intent2.putExtra(Intent.EXTRA_TEXT, imageinfo.getKey());
                intent2.putExtra("name",imageinfo.getValue());
                intent2.putExtra("map", imageinfo);
                holder.itemView.getContext().startActivity(intent);*/
            }
        });

        //Open an implicit intent to open url when LongClicked
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {

                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();

                alertDialog.setTitle("Recycler Opciones");

                alertDialog.setMessage("Que accion desea realizar");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Borrar Item", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        remove(position);

                        Toast toast = Toast.makeText(view.getContext(), "Borrado Exitosamente",
                                Toast.LENGTH_SHORT);
                        toast.show();

                    } });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Compartir", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            holder.imageThumb.setDrawingCacheEnabled(true);


                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            Bitmap bitmap = ((BitmapDrawable)holder.imageThumb.getDrawable()).getBitmap();
                            Uri i = Uri.parse(MediaStore.Images.Media.insertImage(holder.itemView.getContext().getContentResolver(),bitmap,null,null));
                            holder.itemView.getContext().grantUriPermission("com.example.proyecto6",i,Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, i);

                            holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Compartir Imagen Con"));

                        }catch (Exception e){
                            Toast toast = Toast.makeText(view.getContext(), "No se ha podido compartir la imagen",
                                    Toast.LENGTH_SHORT);
                            toast.show();


                        }


                    }});

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancelar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        Toast toast = Toast.makeText(view.getContext(), "No se realizo ninguna accion",
                                Toast.LENGTH_SHORT);
                        toast.show();



                    }});
                alertDialog.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return data.size();
    } //Size

    public static class ImageRecyclerViewHolder extends RecyclerView.ViewHolder //Inner class for each view or item in the recycler view
    {
        //Attributes from view
        TextView nameTextView;
        TextView urlTextView;
        ImageView imageThumb;

        public ImageRecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            urlTextView = itemView.findViewById(R.id.url);
            imageThumb = itemView.findViewById(R.id.imageThumb);
        }

    }

    private class LoadThumbImageTask extends AsyncTask<String, Void, Bitmap>
    {
        private ImageView imageThumbView; // ImageView of thumb image

        // Constructor
        public LoadThumbImageTask(ImageView imageView) {
            this.imageThumbView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings)
        {
            Bitmap bitmap = null; //Bitmap
            HttpURLConnection connection = null; //Connection

            try
            {
                //URL of thumb image
                String stringURL= "https://cdn.spacetelescope.org/archives/images/thumb300y/"+strings[0]+".jpg";

                URL url = new URL(stringURL);

                // open an HttpURLConnection, get its InputStream
                // and download the image
                connection = (HttpURLConnection) url.openConnection();

                try (InputStream inputStream = connection.getInputStream())
                {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    cache.put(strings[0], bitmap);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                connection.disconnect(); // close the HttpURLConnection
            }

            return bitmap;
        }

        // set image in ImageView
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            imageThumbView.setImageBitmap(bitmap);
        }
    }
    public void remove(int pos){
        data.remove(pos);
        notifyItemChanged(pos);
        notifyItemRangeChanged(pos,data.size());
    }
}
