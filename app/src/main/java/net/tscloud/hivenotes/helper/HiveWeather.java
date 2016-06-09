package net.tscloud.hivenotes.helper;

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
	public static final String WU_FEATURES = "conditions";
	public static final String WU_SETTINGS = null;
	public static final String WU_QUERY = null;
	public static final String WU_FORMAT = "json";

	public static Weather requestWunderground(String aQuery) {
		Sting url = WU_ROOT + WU_API_KEY +"/" + WU_FEATURES + "/q/" + aQuery +
					"/" + WU_FORMAT;

        Log.d(TAG, "HiveWeather.requestWunderground(): url: " + url);
        Weather reply = new Weather();

        JSONObject jsonObj = requestWebService(url);

        if (jsonObj != null) {
	        JSONObject jsonHead = jsonObj.optJSONObject("current_observation");
	        if (jsonHead != null) {
	        	reply.getSnapshotDate = System.currentTimeMillis();
	        	reply.setTemperature = (float)jsonHead.optDouble("temp_f", -1);
	        	reply.setRainfall = Float.parseFloat(jsonHead.optString("precip_today_in", "-1"));
	        }
        }

        return reply;
	}

	private static JSONObject requestWebService(String serviceUrl) {
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
	private static void disableConnectionReuseIfNecessary() {
	    // see HttpURLConnection API doc
	    if (Integer.parseInt(Build.VERSION.SDK)
	            < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}

	private static String getResponseText(InputStream inStream) {
	    // very nice trick from
	    // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	    return new Scanner(inStream).useDelimiter("\\A").next();
	}
}