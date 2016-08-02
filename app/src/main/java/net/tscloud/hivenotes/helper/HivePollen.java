package net.tscloud.hivenotes.helper;

import android.util.Log;

import java.util.Date;
import java.text.ParseException;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by tscloud on 6/13/16.
 */
public class HivePollen {

    public static final String TAG = "HivePollen";

    /**
     * TreeMap to hold Date and String objects
     * Date corresponds to four dates that
     */
    private static Map<Date, String> pollenMap = new TreeMap<Date, String>();

    private String location;
    private String pollenType;
    private String zipcode;

    public String getDateToday()
    {
        return HivePollen.pollenMap.entrySet().iterator().next().getKey().toString();
    }

    public String getPollenIndexToday()
    {
        String reply = null;
        try {
            reply = HivePollen.pollenMap.entrySet().iterator().next().getValue();
        }
        catch (NumberFormatException e) {
            Log.e(TAG, "Pollen Index found cannot be coverted to float");
        }
        return reply;
    }

    public String getPollenType()
    {
        return this.pollenType;
    }

    public String getCity()
    {
        return this.location;
    }

    public String getZipcode()
    {
        return this.zipcode;
    }

    public Set<Date> getCorrespondingDate()
    {
        return HivePollen.pollenMap.keySet();
    }

    public Collection<String> getPollenIndex()
    {
        return HivePollen.pollenMap.values();
    }

    public HivePollen(String zipcode)
    {

        try
        {
            this.zipcode = zipcode;
            Document doc;
            // pass address to
            doc = Jsoup.connect("http://www.wunderground.com/DisplayPollen.asp?Zipcode=" + this.zipcode).get();

            if(doc.select("div.columns").first().text() == null)
            {
                throw new NoSuchElementException("Bad News: no div.columns");
            }

            // get "location" from XML
            Element location = doc.select("div.columns").first();
            Log.d(TAG, "location: " + this.location);

            // get "pollen type" from XML
            Element pollenType = doc.select("div.panel h3").first();
            this.pollenType = pollenType.text();
            Log.d(TAG, "pollen type: " + this.pollenType);

            SimpleDateFormat format = new SimpleDateFormat("EEE MMMM dd, yyyy");

            // add the four items of pollen and dates
            // to its respective list
            for(int i = 0; i < 4; i++)
            {
                Element dates = doc.select("td.text-center.even-four").get(i);
                Element levels = doc.select("td.levels").get(i);

                try
                {
                    pollenMap.put(format.parse(dates.text()), levels.text());
                }
                catch (ParseException e)
                {
                    Log.e(TAG, "ParseException");
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "exception", e);
        }
    }

}
