package com.hoofbeats.app.model;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Gpio;
import com.mbientlab.metawear.module.Logging;
import com.mbientlab.metawear.module.SensorFusionBosch;
import com.mbientlab.metawear.module.Settings;

/**
 * Created by royperdue on 12/21/16.
 */
public class Wrapper
{
    private Gpio gpio;
    private Logging logging;
    private Settings settings;
    private MetaWearBoard metaWearBoard;
    private SensorFusionBosch sensorFusionBosch;
    private String hoof;
    private String macAddress;

    public Wrapper(Gpio gpio, SensorFusionBosch sensorFusionBosch, Logging logging, Settings settings,
                   MetaWearBoard metaWearBoard)
    {
        this.gpio = gpio;
        this.logging = logging;
        this.settings = settings;
        this.metaWearBoard = metaWearBoard;
        this.sensorFusionBosch = sensorFusionBosch;
    }

    public Wrapper(String hoof, String macAddress)
    {
        this.hoof = hoof;
        this.macAddress = macAddress;
    }

    public Wrapper(MetaWearBoard metaWearBoard)
    {
        this.metaWearBoard = metaWearBoard;
    }

    public Gpio getGpio()
    {
        return gpio;
    }

    public void setGpio(Gpio gpio)
    {
        this.gpio = gpio;
    }

    public Logging getLogging()
    {
        return logging;
    }

    public void setLogging(Logging logging)
    {
        this.logging = logging;
    }

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    public MetaWearBoard getMetaWearBoard()
    {
        return metaWearBoard;
    }

    public void setMetaWearBoard(MetaWearBoard metaWearBoard)
    {
        this.metaWearBoard = metaWearBoard;
    }

    public SensorFusionBosch getSensorFusionBosch()
    {
        return sensorFusionBosch;
    }

    public void setSensorFusionBosch(SensorFusionBosch sensorFusionBosch)
    {
        this.sensorFusionBosch = sensorFusionBosch;
    }

    public String getHoof()
    {
        return hoof;
    }

    public void setHoof(String hoof)
    {
        this.hoof = hoof;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }
}
