package system;

import geo.GeoObj;
import geo.GeoUtils;
import gl.GLCamera;

import java.util.HashMap;
import java.util.List;

import listeners.EventListener;
import util.Log;
import actions.EventListenerGroup;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import commands.Command;

/**
 * this EventManager is attached to the main {@link Thread} and should react on
 * any kind of event or input
 * 
 * @author Spobo
 * 
 */

public class EventManager implements LocationListener, SensorEventListener {

	private static final String LOG_TAG = "Event Manager";

	private static final long MIN_MS_BEFOR_UPDATE = 200;
	private static final float MIN_DIST_FOR_UPDATE = 1;

	private static EventManager myInstance = new EventManager();

	// all the predefined actions:
	/**
	 * this action will be executed when the user moves the finger over the
	 * screen
	 */
	// private EventAction onTouchMoveAction;
	public EventListener onTrackballEventAction;
	public EventListener onOrientationChangedAction;
	public EventListener onLocationChangedAction;
	public HashMap<Integer, Command> myOnKeyPressedCommandList;

	// public static final boolean USE_ACCEL_AND_MAGNET = true;
	// final float[] inR = new float[16];
	// float[] orientation = new float[3];

	// private float clickPossible = 0;
	// private float[] gravityValues = new float[3];
	// private float[] geomagneticValues = new float[3];

	private GeoObj currentLocation;

	private Activity myTargetActivity;

	private GeoObj zeroPos;

	public static EventManager getInstance() {
		return myInstance;
	}

	public void registerListeners(Activity targetActivity,
			boolean useAccelAndMagnetoSensors) {
		myTargetActivity = targetActivity;
		registerSensorUpdates(targetActivity, useAccelAndMagnetoSensors);
		registerLocationUpdates();

	}

	private void registerSensorUpdates(Activity myTargetActivity,
			boolean useAccelAndMagnetoSensors) {
		SensorManager sensorManager = (SensorManager) myTargetActivity
				.getSystemService(Context.SENSOR_SERVICE);

		if (useAccelAndMagnetoSensors) {
			/*
			 * To register the EventManger for magnet- and accelerometer-sensor
			 * events, two Sensor-objects have to be obtained and then the
			 * EventManager is set as the Listener for these type of sensor
			 * events. The update rate is set by SENSOR_DELAY_GAME to a high
			 * frequency required to react on fast device movement
			 */
			Sensor magnetSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			sensorManager.registerListener(this, magnetSensor,
					SensorManager.SENSOR_DELAY_GAME);
			Sensor accelSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accelSensor,
					SensorManager.SENSOR_DELAY_GAME);
		} else {
			// Register orientation Sensor Listener:
			Sensor orientationSensor = sensorManager.getDefaultSensor(11);// Sensor.TYPE_ROTATION_VECTOR);
			sensorManager.registerListener(this, orientationSensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	/**
	 * This method will try to find the best location source available (probably
	 * GPS if enabled). Remember to wait some seconds before calling this if you
	 * activated GPS programmatically using
	 * {@link GeoUtils#enableGPS(Activity)}
	 */
	public void registerLocationUpdates() {

		if (myTargetActivity == null) {
			Log.e(LOG_TAG, "The target activity was undefined while "
					+ "trying to register for location updates");
		}

		LocationManager locationManager = (LocationManager) myTargetActivity
				.getSystemService(Context.LOCATION_SERVICE);
		Log.i(LOG_TAG, "Got locationmanager: " + locationManager);

		try {

			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Log.i(LOG_TAG, "GPS was enabled so this method should "
						+ "come to the conclusion to use GPS as "
						+ "the location source!");
			}

			/*
			 * To register the EventManager in the LocationManager a Criteria
			 * object has to be created and as the primary attribute accuracy
			 * should be used to get as accurate position data as possible:
			 */

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);

			String provider = locationManager.getBestProvider(criteria, true);
			if (provider == null) {
				Log.w(LOG_TAG, "No location-provider with the "
						+ "specified requierments found.. Trying to find "
						+ "an alternative.");
				List<String> providerList = locationManager.getProviders(true);
				for (String possibleProvider : providerList) {
					if (possibleProvider != null) {
						Log.w(LOG_TAG, "Location-provider alternative "
								+ "found: " + possibleProvider);
						provider = possibleProvider;
					}
				}
				if (provider == null)
					Log.w(LOG_TAG, "No location-provider alternative "
							+ "found!");
			}

			if (!provider.equals(LocationManager.GPS_PROVIDER)) {
				Log.w(LOG_TAG, "The best location provider was not "
						+ LocationManager.GPS_PROVIDER + ", it was " + provider);
			}

			locationManager.requestLocationUpdates(provider,
					MIN_MS_BEFOR_UPDATE, MIN_DIST_FOR_UPDATE, this);
		} catch (Exception e) {
			Log.e(LOG_TAG, "There was an error registering the "
					+ "EventManger for location-updates. The phone might be "
					+ "in airplane-mode..");
			e.printStackTrace();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor s, int accuracy) {
		// Log.d("sensor onAccuracyChanged", arg0 + " " + arg1);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (onOrientationChangedAction != null) {

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				onOrientationChangedAction.onAccelChanged(event.values);
			}
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				onOrientationChangedAction.onMagnetChanged(event.values);
			}

			// else sensor input is set to orientation mode
			if (event.sensor.getType() == 11) {// Sensor.TYPE_ROTATION_VECTOR) {
				onOrientationChangedAction.onOrientationChanged(event.values);
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (onLocationChangedAction != null) {
			onLocationChangedAction.onLocationChanged(location);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.w(LOG_TAG, "Didnt handle onProviderDisabled of " + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.w(LOG_TAG, "Didnt handle onProviderEnabled of " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

		if (myTargetActivity != null) {
			registerLocationUpdates();
		} else
			Log.w(LOG_TAG, "Didnt handle onStatusChanged of " + provider
					+ "(status=" + status + ")");

	}

	public void addOnOrientationChangedAction(EventListener action) {
		Log.d(LOG_TAG, "Adding onOrientationChangedAction");
		onOrientationChangedAction = addActionToTarget(
				onOrientationChangedAction, action);
	}

	public void addOnTrackballAction(EventListener action) {
		Log.d(LOG_TAG, "Adding onTouchMoveAction");
		onTrackballEventAction = addActionToTarget(onTrackballEventAction,
				action);

	}

	public void addOnLocationChangedAction(EventListener action) {
		Log.d(LOG_TAG, "Adding onLocationChangedAction");
		onLocationChangedAction = addActionToTarget(onLocationChangedAction,
				action);
	}

	public static EventListener addActionToTarget(EventListener target,
			EventListener action) {
		if (target == null) {
			target = action;
			Log.d(LOG_TAG, "Setting target command to " + action + "");
		} else if (target instanceof EventListenerGroup) {
			((EventListenerGroup) target).add(action);
			Log.d(LOG_TAG, "Adding " + action + " to existing actiongroup.");
		} else {
			EventListenerGroup g = new EventListenerGroup();
			g.add(target);
			g.add(action);
			target = g;
			Log.d(LOG_TAG, "Adding " + action + " to new actiongroup.");
		}
		return target;
	}

	/**
	 * @param actionToRemove
	 *            the {@link EventListener} to remove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 * @return true if the actionToRemove-EventListener could be removed
	 */
	public boolean exchangeOnTrackballEventAction(EventListener actionToRemove,
			EventListener actionToInsert) {

		if (onTrackballEventAction instanceof EventListenerGroup) {
			return exchangeAction((EventListenerGroup) onTrackballEventAction,
					actionToRemove, actionToInsert);
		} else if (actionToRemove == onTrackballEventAction) {
			onTrackballEventAction = actionToInsert;
			return true;
		}
		return false;
	}

	/**
	 * @param actionToRemove
	 *            the {@link EventListener} to remove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 * @return true if the actionToRemove-EventListener could be removed
	 */
	public boolean exchangeOnOrientationChangedAction(
			EventListener actionToRemove, EventListener actionToInsert) {
		if (onOrientationChangedAction instanceof EventListenerGroup) {
			return exchangeAction(
					(EventListenerGroup) onOrientationChangedAction,
					actionToRemove, actionToInsert);
		} else if (actionToRemove == onOrientationChangedAction) {
			onOrientationChangedAction = actionToInsert;
			return true;
		}
		return false;
	}

	/**
	 * @param actionToRemove
	 *            the {@link EventListener} to remove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 * @return true if the actionToRemove-EventListener could be removed
	 */
	public boolean exchangeOnLocationChangedAction(
			EventListener actionToRemove, EventListener actionToInsert) {
		if (onLocationChangedAction instanceof EventListenerGroup) {
			return exchangeAction((EventListenerGroup) onLocationChangedAction,
					actionToRemove, actionToInsert);
		} else if (actionToRemove == onLocationChangedAction) {
			onLocationChangedAction = actionToInsert;
			return true;
		}
		return false;
	}

	/**
	 * @param targetGroup
	 * @param actionToRemove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 */
	private boolean exchangeAction(EventListenerGroup targetGroup,
			EventListener actionToRemove, EventListener actionToInsert) {
		if (actionToInsert != null)
			targetGroup.add(actionToInsert);
		return targetGroup.remove(actionToRemove);
	}

	public void addOnKeyPressedCommand(int keycode, Command c) {
		if (myOnKeyPressedCommandList == null)
			myOnKeyPressedCommandList = new HashMap<Integer, Command>();
		myOnKeyPressedCommandList.put(keycode, c);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode >= 19 && keyCode <= 22) {
			/*
			 * if the keycode is on of the numbers from 19 to 22 it is a pseudo
			 * trackball event (eg the motorola milestone has pseudo trackball).
			 * here hare the codes (lets hope they are the same on each phone;):
			 * 
			 * top=19 down=20 left=21 right=22
			 */
			if (onTrackballEventAction != null) {
				final float stepLength = 0.3f;
				float x = 0, y = 0;
				switch (keyCode) {
				case 19:
					y = -stepLength;
					break;
				case 20:
					y = stepLength;
					break;
				case 21:
					x = -stepLength;
					break;
				case 22:
					x = stepLength;
					break;
				}
				return onTrackballEventAction.onTrackballEvent(x, y, null);
			}

			return false;
		}

		if (myOnKeyPressedCommandList == null)
			return false;
		Command commandForThisKey = myOnKeyPressedCommandList.get(keyCode);
		if (commandForThisKey != null) {
			Log.d("Command", "Key with command was pressed so executing "
					+ commandForThisKey);
			return commandForThisKey.execute();
		}
		return false;
	}

	/**
	 * This will return the current position of the device according to the
	 * Android system values.
	 * 
	 * The resulting coordinates can differ from
	 * {@link GLCamera#getGPSLocation()} if the camera was not moved according
	 * to the GPS input (eg moved via trackball).
	 * 
	 * Also check the {@link EventManager#getZeroPositionLocationObject()}
	 * method, if you want to know where the virtual zero position (of the
	 * OpenGL world) is.
	 */
	public GeoObj getCurrentLocationObject() {

		Location locaction = getCurrentLocation();
		if (locaction != null) {
			if (currentLocation == null) {
				currentLocation = new GeoObj(locaction, false);
			} else {
				currentLocation.setLocation(locaction);
			}
			return currentLocation;
		} else {
			Log.e(LOG_TAG,
					"Couldn't receive Location object for current location");
		}

		// if its still null set it to a default geo-object:
		if (currentLocation == null) {
			Log.e(LOG_TAG, "Current position set to default 0,0 position");
			currentLocation = new GeoObj(false);
		}

		return currentLocation;
	}

	public Location getCurrentLocation() {
		return GeoUtils.getCurrentLocation(myTargetActivity);
	}

	// /**
	// * The Android system will be asked directly and if the external location
	// * manager knows where the device is located at the moment, this location
	// * will be returned
	// *
	// * @return a new {@link GeoObj} or null if there could be no current
	// * location calculated
	// */
	// public GeoObj getNewCurrentLocationObjectFromSystem() {
	// return getAutoupdatingCurrentLocationObjectFromSystem().copy();
	// }

	public boolean onTrackballEvent(MotionEvent event) {
		if (onTrackballEventAction != null) {
			return onTrackballEventAction.onTrackballEvent(event.getX(),
					event.getY(), event);
		}
		return false;
	}

	@Deprecated
	public void setCurrentLocation(Location location) {
		currentLocation.setLocation(location);
	}

	public static void resetInstance() {
		myInstance = new EventManager();
	}

	/**
	 * This method differs from the normal
	 * {@link EventManager#getCurrentLocationObject()} because it will return
	 * the geoPos of the virtual (0,0,0) position. The other method would return
	 * the current device position (and because of this also the current camera
	 * position)
	 * 
	 * @return the zero position. This will NOT be a copy so do not modify it!
	 */
	public GeoObj getZeroPositionLocationObject() {
		if (zeroPos == null) {
			Log.d(LOG_TAG, "Zero pos was not yet received! "
					+ "The last known position of the device will be used "
					+ "at the zero position.");
			zeroPos = getCurrentLocationObject().copy();
		}
		return zeroPos;
	}

	public void setZeroLocation(Location location) {
		if (zeroPos == null)
			zeroPos = new GeoObj(location);
		else
			zeroPos.setLocation(location);
	}

	public void resumeEventListeners(Activity targetActivity,
			boolean useAccelAndMagnetoSensors) {
		registerListeners(targetActivity, useAccelAndMagnetoSensors);
	}

	public void pauseEventListeners() {
		SensorManager sensorManager = (SensorManager) myTargetActivity
				.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.unregisterListener(this);
		LocationManager locationManager = (LocationManager) myTargetActivity
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(this);
	}

}
