package com.hoofbeats.app.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

/**
 * Created by royperdue on 10/5/17.
 */
@Entity
public class Track
{
    @Id
    private Long id;
    private String trackName;
    private String trackTo;
    private String trackFrom;
    private float trackStartAltitude;
    private float trackStartLatitude;
    private float trackStartLongitude;
    private float trackStartAccuracy;
    private float trackStartSpeed;
    private float trackStartTime;
    private float trackLastFixTime;
    private float trackEndAltitude;
    private float trackEndLatitude;
    private float trackEndLongitude;
    private float trackEndAccuracy;
    private float trackEndSpeed;
    private float trackEndTime;

    private float trackLastStepDistAltitude;
    private float trackLastStepDistLatitude;
    private float trackLastStepDistLongitude;
    private float trackLastStepDistAccuracy;

    private float trackMinLatitude;
    private float trackMinLongitude;

    private float trackMaxLatitude;
    private float trackMaxLongitude;

    private float trackDuration;
    private float trackDurationMoving;

    private float trackDistance;
    private float trackDistanceInProgress;
    private float trackDistanceLastAltitude;

    private float trackAltitudeUp;
    private float trackAltitudeDown;
    private float trackAltitudeInProgress;

    private float trackSpeedMax;
    private float trackSpeedAverageMoving;
    private float trackSpeedAverage;

    private int trackNumberLocations;
    private int trackNumberPlaceMarks;
    private int trackValidMap;
    private int trackType;

    private long workoutId;

    @ToOne(joinProperty = "workoutId")
    private Workout workout;

    @ToMany(referencedJoinProperty = "trackId")
    private List<Location> locations;

    @ToMany(referencedJoinProperty = "trackId")
    private List<PlaceMark> placeMarks;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 506689419)
    private transient TrackDao myDao;

    @Generated(hash = 289722372)
    public Track(Long id, String trackName, String trackTo, String trackFrom,
            float trackStartAltitude, float trackStartLatitude,
            float trackStartLongitude, float trackStartAccuracy,
            float trackStartSpeed, float trackStartTime, float trackLastFixTime,
            float trackEndAltitude, float trackEndLatitude, float trackEndLongitude,
            float trackEndAccuracy, float trackEndSpeed, float trackEndTime,
            float trackLastStepDistAltitude, float trackLastStepDistLatitude,
            float trackLastStepDistLongitude, float trackLastStepDistAccuracy,
            float trackMinLatitude, float trackMinLongitude, float trackMaxLatitude,
            float trackMaxLongitude, float trackDuration, float trackDurationMoving,
            float trackDistance, float trackDistanceInProgress,
            float trackDistanceLastAltitude, float trackAltitudeUp,
            float trackAltitudeDown, float trackAltitudeInProgress,
            float trackSpeedMax, float trackSpeedAverageMoving,
            float trackSpeedAverage, int trackNumberLocations,
            int trackNumberPlaceMarks, int trackValidMap, int trackType,
            long workoutId) {
        this.id = id;
        this.trackName = trackName;
        this.trackTo = trackTo;
        this.trackFrom = trackFrom;
        this.trackStartAltitude = trackStartAltitude;
        this.trackStartLatitude = trackStartLatitude;
        this.trackStartLongitude = trackStartLongitude;
        this.trackStartAccuracy = trackStartAccuracy;
        this.trackStartSpeed = trackStartSpeed;
        this.trackStartTime = trackStartTime;
        this.trackLastFixTime = trackLastFixTime;
        this.trackEndAltitude = trackEndAltitude;
        this.trackEndLatitude = trackEndLatitude;
        this.trackEndLongitude = trackEndLongitude;
        this.trackEndAccuracy = trackEndAccuracy;
        this.trackEndSpeed = trackEndSpeed;
        this.trackEndTime = trackEndTime;
        this.trackLastStepDistAltitude = trackLastStepDistAltitude;
        this.trackLastStepDistLatitude = trackLastStepDistLatitude;
        this.trackLastStepDistLongitude = trackLastStepDistLongitude;
        this.trackLastStepDistAccuracy = trackLastStepDistAccuracy;
        this.trackMinLatitude = trackMinLatitude;
        this.trackMinLongitude = trackMinLongitude;
        this.trackMaxLatitude = trackMaxLatitude;
        this.trackMaxLongitude = trackMaxLongitude;
        this.trackDuration = trackDuration;
        this.trackDurationMoving = trackDurationMoving;
        this.trackDistance = trackDistance;
        this.trackDistanceInProgress = trackDistanceInProgress;
        this.trackDistanceLastAltitude = trackDistanceLastAltitude;
        this.trackAltitudeUp = trackAltitudeUp;
        this.trackAltitudeDown = trackAltitudeDown;
        this.trackAltitudeInProgress = trackAltitudeInProgress;
        this.trackSpeedMax = trackSpeedMax;
        this.trackSpeedAverageMoving = trackSpeedAverageMoving;
        this.trackSpeedAverage = trackSpeedAverage;
        this.trackNumberLocations = trackNumberLocations;
        this.trackNumberPlaceMarks = trackNumberPlaceMarks;
        this.trackValidMap = trackValidMap;
        this.trackType = trackType;
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

    public String getTrackTo() {
        return this.trackTo;
    }

    public void setTrackTo(String trackTo) {
        this.trackTo = trackTo;
    }

    public String getTrackFrom() {
        return this.trackFrom;
    }

    public void setTrackFrom(String trackFrom) {
        this.trackFrom = trackFrom;
    }

    public float getTrackStartAltitude() {
        return this.trackStartAltitude;
    }

    public void setTrackStartAltitude(float trackStartAltitude) {
        this.trackStartAltitude = trackStartAltitude;
    }

    public float getTrackStartLatitude() {
        return this.trackStartLatitude;
    }

    public void setTrackStartLatitude(float trackStartLatitude) {
        this.trackStartLatitude = trackStartLatitude;
    }

    public float getTrackStartLongitude() {
        return this.trackStartLongitude;
    }

    public void setTrackStartLongitude(float trackStartLongitude) {
        this.trackStartLongitude = trackStartLongitude;
    }

    public float getTrackStartAccuracy() {
        return this.trackStartAccuracy;
    }

    public void setTrackStartAccuracy(float trackStartAccuracy) {
        this.trackStartAccuracy = trackStartAccuracy;
    }

    public float getTrackStartSpeed() {
        return this.trackStartSpeed;
    }

    public void setTrackStartSpeed(float trackStartSpeed) {
        this.trackStartSpeed = trackStartSpeed;
    }

    public float getTrackStartTime() {
        return this.trackStartTime;
    }

    public void setTrackStartTime(float trackStartTime) {
        this.trackStartTime = trackStartTime;
    }

    public float getTrackLastFixTime() {
        return this.trackLastFixTime;
    }

    public void setTrackLastFixTime(float trackLastFixTime) {
        this.trackLastFixTime = trackLastFixTime;
    }

    public float getTrackEndAltitude() {
        return this.trackEndAltitude;
    }

    public void setTrackEndAltitude(float trackEndAltitude) {
        this.trackEndAltitude = trackEndAltitude;
    }

    public float getTrackEndLatitude() {
        return this.trackEndLatitude;
    }

    public void setTrackEndLatitude(float trackEndLatitude) {
        this.trackEndLatitude = trackEndLatitude;
    }

    public float getTrackEndLongitude() {
        return this.trackEndLongitude;
    }

    public void setTrackEndLongitude(float trackEndLongitude) {
        this.trackEndLongitude = trackEndLongitude;
    }

    public float getTrackEndAccuracy() {
        return this.trackEndAccuracy;
    }

    public void setTrackEndAccuracy(float trackEndAccuracy) {
        this.trackEndAccuracy = trackEndAccuracy;
    }

    public float getTrackEndSpeed() {
        return this.trackEndSpeed;
    }

    public void setTrackEndSpeed(float trackEndSpeed) {
        this.trackEndSpeed = trackEndSpeed;
    }

    public float getTrackEndTime() {
        return this.trackEndTime;
    }

    public void setTrackEndTime(float trackEndTime) {
        this.trackEndTime = trackEndTime;
    }

    public float getTrackLastStepDistAltitude() {
        return this.trackLastStepDistAltitude;
    }

    public void setTrackLastStepDistAltitude(float trackLastStepDistAltitude) {
        this.trackLastStepDistAltitude = trackLastStepDistAltitude;
    }

    public float getTrackLastStepDistLatitude() {
        return this.trackLastStepDistLatitude;
    }

    public void setTrackLastStepDistLatitude(float trackLastStepDistLatitude) {
        this.trackLastStepDistLatitude = trackLastStepDistLatitude;
    }

    public float getTrackLastStepDistLongitude() {
        return this.trackLastStepDistLongitude;
    }

    public void setTrackLastStepDistLongitude(float trackLastStepDistLongitude) {
        this.trackLastStepDistLongitude = trackLastStepDistLongitude;
    }

    public float getTrackLastStepDistAccuracy() {
        return this.trackLastStepDistAccuracy;
    }

    public void setTrackLastStepDistAccuracy(float trackLastStepDistAccuracy) {
        this.trackLastStepDistAccuracy = trackLastStepDistAccuracy;
    }

    public float getTrackMinLatitude() {
        return this.trackMinLatitude;
    }

    public void setTrackMinLatitude(float trackMinLatitude) {
        this.trackMinLatitude = trackMinLatitude;
    }

    public float getTrackMinLongitude() {
        return this.trackMinLongitude;
    }

    public void setTrackMinLongitude(float trackMinLongitude) {
        this.trackMinLongitude = trackMinLongitude;
    }

    public float getTrackMaxLatitude() {
        return this.trackMaxLatitude;
    }

    public void setTrackMaxLatitude(float trackMaxLatitude) {
        this.trackMaxLatitude = trackMaxLatitude;
    }

    public float getTrackMaxLongitude() {
        return this.trackMaxLongitude;
    }

    public void setTrackMaxLongitude(float trackMaxLongitude) {
        this.trackMaxLongitude = trackMaxLongitude;
    }

    public float getTrackDuration() {
        return this.trackDuration;
    }

    public void setTrackDuration(float trackDuration) {
        this.trackDuration = trackDuration;
    }

    public float getTrackDurationMoving() {
        return this.trackDurationMoving;
    }

    public void setTrackDurationMoving(float trackDurationMoving) {
        this.trackDurationMoving = trackDurationMoving;
    }

    public float getTrackDistance() {
        return this.trackDistance;
    }

    public void setTrackDistance(float trackDistance) {
        this.trackDistance = trackDistance;
    }

    public float getTrackDistanceInProgress() {
        return this.trackDistanceInProgress;
    }

    public void setTrackDistanceInProgress(float trackDistanceInProgress) {
        this.trackDistanceInProgress = trackDistanceInProgress;
    }

    public float getTrackDistanceLastAltitude() {
        return this.trackDistanceLastAltitude;
    }

    public void setTrackDistanceLastAltitude(float trackDistanceLastAltitude) {
        this.trackDistanceLastAltitude = trackDistanceLastAltitude;
    }

    public float getTrackAltitudeUp() {
        return this.trackAltitudeUp;
    }

    public void setTrackAltitudeUp(float trackAltitudeUp) {
        this.trackAltitudeUp = trackAltitudeUp;
    }

    public float getTrackAltitudeDown() {
        return this.trackAltitudeDown;
    }

    public void setTrackAltitudeDown(float trackAltitudeDown) {
        this.trackAltitudeDown = trackAltitudeDown;
    }

    public float getTrackAltitudeInProgress() {
        return this.trackAltitudeInProgress;
    }

    public void setTrackAltitudeInProgress(float trackAltitudeInProgress) {
        this.trackAltitudeInProgress = trackAltitudeInProgress;
    }

    public float getTrackSpeedMax() {
        return this.trackSpeedMax;
    }

    public void setTrackSpeedMax(float trackSpeedMax) {
        this.trackSpeedMax = trackSpeedMax;
    }

    public float getTrackSpeedAverageMoving() {
        return this.trackSpeedAverageMoving;
    }

    public void setTrackSpeedAverageMoving(float trackSpeedAverageMoving) {
        this.trackSpeedAverageMoving = trackSpeedAverageMoving;
    }

    public float getTrackSpeedAverage() {
        return this.trackSpeedAverage;
    }

    public void setTrackSpeedAverage(float trackSpeedAverage) {
        this.trackSpeedAverage = trackSpeedAverage;
    }

    public int getTrackNumberLocations() {
        return this.trackNumberLocations;
    }

    public void setTrackNumberLocations(int trackNumberLocations) {
        this.trackNumberLocations = trackNumberLocations;
    }

    public int getTrackNumberPlaceMarks() {
        return this.trackNumberPlaceMarks;
    }

    public void setTrackNumberPlaceMarks(int trackNumberPlaceMarks) {
        this.trackNumberPlaceMarks = trackNumberPlaceMarks;
    }

    public int getTrackValidMap() {
        return this.trackValidMap;
    }

    public void setTrackValidMap(int trackValidMap) {
        this.trackValidMap = trackValidMap;
    }

    public int getTrackType() {
        return this.trackType;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
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
    @Generated(hash = 1289778146)
    public List<Location> getLocations() {
        if (locations == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LocationDao targetDao = daoSession.getLocationDao();
            List<Location> locationsNew = targetDao._queryTrack_Locations(id);
            synchronized (this) {
                if (locations == null) {
                    locations = locationsNew;
                }
            }
        }
        return locations;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1398170159)
    public synchronized void resetLocations() {
        locations = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1257581687)
    public List<PlaceMark> getPlaceMarks() {
        if (placeMarks == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlaceMarkDao targetDao = daoSession.getPlaceMarkDao();
            List<PlaceMark> placeMarksNew = targetDao._queryTrack_PlaceMarks(id);
            synchronized (this) {
                if (placeMarks == null) {
                    placeMarks = placeMarksNew;
                }
            }
        }
        return placeMarks;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 95543093)
    public synchronized void resetPlaceMarks() {
        placeMarks = null;
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
