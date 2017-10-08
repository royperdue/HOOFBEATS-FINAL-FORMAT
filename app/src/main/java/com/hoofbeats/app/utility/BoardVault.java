package com.hoofbeats.app.utility;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hoofbeats.app.MyApplication;
import com.mbientlab.metawear.MetaWearBoard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by royperdue on 3/7/17.
 */
public class BoardVault
{
    private SharedPreferences sharedPreferences;
    private String DEFAULT_APP_DATA_DIRECTORY;
    static BoardVault instance = null;

    public static BoardVault get()
    {
        if (instance == null)
            instance = new BoardVault();

        return instance;
    }

    private BoardVault()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
    }

    public MetaWearBoard getMetaWearBoard(String hoof, MetaWearBoard metaWearBoard)
    {
        try
        {
            if (hoof.contains("LH"))
            {
                if (!getString(metaWearBoard.getMacAddress()).equals(""))
                {
                    InputStream inputStream = new FileInputStream(getString(metaWearBoard.getMacAddress()));
                    metaWearBoard.deserialize(inputStream);
                }
            } else if (hoof.contains("LF"))
            {
                if (!getString(metaWearBoard.getMacAddress()).equals(""))
                {
                    InputStream inputStream = new FileInputStream(getString(metaWearBoard.getMacAddress()));
                    metaWearBoard.deserialize(inputStream);
                }
            } else if (hoof.contains("RH"))
            {
                if (!getString(metaWearBoard.getMacAddress()).equals(""))
                {
                    InputStream inputStream = new FileInputStream(getString(metaWearBoard.getMacAddress()));
                    metaWearBoard.deserialize(inputStream);
                }
            } else if (hoof.contains("RF"))
            {
                if (!getString(metaWearBoard.getMacAddress()).equals(""))
                {
                    InputStream inputStream = new FileInputStream(getString(metaWearBoard.getMacAddress()));
                    metaWearBoard.deserialize(inputStream);
                }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return metaWearBoard;
    }

    public boolean putMetaWearBoard(String hoof, String folder, String macAddress, MetaWearBoard metaWearBoard)
    {
        if (folder == null || macAddress == null || metaWearBoard == null)
            return false;

        this.DEFAULT_APP_DATA_DIRECTORY = folder;
        String mFullPath = setupFullPath(macAddress);

        if (!mFullPath.equals(""))
        {
            savePath(hoof, mFullPath, macAddress);
            return saveMetaWearBoard(mFullPath, metaWearBoard);
        }

        return false;
    }

    private String setupFullPath(String metaWearBoardName)
    {
        File mFolder = new File(Environment.getExternalStorageDirectory(), DEFAULT_APP_DATA_DIRECTORY);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists())
        {
            if (!mFolder.mkdirs())
            {
                Log.e("ERROR", "-Failed to create folder-");
                return "";
            }
        }

        return mFolder.getPath() + '/' + metaWearBoardName;
    }

    private boolean saveMetaWearBoard(String fullPath, MetaWearBoard metaWearBoard)
    {
        if (fullPath == null || metaWearBoard == null)
            return false;

        boolean fileCreated = false;
        boolean streamClosed = false;

        File file = new File(fullPath);

        if (file.exists())
            if (!file.delete())
                return false;

        try
        {
            fileCreated = file.createNewFile();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(file);
            metaWearBoard.serialize(out);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

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

        return (fileCreated && streamClosed);
    }

    private void savePath(String hoof, String path, String macAddress)
    {
        if (hoof.contains("LH"))
            putString(macAddress, path);
        else if (hoof.contains("LF"))
            putString(macAddress, path);
        else if (hoof.contains("RH"))
            putString(macAddress, path);
        else if (hoof.contains("RF"))
            putString(macAddress, path);
    }

    private static boolean isExternalStorageWritable()
    {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private static boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public void putString(String key, String value)
    {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key)
    {
        return sharedPreferences.getString(key, "");
    }
}
