package com.hoofbeats.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by royperdue on 3/15/17.
 */
@Entity
public class Horse {
    @Id
    private Long id;

    private String horseName;
    private double horseAge;
    private double horseHeight;
    private double horseWeight;
    private String discipline;
    private String horseColor;
    private String horseBreed;
    private String horseSex;
    private String profilePictureURI;

    @ToMany(referencedJoinProperty = "horseId")
    private List<Horseshoe> horseshoes;

    @ToMany(referencedJoinProperty = "horseId")
    private List<AssignmentRecord> assignmentRecords;

    @ToMany(referencedJoinProperty = "horseId")
    private List<Workout> workouts;

    @ToMany(referencedJoinProperty = "horseId")
    private List<Note> notes;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 310510713)
    private transient HorseDao myDao;

    @Generated(hash = 1644312935)
    public Horse(Long id, String horseName, double horseAge, double horseHeight,
            double horseWeight, String discipline, String horseColor,
            String horseBreed, String horseSex, String profilePictureURI) {
        this.id = id;
        this.horseName = horseName;
        this.horseAge = horseAge;
        this.horseHeight = horseHeight;
        this.horseWeight = horseWeight;
        this.discipline = discipline;
        this.horseColor = horseColor;
        this.horseBreed = horseBreed;
        this.horseSex = horseSex;
        this.profilePictureURI = profilePictureURI;
    }

    @Generated(hash = 1301553216)
    public Horse() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHorseName() {
        return this.horseName;
    }

    public void setHorseName(String horseName) {
        this.horseName = horseName;
    }

    public double getHorseAge() {
        return this.horseAge;
    }

    public void setHorseAge(double horseAge) {
        this.horseAge = horseAge;
    }

    public double getHorseHeight() {
        return this.horseHeight;
    }

    public void setHorseHeight(double horseHeight) {
        this.horseHeight = horseHeight;
    }

    public double getHorseWeight() {
        return this.horseWeight;
    }

    public void setHorseWeight(double horseWeight) {
        this.horseWeight = horseWeight;
    }

    public String getDiscipline() {
        return this.discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getHorseColor() {
        return this.horseColor;
    }

    public void setHorseColor(String horseColor) {
        this.horseColor = horseColor;
    }

    public String getHorseBreed() {
        return this.horseBreed;
    }

    public void setHorseBreed(String horseBreed) {
        this.horseBreed = horseBreed;
    }

    public String getHorseSex() {
        return this.horseSex;
    }

    public void setHorseSex(String horseSex) {
        this.horseSex = horseSex;
    }

    public String getProfilePictureURI() {
        return this.profilePictureURI;
    }

    public void setProfilePictureURI(String profilePictureURI) {
        this.profilePictureURI = profilePictureURI;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 607915848)
    public List<Horseshoe> getHorseshoes() {
        if (horseshoes == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            HorseshoeDao targetDao = daoSession.getHorseshoeDao();
            List<Horseshoe> horseshoesNew = targetDao._queryHorse_Horseshoes(id);
            synchronized (this) {
                if (horseshoes == null) {
                    horseshoes = horseshoesNew;
                }
            }
        }
        return horseshoes;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1779725581)
    public synchronized void resetHorseshoes() {
        horseshoes = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1249535370)
    public List<AssignmentRecord> getAssignmentRecords() {
        if (assignmentRecords == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AssignmentRecordDao targetDao = daoSession.getAssignmentRecordDao();
            List<AssignmentRecord> assignmentRecordsNew = targetDao
                    ._queryHorse_AssignmentRecords(id);
            synchronized (this) {
                if (assignmentRecords == null) {
                    assignmentRecords = assignmentRecordsNew;
                }
            }
        }
        return assignmentRecords;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 162950680)
    public synchronized void resetAssignmentRecords() {
        assignmentRecords = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1555881461)
    public List<Workout> getWorkouts() {
        if (workouts == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WorkoutDao targetDao = daoSession.getWorkoutDao();
            List<Workout> workoutsNew = targetDao._queryHorse_Workouts(id);
            synchronized (this) {
                if (workouts == null) {
                    workouts = workoutsNew;
                }
            }
        }
        return workouts;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 586856850)
    public synchronized void resetWorkouts() {
        workouts = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1588731595)
    public List<Note> getNotes() {
        if (notes == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            NoteDao targetDao = daoSession.getNoteDao();
            List<Note> notesNew = targetDao._queryHorse_Notes(id);
            synchronized (this) {
                if (notes == null) {
                    notes = notesNew;
                }
            }
        }
        return notes;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2032098259)
    public synchronized void resetNotes() {
        notes = null;
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
    @Generated(hash = 1546051264)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getHorseDao() : null;
    }
}
