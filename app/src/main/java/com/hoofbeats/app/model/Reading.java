package com.hoofbeats.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by royperdue on 3/15/17.
 */
@Entity
public class Reading
{
    @Id
    private Long id;
    private long workoutId;
    private String hoof;
    private float xValueLinearAcceleration;
    private float yValueLinearAcceleration;
    private float zValueLinearAcceleration;
    private short force;
    private long timestamp;

    @ToOne(joinProperty = "workoutId")
    private Workout workout;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1387097782)
    private transient ReadingDao myDao;

    @Generated(hash = 2126415799)
    public Reading(Long id, long workoutId, String hoof,
            float xValueLinearAcceleration, float yValueLinearAcceleration,
            float zValueLinearAcceleration, short force, long timestamp) {
        this.id = id;
        this.workoutId = workoutId;
        this.hoof = hoof;
        this.xValueLinearAcceleration = xValueLinearAcceleration;
        this.yValueLinearAcceleration = yValueLinearAcceleration;
        this.zValueLinearAcceleration = zValueLinearAcceleration;
        this.force = force;
        this.timestamp = timestamp;
    }

    @Generated(hash = 1633136157)
    public Reading() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getWorkoutId() {
        return this.workoutId;
    }

    public void setWorkoutId(long workoutId) {
        this.workoutId = workoutId;
    }

    public String getHoof() {
        return this.hoof;
    }

    public void setHoof(String hoof) {
        this.hoof = hoof;
    }

    public float getXValueLinearAcceleration() {
        return this.xValueLinearAcceleration;
    }

    public void setXValueLinearAcceleration(float xValueLinearAcceleration) {
        this.xValueLinearAcceleration = xValueLinearAcceleration;
    }

    public float getYValueLinearAcceleration() {
        return this.yValueLinearAcceleration;
    }

    public void setYValueLinearAcceleration(float yValueLinearAcceleration) {
        this.yValueLinearAcceleration = yValueLinearAcceleration;
    }

    public float getZValueLinearAcceleration() {
        return this.zValueLinearAcceleration;
    }

    public void setZValueLinearAcceleration(float zValueLinearAcceleration) {
        this.zValueLinearAcceleration = zValueLinearAcceleration;
    }

    public short getForce() {
        return this.force;
    }

    public void setForce(short force) {
        this.force = force;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
    @Generated(hash = 539256992)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getReadingDao() : null;
    }
}