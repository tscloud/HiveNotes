package net.tscloud.hivenotes.helper;

import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import net.tscloud.hivenotes.db.Weather;
import net.tscloud.hivenotes.db.WeatherHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by tscloud on 6/9/16.
 */
public class HiveWeather {

	public static final String TAG = "HiveWeather";

	// Example URL:
	//  http://api.wunderground.com/api/24a52428b9227583/conditions/q/RI/East_Greenwich.json

	// Needed for all calls to Weather Underground - key is free so no need
	//  to hide
	public static final String WU_API_KEY = "24a52428b9227583";
	public static final String WU_ROOT = "http://api.wunderground.com/api/";
	public static final String WU_CONDITIONS = "conditions";
	public static final String WU_GEOLOOKUP = "geolookup";
	public static final String WU_HISTORY = "history_";
	public static final String WU_PWS = "pws:";
	public static final String WU_SETTINGS = null;
	public static final String WU_QUERY = null;
	public static final String WU_FORMAT = "json";

    public static final String OPEN_API_KEY = "6662ef1917e9cf9560a7cf277e423059";

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int DATARETRIEVAL_TIMEOUT = 10000;

    // These are the JSON field we're interested in
    private static final String [] dataFields = {"temp_f", "precip_today_in", "pressure_in", "weather",
		"wind_dir", "wind_mph", "relative_humidity", "dewpoint_f", "visibility_mi",
		"solarradiation", "UV"};

    private static final String [] mergeFields = {"solarradiation", "UV"};

    public HiveWeather() {

    }

	public Weather requestWunderground(String aQuery) {
        Log.d(TAG, "HiveWeather.requestWunderground()");
        Weather reply = new Weather();

        // Make the wunderground call - get JSON back
        JSONObject jsonHead = queryCondition(aQuery);
        if (jsonHead != null) {
            reply.setSnapshotDate(System.currentTimeMillis());
            reply.setTemperature((float)jsonHead.optDouble("temp_f", -1));
            reply.setRainfall(Float.parseFloat(jsonHead.optString("precip_today_in", "-1")));
            reply.setPressure(Float.parseFloat(jsonHead.optString("pressure_in", "-1")));
            reply.setWeather(jsonHead.optString("weather", "weather conditions not available"));
            reply.setWindDirection(jsonHead.optString("wind_dir", "wind direction not available"));
            reply.setWindMPH((float)jsonHead.optDouble("wind_mph", -1));
            reply.setHumidity(jsonHead.optString("relative_humidity", "humidity not available"));
            reply.setDewPoint((float)jsonHead.optDouble("dewpoint_f", -1));
            reply.setVisibility(jsonHead.optString("visibility_mi", "visibility not available"));
            reply.setSolarRadiation(jsonHead.optString("solarradiation", "solar radiation not available"));
            reply.setUvIndex(jsonHead.optString("UV", "UV index not available"));
        }

        return reply;
	}

	public Weather requestWundergroundExtended(String aQuery) {
		/** Here's the skinny:
		 *   Not all weather station provide all data
		 *    We've going to retrieve data from an array of nearby stations (how many?)
		 *    We'll build the return data set based on a union of this data
		 */
        Log.d(TAG, "HiveWeather.requestWundergroundExtended()");
		Weather reply = new Weather();

		// Make the wunderground call to get weather stations - get JSON back
        try {
            JSONArray jsonStationArray = queryStationArray(aQuery);

            // How many stations do we want to check?  This needs to be configurable
            int staThresh = 3;
            // this is where we are going to keep each station's conditions
            List<Map<String, String>> staCondHashArray = new ArrayList<>();

            // interate over the retrieved station list & use the top x
            for (int i=0; (i < jsonStationArray.length()) && (i < staThresh); i++) {
                JSONObject station = jsonStationArray.getJSONObject(i);
                String staId = station.optString("id");
                // get this station's conditions
                JSONObject staCond = queryCondition(WU_PWS + staId);
                // iterate over our list of data fields array
                HashMap<String, String> staCondHash = new HashMap<>();
                staCondHash.put("station", staId);
                for (String tag : dataFields) {
                    staCondHash.put(tag, staCond.optString(tag));
                }
                staCondHashArray.add(staCondHash);
            }
            Log.d(TAG, "finished load staCondHashArray");
            reply = mergeConditions(staCondHashArray);
        }
        catch (JSONException e) {
            Log.d(TAG, "JSONException thrown in requestWundergroundExtended()", e);
        };

        return reply;
    }

    public WeatherHistory requestWundergroundHistory(String aLoc, long aDate) {
        Log.d(TAG, "HiveWeather.requestWundergroundHistory()");
        WeatherHistory reply = new WeatherHistory();

        // convert date
        Date date = new Date(aDate);
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String newDate = formatter.format(date);

        // Make the wunderground call to get weather stations - get JSON back
        JSONObject jsonHead = queryHistory(aLoc, newDate);
        if (jsonHead != null) {
            reply.setSnapshot_date(aDate);
            reply.setFog(Long.parseLong(jsonHead.optString("fog", "-1")));
            reply.setRain(Long.parseLong(jsonHead.optString("rain", "-1")));
            reply.setSnow(Long.parseLong(jsonHead.optString("snow", "-1")));
            reply.setThunder(Long.parseLong(jsonHead.optString("thunder", "-1")));
            reply.setHail(Long.parseLong(jsonHead.optString("hail", "-1")));
            reply.setMaxtempi(Long.parseLong(jsonHead.optString("maxtempi", "-1")));
            reply.setMintempi(Long.parseLong(jsonHead.optString("mintempi", "-1")));
            reply.setMaxdewpti(Long.parseLong(jsonHead.optString("maxdewpti", "-1")));
            reply.setMindewpti(Long.parseLong(jsonHead.optString("mindewpti", "-1")));
            reply.setMaxpressurei(Float.parseFloat(jsonHead.optString("maxpressurei", "-1")));
            reply.setMinpressurei(Float.parseFloat(jsonHead.optString("minpressurei", "-1")));
            reply.setMaxwspdi(Long.parseLong(jsonHead.optString("maxwspdi", "-1")));
            reply.setMinwspdi(Long.parseLong(jsonHead.optString("minwspdi", "-1")));
            reply.setMeanwdird(Long.parseLong(jsonHead.optString("meanwdird", "-1")));
            reply.setMaxhumidity(Long.parseLong(jsonHead.optString("maxhumidity", "-1")));
            reply.setMinhumidity(Long.parseLong(jsonHead.optString("minhumidity", "-1")));
            reply.setMaxvisi(Float.parseFloat(jsonHead.optString("maxvisi", "-1")));
            reply.setMinvisi(Float.parseFloat(jsonHead.optString("minvisi", "-1")));
            reply.setPrecipi(Float.parseFloat(jsonHead.optString("precipi", "-1")));
            reply.setCoolingdegreedays(Long.parseLong(jsonHead.optString("coolingdegreedays", "-1")));
            reply.setHeatingdegreedays(Long.parseLong(jsonHead.optString("heatingdegreedays", "-1")));
        }

        return reply;
    }

    private JSONObject queryCondition(String aQuery) {
        String url = WU_ROOT + WU_API_KEY + "/" + WU_CONDITIONS + "/q/" + aQuery +
                "." + WU_FORMAT;

        Log.d(TAG, "HiveWeather.queryCondition(): url: " + url);
        JSONObject reply = null;

        // Make the wunderground call - get JSON back
        JSONObject jsonCallResult = requestWebService(url);
        if (jsonCallResult != null) {
            reply = jsonCallResult.optJSONObject("current_observation");
        }

        return reply;
    }

    private JSONArray queryStationArray(String aQuery) {
        String url = WU_ROOT + WU_API_KEY + "/" + WU_GEOLOOKUP + "/q/" + aQuery +
                "." + WU_FORMAT;

        Log.d(TAG, "HiveWeather.queryStationArray(): url: " + url);
        JSONArray reply = null;

        // Make the wunderground call to get weather stations - get JSON back
        JSONObject jsonCallResult = requestWebService(url);
        if (jsonCallResult != null) {
            try {
                reply = jsonCallResult.getJSONObject("location")
                        .getJSONObject("nearby_weather_stations")
                        .getJSONObject("pws")
                        .getJSONArray("station");

            } catch (JSONException e) {
                Log.d(TAG, "JSONException thrown in queryStationArray()", e);
            };
        }

        return reply;
    }

    private JSONObject queryHistory(String aLoc, String aDate) {
        String url = WU_ROOT + WU_API_KEY + "/" + WU_HISTORY + aDate + "/q/" + aLoc +
                "." + WU_FORMAT;

        Log.d(TAG, "HiveWeather.queryHistory(): url: " + url);
        JSONObject reply = null;

        // Make the wunderground call to get history - get JSON back
        JSONObject jsonCallResult = requestWebService(url);
        if (jsonCallResult != null) {
            try {
                // "dailysummery" a one element array
                reply = jsonCallResult.getJSONObject("history")
                        .getJSONArray("dailysummary")
                        .getJSONObject(0);
            }
            catch (JSONException e) {
                Log.d(TAG, "JSONException thrown in queryHistory()", e);
            }
        }

        return reply;
    }

    private Weather mergeConditions(List<Map<String, String>> aConditions) {
        Log.d(TAG, "HiveWeather.mergeConditions()");
        Weather reply = new Weather();

        //start w/ the 1st entry in the array - it's the closest to our position
        Map<String, String> returnHash = aConditions.get(0);

        for (String tag : mergeFields) {
            if (!checkTag(returnHash.get(tag))) {
                //start at 1 since we do not need to check the 1st
                for (int i = 1; i < aConditions.size(); i++) {
                    Map<String, String> otherData = aConditions.get(i);
                    if (checkTag(otherData.get(tag))) {
                        returnHash.put(tag, otherData.get(tag));
                        break;
                    }
                }

                //if we didn't end up w/ a "good" value => just set it to 0
                if (!checkTag(returnHash.get(tag))) {
                    returnHash.put(tag, "0");
                }
            }
        }

        reply.setSnapshotDate(System.currentTimeMillis());
        reply.setTemperature(Float.parseFloat(returnHash.get("temp_f")));
        reply.setRainfall(Float.parseFloat(returnHash.get("precip_today_in")));
        reply.setPressure(Float.parseFloat(returnHash.get("pressure_in")));
        reply.setWeather(returnHash.get("weather"));
        reply.setWindDirection(returnHash.get("wind_dir"));
        reply.setWindMPH(Float.parseFloat(returnHash.get("wind_mph")));
        reply.setHumidity(returnHash.get("relative_humidity"));
        reply.setDewPoint(Float.parseFloat(returnHash.get("dewpoint_f")));
        reply.setVisibility(returnHash.get("visibility_mi"));
        reply.setSolarRadiation(returnHash.get("solarradiation"));
        reply.setUvIndex(returnHash.get("UV"));

        return reply;
    }

    private boolean checkTag(String aTag) {
        // return true if the value appears to contain viable data
        boolean reply = true;

        try {
            if ((aTag == null) ||
                    (aTag.length() == 0) ||
                    (aTag.equals("--") ||
                            (Double.parseDouble(aTag) == 0))) {

                reply = false;
            }
        }
        catch (NumberFormatException e) {
            // got something unexpected - return bad
            reply = false;
        }

        return reply;
    }

	@Nullable
    private JSONObject requestWebService(String serviceUrl) {
	    disableConnectionReuseIfNecessary();

	    HttpURLConnection urlConnection = null;
	    try {
	        // create connection
	        URL urlToRequest = new URL(serviceUrl);
	        urlConnection = (HttpURLConnection)
	            urlToRequest.openConnection();
	        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
	        urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

	        // handle issues
	        int statusCode = urlConnection.getResponseCode();
	        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
	            // handle unauthorized (if service requires user login)
	        } else if (statusCode != HttpURLConnection.HTTP_OK) {
	            // handle any other errors, like 404, 500,..
	        }

	        // create JSON object from content
	        InputStream in = new BufferedInputStream(
	            urlConnection.getInputStream());
	        return new JSONObject(getResponseText(in));

	    } catch (MalformedURLException e) {
	        // URL is invalid
	    } catch (SocketTimeoutException e) {
	        // data retrieval or connection timed out
	    } catch (IOException e) {
	        // could not read response body
	        // (could not create input stream)
	    } catch (JSONException e) {
	        // response body is no valid JSON string
	    } finally {
	        if (urlConnection != null) {
	            urlConnection.disconnect();
	        }
	    }

	    return null;
	}

	/**
	 * required in order to prevent issues in earlier Android version.
	 */
	private void disableConnectionReuseIfNecessary() {
	    // see HttpURLConnection API doc
	    if (Integer.parseInt(Build.VERSION.SDK)
	            < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}

	private String getResponseText(InputStream inStream) {
	    // very nice trick from
	    // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	    return new Scanner(inStream).useDelimiter("\\A").next();
	}
}