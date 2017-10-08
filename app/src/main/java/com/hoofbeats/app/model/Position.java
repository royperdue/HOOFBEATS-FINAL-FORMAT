package com.hoofbeats.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by royperdue on 10/5/17.
 */
@Entity
public class Position
{
    @Id
    private Long id;
    private Long trackId;

    private String positionAltitude;
    private String positionLatitude;
    private String positionLongitude;
    private String positionAccuracy;
    private String positionSpeed;
    private String positionTime;
    private String positionBearing;

    @ToOne(joinProperty = "trackId")
    private Track track;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 322506926)
    private transient PositionDao myDao;

    @Generated(hash = 1527890495)
    public Position(Long id, Long trackId, String positionAltitude,
            String positionLatitude, String positionLongitude,
            String positionAccuracy, String positionSpeed, String positionTime,
            String positionBearing) {
        this.id = id;
        this.trackId = trackId;
        this.positionAltitude = positionAltitude;
        this.positionLatitude = positionLatitude;
        this.positionLongitude = positionLongitude;
        this.positionAccuracy = positionAccuracy;
        this.positionSpeed = positionSpeed;
        this.positionTime = positionTime;
        this.positionBearing = positionBearing;
    }

    @Generated(hash = 958937587)
    public Position() {
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

    public String getPositionAltitude() {
        return this.positionAltitude;
    }

    public void setPositionAltitude(String positionAltitude) {
        this.positionAltitude = positionAltitude;
    }

    public String getPositionLatitude() {
        return this.positionLatitude;
    }

    public void setPositionLatitude(String positionLatitude) {
        this.positionLatitude = positionLatitude;
    }

    public String getPositionLongitude() {
        return this.positionLongitude;
    }

    public void setPositionLongitude(String positionLongitude) {
        this.positionLongitude = positionLongitude;
    }

    public String getPositionAccuracy() {
        return this.positionAccuracy;
    }

    public void setPositionAccuracy(String positionAccuracy) {
        this.positionAccuracy = positionAccuracy;
    }

    public String getPositionSpeed() {
        return this.positionSpeed;
    }

    public void setPositionSpeed(String positionSpeed) {
        this.positionSpeed = positionSpeed;
    }

    public String getPositionTime() {
        return this.positionTime;
    }

    public void setPositionTime(String positionTime) {
        this.positionTime = positionTime;
    }

    public String getPositionBearing() {
        return this.positionBearing;
    }

    public void setPositionBearing(String positionBearing) {
        this.positionBearing = positionBearing;
    }

    @Generated(hash = 668638957)
    private transient Long track__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1592264412)
    public Track getTrack() {
        Long __key = this.trackId;
        if (track__resolvedKey == null || !track__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TrackDao targetDao = daoSession.getTrackDao();
            Track trackNew = targetDao.load(__key);
            synchronized (this) {
                track = trackNew;
                track__resolvedKey = __key;
            }
        }
        return track;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 896036592)
    public void setTrack(Track track) {
        synchronized (this) {
            this.track = track;
            trackId = track == null ? null : track.getId();
            track__resolvedKey = trackId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 741478416)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPositionDao() : null;
    }
}
