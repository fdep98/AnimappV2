package com.example.animapp.Model;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.example.animapp.animapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageGalerie {

    private static final String TAG = ImageGalerie.class.getSimpleName();

    public  String description;
    public  Uri dynamicUrl;
    public  String url;
    public String date;
    public  Date date1;

    public ImageGalerie(String description, String dynamicUrl, String url,String date){
        this.description = description;
        this.dynamicUrl = Uri.parse(dynamicUrl);
        this.url = url;
        this.date = date;
    }
    public ImageGalerie(String description,String url,String date){
        this.description = description;
        this.url = url;
        this.date = date;
    }

    public ImageGalerie(){ }

    /**
     * Loads a raw JSON at R.raw.products and converts it into a list of ProductEntry objects
     * Code de Google Material Design
     */
    public static List<ImageGalerie> initImgageList(Resources resources) {
        InputStream inputStream = resources.openRawResource(R.raw.image_galerie);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int pointer;
            while ((pointer = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, pointer);
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error writing/reading from the JSON file.", exception);
        } finally {
            try {
                inputStream.close();
            } catch (IOException exception) {
                Log.e(TAG, "Error closing the input stream.", exception);
            }
        }
        String jsonProductsString = writer.toString();
        Gson gson = new Gson();
        Type imageListType = new TypeToken<ArrayList<ImageGalerie>>() {
        }.getType();
        return gson.fromJson(jsonProductsString, imageListType);
    }
}
