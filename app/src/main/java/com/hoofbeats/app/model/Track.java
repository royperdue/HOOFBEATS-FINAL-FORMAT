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
 * Created by royperdue on 10/5/17.
 */
@Entity
public class Track
{
    @Id
    private Long id;
    private String trackName;
    private String trackDate;

    private long workoutId;

    @ToOne(joinProperty = "workoutId")
    private Workout workout;

    @ToMany(referencedJoinProperty = "trackId")
    private List<Position> positions;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 506689419)
    private transient TrackDao myDao;

    @Generated(hash = 1828103473)
    public Track(Long id, String trackName, String trackDate, long workoutId) {
        this.id = id;
        this.trackName = trackName;
        this.trackDate = trackDate;
        this.workoutId = workoutId;
    }

    @Generated(hash = 1672506944)
    public Track() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackName() {
        return this.trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackDate() {
        return this.trackDate;
    }

    public void setTrackDate(String trackDate) {
        this.trackDate = trackDate;
    }

    public long getWorkoutId() {
        return this.workoutId;
    }

    public void setWorkoutId(long workoutId) {
        this.workoutId = workoutId;
    }

    @Generated(hash = 1754015077)
    private transient Long workout__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 909503565)
    public Workout getWorkout() {
        long __key = this.workoutId;
        if (workout__resolvedKey == null || !workout__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WorkoutDao targetDao = daoSession.getWorkoutDao();
            Workout workoutNew = targetDao.load(__key);
            synchronized (this) {
                workout = workoutNew;
                workout__resolvedKey = __key;
            }
        }
        return workout;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1816652435)
    public void setWorkout(@NotNull Workout workout) {
        if (workout == null) {
            throw new DaoException(
                    "To-one property 'workoutId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.workout = workout;
            workoutId = workout.getId();
            workout__resolvedKey = workoutId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1248358576)
    public List<Position> getPositions() {
        if (positions == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PositionDao targetDao = daoSession.getPositionDao();
            List<Position> positionsNew = targetDao._queryTrack_Positions(id);
            synchronized (this) {
                if (positions == null) {
                    positions = positionsNew;
                }
            }
        }
        return positions;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1228876919)
    public synchronized void resetPositions() {
        positions = null;
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
    @Generated(hash = 1269964033)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTrackDao() : null;
    }
}
