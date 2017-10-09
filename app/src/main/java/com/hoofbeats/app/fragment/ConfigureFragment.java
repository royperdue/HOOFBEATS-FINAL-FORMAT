package com.hoofbeats.app.fragment;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoofbeats.app.Config;
import com.hoofbeats.app.MyApplication;
import com.hoofbeats.app.R;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.model.Horseshoe;
import com.hoofbeats.app.model.Workout;
import com.hoofbeats.app.model.Wrapper;
import com.hoofbeats.app.utility.BoardVault;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.DialogUtility;
import com.hoofbeats.app.utility.LittleDB;
import com.hoofbeats.app.utility.SaveUtility;
import com.mbientlab.metawear.DataToken;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Gpio;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.Logging;
import com.mbientlab.metawear.module.SensorFusionBosch;
import com.mbientlab.metawear.module.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ConfigureFragment extends ModuleFragmentBase
{
    public static class MetaBootWarningFragment extends DialogFragment
    {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.title_warning)
                    .setPositiveButton(R.string.label_ok, null)
                    .setCancelable(false)
                    .setMessage(R.string.message_metaboot)
                    .create();
        }
    }

    public ConfigureFragment()
    {
        super(R.string.navigation_fragment_capture);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_configure, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setupDfuDialog(AlertDialog.Builder builder, int msgResId)
    {
        builder.setTitle(R.string.title_firmware_update)
                .setPositiveButton(R.string.label_yes, (dialogInterface, i) -> fragBus.initiateDfu(null))
                .setNegativeButton(R.string.label_no, null)
                .setCancelable(false)
                .setMessage(msgResId)
                .show();
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException
    {
        setupFragment(getView());
    }

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter)
    {

    }

    @Override
    public void reconnected()
    {
        setupFragment(getView());
    }

    private void configureChannel(Led.PatternEditor editor)
    {
        final short PULSE_WIDTH = 1000;
        editor.highIntensity((byte) 31).lowIntensity((byte) 31)
                .highTime((short) (PULSE_WIDTH >> 1)).pulseDuration(PULSE_WIDTH)
                .repeatCount((byte) -1).commit();
    }

    private void setupFragment(final View v)
    {
        for (int i = 0; i < metaWearBoards.size(); i++)
        {
            Gpio gpio = metaWearBoards.get(i).getModule(Gpio.class);
            SensorFusionBosch sensorFusionBosch = metaWearBoards.get(i).getModule(SensorFusionBosch.class);
            Logging logging = metaWearBoards.get(i).getModule(Logging.class);
            Settings settings = metaWearBoards.get(i).getModule(Settings.class);
            Wrapper wrapper = new Wrapper(gpio, sensorFusionBosch, logging,
                    settings, metaWearBoards.get(i));

            for (BluetoothDevice bluetoothDevice : bluetoothDevices)
            {
                if (bluetoothDevice.getAddress().equals(metaWearBoards.get(i).getMacAddress()))
                    modules.put(wrapper, bluetoothDevice);
            }
        }

        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            if (entry.getKey().getHoof().contains("LH"))
            {
                configureGpioLH(entry.getKey());
                configureSensorFusionLH(entry.getKey());
            } else if (entry.getKey().getHoof().contains("LF"))
            {
                configureGpioLF(entry.getKey());
                configureSensorFusionLF(entry.getKey());
            } else if (entry.getKey().getHoof().contains("RH"))
            {
                configureGpioRH(entry.getKey());
                configureSensorFusionRH(entry.getKey());
            } else if (entry.getKey().getHoof().contains("RF"))
            {
                configureGpioRF(entry.getKey());
                configureSensorFusionRF(entry.getKey());
            }
        }
    }
    // ************************** Configure horseshoes start ****************************

    private void configureSensorFusionLH(Wrapper wrapper)
    {
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        SaveUtility saveUtility = ((SaveUtility) env[0]);
                        saveUtility.setSensor(Config.SENSOR_FUSION_BOSCH);
                        saveUtility.setHoof("LH");
                        saveUtility.setTimestamp(System.nanoTime());
                        saveUtility.setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        saveUtility.setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        saveUtility.setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        saveUtility.executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }

    private void configureSensorFusionLF(Wrapper wrapper)
    {
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        ((SaveUtility) env[0]).setSensor(Config.SENSOR_FUSION_BOSCH);
                        ((SaveUtility) env[0]).setHoof("LF");
                        ((SaveUtility) env[0]).setTimestamp(System.nanoTime());
                        ((SaveUtility) env[0]).setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        ((SaveUtility) env[0]).setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        ((SaveUtility) env[0]).setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        ((SaveUtility) env[0]).executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }

    private void configureSensorFusionRH(Wrapper wrapper)
    {
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        ((SaveUtility) env[0]).setSensor(Config.SENSOR_FUSION_BOSCH);
                        ((SaveUtility) env[0]).setHoof("RH");
                        ((SaveUtility) env[0]).setTimestamp(System.nanoTime());
                        ((SaveUtility) env[0]).setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        ((SaveUtility) env[0]).setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        ((SaveUtility) env[0]).setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        ((SaveUtility) env[0]).executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }

    private void configureSensorFusionRF(Wrapper wrapper)
    {
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        ((SaveUtility) env[0]).setSensor(Config.SENSOR_FUSION_BOSCH);
                        ((SaveUtility) env[0]).setHoof("RF");
                        ((SaveUtility) env[0]).setTimestamp(System.nanoTime());
                        ((SaveUtility) env[0]).setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        ((SaveUtility) env[0]).setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        ((SaveUtility) env[0]).setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        ((SaveUtility) env[0]).executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }

    private void configureGpioLH(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                System.out.println("GPIO-READING: " + data.value(Short.class));

                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("LH");
                                ((SaveUtility) env[0]).setTimestamp(System.nanoTime());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWithTask(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }

    private void configureGpioLF(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("LF");
                                ((SaveUtility) env[0]).setTimestamp(System.nanoTime());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }

    private void configureGpioRH(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("RH");
                                ((SaveUtility) env[0]).setTimestamp(System.nanoTime());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }

    private void configureGpioRF(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("RF");
                                ((SaveUtility) env[0]).setTimestamp(System.nanoTime());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWith(task -> {
            task.getResult().setEnvironment(0, new SaveUtility(getActivity()));
            return null;
        });
    }// **************************** Configure horseshoes end **********************************

    private MetaWearBoard getMetaWearBoard(String hoof, BluetoothDevice bluetoothDevice)
    {
        MetaWearBoard metaWearBoard = BoardVault.get().getMetaWearBoard(hoof, serviceBinder.getMetaWearBoard(bluetoothDevice));

        metaWearBoards.add(metaWearBoard);

        return metaWearBoard;
    }

    private void startModules()
    {
        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            if (entry.getKey().getLogging() != null)
            {
                entry.getKey().getLogging().start(Config.LOGGING_OVERWRITE_MODE);
                System.out.println("LOGGING-STARTED");
            }

            if (entry.getKey().getGpio() != null)
            {
                System.out.println("GPIO-NOT-NULL");

                if (entry.getKey().getSensorFusionBosch() != null)
                {
                    entry.getKey().getSensorFusionBosch().linearAcceleration().start();
                    entry.getKey().getSensorFusionBosch().start();

                    System.out.println("ACCELEROMETER-STARTED");
                }

                Horse horse = null;

                Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(entry.getKey().getMetaWearBoard().getMacAddress());

                if (horseshoe != null)
                {
                    horseshoe.setLogging(true);
                    horseshoe.update();

                    horse = horseshoe.getHorse();
                }

                Workout workout = null;

                List<Workout> workouts = DatabaseUtility.retrieveWorkouts(horse.getId());

                if (workouts.size() == 0)
                {
                    System.out.println("WORKOUT-1");
                    workout = new Workout();
                    workout.setHorseId(horse.getId());
                    workout.setHorse(horse);
                    workout.setDate(new SimpleDateFormat("d-M-yyyy").format(Calendar.getInstance().getTime()));
                    workout.setStartTime(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(Calendar.getInstance().getTime()));
                    MyApplication.getInstance().getDaoSession().getWorkoutDao().insert(workout);
                } else if (workouts.size() > 0)
                {
                    for (int i = 0; i < workouts.size(); i++)
                    {
                        if (workouts.get(i).getDate().equals(new SimpleDateFormat("d-M-yyyy").format(Calendar.getInstance().getTime())))
                        {
                            System.out.println("WORKOUT-2");

                            // UPDATE SO CAN SELECT START TIME FROM DATE/TIME PICKER TO ENSURE CORRECT WORKOUT IS
                            // SELECTED IF HORSE HAS MULTIPLE WORKOUTS FOR SAME DATE.
                            workout = workouts.get(i);
                        }
                    }

                    if (workout == null)
                    {
                        System.out.println("WORKOUT-3");
                        workout = new Workout();
                        workout.setHorseId(horse.getId());
                        workout.setHorse(horse);
                        workout.setDate(new SimpleDateFormat("d-M-yyyy").format(Calendar.getInstance().getTime()));
                        workout.setStartTime(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(Calendar.getInstance().getTime()));
                        MyApplication.getInstance().getDaoSession().getWorkoutDao().insert(workout);
                    }
                }

                System.out.println("WORKOUT-ID: " + workout.getId());

                LittleDB.get().putLong(Config.WORKOUT_ID, workout.getId());
                horse.getWorkouts().add(workout);

                // SET TO FALSE IN THE LOGGING DOWNLOAD SECTION.
                LittleDB.get().putBoolean(Config.MODULES_CURRENTLY_LOGGING, true);

                DialogUtility.showStartLoggingDialog(getActivity(), ConfigureFragment.this);
            }
        }
    }

    public void closeModules()
    {
        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            BoardVault.get().putMetaWearBoard(entry.getValue().getName(), Config.SERIALIZED_BOARDS_FILE,
                    entry.getKey().getMetaWearBoard().getMacAddress(), entry.getKey().getMetaWearBoard());

            if (entry.getKey().getMetaWearBoard().isConnected())
            {
                entry.getKey().getMetaWearBoard().tearDown();
                entry.getKey().getMetaWearBoard().disconnectAsync();
            }
        }

        getActivity().finish();
    }

}
