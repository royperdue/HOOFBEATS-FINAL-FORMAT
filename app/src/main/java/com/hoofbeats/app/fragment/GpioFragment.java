package com.hoofbeats.app.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hoofbeats.app.Config;
import com.hoofbeats.app.MyApplication;
import com.hoofbeats.app.R;
import com.hoofbeats.app.SettingsActivity;
import com.hoofbeats.app.custom.gps.Alert;
import com.hoofbeats.app.custom.gps.Logger;
import com.hoofbeats.app.custom.gps.LoggerService;
import com.hoofbeats.app.help.HelpOption;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.model.Track;
import com.hoofbeats.app.model.Workout;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.LittleDB;
import com.mbientlab.metawear.UnsupportedModuleException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class GpioFragment extends SingleDataSensorFragment
{
    private static final int GPIO_SAMPLE_PERIOD = 33;
    public static final String UPDATED_PREFS = "extra_updated_prefs";

    private final String TAG = "GPS";

    private final static int LED_GREEN = 1;
    private final static int LED_RED = 2;
    private final static int LED_YELLOW = 3;

    private final static int PERMISSION_LOCATION = 1;
    private final static int PERMISSION_WRITE = 2;
    private final static int RESULT_PREFS_UPDATED = 1;

    private String pref_units;
    private long pref_minTimeMillis;
    private boolean pref_liveSync;

    private final static double KM_MILE = 0.621371;

    private static boolean syncError = false;
    private boolean isUploading = false;
    private TextView syncErrorLabel;
    private TextView syncLabel;
    private TextView syncLed;
    private TextView locLabel;
    private TextView locLed;
    private static String TXT_START;
    private static String TXT_STOP;
    private Button toggleButton;
    private View view;

    public GpioFragment()
    {
        super(R.string.navigation_fragment_gpio, "adc", R.layout.fragment_gps, GPIO_SAMPLE_PERIOD / 1000.f, 0, 1023);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        updatePreferences();
        TXT_START = getString(R.string.button_start);
        TXT_STOP = getString(R.string.button_stop);
        toggleButton = (Button) view.findViewById(R.id.toggle_button);
        syncErrorLabel = (TextView) view.findViewById(R.id.sync_error);
        syncLabel = (TextView) view.findViewById(R.id.sync_status);
        syncLed = (TextView) view.findViewById(R.id.sync_led);
        locLabel = (TextView) view.findViewById(R.id.location_status);
        locLed = (TextView) view.findViewById(R.id.loc_led);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_gps, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:

                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(i, RESULT_PREFS_UPDATED);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException
    {
        //gpio = mwBoard.getModuleOrThrow(Gpio.class);
        //timer = mwBoard.getModuleOrThrow(Timer.class);
    }

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter)
    {
        adapter.add(new HelpOption(R.string.config_name_gpio_pin, R.string.config_desc_gpio_pin));
        adapter.add(new HelpOption(R.string.config_name_gpio_read_mode, R.string.config_desc_gpio_read_mode));
        adapter.add(new HelpOption(R.string.config_name_output_control, R.string.config_desc_output_control));
        adapter.add(new HelpOption(R.string.config_name_pull_mode, R.string.config_desc_pull_mode));
    }

    @Override
    protected void setup()
    {

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
    public void onResume()
    {
        super.onResume();
        if (Logger.DEBUG)
        {
            Log.d(TAG, "[onResume]");
        }

        String trackName = DatabaseUtility.retrieveTrackNameForId(LittleDB.get().getLong(Config.CURRENT_TRACK_ID, -1));
        if (trackName != null)
        {
            updateTrackLabel(trackName);
        }

        if (LoggerService.isRunning())
        {
            toggleButton.setText(TXT_STOP);
            setLocLed(LED_GREEN);
        } else
        {
            toggleButton.setText(TXT_START);
            setLocLed(LED_RED);
        }
        registerBroadcastReceiver();
        //updateStatus();
    }


    @Override
    public void onPause()
    {
        if (Logger.DEBUG)
        {
            Log.d(TAG, "[onPause]");
        }
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        if (Logger.DEBUG)
        {
            Log.d(TAG, "[onDestroy]");
        }
        super.onDestroy();
    }

    /**
     * Callback on permission request result
     * Called after user granted/rejected location permission
     *
     * @param requestCode  Permission code
     * @param permissions  Permissions
     * @param grantResults Result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        // onPause closed db
        //db.open(this);
        switch (requestCode)
        {
            case PERMISSION_LOCATION:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    // onPause closed db
                    //db.open(this);
                    startLogger();
                }
                break;
        }
        //db.close();
    }

    /**
     * Callback on activity result.
     * Called after user updated preferences
     *
     * @param requestCode Activity code
     * @param resultCode  Result
     * @param data        Data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case RESULT_PREFS_UPDATED:
                // Preferences updated
                updatePreferences();
                if (LoggerService.isRunning())
                {
                    // restart logging
                    Intent intent = new Intent(getActivity(), LoggerService.class);
                    intent.putExtra(UPDATED_PREFS, true);
                    getActivity().startService(intent);
                }
                break;
        }
    }

    /**
     * Reread user preferences
     */
    private void updatePreferences()
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref_units = prefs.getString("prefUnits", getString(R.string.pref_units_default));
        pref_minTimeMillis = Long.parseLong(prefs.getString("prefMinTime", getString(R.string.pref_mintime_default))) * 1000;
        pref_liveSync = prefs.getBoolean("prefLiveSync", false);
    }

    /**
     * Called when the user clicks the Start/Stop button
     *
     * @param view View
     */
    public void toggleLogging(@SuppressWarnings("UnusedParameters") View view)
    {
        if (LoggerService.isRunning())
        {
            stopLogger();
        } else
        {
            startLogger();
        }
    }

    private void startLogger()
    {
        // start tracking
        if (DatabaseUtility.retrieveTrackNameForId(LittleDB.get().getLong(Config.CURRENT_TRACK_ID, -1)) != null)
        {
            Intent intent = new Intent(getActivity(), LoggerService.class);
            getActivity().startService(intent);
        } else
        {
            showEmptyTrackNameWarning();
        }
    }

    private void stopLogger()
    {
        // stop tracking
        Intent intent = new Intent(getActivity(), LoggerService.class);
        getActivity().stopService(intent);
    }

    public void newTrack(@SuppressWarnings("UnusedParameters") View view)
    {
        if (LoggerService.isRunning())
        {
            showToast(getString(R.string.logger_running_warning));
        }  else
        {
            showTrackDialog();
        }
    }

    /*public void trackSummary(@SuppressWarnings("UnusedParameters") View view)
    {
        final TrackSummary summary = db.getTrackSummary();
        if (summary == null)
        {
            showToast(getString(R.string.no_positions));
            return;
        }

        final AlertDialog dialog = Alert.showAlert(getActivity(),
                getString(R.string.track_summary),
                R.layout.summary,
                R.drawable.ic_equalizer_white_24dp);
        final Button okButton = (Button) dialog.findViewById(R.id.summary_button_ok);
        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        final TextView summaryDistance = (TextView) dialog.findViewById(R.id.summary_distance);
        final TextView summaryDuration = (TextView) dialog.findViewById(R.id.summary_duration);
        final TextView summaryPositions = (TextView) dialog.findViewById(R.id.summary_positions);
        double distance = (double) summary.getDistance() / 1000;
        String unitName = getString(R.string.unit_kilometer);

        if (pref_units.equals(getString(R.string.pref_units_imperial)))
        {
            distance *= KM_MILE;
            unitName = getString(R.string.unit_mile);
        }

        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        final String distanceString = nf.format(distance);
        summaryDistance.setText(getString(R.string.summary_distance, distanceString, unitName));
        final long h = summary.getDuration() / 3600;
        final long m = summary.getDuration() % 3600 / 60;
        summaryDuration.setText(getString(R.string.summary_duration, h, m));
        int positionsCount = (int) summary.getPositionsCount();
        if (needsPluralFewHack(positionsCount))
        {
            summaryPositions.setText(getResources().getString(R.string.summary_positions_few, positionsCount));
        } else
        {
            summaryPositions.setText(getResources().getQuantityString(R.plurals.summary_positions, positionsCount, positionsCount));
        }
    }*/

    private void showToast(CharSequence text)
    {
        showToast(text, Toast.LENGTH_SHORT);
    }

    private void showToast(CharSequence text, int duration)
    {
        Context context = getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @SuppressWarnings("deprecation")
    private static CharSequence fromHtmlDepreciated(String text)
    {
        return Html.fromHtml(text);
    }

    private void showNotSyncedWarning()
    {
        Alert.showConfirm(getActivity(),
                getString(R.string.warning),
                getString(R.string.notsync_warning),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        showTrackDialog();
                    }
                }
        );
    }

    private void showEmptyTrackNameWarning()
    {
        showToast(getString(R.string.empty_trackname_warning));
    }

    private void showTrackDialog()
    {
        final AlertDialog dialog = Alert.showAlert(getActivity(),
                getString(R.string.title_newtrack),
                R.layout.newtrack_dialog);
        final EditText editText = (EditText) dialog.findViewById(R.id.newtrack_edittext);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        final String dateSuffix = sdf.format(Calendar.getInstance().getTime());
        final String autoName = "Auto_" + dateSuffix;
        editText.setText(autoName);
        editText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editText.selectAll();
            }
        });

        final Button submit = (Button) dialog.findViewById(R.id.newtrack_button_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String trackName = editText.getText().toString();
                if (trackName.length() == 0)
                {
                    return;
                }

                Horse horse = DatabaseUtility.retrieveHorseForId(LittleDB.get().getLong(Config.SELECTED_HORSE_ID, -1));

                if (horse != null)
                {
                    long workoutId = -1;
                    List<Track> tracks = null;
                    List<Workout> workouts = horse.getWorkouts();

                    for (int i = 0; i < workouts.size(); i++)
                    {
                        tracks = workouts.get(i).getTracks();

                        if (tracks.size() == 0)
                        {
                            workoutId = workouts.get(i).getId();
                            Track track = new Track();
                            track.setTrackName(trackName);
                            track.setTrackDate(new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime()));

                            if (workoutId > -1)
                                track.setWorkoutId(workoutId);

                            MyApplication.getInstance().getDaoSession().getTrackDao().insert(track);
                            tracks.add(track);

                            workouts.get(i).resetTracks();

                            LittleDB.get().putLong(Config.CURRENT_TRACK_ID, workouts.get(i).getTracks().get(0).getId());
                            break;
                        }
                    }
                }
                LoggerService.resetUpdateRealtime();
                updateTrackLabel(trackName);
                //updateStatus();
                dialog.cancel();
            }
        });

        final Button cancel = (Button) dialog.findViewById(R.id.newtrack_button_cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
            }
        });
    }

    /**
     * Update track name label
     *
     * @param trackName Track name
     */
    private void updateTrackLabel(String trackName)
    {
        final TextView trackLabel = (TextView) view.findViewById(R.id.newtrack_label);
        trackLabel.setText(trackName);
    }

    /**
     * Update location tracking status label
     *
     * @param lastUpdateRealtime Real time of last location update
     */
    private void updateLocationLabel(long lastUpdateRealtime)
    {
        // get last location update time
        String timeString;
        long timestamp = 0;
        long elapsed = 0;
        long dbTimestamp;
        if (lastUpdateRealtime > 0)
        {
            elapsed = (SystemClock.elapsedRealtime() - lastUpdateRealtime);
            timestamp = System.currentTimeMillis() - elapsed;
        }/* else if ((dbTimestamp = db.getLastTimestamp()) > 0)
        {
            timestamp = dbTimestamp * 1000;
            elapsed = System.currentTimeMillis() - timestamp;
        }*/

        if (timestamp > 0)
        {
            final Date updateDate = new Date(timestamp);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(updateDate);
            final Calendar today = Calendar.getInstance();
            SimpleDateFormat sdf;

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            } else
            {
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            }
            sdf.setTimeZone(TimeZone.getDefault());
            timeString = String.format(getString(R.string.label_last_update), sdf.format(updateDate));
        } else
        {
            timeString = "-";
        }
        locLabel.setText(timeString);
        // Change led if more than 2 update periods elapsed since last location update
        if (LoggerService.isRunning() && (timestamp == 0 || elapsed > pref_minTimeMillis * 2))
        {
            setLocLed(LED_YELLOW);
        }
    }

    /**
     * Update synchronization status label and led
     *
     * @param unsynced Count of not synchronized positions
     */
    private void updateSyncStatus(int unsynced)
    {
        String text;
        if (unsynced > 0)
        {
            if (needsPluralFewHack(unsynced))
            {
                text = getString(R.string.label_positions_behind_few, unsynced);
            } else
            {
                text = getResources().getQuantityString(R.plurals.label_positions_behind, unsynced, unsynced);
            }
            if (syncError)
            {
                setSyncLed(LED_RED);
            } else
            {
                setSyncLed(LED_YELLOW);
            }
        } else
        {
            text = getString(R.string.label_synchronized);
            setSyncLed(LED_GREEN);
        }

        syncLabel.setText(text);
    }

    /**
     * Update location tracking and synchronization status
     */
   /* private void updateStatus()
    {
        updateLocationLabel(LoggerService.lastUpdateRealtime());
        // get sync status
       // int count = db.countUnsynced();
       // String error = db.getError();
        if (error != null)
        {
            if (Logger.DEBUG)
            {
                Log.d(TAG, "[sync error: " + error + "]");
            }
            syncError = true;
            syncErrorLabel.setText(error);
        } else if (syncError)
        {
            syncError = false;
            syncErrorLabel.setText(null);
        }
        updateSyncStatus(count);
    }*/

    /**
     * Set status led color
     *
     * @param led   Led text view
     * @param color Color (red, yellow or green)
     */
    private void setLedColor(TextView led, int color)
    {
        Drawable l;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            l = led.getCompoundDrawables()[0];
        } else
        {
            l = led.getCompoundDrawablesRelative()[0];
        }
        switch (color)
        {
            case LED_RED:
                l.setColorFilter(ContextCompat.getColor(getActivity(), R.color.red_400), PorterDuff.Mode.SRC_ATOP);
                break;

            case LED_GREEN:
                l.setColorFilter(ContextCompat.getColor(getActivity(), R.color.green), PorterDuff.Mode.SRC_ATOP);
                break;

            case LED_YELLOW:
                l.setColorFilter(ContextCompat.getColor(getActivity(), R.color.yellow_300), PorterDuff.Mode.SRC_ATOP);
                break;
        }
        l.invalidateSelf();
    }

    /**
     * Set synchronization status led color
     * Red - synchronization error
     * Yellow - synchronization delay
     * Green - synchronized
     *
     * @param color Color
     */
    private void setSyncLed(int color)
    {
        if (Logger.DEBUG)
        {
            Log.d(TAG, "[setSyncLed " + color + "]");
        }
        setLedColor(syncLed, color);
    }

    /**
     * Set location tracking status led color
     * Red - tracking off
     * Yellow - tracking on, long time since last update
     * Green - tracking on, recently updated
     *
     * @param color Color
     */
    private void setLocLed(int color)
    {
        if (Logger.DEBUG)
        {
            Log.d(TAG, "[setLocLed " + color + "]");
        }
        setLedColor(locLed, color);
    }

    /**
     * Register broadcast receiver for synchronization
     * and tracking status updates
     */
    private void registerBroadcastReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoggerService.BROADCAST_LOCATION_STARTED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_STOPPED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_UPDATED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_DISABLED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_GPS_DISABLED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_NETWORK_DISABLED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_GPS_ENABLED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_NETWORK_ENABLED);
        filter.addAction(LoggerService.BROADCAST_LOCATION_PERMISSION_DENIED);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * Broadcast receiver
     */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (Logger.DEBUG)
            {
                Log.d(TAG, "[broadcast received " + intent + "]");
            }
            if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_UPDATED))
            {
                updateLocationLabel(LoggerService.lastUpdateRealtime());
                setLocLed(LED_GREEN);
                if (!pref_liveSync)
                {
                    //updateSyncStatus(db.countUnsynced());
                }
            } else if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_STARTED))
            {
                toggleButton.setText(TXT_STOP);
                showToast(getString(R.string.tracking_started));
                setLocLed(LED_YELLOW);
            } else if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_STOPPED))
            {
                toggleButton.setText(TXT_START);
                showToast(getString(R.string.tracking_stopped));
                setLocLed(LED_RED);
            } else if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_GPS_DISABLED))
            {
                showToast(getString(R.string.gps_disabled_warning), Toast.LENGTH_LONG);
            } else if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_NETWORK_DISABLED))
            {
                showToast(getString(R.string.net_disabled_warning), Toast.LENGTH_LONG);
            } else if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_DISABLED))
            {
                showToast(getString(R.string.location_disabled), Toast.LENGTH_LONG);
                setLocLed(LED_RED);
            } else if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_NETWORK_ENABLED))
            {
                showToast(getString(R.string.using_network), Toast.LENGTH_LONG);
            } else if (intent.getAction().equals(LoggerService.BROADCAST_LOCATION_GPS_ENABLED))
            {
                showToast(getString(R.string.using_gps), Toast.LENGTH_LONG);
            }
        }
    };

    /**
     * Check if count matches "few" plurals and language is Polish
     * Hack for API <= 10 where "few" plural is missing for some languages
     * todo: this simple hack currently supports only language "pl"
     *
     * @param i Count
     * @return True if hack is needed, false otherwise
     */
    private boolean needsPluralFewHack(int i)
    {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                && Locale.getDefault().getLanguage().equalsIgnoreCase("pl")
                && (i % 10 >= 2 && i % 10 <= 4)
                && (i % 100 < 12 || i % 100 > 14);
    }
}
