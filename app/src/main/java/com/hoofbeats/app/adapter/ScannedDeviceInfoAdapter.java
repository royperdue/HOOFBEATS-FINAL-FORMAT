package com.hoofbeats.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hoofbeats.app.R;
import com.hoofbeats.app.bluetooth.ScannedDeviceInfo;
import com.hoofbeats.app.utility.DialogUtility;

import java.util.Locale;

public class ScannedDeviceInfoAdapter extends ArrayAdapter<ScannedDeviceInfo>
{
    private static OnDeviceConfiguredListener onDeviceConfiguredListener = null;
    private final static int RSSI_BAR_LEVELS = 5;
    private final static int RSSI_BAR_SCALE = 100 / RSSI_BAR_LEVELS;
    private String hoof;
    private Activity activity;
    private int checkedId = -1;
    private View convertView;

    public interface OnDeviceConfiguredListener
    {
        void onDeviceConfigured(View convertView, ScannedDeviceInfo deviceInfo);
    }

    public ScannedDeviceInfoAdapter(Context context, int resource)
    {
        super(context, resource);

        this.activity = (Activity) context;

        if ((context instanceof OnDeviceConfiguredListener))
            onDeviceConfiguredListener = (OnDeviceConfiguredListener) context;
        else
            throw new ClassCastException(String.format(Locale.US, "%s %s", context.toString(),
                    context.getString(R.string.error_scanned_device_info_adapter)));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.blescan_entry, parent, false);

            this.convertView = convertView;
            viewHolder = new ViewHolder();

            viewHolder.horseName = (TextView) convertView.findViewById(R.id.ble_horse_name);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.ble_device);
            viewHolder.deviceName.setText(R.string.app_name);

            viewHolder.deviceRSSI = (TextView) convertView.findViewById(R.id.ble_rssi_value);
            viewHolder.rssiChart = (ImageView) convertView.findViewById(R.id.ble_rssi_png);
            viewHolder.connectedCheck = (ImageView) convertView.findViewById(R.id.ble_check_connected);
            viewHolder.configureButton = (Button) convertView.findViewById(R.id.configure);
            viewHolder.radioGroupHooves = (RadioGroup) convertView.findViewById(R.id.radio_group_hooves);
            viewHolder.radioGroupHooves.clearCheck();

            viewHolder.radioGroupHooves.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    ScannedDeviceInfoAdapter.this.checkedId = checkedId;
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    if (radioButton != null && checkedId > -1)
                    {
                        if (radioButton.getText().toString().contains("LH"))
                            hoof = "Left Hind";
                        else if (radioButton.getText().toString().contains("LF"))
                            hoof = "Left Front";
                        else if (radioButton.getText().toString().contains("RH"))
                            hoof = "Right Hind";
                        else if (radioButton.getText().toString().contains("RF"))
                            hoof = "Right Front";
                    }
                }
            });

            ScannedDeviceInfo scannedDeviceInfo = getItem(position);

            scannedDeviceInfo.setHoof(hoof);
            scannedDeviceInfo.setHorseNameTextView(viewHolder.horseName);
            scannedDeviceInfo.setActivity(activity);
            scannedDeviceInfo.setConnectedCheck(viewHolder.connectedCheck);
            scannedDeviceInfo.setDeviceName(viewHolder.deviceName);
            scannedDeviceInfo.setRadioGroupHooves(viewHolder.radioGroupHooves);
            scannedDeviceInfo.setDeviceRSSI(viewHolder.deviceRSSI);
            scannedDeviceInfo.setConfigureButton(viewHolder.configureButton);
            scannedDeviceInfo.setRssiChart(viewHolder.rssiChart);

            scannedDeviceInfo.setHoofFromDatabase();

            if (scannedDeviceInfo.getHoof() != null)
            {
                if (scannedDeviceInfo.getHoof().equals("Left Hind"))
                {
                    RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_left_hind);
                    scannedDeviceInfo.setRadioButton(radioButton);
                } else if (scannedDeviceInfo.getHoof().equals("Left Front"))
                {
                    RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_left_front);
                    scannedDeviceInfo.setRadioButton(radioButton);
                } else if (scannedDeviceInfo.getHoof().equals("Right Hind"))
                {
                    RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_right_hind);
                    scannedDeviceInfo.setRadioButton(radioButton);
                } else if (scannedDeviceInfo.getHoof().equals("Right Front"))
                {
                    RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_right_front);
                    scannedDeviceInfo.setRadioButton(radioButton);
                }
            }

            if (scannedDeviceInfo.getHoof() != null)
                scannedDeviceInfo.checkListItemConfiguration();

            viewHolder.deviceRSSI.setText(String.format(Locale.US, "%d dBm", scannedDeviceInfo.rssi));
            viewHolder.rssiChart.setImageLevel(Math.min(RSSI_BAR_LEVELS - 1, (127 + scannedDeviceInfo.rssi + 5) / RSSI_BAR_SCALE));

            viewHolder.configureButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ScannedDeviceInfo deviceInfo = getItem(position);

                    if (hoof != null)
                        deviceInfo.setHoof(hoof);

                    viewHolder.deviceRSSI.setText(String.format(Locale.US, "%d dBm", deviceInfo.rssi));
                    viewHolder.rssiChart.setImageLevel(Math.min(RSSI_BAR_LEVELS - 1, (127 + deviceInfo.rssi + 5) / RSSI_BAR_SCALE));

                    scannedDeviceInfo.setHoof(hoof);
                    scannedDeviceInfo.setHorseNameTextView(viewHolder.horseName);
                    scannedDeviceInfo.setActivity(activity);
                    scannedDeviceInfo.setConnectedCheck(viewHolder.connectedCheck);
                    scannedDeviceInfo.setDeviceName(viewHolder.deviceName);
                    scannedDeviceInfo.setRadioGroupHooves(viewHolder.radioGroupHooves);
                    scannedDeviceInfo.setDeviceRSSI(viewHolder.deviceRSSI);
                    scannedDeviceInfo.setConfigureButton(viewHolder.configureButton);
                    scannedDeviceInfo.setRssiChart(viewHolder.rssiChart);

                    if (scannedDeviceInfo.getHoof().equals("Left Hind"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_left_hind);
                        scannedDeviceInfo.setRadioButton(radioButton);
                    } else if (scannedDeviceInfo.getHoof().equals("Left Front"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_left_front);
                        scannedDeviceInfo.setRadioButton(radioButton);
                    } else if (scannedDeviceInfo.getHoof().equals("Right Hind"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_right_hind);
                        scannedDeviceInfo.setRadioButton(radioButton);
                    } else if (scannedDeviceInfo.getHoof().equals("Right Front"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_right_front);
                        scannedDeviceInfo.setRadioButton(radioButton);
                    }

                    if (((Button) v).getText().toString().equals(activity.getString(R.string.label_horseshoe_assign)))
                        scannedDeviceInfo.assignListItem();
                    else
                        scannedDeviceInfo.removeListItem();
                }
            });

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder
    {
        public TextView horseName;
        public TextView deviceName;
        public TextView deviceRSSI;
        public ImageView rssiChart;
        public ImageView connectedCheck;
        public Button configureButton;
        public RadioGroup radioGroupHooves;
    }

    public void update(ScannedDeviceInfo newInfo)
    {
        int pos = getPosition(newInfo);
        if (pos == -1)
        {
            add(newInfo);
        } else
        {
            getItem(pos).rssi = newInfo.rssi;
            notifyDataSetChanged();
        }
    }

    public void connectAssignedDevices()
    {
        if (getCount() > 0)
        {
            for (int i = 0; i < getCount(); i++)
            {
                if (convertView != null)
                {
                    ScannedDeviceInfo deviceInfo = getItem(i);

                    if (deviceInfo != null)
                        onDeviceConfiguredListener.onDeviceConfigured(convertView, deviceInfo);
                } else
                    DialogUtility.showAlertSnackBarMedium(activity, activity.getString(R.string.message_convertView_null));
            }
        } else
            DialogUtility.showAlertSnackBarMedium(activity, activity.getString(R.string.message_no_horseshoes_assigned));
    }
};
