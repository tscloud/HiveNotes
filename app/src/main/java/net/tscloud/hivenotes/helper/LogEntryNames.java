package net.tscloud.hivenotes.helper;

import android.content.Context;

import net.tscloud.hivenotes.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class LogEntryNames {

    /**
     * An array of LogEntryItem items.
     */
    private static List<LogEntryItem> ITEMS = new ArrayList<LogEntryItem>();

    /**
     * A map of LogEntryItem items, by ID.
     */
    private static Map<String, LogEntryItem> ITEM_MAP = new HashMap<String, LogEntryItem>();

    /**
     * DO NOT use dummy values contained w/in LogEntryNames.ITEMS created by static
     *  initializer - strings should come from strings.xml or similar resource, keep
     *  icon name for now but should be replaced w/ image resource references
     */
    /*
    /*
    static {
        // Add 3 sample items.
        addItem(new LogEntryItem("1", "General Notes", "general_icon"));
        addItem(new LogEntryItem("2", "Productivity", "production_icon"));
        addItem(new LogEntryItem("3", "Pest Management", "pest_icon"));
        addItem(new LogEntryItem("4", "Feeding", "feeding_icon"));
        addItem(new LogEntryItem("5", "Other", "other_icon"));
        addItem(new LogEntryItem("6", "Save", "other_icon"));
    }
    */

    public static List<LogEntryItem> getItems(Context aCtx) {
        if (ITEMS.isEmpty()) {
            LogEntryNames.addItem(new LogEntryNames.LogEntryItem("1",
                    aCtx.getResources().getString(R.string.general_notes_log_string), "general_icon"));
            LogEntryNames.addItem(new LogEntryNames.LogEntryItem("2",
                    aCtx.getResources().getString(R.string.productivity_notes_string), "production_icon"));
            LogEntryNames.addItem(new LogEntryNames.LogEntryItem("3",
                    aCtx.getResources().getString(R.string.hivehealth_notes_string), "pest_icon"));
            LogEntryNames.addItem(new LogEntryNames.LogEntryItem("4",
                    aCtx.getResources().getString(R.string.feeding_notes_string), "feeding_icon"));
            LogEntryNames.addItem(new LogEntryNames.LogEntryItem("5",
                    aCtx.getResources().getString(R.string.events_notes_string), "other_icon"));
            LogEntryNames.addItem(new LogEntryNames.LogEntryItem("6",
                    aCtx.getResources().getString(R.string.save_string), "other_icon"));
        }

        return ITEMS;
    }

    public static Map<String, LogEntryItem> getItemMap() {
        return ITEM_MAP;
    }

    public static void addItem(LogEntryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A LogEntryItem item representing a piece of content.
     */
    public static class LogEntryItem {
        private String id;
        private String content;
        private String imageSrc;

        public LogEntryItem(String id, String content, String imageSrc) {
            this.id = id;
            this.content = content;
            this.imageSrc = imageSrc;
        }

        @Override
        public String toString() {
            return content;
        }

        public String getId() { return id; }

        public String getContent() { return content; }

        public String getImageSrc() { return imageSrc; }
    }
}
