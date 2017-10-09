package com.hoofbeats.app.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hoofbeats.app.Config;
import com.hoofbeats.app.R;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.model.Horseshoe;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.DialogUtility;
import com.hoofbeats.app.utility.LittleDB;

public class ScannedDeviceInfo
{
    public BluetoothDevice btDevice;
    public int rssi;
    public String hoof;
    public TextView deviceName;
    public TextView deviceRSSI;
    public ImageView rssiChart;
    public ImageView connectedCheck;
    public Button configureButton;
    public RadioGroup radioGroupHooves;
    public RadioButton radioButton;
    public Activity activity;
    public boolean isConnected = false;
    public boolean isAssigned = false;

    public ScannedDeviceInfo(Activity activity)
    {
        this.activity = activity;
    }

    public ScannedDeviceInfo(BluetoothDevice btDevice, int rssi)
    {
        this.btDevice = btDevice;
        this.rssi = rssi;
    }

    public ScannedDeviceInfo(BluetoothDevice btDevice, int rssi, String hoof)
    {
        this.btDevice = btDevice;
        this.rssi = rssi;
        this.hoof = hoof;
    }

    public ScannedDeviceInfo(BluetoothDevice btDevice, int rssi, String hoof, TextView deviceName, TextView deviceRSSI, ImageView rssiChart, ImageView connectedCheck, Button configureButton, RadioGroup radioGroupHooves, RadioButton radioButton)
    {
        this.btDevice = btDevice;
        this.rssi = rssi;
        this.hoof = hoof;
        this.deviceName = deviceName;
        this.deviceRSSI = deviceRSSI;
        this.rssiChart = rssiChart;
        this.connectedCheck = connectedCheck;
        this.configureButton = configureButton;
        this.radioGroupHooves = radioGroupHooves;
        this.radioButton = radioButton;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj == this) ||
                ((obj instanceof ScannedDeviceInfo) && btDevice.equals(((ScannedDeviceInfo) obj).btDevice));
    }

    public BluetoothDevice getBtDevice()
    {
        return btDevice;
    }

    public int getRssi()
    {
        return rssi;
    }

    public void setRssi(int rssi)
    {
        this.rssi = rssi;
    }

    public String getHoof()
    {
        return hoof;
    }

    public void setHoof(String hoof)
    {
        this.hoof = hoof;
    }

    public TextView getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(TextView deviceName)
    {
        this.deviceName = deviceName;
    }

    public TextView getDeviceRSSI()
    {
        return deviceRSSI;
    }

    public void setDeviceRSSI(TextView deviceRSSI)
    {
        this.deviceRSSI = deviceRSSI;
    }

    public ImageView getRssiChart()
    {
        return rssiChart;
    }

    public void setRssiChart(ImageView rssiChart)
    {
        this.rssiChart = rssiChart;
    }

    public ImageView getConnectedCheck()
    {
        return connectedCheck;
    }

    public void setConnectedCheck(ImageView connectedCheck)
    {
        this.connectedCheck = connectedCheck;
    }

    public Button getConfigureButton()
    {
        return configureButton;
    }

    public void setConfigureButton(Button configureButton)
    {
        this.configureButton = configureButton;
    }

    public RadioGroup getRadioGroupHooves()
    {
        return radioGroupHooves;
    }

    public void setRadioGroupHooves(RadioGroup radioGroupHooves)
    {
        this.radioGroupHooves = radioGroupHooves;
    }

    public RadioButton getRadioButton()
    {
        return radioButton;
    }

    public void setRadioButton(RadioButton radioButton)
    {
        this.radioButton = radioButton;
    }

    public Activity getActivity()
    {
        return activity;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void setConnected(boolean connected)
    {
        isConnected = connected;
    }

    public void setHoofFromDatabase()
    {
        Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(btDevice.getAddress());

        if (horseshoe != null)
        {
            hoof = horseshoe.getHoof();
        }
    }

    public void checkListItemConfiguration()
    {
        Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(btDevice.getAddress());

        if (horseshoe != null)
        {
            deviceName.setText(horseshoe.getHoof());
            radioButton.setChecked(true);
            radioButton.setClickable(false);
            radioButton.setEnabled(false);
            configureButton.setText(activity.getString(R.string.label_horseshoe_remove));
            isAssigned = true;

        }
    }

    public void assignListItem()
    {
        System.out.println("----Assign---");

        Horse horse = DatabaseUtility.retrieveHorseForId(LittleDB.get().getLong(Config.SELECTED_HORSE_ID, -1));
        DatabaseUtility.addHorseshoeToHorse(horse, hoof, btDevice.getAddress());

        Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(btDevice.getAddress());

        if (horseshoe != null)
        {
            deviceName.setText(hoof);
            radioButton.setChecked(true);
            radioButton.setClickable(false);
            radioButton.setEnabled(false);
            configureButton.setText(activity.getString(R.string.label_horseshoe_remove));
            isAssigned = true;
            DialogUtility.showNoticeSnackBarShort(activity, activity.getString(R.string.message_successful));
        } else
            DialogUtility.showAlertSnackBarMedium(activity, activity.getString(R.string.message_unsuccessful));
    }

    public void removeListItem()
    {
        System.out.println("----Remove---");

        DatabaseUtility.removeHorseshoeMacAddress(btDevice.getAddress());
        Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(btDevice.getAddress());

        if (horseshoe == null)
        {
            deviceName.setText(activity.getString(R.string.app_name));
            radioButton.setChecked(false);
            radioButton.setClickable(true);
            radioButton.setEnabled(true);
            configureButton.setText(activity.getString(R.string.label_horseshoe_assign));
            isAssigned = false;
            DialogUtility.showNoticeSnackBarShort(activity, activity.getString(R.string.message_successful));
        } else
            DialogUtility.showAlertSnackBarMedium(activity, activity.getString(R.string.message_unsuccessful));
    }

    public void setColorCheckMark()
    {
        if (isConnected)
            connectedCheck.setImageDrawable(activity.getDrawable(R.drawable.ic_check_circle_green_600_24dp));
        else
            connectedCheck.setImageDrawable(activity.getDrawable(R.drawable.ic_check_circle_red_700_36dp));

        connectedCheck.invalidate();
    }
}
