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

import com.hoofbeats.app.Config;
import com.hoofbeats.app.R;
import com.hoofbeats.app.bluetooth.ScannedDeviceInfo;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.model.Horseshoe;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.LittleDB;

import java.util.List;
import java.util.Locale;

public class ScannedDeviceInfoAdapter extends ArrayAdapter<ScannedDeviceInfo>
{
    private final static int RSSI_BAR_LEVELS = 5;
    private final static int RSSI_BAR_SCALE = 100 / RSSI_BAR_LEVELS;
    private String hoof;
    private Activity activity;
    private int checkedId = -1;

    public ScannedDeviceInfoAdapter(Context context, int resource)
    {
        super(context, resource);

        this.activity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.blescan_entry, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.ble_mac_address);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.ble_device);
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

            ScannedDeviceInfo deviceInfo = getItem(position);

            if (hoof != null)
                deviceInfo.setHoof(hoof);

            for (int i = 0; i < getCount(); i++)
            {
                if (getItem(i).getHoof() != null)
                {
                    if (getItem(i).getHoof().equals("Left Hind"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_left_hind);
                        radioButton.setChecked(true);
                        radioButton.setClickable(false);
                        radioButton.setEnabled(false);
                    } else if (getItem(i).getHoof().equals("Left Front"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_left_front);
                        radioButton.setChecked(true);
                        radioButton.setClickable(false);
                        radioButton.setEnabled(false);
                    } else if (getItem(i).getHoof().equals("Right Hind"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_right_hind);
                        radioButton.setChecked(true);
                        radioButton.setClickable(false);
                        radioButton.setEnabled(false);
                    } else if (getItem(i).getHoof().equals("Right Front"))
                    {
                        RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(R.id.radio_right_front);
                        radioButton.setChecked(true);
                        radioButton.setClickable(false);
                        radioButton.setEnabled(false);
                    }
                }
            }

            List<Horse> horses = DatabaseUtility.retrieveHorseForId(activity, LittleDB.get().getLong(Config.SELECTED_HORSE_ID, -1));

            if (horses.size() > 0)
            {
                List<Horseshoe> horseshoes = horses.get(0).getHorseshoes();

                if (horseshoes.size() > 0)
                {
                    for (int i = 0; i < horseshoes.size(); i++)
                    {
                        if (horseshoes.get(i).getMacAddress().equals(deviceInfo.btDevice.getAddress()))
                            viewHolder.deviceName.setText(horseshoes.get(i).getHoof());

                        if (hoof != null)
                        {
                            if (horseshoes.get(i).getHoof().equals(hoof))
                            {
                                RadioButton radioButton = (RadioButton) viewHolder.radioGroupHooves.findViewById(checkedId);
                                radioButton.setChecked(true);
                                radioButton.setClickable(false);
                                radioButton.setEnabled(false);
                            }
                        }
                    }
                }

                viewHolder.configureButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        DatabaseUtility.addHorseshoeToHorse(horses.get(0), hoof, deviceInfo.btDevice.getAddress());
                    }
                });
            } else
            {
                viewHolder.deviceName.setText(R.string.app_name);
            }

            viewHolder.deviceAddress.setText(deviceInfo.btDevice.getAddress());
            viewHolder.deviceRSSI.setText(String.format(Locale.US, "%d dBm", deviceInfo.rssi));
            viewHolder.rssiChart.setImageLevel(Math.min(RSSI_BAR_LEVELS - 1, (127 + deviceInfo.rssi + 5) / RSSI_BAR_SCALE));
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder
    {
        public TextView deviceAddress;
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
};
