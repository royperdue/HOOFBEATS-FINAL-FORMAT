package com.hoofbeats.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by royperdue on 3/15/17.
 */
@Entity
public class Workout {
    @Id
    private Long id;
    private long horseId;
    private String date;
    private String startTime;
    private String endTime;

    @ToOne(joinProperty = "horseId")
    private Horse horse;

    @ToMany(referencedJoinProperty = "workoutId")
    private List<Reading> readings;

    @ToMany(referencedJoinProperty = "workoutId")
    private List<Track> tracks;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1950649078)
    private transient WorkoutDao myDao;

    @Generated(hash = 982297545)
    public Workout(Long id, long horseId, String date, String startTime,
            String endTime) {
        this.id = id;
        this.horseId = horseId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Generated(hash = 570607860)
    public Workout() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getHorseId() {
        return this.horseId;
    }

    public void setHorseId(long horseId) {
        this.horseId = horseId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Generated(hash = 809391966)
    private transient Long horse__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 141299770)
    public Horse getHorse() {
        long __key = this.horseId;
        if (horse__resolvedKey == null || !horse__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            HorseDao targetDao = daoSession.getHorseDao();
            Horse horseNew = targetDao.load(__key);
            synchronized (this) {
                horse = horseNew;
                horse__resolvedKey = __key;
            }
        }
        return horse;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2041230005)
    public void setHorse(@NotNull Horse horse) {
        if (horse == null) {
            throw new DaoException(
                    "To-one property 'horseId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.horse = horse;
            horseId = horse.getId();
            horse__resolvedKey = horseId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 995895700)
    public List<Reading> getReadings() {
        if (readings == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReadingDao targetDao = daoSession.getReadingDao();
            List<Reading> readingsNew = targetDao._queryWorkout_Readings(id);
            synchronized (this) {
                if (readings == null) {
                    readings = readingsNew;
                }
            }
        }
        return readings;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1079093920)
    public synchronized void resetReadings() {
        readings = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 22376255)
    public List<Track> getTracks() {
        if (tracks == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TrackDao targetDao = daoSession.getTrackDao();
            List<Track> tracksNew = targetDao._queryWorkout_Tracks(id);
            synchronized (this) {
                if (tracks == null) {
                    tracks = tracksNew;
                }
            }
        }
        return tracks;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1878244390)
    public synchronized void resetTracks() {
        tracks = null;
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
    @Generated(hash = 1398188052)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWorkoutDao() : null;
    }
}