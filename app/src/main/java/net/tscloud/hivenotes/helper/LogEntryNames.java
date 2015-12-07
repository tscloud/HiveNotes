package net.tscloud.hivenotes.helper;

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
     * An array of sample (dummy) items.
     */
    public static List<LogEntryItem> ITEMS = new ArrayList<LogEntryItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, LogEntryItem> ITEM_MAP = new HashMap<String, LogEntryItem>();

    static {
        // Add 3 sample items.
        addItem(new LogEntryItem("1", "General Notes", "general_icon"));
        addItem(new LogEntryItem("2", "Productivity", "production_icon"));
        addItem(new LogEntryItem("3", "Pest Management", "pest_icon"));
        addItem(new LogEntryItem("4", "Feeding", "feeding_icon"));
        addItem(new LogEntryItem("5", "Other", "other_icon"));
        addItem(new LogEntryItem("6", "Save", "other_icon"));
    }

    private static void addItem(LogEntryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
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
