package com.hoofbeats.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by royperdue on 10/5/17.
 */
@Entity
public class PlaceMark
{
    @Id
    private Long id;
    private Long trackId;
    private int locationNumber;

    private float locationAltitude;
    private float locationLatitude;
    private float locationLongitude;
    private float locationAccuracy;
    private float locationSpeed;
    private float locationTime;
    private float locationBearing;

    private int locationNumberSatellites;
    private int locationNumberSatellitesUsedFix;
    private int locationType;
    private String locationName;
    @Generated(hash = 168320199)
    public PlaceMark(Long id, Long trackId, int locationNumber,
            float locationAltitude, float locationLatitude, float locationLongitude,
            float locationAccuracy, float locationSpeed, float locationTime,
            float locationBearing, int locationNumberSatellites,
            int locationNumberSatellitesUsedFix, int locationType,
            String locationName) {
        this.id = id;
        this.trackId = trackId;
        this.locationNumber = locationNumber;
        this.locationAltitude = locationAltitude;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationAccuracy = locationAccuracy;
        this.locationSpeed = locationSpeed;
        this.locationTime = locationTime;
        this.locationBearing = locationBearing;
        this.locationNumberSatellites = locationNumberSatellites;
        this.locationNumberSatellitesUsedFix = locationNumberSatellitesUsedFix;
        this.locationType = locationType;
        this.locationName = locationName;
    }
    @Generated(hash = 306796901)
    public PlaceMark() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getTrackId() {
        return this.trackId;
    }
    public void setTrackId(Long trackId) {
        this.trackId = trackId;
    }
    public int getLocationNumber() {
        return this.locationNumber;
    }
    public void setLocationNumber(int locationNumber) {
        this.locationNumber = locationNumber;
    }
    public float getLocationAltitude() {
        return this.locationAltitude;
    }
    public void setLocationAltitude(float locationAltitude) {
        this.locationAltitude = locationAltitude;
    }
    public float getLocationLatitude() {
        return this.locationLatitude;
    }
    public void setLocationLatitude(float locationLatitude) {
        this.locationLatitude = locationLatitude;
    }
    public float getLocationLongitude() {
        return this.locationLongitude;
    }
    public void setLocationLongitude(float locationLongitude) {
        this.locationLongitude = locationLongitude;
    }
    public float getLocationAccuracy() {
        return this.locationAccuracy;
    }
    public void setLocationAccuracy(float locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }
    public float getLocationSpeed() {
        return this.locationSpeed;
    }
    public void setLocationSpeed(float locationSpeed) {
        this.locationSpeed = locationSpeed;
    }
    public float getLocationTime() {
        return this.locationTime;
    }
    public void setLocationTime(float locationTime) {
        this.locationTime = locationTime;
    }
    public float getLocationBearing() {
        return this.locationBearing;
    }
    public void setLocationBearing(float locationBearing) {
        this.locationBearing = locationBearing;
    }
    public int getLocationNumberSatellites() {
        return this.locationNumberSatellites;
    }
    public void setLocationNumberSatellites(int locationNumberSatellites) {
        this.locationNumberSatellites = locationNumberSatellites;
    }
    public int getLocationNumberSatellitesUsedFix() {
        return this.locationNumberSatellitesUsedFix;
    }
    public void setLocationNumberSatellitesUsedFix(
            int locationNumberSatellitesUsedFix) {
        this.locationNumberSatellitesUsedFix = locationNumberSatellitesUsedFix;
    }
    public int getLocationType() {
        return this.locationType;
    }
    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }
    public String getLocationName() {
        return this.locationName;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
