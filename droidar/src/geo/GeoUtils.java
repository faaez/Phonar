package geo;

import gl.GLCamera;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import util.Log;
import util.Wrapper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;

/**
 * This util class is a collection of all common location related operations
 * like enabling GPS, receiving the current position, mapping an address to gps
 * coordinates or converting to correct position values
 * {@link GeoUtils#convertDegreesMinutesSecondsToDecimalDegrees(double, double, double)}
 * 
 * @author Spobo
 * 
 */
public class GeoUtils {

	private static final String LOG_TAG = "Geo Utils";
	private Geocoder myGeoCoder;
	private Context myContext;

	private SimpleNodeEdgeListener defaultNEListener;

	public GeoUtils(Context context, GLCamera glCamera) {
		myContext = context;
		myGeoCoder = new Geocoder(context, Locale.getDefault());
		defaultNEListener = new DefaultNodeEdgeListener(glCamera);
	}

	/**
	 * In DroidAR all coordinates have to be decimal degrees. Use this method if
	 * you have to convert to decimal degrees.
	 * 
	 * Example usage: <br>
	 * 16� 19' 28,29" to 16,324525�
	 * 
	 * @param degree
	 *            16
	 * @param minutes
	 *            19
	 * @param seconds
	 *            28,29
	 * @return 16,324525�
	 */
	public static double convertDegreesMinutesSecondsToDecimalDegrees(
			double degree, double minutes, double seconds) {
		return degree + ((minutes + (seconds / 60)) / 60) / 60;
	}

	/**
	 * This method returns the best match for a specified position. It could for
	 * example be used to calculate the closest address to your current
	 * location.
	 * 
	 * @param location
	 * @return the closest address to the {@link GeoObj}
	 */
	public Address getBestAddressForLocation(GeoObj location) {
		try {
			List<Address> locations = myGeoCoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 1);
			if (locations.size() > 0) {
				return locations.get(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the position of an specified address (Streetname e.g.)
	 * 
	 * @param address
	 * @return null if the address could not be found
	 */
	public GeoObj getBestLocationForAddress(String address) {
		try {
			List<Address> addresses = myGeoCoder
					.getFromLocationName(address, 5);
			if (addresses.size() > 0) {
				GeoObj g = new GeoObj(addresses.get(0));
				g.getInfoObject().setShortDescr(
						address + " (" + g.getInfoObject().getShortDescr()
								+ ")");
				return g;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This will search for a specified address and return the found results
	 * 
	 * @param address
	 * @param maxResults
	 *            number of results
	 * @return a {@link GeoGraph} with maxResults many {@link GeoObj}s as
	 *         specified
	 */
	public GeoGraph getLocationListForAddress(String address, int maxResults) {
		try {
			List<Address> addresses = myGeoCoder.getFromLocationName(address,
					maxResults);
			if (addresses.size() > 0) {
				GeoGraph result = new GeoGraph();
				for (int i = 0; i < addresses.size(); i++) {
					result.add(new GeoObj(addresses.get(i)));
				}
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getStreetFor(GeoObj geoPos) {
		try {
			return getBestAddressForLocation(geoPos).getAddressLine(0);
		} catch (Exception e) {
		}
		return null;
	}

	public String getCityFor(GeoObj currentPos) {
		try {
			return getBestAddressForLocation(currentPos).getAddressLine(1)
					.split(" ")[1];
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * This method will try to get the most accurate position currently
	 * available. This includes also the last known position of the device if no
	 * current position sources can't be accessed so the returned position might
	 * be outdated
	 * 
	 * @param context
	 * @return
	 */
	public static Location getCurrentLocation(Context context) {
		Location l = GeoUtils.getCurrentLocation(context,
				Criteria.ACCURACY_FINE);
		if (l == null) {
			Log.e(LOG_TAG,
					"Fine accuracy position could not be detected! Will use coarse location.");
			l = GeoUtils.getCurrentLocation(context, Criteria.ACCURACY_COARSE);
			if (l == null) {
				Log.e(LOG_TAG,
						"Coarse accuracy position could not be detected! Last try..");
				try {
					LocationManager lm = ((LocationManager) context
							.getSystemService(Context.LOCATION_SERVICE));
					Log.d(LOG_TAG, "Searching through "
							+ lm.getAllProviders().size()
							+ " location providers");
					for (int i = lm.getAllProviders().size() - 1; i >= 0; i--) {
						l = lm.getLastKnownLocation(lm.getAllProviders().get(i));
						if (l != null)
							break;
					}
				} catch (Exception e) {
				}
			}
		}
		Log.d(LOG_TAG, "current position=" + l);
		
		
		Location location = new Location("40.349463,-74.652733");
		location.setLatitude(40.349463); // Sherrerd
		location.setLongitude(-74.652733);
		location.setAccuracy(0.000001F);
		return location;
		
		/*
		return l;
		*/
	}

	public Location getCurrentLocation() {
		return getCurrentLocation(myContext);
	}

	public static Location getCurrentLocation(Context context, int accuracy) {
		if (context != null) {
			try {
				LocationManager lm = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				criteria.setAccuracy(accuracy);
				/*
				return lm.getLastKnownLocation(lm.getBestProvider(criteria,
						true));
				*/
				
				Location location = new Location("40.349463,-74.652733");
				location.setLatitude(40.349463); // Sherrerd
				location.setLongitude(-74.652733);
				location.setAccuracy(0.000001F);
				return location;
				
			} catch (Exception e) {
				Log.e(LOG_TAG, "Could not receive the current location");
				e.printStackTrace();
				return null;
			}
		}
		Log.e(LOG_TAG, "The passed activity was null!");
		return null;
	}

	/**
	 * @param startPos
	 * @param destPos
	 * @param myResultingPath
	 *            in this Wrapper the resulting path will be stored
	 * @param byWalk
	 * @return
	 */
	public boolean getPathFromAtoB(GeoObj startPos, GeoObj destPos,
			Wrapper myResultingPath, boolean byWalk) {
		GeoGraph result = getPathFromAtoB(startPos, destPos, byWalk);
		if (result != null) {
			Log.d(LOG_TAG, "Found way on maps!");
			Log.d(LOG_TAG, "Path infos: " + result.toString());
			myResultingPath.setTo(result);
			return true;
		}
		Log.d(LOG_TAG, "No way on maps found :(");
		return false;
	}

	public GeoGraph getPathFromAtoB(GeoObj startPos, GeoObj destPos,
			boolean byWalk) {
		return getPathFromAtoB(startPos, destPos, byWalk, null, null);
	}

	/**
	 * Uses google maps to calculate the way from the start pos to the
	 * destination pos
	 * 
	 * @param startPos
	 * @param destPos
	 * @param byWalk
	 * @param nodeListener
	 * @param edgeListener
	 * @return
	 */
	public GeoGraph getPathFromAtoB(GeoObj startPos, GeoObj destPos,
			boolean byWalk, NodeListener nodeListener, EdgeListener edgeListener) {

		if (startPos == null || destPos == null) {
			Log.d(LOG_TAG,
					"Gmap getPathFromAtoB error: startPoint or target were null");
			return null;
		}

		// try to open the url:
		try {
			String url = generateUrl(startPos, destPos, byWalk);
			Document kml = getDocumentFromUrl(url);

			if (kml.getElementsByTagName("GeometryCollection").getLength() > 0) {

				String path = kml.getElementsByTagName("GeometryCollection")
						.item(0).getFirstChild().getFirstChild()
						.getFirstChild().getNodeValue();

				final String[] pairs = path.split(" ");
				GeoGraph result = new GeoGraph();
				result.getInfoObject().setShortDescr(
						"Resulting graph for "
								+ destPos.getInfoObject().getShortDescr());
				result.setIsPath(true);
				result.setNonDirectional(false);

				if (nodeListener != null) {
					nodeListener.addFirstNodeToGraph(result, startPos);
				} else {
					defaultNEListener.addFirstNodeToGraph(result, startPos);
				}

				GeoObj lastPoint = startPos;
				for (int i = 1; i < pairs.length; i++) {
					String[] geoCords = pairs[i].split(",");

					GeoObj currentPoint = new GeoObj(
							Double.parseDouble(geoCords[1]),
							Double.parseDouble(geoCords[0]),
							Double.parseDouble(geoCords[2]));

					if (!currentPoint.hasSameCoordsAs(lastPoint)) {
						if (nodeListener != null) {
							nodeListener.addNodeToGraph(result, currentPoint);
						} else {
							defaultNEListener.addNodeToGraph(result,
									currentPoint);
						}
						if (edgeListener != null) {
							edgeListener.addEdgeToGraph(result, lastPoint,
									currentPoint);
						} else {
							defaultNEListener.addEdgeToGraph(result, lastPoint,
									currentPoint);
						}
						Log.d(LOG_TAG, "     + adding Waypoint:" + pairs[i]);
						lastPoint = currentPoint;
					}

				}
				if (lastPoint != null && !lastPoint.hasSameCoordsAs(destPos)) {

					/*
					 * add the egde to the past point:
					 */
					if (edgeListener != null) {
						edgeListener.addEdgeToGraph(result, lastPoint, destPos);
					} else {
						defaultNEListener.addEdgeToGraph(result, lastPoint,
								destPos);
					}

				}

				if (nodeListener != null) {
					nodeListener.addNodeToGraph(result, destPos);
				} else {
					defaultNEListener.addNodeToGraph(result, destPos);
				}

				/*
				 * an alternative for adding the edges would be to call
				 * result.addEdgesToCreatePath(); but this would be a bit
				 * slower..
				 */

				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Document getDocumentFromUrl(String url) throws IOException,
			MalformedURLException, ProtocolException,
			FactoryConfigurationError, ParserConfigurationException,
			SAXException {
		HttpURLConnection urlConnection = (HttpURLConnection) new URL(url)
				.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.connect();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		// get the kml file. And parse it to get the coordinates(direction
		// route):
		Document doc = db.parse(urlConnection.getInputStream());
		return doc;
	}

	private String generateUrl(GeoObj startPos, GeoObj destPos, boolean byWalk) {
		// build the url string:
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		if (byWalk) {
			urlString.append("&dirflg=w");
		}
		urlString.append("&saddr=");// from
		urlString.append(Double.toString(startPos.getLatitude()));
		urlString.append(",");
		urlString.append(Double.toString(startPos.getLongitude()));
		urlString.append("&daddr=");// to
		urlString.append(Double.toString(destPos.getLatitude()));
		urlString.append(",");
		urlString.append(Double.toString(destPos.getLongitude()));
		urlString.append("&;ie=UTF8&0&om=0&output=kml");
		return urlString.toString();
	}

	public static boolean isGPSDisabled(Context context) {
		return !((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE))
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public boolean isGPSDisabled() {
		return isGPSDisabled(myContext);
	}

	/**
	 * @param activity
	 * @return true if GPS could be enabled without user interaction, else the
	 *         settings will be started and false is returned
	 */
	public static boolean enableGPS(Activity activity) {

		return switchGPS(activity, true, true);
	}

	public static void enableLocationProvidersIfNeeded(Activity activity) {
		try {
			boolean useWifiForLocation = ((LocationManager) activity
					.getSystemService(Context.LOCATION_SERVICE))
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (isGPSDisabled(activity) && !useWifiForLocation) {
				openLocationSettingsPage(activity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param activity
	 * @return true if GPS could be disabled without user interaction, else the
	 *         settings will be started and false is returned
	 */
	public static boolean disableGPS(Activity activity) {
		return disableGPS(activity, false);
	}

	/**
	 * @param activity
	 * @return true if GPS could be disabled without user interaction, else the
	 *         settings will be started and false is returned
	 */
	public static boolean disableGPS(Activity activity,
			boolean showSettingsIfAutoSwitchImpossible) {
		return switchGPS(activity, false, showSettingsIfAutoSwitchImpossible);
	}

	/**
	 * @param activity
	 * @param enableGPS
	 * @param showSettingsIfAutoSwitchImpossible
	 * @return true if GPS could be switched to the desired value without user
	 *         interaction, else the settings will be started and false is
	 *         returned
	 */
	public static boolean switchGPS(Activity activity, boolean enableGPS,
			boolean showSettingsIfAutoSwitchImpossible) {
		if (canTurnOnGPSAutomatically(activity)) {
			String provider = Settings.Secure.getString(
					activity.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			boolean currentlyEnabled = provider.contains("gps");
			if (!currentlyEnabled && enableGPS) {
				pokeGPSButton(activity);
			} else if (currentlyEnabled && !enableGPS) {
				pokeGPSButton(activity);
			}
			return true;
		} else if (showSettingsIfAutoSwitchImpossible) {
			Log.d(LOG_TAG, "Can't enable GPS automatically, will start "
					+ "settings for manual enabling!");
			openLocationSettingsPage(activity);
		}
		return false;
	}

	public static void openLocationSettingsPage(Activity activity) {
		activity.startActivity(new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	private static void pokeGPSButton(Activity activity) {
		final Intent poke = new Intent();
		poke.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
		poke.setData(Uri.parse("3"));
		activity.sendBroadcast(poke);
	}

	/**
	 * source from
	 * http://stackoverflow.com/questions/4721449/enable-gps-programatically
	 * -like-tasker
	 */
	private static boolean canTurnOnGPSAutomatically(Context c) {
		PackageInfo pacInfo = null;
		try {
			pacInfo = c.getPackageManager().getPackageInfo(
					"com.android.settings", PackageManager.GET_RECEIVERS);
		} catch (NameNotFoundException e) {
			Log.e(LOG_TAG, "com.android.settings package not found");
			return false; // package not found
		}
		if (pacInfo != null) {
			for (ActivityInfo actInfo : pacInfo.receivers) {
				// test if recevier is exported. if so, we can toggle GPS.
				if (actInfo.name
						.equals("com.android.settings.widget.SettingsAppWidgetProvider")
						&& actInfo.exported) {
					return true;
				}
			}
		}
		return false; // default
	}

}
