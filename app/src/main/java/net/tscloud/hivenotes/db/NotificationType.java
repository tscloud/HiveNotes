package net.tscloud.hivenotes.db;

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
    public static int NOTIFY_PEST_REMOVE_DRONE = 0;
    public static int NOTIFY_PEST_REMOVE_MITES = 1;
    public static int NOTIFY_OTHER_REQUEEN = 2;
    public static int NOTIFY_OTHER_SWARM = 3;
    public static int NOTIFY_OTHER_SPLIT_HIVE = 4;

    // Notification Descriptions
    private static String NOTIFY_PEST_REMOVE_DRONE_DESC =
        "Remove drone cell foundation frames";
    private static String NOTIFY_PEST_REMOVE_MITES_DESC =
        "Remove mites treatment";
    private static String NOTIFY_OTHER_REQUEEN_DESC =
        "Check on queen release";
    private static String NOTIFY_OTHER_SWARM_DESC =
        "Check for swarm laying queen";
    private static String NOTIFY_OTHER_SPLIT_HIVE_DESC =
        "Check for split hive laying queen";

    private static Map<Integer, String> keyToDesc;

    public static String getDesc(int aKey) {
        return keyToDesc.get(aKey);
    }

    static {
        // TODO - use SparseArray
        keyToDesc = new HashMap<Integer, String>(5);
        keyToDesc.put(NOTIFY_PEST_REMOVE_DRONE, NOTIFY_PEST_REMOVE_DRONE_DESC);
        keyToDesc.put(NOTIFY_PEST_REMOVE_MITES, NOTIFY_PEST_REMOVE_MITES_DESC);
        keyToDesc.put(NOTIFY_OTHER_REQUEEN, NOTIFY_OTHER_REQUEEN_DESC);
        keyToDesc.put(NOTIFY_OTHER_SWARM, NOTIFY_OTHER_SWARM_DESC);
        keyToDesc.put(NOTIFY_OTHER_SPLIT_HIVE, NOTIFY_OTHER_SPLIT_HIVE_DESC);
    }

}
