package net.tscloud.hivenotes.db;

import java.io.Serializable;

/**
 * Created by tscloud on 3/24/16.
 */
public interface HiveNotesLogDO {

    long getId();
    void setId(long id);
    long getHive();
    void setHive(long hive);
}
