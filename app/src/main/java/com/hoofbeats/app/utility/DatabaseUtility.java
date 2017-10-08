package com.hoofbeats.app.utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    public static List<Workout> retrieveWorkouts(Activity activity, long horseId)
    {
        List<Workout> workouts = null;

        workouts = MyApplication.getInstance().getDaoSession().getWorkoutDao().queryBuilder()
                .where(WorkoutDao.Properties.HorseId.eq(horseId)).list();

        clearSession();

        return workouts;
    }

    public static List<Workout> retrieveWorkoutsForDate(Activity activity, String date, long horseId)
    {
        List<Workout> workouts = null;

        workouts = MyApplication.getInstance().getDaoSession().getWorkoutDao().queryBuilder()
                .where(WorkoutDao.Properties.Date.eq(date), WorkoutDao.Properties.HorseId.eq(horseId)).list();


        clearSession();

        return workouts;
    }

    public static List<Horse> retrieveHorses(Activity activity)
    {
        List<Horse> horses = null;

        horses = MyApplication.getInstance().getDaoSession().getHorseDao().queryBuilder().list();

        clearSession();

        return horses;
    }

    public static List<Horse> retrieveHorse(Activity activity, String horseName)
    {
        List<Horse> horses = null;

        horses = MyApplication.getInstance().getDaoSession().getHorseDao().queryBuilder()
                .where(HorseDao.Properties.HorseName.eq(horseName)).list();

        clearSession();

        return horses;
    }

    public static List<Horseshoe> retrieveHorseShoes(Activity activity, String macAddress)
    {
        List<Horseshoe> horseshoes = null;

        horseshoes = MyApplication.getInstance().getDaoSession().getHorseshoeDao().queryBuilder()
                .where(HorseshoeDao.Properties.MacAddress.eq(macAddress)).list();

        clearSession();

        return horseshoes;
    }

    public static List<Horse> retrieveHorseForId(Activity activity, long horseId)
    {
        List<Horse> horses = null;

        horses = MyApplication.getInstance().getDaoSession().getHorseDao().queryBuilder()
                .where(HorseDao.Properties.Id.eq(horseId)).list();

        clearSession();

        return horses;
    }

    public static List<Track> retrieveTracksForId(Activity activity, long trackId)
    {
        List<Track> tracks = null;

        tracks = MyApplication.getInstance().getDaoSession().getTrackDao().queryBuilder()
                .where(TrackDao.Properties.Id.eq(trackId)).list();

        clearSession();

        return tracks;
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

   /* public static List<LatLng> retrieveLatLngList(Activity activity, long trackId, long startNumber, long endNumber)
    {
        List<LatLng> latlngList = new ArrayList<>();
        List<Track> tracks = null;

        tracks = MyApplication.getInstance().getDaoSession().getTrackDao().queryBuilder()
                .where(TrackDao.Properties.Id.eq(trackId)).list();

        List<Location> locations = tracks.get(0).getLocations();

        for (int i = 0; i < locations.size(); i++)
        {
            if (locations.get(i).getLocationNumber() > startNumber && locations.get(i).getLocationNumber() < endNumber)
            {
                LatLng latLng = new LatLng();
                latLng.Latitude = locations.get(i).getLocationLatitude();
                latLng.Longitude = locations.get(i).getLocationLongitude();

                latlngList.add(latLng);
            }
        }

        clearSession();

        return latlngList;
    }*/

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
            List<Workout> workouts = retrieveWorkoutsForDate(activity, LittleDB.get().getString(Config.WORKOUT_START_DATE),
                    LittleDB.get().getLong(Config.HORSE_ID, -1L));

            if (workouts.size() == 1)
                workout = workouts.get(0);
        }

        return workout;
    }

    public static String[] createHorsesArray(Activity activity, Map horsesMap)
    {
        List<Horse> horses = DatabaseUtility.retrieveHorses(activity);
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
        List<Workout> workouts = DatabaseUtility.retrieveWorkouts(activity, LittleDB.get().getLong(Config.HORSE_ID, -1L));
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
    
    /*public static Track convertTrack(com.hoofbeats.custom.gpslogger.Track track)
    {
        Track trackObject = new Track();

        trackObject.setTrackName(track.getName());
        trackObject.setTrackStartAltitude((float) track.getStart_Altitude());
        trackObject.setTrackStartLatitude((float) track.getStart_Latitude());
        trackObject.setTrackStartLongitude((float) track.getStart_Longitude());
        trackObject.setTrackStartAccuracy((float) track.getStart_Accuracy());
        trackObject.setTrackStartSpeed((float) track.getStart_Speed());
        trackObject.setTrackStartTime((float) track.getStart_Time());
        trackObject.setTrackLastFixTime((float) track.getLastFix_Time());
        trackObject.setTrackEndAltitude((float) track.getEnd_Altitude());
        trackObject.setTrackEndLatitude((float) track.getEnd_Latitude());
        trackObject.setTrackEndLongitude((float) track.getEnd_Longitude());
        trackObject.setTrackEndAccuracy((float) track.getEnd_Accuracy());
        trackObject.setTrackEndSpeed((float) track.getEnd_Speed());
        trackObject.setTrackEndTime((float) track.getEnd_Time());

        trackObject.setTrackLastStepDistAltitude((float) track.getLastStepAltitude_Altitude());
        trackObject.setTrackLastStepDistLatitude((float) track.getLastStepDistance_Latitude());
        trackObject.setTrackLastStepDistLongitude((float) track.getLastStepDistance_Longitude());
        trackObject.setTrackLastStepDistAccuracy((float) track.getLastStepDistance_Accuracy());

        trackObject.setTrackMinLatitude((float) track.getMin_Latitude());
        trackObject.setTrackMinLongitude((float) track.getMin_Longitude());

        trackObject.setTrackMaxLatitude((float) track.getMax_Latitude());
        trackObject.setTrackMaxLongitude((float) track.getMax_Longitude());

        trackObject.setTrackDuration((float) track.getDuration());
        trackObject.setTrackDurationMoving((float) track.getDuration_Moving());

        trackObject.setTrackDistance((float) track.getDistance());
        trackObject.setTrackDistanceInProgress((float) track.getDistanceInProgress());
        trackObject.setTrackDistanceLastAltitude((float) track.getDistanceLastAltitude());

        trackObject.setTrackAltitudeUp((float) track.getAltitude_Up());
        trackObject.setTrackAltitudeDown((float) track.getAltitude_Down());
        trackObject.setTrackAltitudeInProgress((float) track.getAltitude_InProgress());

        trackObject.setTrackSpeedMax((float) track.getSpeedMax());
        trackObject.setTrackSpeedAverageMoving((float) track.getSpeedAverageMoving());
        trackObject.setTrackSpeedAverage((float) track.getSpeedAverage());

        trackObject.setTrackNumberLocations((int) track.getNumberOfLocations());
        trackObject.setTrackNumberPlaceMarks((int) track.getNumberOfPlacemarks());
        trackObject.setTrackValidMap((int) track.getValidMap());
        trackObject.setTrackType((int) track.getTrackType());

        return trackObject;
    }

    public static Location addLocationToTrack(LocationExtended locationExtended)
    {
        Location location = new Location();

        location.setLocationLatitude((float) locationExtended.getLatitude());
        location.setLocationLongitude((float) locationExtended.getLongitude());
        location.setLocationAltitude((float) locationExtended.getAltitude());
        location.setLocationSpeed(locationExtended.getSpeed());
        location.setLocationAccuracy(locationExtended.getAccuracy());
        location.setLocationBearing(locationExtended.getBearing());
        location.setLocationTime(locationExtended.getTime());
        location.setLocationNumberSatellites(locationExtended.getNumberOfSatellites());
        //location.setLocationType(LOCATION_TYPE_LOCATION);
        location.setLocationNumberSatellitesUsedFix(locationExtended.getNumberOfSatellitesUsedInFix());

        return location;
    }

    public static PlaceMark addPlaceMarkToTrack(LocationExtended locationExtended)
    {
        PlaceMark placeMark = new PlaceMark();

        placeMark.setLocationLatitude((float) locationExtended.getLatitude());
        placeMark.setLocationLongitude((float) locationExtended.getLongitude());
        placeMark.setLocationAltitude((float) locationExtended.getAltitude());
        placeMark.setLocationSpeed(locationExtended.getSpeed());
        placeMark.setLocationAccuracy(locationExtended.getAccuracy());
        placeMark.setLocationBearing(locationExtended.getBearing());
        placeMark.setLocationTime(locationExtended.getTime());
        placeMark.setLocationNumberSatellites(locationExtended.getNumberOfSatellites());
        //placeMark.setLocationType(LOCATION_TYPE_LOCATION);
        placeMark.setLocationNumberSatellitesUsedFix(locationExtended.getNumberOfSatellitesUsedInFix());

        return placeMark;
    }*/

    public static byte[] bitmap2bytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
