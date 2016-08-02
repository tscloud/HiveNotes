package net.tscloud.hivenotes.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tscloud on 6/30/16.
 */
public class HiveUtil {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in View.setID.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Align given date to midnight by zeroing out all but date portion
     */
    public static long alignDateToMidnight(long aDate) {
        //Need to set dates to midnight to grab all for any given day
        long reply;

        Date dateDateMN = new Date(aDate);

        Calendar cal = Calendar.getInstance();

        cal.setTime(dateDateMN);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        reply = cal.getTimeInMillis();

        return reply;
    }
}