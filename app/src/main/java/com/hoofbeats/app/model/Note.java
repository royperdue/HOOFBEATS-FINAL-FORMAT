package com.hoofbeats.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by royperdue on 10/7/17.
 */
@Entity
public class Note
{
    @Id
    private Long id;
    private String date;
    private String note;
    private long horseId;

    @ToOne(joinProperty = "horseId")
    private Horse horse;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 363862535)
    private transient NoteDao myDao;

    @Generated(hash = 277618188)
    public Note(Long id, String date, String note, long horseId) {
        this.id = id;
        this.date = date;
        this.note = note;
        this.horseId = horseId;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getHorseId() {
        return this.horseId;
    }

    public void setHorseId(long horseId) {
        this.horseId = horseId;
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
    @Generated(hash = 799086675)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getNoteDao() : null;
    }
}
