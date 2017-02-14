package net.tscloud.hivenotes.db;

/**
 * Created by tscloud on 2/14/17.
 */
public abstract class AbstactLogDAO extends AbstactDAO {

    public static final String TAG = "AbstactLogDAO";

    public abstract HiveNotesLogDO getLogEntryById(long aID);

    public abstract HiveNotesLogDO getLogEntryByDate(long aDate);
}