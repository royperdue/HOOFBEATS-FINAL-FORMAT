package com.hoofbeats.app.utility;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hoofbeats.app.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class LittleDB
{
    private SharedPreferences preferences;
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private final String LAST_IMAGE_PATH = "lastImagePath";
    private String lastImagePath = "";
    private static Gson GSON = new Gson();
    static LittleDB instance = null;

    public static LittleDB get()
    {
        if (instance == null)
        {
            instance = new LittleDB();
        }
        return instance;
    }

    private LittleDB()
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
    }

    // Decodes the Bitmap from 'path' and returns it.
    public Bitmap getImage(String path)
    {
        Bitmap bitmapFromPath = null;
        try
        {
            bitmapFromPath = BitmapFactory.decodeFile(path);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return bitmapFromPath;
    }

    // Returns the String path of the last saved image
    public String getSavedImagePath()
    {
        return getString(LAST_IMAGE_PATH);
    }

    // Saves 'theBitmap' into folder 'theFolder' with the name 'theImageName'
    // @param theFolder the folder path dir you want to save it.
    // @param theImageName the name you want to assign to the image file(extension included).
    // @param theBitmap the image you want to save as a Bitmap
    // @return true if image was saved, false otherwise
    public boolean putImage(String theFolder, String theImageName, Bitmap theBitmap)
    {
        if (theFolder == null || theImageName == null || theBitmap == null)
            return false;

        this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
        String mFullPath = setupFullPath(theImageName);

        if (!mFullPath.equals(""))
        {
            lastImagePath = mFullPath;
            putString(LAST_IMAGE_PATH, lastImagePath);
            return saveBitmap(mFullPath, theBitmap);
        }

        return false;
    }

    // returns true if image was saved, false otherwise
    public boolean putImageWithFullPath(String fullPath, Bitmap theBitmap)
    {
        return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap);
    }

    // Creates the path for the image with name 'imageName' in DEFAULT_APP.. directory.
    // returns the full path of the image. If it failed to create directory, returns an empty string.
    private String setupFullPath(String imageName)
    {
        File mFolder = new File(Environment.getExternalStorageDirectory(), DEFAULT_APP_IMAGEDATA_DIRECTORY);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists())
        {
            if (!mFolder.mkdirs())
            {
                Log.e("ERROR", "Failed to create folder");
                return "";
            }
        }

        return mFolder.getPath() + '/' + imageName;
    }

    // Saves the Bitmap as a PNG file at path 'fullPath' returns true if it successfully saved, false otherwise.
    private boolean saveBitmap(String fullPath, Bitmap bitmap)
    {
        if (fullPath == null || bitmap == null)
            return false;

        boolean fileCreated = false;
        boolean bitmapCompressed = false;
        boolean streamClosed = false;

        File imageFile = new File(fullPath);

        if (imageFile.exists())
            if (!imageFile.delete())
                return false;

        try
        {
            fileCreated = imageFile.createNewFile();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(imageFile);
            bitmapCompressed = bitmap.compress(CompressFormat.PNG, 100, out);

        } catch (Exception e)
        {
            e.printStackTrace();
            bitmapCompressed = false;

        } finally
        {
            if (out != null)
            {
                try
                {
                    out.flush();
                    out.close();
                    streamClosed = true;

                } catch (IOException e)
                {
                    e.printStackTrace();
                    streamClosed = false;
                }
            }
        }

        return (fileCreated && bitmapCompressed && streamClosed);
    }

    public void putObject(String key, Object object) {
        if(object == null){
            throw new IllegalArgumentException("object is null");
        }

        if(key.equals("") || key == null){
            throw new IllegalArgumentException("key is empty or null");
        }

        preferences.edit().putString(key, GSON.toJson(object)).commit();
    }

    public <T> T getObject(String key, Class<T> a) {

        String gson = preferences.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try{
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key " + key + " is instance of other class");
            }
        }
    }

    public int getInt(String key, int defaultValue)
    {
        return preferences.getInt(key, defaultValue);
    }

    public ArrayList<Integer> getListInt(String key)
    {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Integer> newList = new ArrayList<Integer>();

        for (String item : arrayToList)
            newList.add(Integer.parseInt(item));

        return newList;
    }

    public long getLong(String key, long defaultValue)
    {
        return preferences.getLong(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue)
    {
        return preferences.getFloat(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue)
    {
        String number = getString(key);

        try
        {
            return Double.parseDouble(number);

        } catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    public ArrayList<Double> getListDouble(String key)
    {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "â€šâ€—â€š");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Double> newList = new ArrayList<Double>();

        for (String item : arrayToList)
            newList.add(Double.parseDouble(item));

        return newList;
    }

    public String getString(String key)
    {
        return preferences.getString(key, "");
    }

    public ArrayList<String> getListString(String key)
    {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        return preferences.getBoolean(key, defaultValue);
    }

    public ArrayList<Boolean> getListBoolean(String key)
    {
        ArrayList<String> myList = getListString(key);
        ArrayList<Boolean> newList = new ArrayList<Boolean>();

        for (String item : myList)
        {
            if (item.equals("true"))
            {
                newList.add(true);
            } else
            {
                newList.add(false);
            }
        }

        return newList;
    }

    public SharedPreferences getPreferences()
    {
        return this.preferences;
    }

    public void putInt(String key, int value)
    {
        preferences.edit().putInt(key, value).apply();
    }

    public void putListInt(String key, ArrayList<Integer> intList)
    {
        Integer[] myIntList = intList.toArray(new Integer[intList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply();
    }

    public void putLong(String key, long value)
    {
        preferences.edit().putLong(key, value).apply();
    }

    public void putFloat(String key, float value)
    {
        preferences.edit().putFloat(key, value).apply();
    }

    public void putDouble(String key, double value)
    {
        putString(key, String.valueOf(value));
    }

    public void putListDouble(String key, ArrayList<Double> doubleList)
    {
        Double[] myDoubleList = doubleList.toArray(new Double[doubleList.size()]);
        preferences.edit().putString(key, TextUtils.join("â€šâ€—â€š", myDoubleList)).apply();
    }

    public void putString(String key, String value)
    {
        preferences.edit().putString(key, value).apply();
    }

    public void putListString(String key, ArrayList<String> stringList)
    {
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public void putBoolean(String key, boolean value)
    {
        preferences.edit().putBoolean(key, value).apply();
    }

    public void putListBoolean(String key, ArrayList<Boolean> boolList)
    {
        ArrayList<String> newList = new ArrayList<String>();

        for (Boolean item : boolList)
        {
            if (item)
            {
                newList.add("true");
            } else
            {
                newList.add("false");
            }
        }

        putListString(key, newList);
    }

    public void remove(String key)
    {
        preferences.edit().remove(key).apply();
    }

    public boolean deleteImage(String path)
    {
        return new File(path).delete();
    }

    // Clear SharedPreferences (remove everything).
    public void clear()
    {
        preferences.edit().clear().apply();
    }


    // Retrieve all values from SharedPreferences. Do not modify collection return by method.
    // Returns a Map representing a list of key/value pairs from SharedPreferences.
    public Map<String, ?> getAll()
    {
        return preferences.getAll();
    }

    // Register SharedPreferences change listener.
    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    // Unregister SharedPreferences change listener.
    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener)
    {

        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    // Check if external storage is writable or not.
    public static boolean isExternalStorageWritable()
    {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    // Check if external storage is readable or not.
    public static boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}

