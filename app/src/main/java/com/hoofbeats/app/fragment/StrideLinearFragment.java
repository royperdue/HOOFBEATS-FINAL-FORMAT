package com.hoofbeats.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoofbeats.app.Config;
import com.hoofbeats.app.R;
import com.hoofbeats.app.help.HelpOption;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.hoofbeats.app.model.Reading;
import com.hoofbeats.app.model.Workout;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.ForceTracker;
import com.hoofbeats.app.utility.LittleDB;
import com.hoofbeats.app.utility.MathUtility;
import com.mbientlab.metawear.UnsupportedModuleException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StrideLinearFragment extends ThreeAxisChartFragment
{
    protected List<Workout> workouts = null;
    private Float xPosition = 0f;
    private Float zPosition = 0f;

    public StrideLinearFragment()
    {
        super("linear", R.layout.fragment_stride_linear, R.string.navigation_fragment_stride_linear, 0f, 14f, 25f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        System.out.println("ON-CREATE-VIEW");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("ON-VIEW-CREATED");
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException
    {
        System.out.println("---BOARD-READY");

    }

    @Override
    protected void setup()
    {
        workouts = DatabaseUtility.retrieveWorkouts(LittleDB.get().getLong(Config.SELECTED_HORSE_ID, -1));

        //if (LittleDB.get().getBoolean(Config.WORKOUT_DATE_SELECTED, false) || LittleDB.get().getBoolean(Config.WORKOUT_TIME_SELECTED, false))
            prepareData();
    }

    @Override
    protected void clean()
    {
    }

    @Override
    protected void resetData(boolean clearData)
    {
        super.resetData(clearData);
    }



    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter)
    {
        adapter.add(new HelpOption(R.string.config_name_gpio_pin, R.string.config_desc_gpio_pin));
        adapter.add(new HelpOption(R.string.config_name_gpio_read_mode, R.string.config_desc_gpio_read_mode));
        adapter.add(new HelpOption(R.string.config_name_output_control, R.string.config_desc_output_control));
        adapter.add(new HelpOption(R.string.config_name_pull_mode, R.string.config_desc_pull_mode));
    }

    protected void prepareData()
    {
        workouts = DatabaseUtility.retrieveWorkouts(LittleDB.get().getLong(Config.SELECTED_HORSE_ID, -1));
        List<Reading> readingsLH = new ArrayList<>();
        List<Reading> readingsLF = new ArrayList<>();
        List<Reading> readingsRH = new ArrayList<>();
        List<Reading> readingsRF = new ArrayList<>();

        Workout workout = workouts.get(0);

        if (workout != null)
        {
            List<Reading> readings = workout.getReadings();

            for (int i = 0; i < readings.size(); i++)
            {
                if (readings.get(i).getHoof().equals("Left Hind"))
                    readingsLH.add(readings.get(i));
                else if (readings.get(i).getHoof().equals("Left Front"))
                    readingsLF.add(readings.get(i));
                else if (readings.get(i).getHoof().equals("Right Hind"))
                    readingsRH.add(readings.get(i));
                else if (readings.get(i).getHoof().equals("Right Front"))
                    readingsRF.add(readings.get(i));
            }

            if (readingsLH.size() > 0)
            {
                performCalculations("Left Hind", readingsLH);
            }

            if (readingsLF.size() > 0)
            {
                performCalculations("Left Front", readingsLF);
            }

            if (readingsRH.size() > 0)
            {
                performCalculations("Right Hind", readingsRH);
            }

            if (readingsRF.size() > 0)
            {
                performCalculations("Right Front", readingsRF);
            }
        }
    }

    private void performCalculations(String hoof, List<Reading> readings)
    {
        ForceTracker forceTracker = new ForceTracker();
        float[] accelerationX = new float[2];
        long[] durationsX = new long[2];
        float[] accelerationZ = new float[2];
        long[] durationsZ = new long[2];

        // Sorts list of readings by timestamp, least to greatest.
        Collections.sort(readings, (a, b) -> a.getTimestamp() < b.getTimestamp() ? -1 : a.getTimestamp() == b.getTimestamp() ? 0 : 1);

        for (int i = 0; i < readings.size(); i++)
        {
            if (i == 0)
            {
                accelerationX[0] = 0;
                accelerationZ[0] = 0;

                System.out.println("ACCELERATION-X-1: " + accelerationX[0]);
                System.out.println("ACCELERATION-Z-1: " + accelerationZ[0]);

                durationsX[0] = 0;
                durationsZ[0] = 0;
            } else
            {
                accelerationX[0] = accelerationX[1];
                accelerationZ[0] = accelerationZ[1];

                durationsX[0] = durationsX[1];
                durationsZ[0] = durationsZ[1];

                System.out.println("ACCELERATION-X-2: " + accelerationX[0]);
                System.out.println("ACCELERATION-Z-2: " + accelerationZ[0]);
            }

                accelerationX[1] = readings.get(i).getYValueLinearAcceleration();
                accelerationZ[1] = readings.get(i).getZValueLinearAcceleration();

                durationsX[1] = readings.get(i).getTimestamp();
                durationsZ[1] = readings.get(i).getTimestamp();

                System.out.println("ACCELERATION-X-3: " + accelerationX[1]);
                System.out.println("ACCELERATION-Z-3: " + accelerationZ[1]);

            // Calculates velocity.
            float[] velocityX = MathUtility.getVelocity(accelerationX, durationsX);
            float[] velocityZ = MathUtility.getVelocity(accelerationZ, durationsZ);

            // Calculates displacement.
            float distanceX = MathUtility.getDistance(velocityX, durationsX);
            float distanceZ = MathUtility.getDistance(velocityZ, durationsZ);

            System.out.println("X-Distance: " + distanceX);
            System.out.println("Z-Distance: " + distanceZ);
            xPosition = (xPosition + distanceX);
            zPosition = (zPosition + distanceZ);
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(4);
            numberFormat.setGroupingUsed(false);

            xPosition = (xPosition + distanceX);
            zPosition = (zPosition + distanceZ);

            String stringValueDX = String.valueOf(distanceX).replace("E", "");
            distanceX = Float.parseFloat(numberFormat.format(new Float(stringValueDX)));

            System.out.println("X-Position1: " + xPosition);

            String stringValueX = String.valueOf(xPosition).replace("E", "");
            xPosition = Float.parseFloat(numberFormat.format(new Float(stringValueX)));
            String stringValueZ = String.valueOf(zPosition).replace("E", "");
            zPosition = Float.parseFloat(numberFormat.format(new Float(stringValueZ)));

            System.out.println("X-Position2: " + xPosition);
            System.out.println("Z-Position: " + zPosition);

            if (hoof.equals("Left Hind"))
            {
                forceTracker.setCurrentForceReadingLH(readings.get(i).getForce());

                if (forceTracker.isTurningPointUpLH())
                {
                    addChartDataLH(xPosition, zPosition);
                    zPosition = 0f;
                }
            } else if (hoof.equals("Left Front"))
            {
                forceTracker.setCurrentForceReadingLF(readings.get(i).getForce());

                if (forceTracker.isTurningPointUpLF())
                {
                    addChartDataLF(xPosition, zPosition);
                    zPosition = 0f;
                }
            } else if (hoof.equals("Right Hind"))
            {
                forceTracker.setCurrentForceReadingRH(readings.get(i).getForce());

                if (forceTracker.isTurningPointUpRH())
                {
                    addChartDataRH(xPosition, zPosition);
                    zPosition = 0f;
                }
            } else if (hoof.equals("Right Front"))
            {
                forceTracker.setCurrentForceReadingRF(readings.get(i).getForce());

                if (forceTracker.isTurningPointUpRF())
                {
                    addChartDataRF(xPosition, zPosition);
                    zPosition = 0f;
                }
            }
        }
       xPosition = 0f;
       zPosition = 0f;
    }

}
