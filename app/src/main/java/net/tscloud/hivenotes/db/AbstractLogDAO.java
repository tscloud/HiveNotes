package net.tscloud.hivenotes.db;

import android.content.Context;

/**
 * Created by tscloud on 2/14/17.
 */
public abstract class AbstractLogDAO extends AbstractDAO {

    public static final String TAG = "AbstractLogDAO";

    public AbstractLogDAO(Context context) {
        super(context);
    }

    public abstract HiveNotesLogDO getLogEntryById(long aID);

    public abstract HiveNotesLogDO getLogEntryByDate(long aDate);
}