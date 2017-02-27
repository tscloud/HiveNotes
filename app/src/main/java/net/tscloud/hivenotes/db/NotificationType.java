package net.tscloud.hivenotes.db;

import net.tscloud.hivenotes.LogGeneralNotesFragment;
import net.tscloud.hivenotes.LogHiveHealthFragment;
import net.tscloud.hivenotes.LogOtherFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tscloud on 4/15/16.
 */
public class NotificationType {

    public static final String TAG = "NotificationType";

    // This is general phraseology that goes in the Reminder Title
    public static String NOTIFICATION_TITLE = "HiveNotes Reminder";

    // Notification_Type key values
    public static int NOTIFY_OTHER_ADD_HONEY_SUPERS = 0;
    public static int NOTIFY_OTHER_REMOVE_DRONE_COMB = 1;
    public static int NOTIFY_FEEDING_SUGER_SYRUP = 2;
    public static int NOTIFY_HEALTH_REMOVE_MITE = 3;
    public static int NOTIFY_GENERAL_LAYING_QUEEN = 4;
    public static int NOTIFY_OTHER_MOUSE_GUARD = 5;
    public static int NOTIFY_OTHER_SPRING_INSPECTION = 6;
    public static int NOTIFY_OTHER_TREAT_MITES = 7;
    public static int NOTIFY_OTHER_OTHER = 8;

    // Notification Descriptions
    private static String NOTIFY_OTHER_ADD_HONEY_SUPERS_DESC =
            "Add Honey supers";
    private static String NOTIFY_OTHER_REMOVE_DRONE_COMB_DESC =
            "Remove drone comb";
    private static String NOTIFY_FEEDING_SUGER_SYRUP_DESC =
            "Feed sugar syrup";
    private static String NOTIFY_HEALTH_REMOVE_MITE_DESC =
            "Remove mite treament";
    private static String NOTIFY_GENERAL_LAYING_QUEEN_DESC =
            "Check for laying queen";
    private static String NOTIFY_OTHER_MOUSE_GUARD_DESC =
            "Add mouse guard";
    private static String NOTIFY_OTHER_SPRING_INSPECTION_DESC =
            "Spring inspection";
    private static String NOTIFY_OTHER_TREAT_MITES_DESC =
            "Treat for mites";
    private static String NOTIFY_OTHER_OTHER_DESC =
            "Other";

    //Lookup table to map Type --> Description
    private static Map<Integer, String> keyToDesc;
    //Lookup table to map Dialog tag -->  Type
    public static Map<String, Integer> notificationTypeLookup;

    public static String getDesc(int aKey) {
        return keyToDesc.get(aKey);
    }

    static {
        // TODO - use SparseArray
        keyToDesc = new HashMap<Integer, String>();
        keyToDesc.put(NOTIFY_OTHER_ADD_HONEY_SUPERS, NOTIFY_OTHER_ADD_HONEY_SUPERS_DESC);
        keyToDesc.put(NOTIFY_OTHER_REMOVE_DRONE_COMB, NOTIFY_OTHER_REMOVE_DRONE_COMB_DESC);
        keyToDesc.put(NOTIFY_FEEDING_SUGER_SYRUP, NOTIFY_FEEDING_SUGER_SYRUP_DESC);
        keyToDesc.put(NOTIFY_HEALTH_REMOVE_MITE, NOTIFY_HEALTH_REMOVE_MITE_DESC);
        keyToDesc.put(NOTIFY_GENERAL_LAYING_QUEEN, NOTIFY_GENERAL_LAYING_QUEEN_DESC);
        keyToDesc.put(NOTIFY_OTHER_MOUSE_GUARD, NOTIFY_OTHER_MOUSE_GUARD_DESC);
        keyToDesc.put(NOTIFY_OTHER_SPRING_INSPECTION, NOTIFY_OTHER_SPRING_INSPECTION_DESC);
        keyToDesc.put(NOTIFY_OTHER_TREAT_MITES, NOTIFY_OTHER_TREAT_MITES_DESC);
        keyToDesc.put(NOTIFY_OTHER_OTHER, NOTIFY_OTHER_OTHER_DESC);

        notificationTypeLookup = new HashMap<String, Integer>();
        notificationTypeLookup.put(LogHiveHealthFragment.DIALOG_TAG_VARROA, NOTIFY_HEALTH_REMOVE_MITE);
        notificationTypeLookup.put(LogGeneralNotesFragment.DIALOG_TAG_QUEEN, NOTIFY_GENERAL_LAYING_QUEEN);
        notificationTypeLookup.put(LogOtherFragment.DIALOG_TAG_EVENTS, NOTIFY_OTHER_OTHER);
    }
}
