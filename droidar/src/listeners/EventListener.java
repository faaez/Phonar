package listeners;

import gui.CustomGestureListener;
import util.Vec;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.view.MotionEvent;

public interface EventListener {

	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
	public abstract boolean onOrientationChanged(float[] values);

	public abstract boolean onLocationChanged(Location location);

	/**
	 * see
	 * {@link CustomGestureListener#onScroll(MotionEvent, MotionEvent, float, float)}
	 * 
	 */
	public abstract boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY);

	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
	public abstract boolean onAccelChanged(float[] values);

	/**
	 * see
	 * {@link SensorEventListener#onSensorChanged(android.hardware.SensorEvent)}
	 * 
	 * @param values
	 * @return
	 */
	public abstract boolean onMagnetChanged(float[] values);

	public abstract boolean onReleaseTouchMove();

	/**
	 * @param x
	 * @param y
	 * @param event
	 *            might be null so check first!
	 * @return
	 */
	public abstract boolean onTrackballEvent(float x, float y, MotionEvent event);

	public void onCamRotationVecUpdate(Vec target, Vec values, float timeDelta);

	// public void onCamOffsetVecUpdate(Vec target, Vec values, float
	// timeDelta);

	// public void onCamPositionVecUpdate(Vec target, Vec values, float
	// timeDelta);

}