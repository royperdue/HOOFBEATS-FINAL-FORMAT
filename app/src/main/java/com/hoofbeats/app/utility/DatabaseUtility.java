package com.hoofbeats.app.utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.hoofbeats.app.Config;
import com.hoofbeats.app.MyApplication;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.model.HorseDao;
import com.hoofbeats.app.model.Horseshoe;
import com.hoofbeats.app.model.HorseshoeDao;
import com.hoofbeats.app.model.Track;
import com.hoofbeats.app.model.TrackDao;
import com.hoofbeats.app.model.Workout;
import com.hoofbeats.app.model.WorkoutDao;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by royperdue on 6/27/17.
 */
public final class DatabaseUtility
{
    public static List<Workout> retrieveWorkouts(long horseId)
    {
        List<Workout> workouts = null;

        workouts = MyApplication.getInstance().getDaoSession().getWorkoutDao().queryBuilder()
                .where(WorkoutDao.Properties.HorseId.eq(horseId)).list();

        clearSession();

        return workouts;
    }

    public static List<Workout> retrieveWorkoutsForDate(String date, long horseId)
    {
        List<Workout> workouts = null;

        workouts = MyApplication.getInstance().getDaoSession().getWorkoutDao().queryBuilder()
                .where(WorkoutDao.Properties.Date.eq(date), WorkoutDao.Properties.HorseId.eq(horseId)).list();


        clearSession();

        return workouts;
    }

    public static List<Horse> retrieveHorses()
    {
        List<Horse> horses = null;

        horses = MyApplication.getInstance().getDaoSession().getHorseDao().queryBuilder().list();

        clearSession();

        return horses;
    }

    public static Horse retrieveHorse(String horseName)
    {
        List<Horse> horses = null;

        horses = MyApplication.getInstance().getDaoSession().getHorseDao().queryBuilder()
                .where(HorseDao.Properties.HorseName.eq(horseName)).list();

        clearSession();

        if (horses.size() > 0)
            return horses.get(0);
        else
            return null;
    }

    public static Horseshoe retrieveHorseShoeForMacAddress(String macAddress)
    {
        List<Horseshoe> horseshoes = null;

        horseshoes = MyApplication.getInstance().getDaoSession().getHorseshoeDao().queryBuilder()
                .where(HorseshoeDao.Properties.MacAddress.eq(macAddress)).list();

        clearSession();

        if (horseshoes.size() > 0)
            return horseshoes.get(0);
        else
            return null;
    }

    public static Horse retrieveHorseForMacAddress(String macAddress)
    {
        System.out.println("----Five---");
        List<Horseshoe> horseshoes = null;

        horseshoes = MyApplication.getInstance().getDaoSession().getHorseshoeDao().queryBuilder()
                .where(HorseshoeDao.Properties.MacAddress.eq(macAddress)).list();

        clearSession();

        if (horseshoes.size() > 0)
            return horseshoes.get(0).getHorse();
        else
            return null;
    }

    public static void removeHorseshoeMacAddress(String macAddress)
    {
        List<Horseshoe> horseshoes = null;

        horseshoes = MyApplication.getInstance().getDaoSession().getHorseshoeDao().queryBuilder()
                .where(HorseshoeDao.Properties.MacAddress.eq(macAddress)).list();

        MyApplication.getInstance().getDaoSession().getHorseshoeDao().delete(horseshoes.get(0));
        horseshoes.get(0).getHorse().getHorseshoes().remove(horseshoes.get(0));
        horseshoes.get(0).getHorse().update();
        horseshoes.get(0).getHorse().resetHorseshoes();

        clearSession();
    }

    public static Horse retrieveHorseForId(long horseId)
    {
        List<Horse> horses = null;

        horses = MyApplication.getInstance().getDaoSession().getHorseDao().queryBuilder()
                .where(HorseDao.Properties.Id.eq(horseId)).list();

        clearSession();

        if (horses.size() > 0)
            return horses.get(0);
        else
            return null;
    }

    public static List<Track> retrieveTracksForId(long trackId)
    {
        List<Track> tracks = null;

        tracks = MyApplication.getInstance().getDaoSession().getTrackDao().queryBuilder()
                .where(TrackDao.Properties.Id.eq(trackId)).list();

        clearSession();

        return tracks;
    }

    public static String retrieveTrackNameForId(long trackId)
    {
        List<Track> tracks = null;

        tracks = MyApplication.getInstance().getDaoSession().getTrackDao().queryBuilder()
                .where(TrackDao.Properties.Id.eq(trackId)).list();

        clearSession();

        return tracks.get(0).getTrackName();
    }

    public static List<Track> retrieveTracks(Activity activity)
    {
        List<Track> tracks = null;

        tracks = MyApplication.getInstance().getDaoSession().getTrackDao().queryBuilder().list();

        clearSession();

        return tracks;
    }

    public static Track retrieveLastTrack(Activity activity)
    {
        List<Track> tracks = null;

        tracks = MyApplication.getInstance().getDaoSession().getTrackDao().queryBuilder().orderDesc(TrackDao.Properties.Id).list();

        clearSession();

        return tracks.get(0);
    }

    public static void addHorseshoeToHorse(Horse horse, String hoof, String macAddress)
    {
        List<Horseshoe> horseshoes = horse.getHorseshoes();
        Horseshoe horseshoe = new Horseshoe();
        horseshoe.setHorseId(horse.getId());
        horseshoe.setHoof(hoof);
        horseshoe.setMacAddress(macAddress);
        horseshoe.setDateAssigned(new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime()));

        MyApplication.getInstance().getDaoSession().getHorseshoeDao().insert(horseshoe);
        horseshoes.add(horseshoe);

        horse.resetHorseshoes();

        System.out.println("----Three---");

        clearSession();
    }

    public static void insertTrack(Track track)
    {
        MyApplication.getInstance().getDaoSession().getTrackDao().insert(track);

        clearSession();
    }

    public static void deleteTrack(long trackId)
    {
        MyApplication.getInstance().getDaoSession().getTrackDao().deleteByKey(trackId);

        clearSession();
    }

    public static List<String> retrieveHorseNames(Activity activity)
    {
        List<String> horseNames = new ArrayList<>();
        List<Horse> horses = null;

        horses = MyApplication.getInstance().getDaoSession().getHorseDao().queryBuilder().list();

        for (int i = 0; i < horses.size(); i++)
        {
            horseNames.add(horses.get(i).getHorseName());
        }

        clearSession();

        return horseNames;
    }


    public static void clearSession()
    {
        MyApplication.getInstance().getDaoSession().clear();
    }

    public static Workout checkWorkoutSelection(Activity activity)
    {
        Workout workout = null;

        // Makes sure both a horse and a workout have been selected.
        if (LittleDB.get().getBoolean(Config.HORSE_SELECTED, false)
                && !LittleDB.get().getString(Config.WORKOUT_START_DATE).equals(""))
        {
            System.out.println("-CALLED-1");
            List<Workout> workouts = retrieveWorkoutsForDate(LittleDB.get().getString(Config.WORKOUT_START_DATE),
                    LittleDB.get().getLong(Config.HORSE_ID, -1L));

            if (workouts.size() == 1)
                workout = workouts.get(0);
        }

        return workout;
    }

    public static String[] createHorsesArray(Activity activity, Map horsesMap)
    {
        List<Horse> horses = DatabaseUtility.retrieveHorses();
        List<String> horseNames = new ArrayList<>();

        if (horses.size() > 0)
        {
            for (int i = 0; i < horses.size(); i++)
            {
                horseNames.add(horses.get(i).getHorseName());
                horsesMap.put(horses.get(i).getHorseName(), horses.get(i).getId());
            }
        }

        if (horseNames.size() > 0)
            return horseNames.toArray(new String[horseNames.size()]);
        else
            return null;
    }

    public static Calendar[] createDateArray(Activity activity)
    {
        List<Workout> workouts = DatabaseUtility.retrieveWorkouts(LittleDB.get().getLong(Config.HORSE_ID, -1L));
        List<Calendar> calendars = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-yyyy");

        if (workouts.size() > 0)
        {
            for (int i = 0; i < workouts.size(); i++)
            {
                String workoutDate = workouts.get(i).getDate();

                try
                {
                    Date date = dateFormat.parse(workoutDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    calendars.add(cal);
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }

            return calendars.toArray(new Calendar[calendars.size()]);
        } else
            return null;
    }

    public static byte[] bitmap2bytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage, String horseName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, horseName, null);
        return Uri.parse(path);
    }

    public static void deleteHorseProfileImage(Activity activity, String imagePath)
    {
        ContentResolver contentResolver = activity.getContentResolver();
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ imagePath });
    }
}
