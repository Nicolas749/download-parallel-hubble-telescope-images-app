package com.example.proyecto6;

import android.graphics.Bitmap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SingletonImages //Singleton class
{
    //Attributes
    private Map <String, String> mapImagesInformation; //Map to save information
    private Map <String, Bitmap> cache;
    ArrayList<String> names = new ArrayList<>();
    private static final SingletonImages imagesInformationInstance = new SingletonImages(); //Instance of class (Singleton)

    public static SingletonImages getInformation() //Method to retrieve the singleton
    {
        return imagesInformationInstance;
    }

    private SingletonImages()
    {
        mapImagesInformation = new LinkedHashMap<>();
        cache = new LinkedHashMap<>();
    }
    public void  saveName(String name){

        names.add(name);

    }
    public ArrayList<String> getNames(){
        return names;
    }

    public void clear()
    {
        mapImagesInformation.clear();
    }

    public void setImageInformation(Map<String,String> map)
    {
        mapImagesInformation=map;
    }

    public List <Map.Entry<String, String>> getInformationMap()
    {
        List <Map.Entry<String, String>> informationList= new ArrayList<>();

        mapImagesInformation.forEach(((x,y) -> informationList.add(new AbstractMap.SimpleEntry<String, String> (x,y))));

        return informationList;
    }

    //For second activity cache
    public void setCache(String string, Bitmap bitMap)
    {
        cache.put(string,bitMap);
    }
    public boolean containsInCache(String string)
    {
        return cache.containsKey(string);
    }
    public Bitmap getCache(String string)
    {
        return cache.get(string);
    }
}
