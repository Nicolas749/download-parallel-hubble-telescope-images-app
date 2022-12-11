package com.example.proyecto6;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageActivity extends AppCompatActivity
{
    //Attributes of the view
    TextView imageName;
    ImageView imageLarge;
    ImageButton btnShare;
    Context con = this;

    //Singleton with information and cache2
    SingletonImages imagesInformationImage = SingletonImages.getInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Initialization of attributes from view
        imageName = findViewById(R.id.imageName);
        imageLarge = findViewById(R.id.imageLarge);
        btnShare = findViewById(R.id.btnShare);

        String loadingURL = "https://i.gifer.com/VAyR.gif";
        Uri uri = Uri.parse(loadingURL);


        //Obtenemos del Intent los datos necessarios para mostrar el activity
        imageName.setText(getIntent().getStringExtra("name"));
        String id = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        //descargamos o lo sacamos del cache
        if (imagesInformationImage.containsInCache(id)) {
            imageLarge.setImageBitmap(imagesInformationImage.getCache(id));
        }
        else {
            Glide.with(getApplicationContext()).load(uri).into(imageLarge);


            new LoadLargeImageTask(imageLarge).execute(id);

        }

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hisIn = new Intent(con,historial.class);

                hisIn.putExtra("name",imageName.getText());
                hisIn.putExtra("id",id);
                startActivity(hisIn);

            }
        });
    }

    private class LoadLargeImageTask extends AsyncTask<String, Void, Bitmap>
    {
        private ImageView imageLargeView; // ImageView for large image

        //Constructor
        public LoadLargeImageTask(ImageView imageView) {
            this.imageLargeView = imageView;
        }






        @Override
        protected Bitmap doInBackground(String... strings)
        {
            Bitmap bitmap = null;
            HttpURLConnection connection = null; //creamos la conexion a la pagina

            try {

                //URL para obtener la foto especifica [foto en buena calidad]
                String stringURL= "https://cdn.spacetelescope.org/archives/images/large/"+strings[0]+".jpg";

                URL url = new URL(stringURL);


                connection = (HttpURLConnection) url.openConnection();

                try (InputStream inputStream = connection.getInputStream()) {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=12;

                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                    imagesInformationImage.setCache(strings[0], bitmap); // cache for later use
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect(); // close the HttpURLConnection
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            imageLargeView.setImageBitmap(bitmap);
        }
    }
}
