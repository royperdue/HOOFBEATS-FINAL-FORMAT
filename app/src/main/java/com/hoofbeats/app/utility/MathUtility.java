package com.hoofbeats.app.utility;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by royperdue on 6/7/17.
 */
public final class MathUtility
{
    public static float getDistance(float[] velocity, long[] distribution)
    {
        float distance = 0;
        long[] durations = getDurations(distribution);
        int i = 0;

        for (float v : velocity)
        {
            distance += (v * durations[i]);
            i++;
        }
        return distance;
    }

    // Assumes v_initial = 0
    // Measures distance traveled rather than displacement
    public static float[] getVelocity(float[] acceleration, long[] distribution)
    {
        float velocity[] = new float[acceleration.length];
        long[] durations = getDurations(distribution);
        int i = 0;

        for (float a : acceleration)
        {
            if (i == 0)
                velocity[i] = 0;
            else velocity[i] = Math.abs(velocity[i - 1] + a * durations[i]);
            //else velocity[i] = velocity[i - 1] + a * durations[i];
            i++;
        }
        return velocity;
    }

    public static long[] getDurations(long[] times)
    {
        long[] durations = new long[times.length];

        for (int i = 0; i < times.length; i++)
        {
            if (i == 0)
                durations[i] = times[i] - times[0];
            else
                durations[i] = times[i] - times[i - 1];
        }

        for (int i = 0; i < durations.length; i++)
        {
            durations[i] /= 1000.0;
        }

        return durations;
    }

    //Method to calculate the standard deviation of an interval of data
    public static float getStandardDeviation(List<Float> data)
    {
        float mean = getMean(data);
        float sum = 0;

        for (int i = 0; i < data.size(); i++)
        {
            sum += (data.get(i) - mean) * (data.get(i) - mean);
        }
        return (float) Math.sqrt(sum / ((float) (data.size() - 1)));
    }

    public static float getMean(List<Float> data)
    {
        float sum = 0;

        for (float a : data)
            sum += a;

        return sum / data.size();
    }

    public static float getVariance(List<Float> data)
    {
        float mean = getMean(data);
        float temp = 0;

        for (float a : data)
            temp += (mean - a) * (mean - a);

        return temp / data.size();
    }

    public static float getMedian(List<Float> data)
    {
        Collections.sort(data);

        if (data.size() % 2 == 0)
        {
            return (data.get((data.size() / 2) - 1) + data.get(data.size() / 2)) / 2.0f;
        } else
        {
            return data.get(data.size() / 2);
        }
    }

    //Method to calculate the slope of an interval of data
    public static float getSlope(List<Float> data)
    {
        //We return the last point - the first point divided by the number of points
        return (data.get(data.size() - 1) - data.get(0)) / ((float) data.size());
    }

    private static double truncate(double d)
    {
        return Math.floor(d * 1e2) / 1e2;
    }

    //Method to determine the minimum of two floats
    public static float min(float a, float b)
    {
        if (a >= b)
        {
            return b;
        } else
        {
            return a;
        }
    }

    //method to get max
    public static float max(float a, float b)
    {
        if (a <= b)
        {
            return b;
        } else
        {
            return a;
        }
    }

    public static double[] convertDoubles(List<Double> doubles)
    {
        double[] ret = new double[doubles.size()];
        Iterator<Double> iterator = doubles.iterator();
        int i = 0;

        while (iterator.hasNext())
        {
            ret[i] = iterator.next();
            i++;
        }

        return ret;
    }

    public long[] convertLongs(ArrayList<Long> longs)
    {
        long[] ret = new long[longs.size()];

        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = longs.get(i).longValue();
        }

        return ret;
    }

    public static float[] toFloatArray(List<Float> list)
    {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list)
        {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    public static float round(float d, int decimalPlace)
    {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static String convertMilliSecondsToFormattedDate(Float milliSeconds)
    {
        String dateFormat = "dd-MM-yyyy hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) Float.parseFloat(String.valueOf(milliSeconds)));

        return simpleDateFormat.format(calendar.getTime());
    }
}
