package com.hoofbeats.app.utility;

import android.app.Activity;

import com.hoofbeats.app.Config;
import com.hoofbeats.app.MyApplication;
import com.hoofbeats.app.model.Reading;
import com.hoofbeats.app.model.ReadingDao;
import com.hoofbeats.app.model.Workout;
import com.hoofbeats.app.model.WorkoutDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by royperdue on 3/20/17.
 */
public class SaveUtility
{
    private Activity activity;
    private String hoof;
    private short forceValue;
    private Long timestamp;
    private String sensor;
    private Float xValueLinearAcceleration;
    private Float yValueLinearAcceleration;
    private Float zValueLinearAcceleration;

    public SaveUtility(Activity activity)
    {
        this.activity = activity;
    }

    public String getHoof()
    {
        return hoof;
    }

    public void setHoof(String hoof)
    {
        this.hoof = hoof;
    }

    public void setForceValue(short forceValue)
    {
        this.forceValue = forceValue;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getSensor()
    {
        return sensor;
    }

    public void setSensor(String sensor)
    {
        this.sensor = sensor;
    }

    public Float getxValueLinearAcceleration()
    {
        return xValueLinearAcceleration;
    }

    public void setxValueLinearAcceleration(Float xValueLinearAcceleration)
    {
        this.xValueLinearAcceleration = xValueLinearAcceleration;
    }

    public Float getyValueLinearAcceleration()
    {
        return yValueLinearAcceleration;
    }

    public void setyValueLinearAcceleration(Float yValueLinearAcceleration)
    {
        this.yValueLinearAcceleration = yValueLinearAcceleration;
    }

    public Float getzValueLinearAcceleration()
    {
        return zValueLinearAcceleration;
    }

    public void setzValueLinearAcceleration(Float zValueLinearAcceleration)
    {
        this.zValueLinearAcceleration = zValueLinearAcceleration;
    }

    public void executeSave()
    {
        Workout workout = null;

        List<Workout> workouts = ((MyApplication) activity.getApplication()).getDaoSession().getWorkoutDao().queryBuilder()
                .where(WorkoutDao.Properties.Id.eq(LittleDB.get().getLong(Config.WORKOUT_ID, -1L))).list();

        if (workouts.size() > 0)
        {
            for (int i = 0; i < workouts.size(); i++)
            {
                if (workouts.get(i).getDate().equals(new SimpleDateFormat("d-M-yyyy").format(Calendar.getInstance().getTime())))
                {
                    System.out.println("WORKOUT-LOCATED");
                    workout = workouts.get(i);
                }
            }

            if (workout != null)
            {
                if (sensor.equals(Config.SENSOR_FUSION_BOSCH))
                {
                    Reading reading = new Reading();
                    reading.setWorkoutId(workout.getId());
                    reading.setWorkout(workout);

                    reading.setHoof(hoof);

                    reading.setXValueLinearAcceleration(xValueLinearAcceleration);
                    reading.setYValueLinearAcceleration(yValueLinearAcceleration);
                    reading.setZValueLinearAcceleration(zValueLinearAcceleration);

                    reading.setTimestamp(timestamp);

                    LittleDB.get().putLong(Config.TIME_SENSOR_FUSION_READING, timestamp);

                    ((MyApplication) activity.getApplication()).getDaoSession().getReadingDao().insert(reading);

                    workout.getReadings().add(reading);
                    ((MyApplication) activity.getApplication()).getDaoSession().getWorkoutDao().update(workout);

                    workout.getHorse().getWorkouts().add(workout);

                    ((MyApplication) activity.getApplication()).getDaoSession().getHorseDao().update(workout.getHorse());

                    ((MyApplication) activity.getApplication()).getDaoSession().clear();
                }

                if (sensor.equals(Config.GPIO_SENSOR))
                {
                    ReadingDao sensorFusionReadingDao = ((MyApplication) activity.getApplication()).getDaoSession().getReadingDao();
                    List<Reading> readings = sensorFusionReadingDao.queryBuilder()
                            .where(ReadingDao.Properties.Timestamp.eq(LittleDB.get().getLong(Config.TIME_SENSOR_FUSION_READING, -1L))).list();

                    readings.get(0).setForce(forceValue);

                    ((MyApplication) activity.getApplication()).getDaoSession().getReadingDao().update(readings.get(0));

                    ((MyApplication) activity.getApplication()).getDaoSession().clear();

                    /*ForceReading forceReading = new ForceReading();
                    forceReading.setWorkoutId(workout.getId());
                    forceReading.setWorkout(workout);

                    forceReading.setHoof(hoof);

                    forceReading.setForceValue(forceValue);

                    forceReading.setTimestamp(timestamp);

                    ((MyApplication) activity.getApplication()).getDaoSession().getForceReadingDao().insert(forceReading);

                    workout.getForceReadings().add(forceReading);
                    ((MyApplication) activity.getApplication()).getDaoSession().getWorkoutDao().update(workout);

                    workout.getHorse().getWorkouts().add(workout);

                    ((MyApplication) activity.getApplication()).getDaoSession().getHorseDao().update(workout.getHorse());

                    ((MyApplication) activity.getApplication()).getDaoSession().clear();*/
                }
            } else
                System.out.println("WORKOUT-NULL");
        }
    }
}
