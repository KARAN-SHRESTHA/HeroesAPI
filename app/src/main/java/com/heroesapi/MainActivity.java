package com.heroesapi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.api.API;
import com.model.Heroes;
import com.url.Url;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private EditText etName, etDesc;
    private Button btnSave;
    private ImageView img;
    String imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etDesc = findViewById(R.id.etDesc);
        btnSave = findViewById(R.id.btnSave);
        img = findViewById(R.id.img);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }


        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BrowseImage();

            }
        });

    }

    private void Save() {

        String name = etName.getText().toString();
        String desc = etDesc.getText().toString();

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("desc", desc);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);

        Call<Void> heroesCall = api.addHero(map);

        heroesCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(!response.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void BrowseImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,0);
    }

    private void previewImage(String imagepath) {
        StrictMode();
        File imgFile = new File(imagepath);
        if (imgFile.exists())
        {
            Bitmap mybitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            img.setImageBitmap(mybitmap);
        }

    }

    private String getRealPathFromUri(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        cursor.close();
        return result;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (data == null)
            {
                Toast.makeText(this, "Please Select an Iamge", Toast.LENGTH_SHORT).show();
            }
        }
        Uri uri = data.getData();
        imagepath = getRealPathFromUri(uri);
        previewImage(imagepath);

    }

    private void StrictMode()
    {
        android.os.StrictMode.ThreadPolicy policy =
                new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
        android.os.StrictMode.setThreadPolicy(policy);
    }



}
