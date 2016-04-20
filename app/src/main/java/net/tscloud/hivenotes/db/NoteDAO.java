package net.tscloud.hivenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tscloud on 8/15/15.
 */
public class NoteDAO {

    public static final String TAG = "NoteDAO";

    // Database table columns
    // columns of the Profile table
    public static final String TABLE_NOTE = "Note";
    public static final String COLUMN_NOTE_ID = "_id";
    public static final String COLUMN_NOTE_APIARY = "apiary";
    public static final String COLUMN_NOTE_HIVE = "hive";
    public static final String COLUMN_NOTE_TYPE = "type";
    public static final String COLUMN_NOTE_NOTE_TEXT = "note_text";

    // Database fields
    private SQLiteDatabase mDatabase;
    private MyDBHandler mDbHelper;
    private Context mContext;
    private String[] mAllColumns = { COLUMN_NOTE_ID, COLUMN_NOTE_APIARY, COLUMN_NOTE_HIVE,
            COLUMN_NOTE_TYPE, COLUMN_NOTE_NOTE_TEXT };

    public NoteDAO(Context context) {
        this.mContext = context;
        mDbHelper = MyDBHandler.getInstance(context);
        // open the database
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    // --DB access methods--

    public Note createNote(long apiary, long hive, String type, String noteText) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_APIARY, apiary);
        values.put(COLUMN_NOTE_HIVE, hive);
        values.put(COLUMN_NOTE_TYPE, type);
        values.put(COLUMN_NOTE_NOTE_TEXT, noteText);
        long insertId = mDatabase.insert(TABLE_NOTE, null, values);

        Note newNote = null;
        if (insertId >= 0) {
            Cursor cursor = mDatabase.query(TABLE_NOTE, mAllColumns,
                    COLUMN_NOTE_ID + " = " + insertId, null, null, null, null);
            if (cursor.moveToFirst()) {
                newNote = cursorToNote(cursor);
            }
            cursor.close();
        }

        return newNote;
    }

    public Note createNote(Note aDO) {
        return createNote(aDO.getApiary(), aDO.getHive(), aDO.getType(), aDO.getNoteText());
    }

    public void deleteNote(Note note) {
        long id = note.getId();
        mDatabase.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = " + id, null);
    }

    public Note getNoteById(long id){
        Cursor cursor = mDatabase.query(TABLE_NOTE, mAllColumns,
                COLUMN_NOTE_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null);

        Note retrievedNote = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                cursorToNote(cursor);
            }
            cursor.close();
        }

        return cursorToNote(cursor);
    }

    protected Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(0));
        note.setApiary(cursor.getLong(1));
        note.setHive(cursor.getLong(2));
        note.setType(cursor.getString(3));
        note.setNoteText(cursor.getString(4));

        return note;
    }
}
