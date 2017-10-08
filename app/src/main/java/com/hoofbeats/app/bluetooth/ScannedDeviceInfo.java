package com.hoofbeats.app.bluetooth;

import android.bluetooth.BluetoothDevice;

public class ScannedDeviceInfo {
    public final BluetoothDevice btDevice;
    public int rssi;
    private String hoof;

    public ScannedDeviceInfo(BluetoothDevice btDevice, int rssi, String hoof) {
        this.btDevice= btDevice;
        this.rssi= rssi;
        this.hoof = hoof;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this) ||
                ((obj instanceof ScannedDeviceInfo) && btDevice.equals(((ScannedDeviceInfo) obj).btDevice));
    }

    public String getHoof()
    {
        return hoof;
    }

    public void setHoof(String hoof)
    {
        this.hoof = hoof;
    }
}
