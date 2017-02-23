package net.tscloud.hivenotes.db;

import android.os.Parcelable;

/**
 * Created by tscloud on 3/24/16.
 */
public interface HiveNotesLogDO extends Parcelable {

    long getId();
    void setId(long id);
    long getHive();
    void setHive(long hive);
    long getVisitDate();
    void setVisitDate(long hive);
}
