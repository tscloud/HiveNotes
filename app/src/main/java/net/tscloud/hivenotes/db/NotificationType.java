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
    public static int NOTIFY_REMOVE_DRONE = 0;
    public static int NOTIFY_REMOVE_MITES = 1;
    public static int NOTIFY_QUEEN_RELEASE = 2;
    public static int NOTIFY_SWARM = 3;
    public static int NOTIFY_SPLIT_HIVE = 4;

    // Notification Descriptions
    private static String NOTIFY_REMOVE_DRONE_DESC =
        "Remove drone cell foundation frames";
    private static String NOTIFY_REMOVE_MITES_DESC =
        "Remove mites treatment";
    private static String NOTIFY_QUEEN_RELEASE_DESC =
        "Check on queen release";
    private static String NOTIFY_SWARM_DESC =
        "Check for swarm laying queen";
    private static String NOTIFY_SPLIT_HIVE_DESC =
        "Check for split hive laying queen";

    private static Map<Integer, String> keyToDesc;

    public static String getDesc(int aKey) {
        return keyToDesc.get(aKey);
    }

    static {
        keyToDesc = new HashMap<Integer, String>(5);
        keyToDesc.put(NOTIFY_REMOVE_DRONE, NOTIFY_REMOVE_DRONE_DESC);
        keyToDesc.put(NOTIFY_REMOVE_MITES, NOTIFY_REMOVE_MITES_DESC);
        keyToDesc.put(NOTIFY_QUEEN_RELEASE, NOTIFY_QUEEN_RELEASE_DESC);
        keyToDesc.put(NOTIFY_SWARM, NOTIFY_SWARM_DESC);
        keyToDesc.put(NOTIFY_SPLIT_HIVE, NOTIFY_SPLIT_HIVE_DESC);
    }

}
