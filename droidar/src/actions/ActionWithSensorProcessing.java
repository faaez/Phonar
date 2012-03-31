package actions;

import gl.GLCamRotationController;
import gl.GLUtilityClass;
import util.Calculus;
import worldData.Updateable;
import actions.algos.Algo;
import android.hardware.SensorManager;
import android.view.MotionEvent;

public abstract class ActionWithSensorProcessing extends Action {

	private static final String LOG_TAG = "ActionWithSensorProcessing";

	private GLCamRotationController myTargetCamera;

	public Algo magnetAlgo;
	public Algo accelAlgo;
	public Algo orientAlgo;
	public Algo accelBufferAlgo;
	public Algo magnetBufferAlgo;
	public Algo orientationBufferAlgo;

	private float[] myAccelValues = new float[3];
	private float[] myMagnetValues = new float[3];
	private float[] myOrientValues = new float[3];

	private boolean accelChanged;
	private float[] myNewAccelValues;
	private boolean magnetoChanged;
	private float[] myNewMagnetValues;
	private boolean orientationDataChanged;
	private float[] myNewOrientValues;

	private float[] unrotatedMatrix = Calculus.createIdentityMatrix();
	private float[] rotationMatrix = Calculus.createIdentityMatrix();

	public ActionWithSensorProcessing(GLCamRotationController targetCamera) {
		myTargetCamera = targetCamera;
		initAlgos();
	}

	protected abstract void initAlgos();

	@Override
	public boolean onTouchMove(MotionEvent e1, MotionEvent e2,
			float screenDeltaX, float screenDeltaY) {
		myTargetCamera.changeZAngleBuffered(screenDeltaY);
		return true;
	}

	@Override
	public synchronized boolean onAccelChanged(float[] values) {

		if (accelAlgo != null)
			myNewAccelValues = accelAlgo.execute(values);
		else
			myNewAccelValues = values;
		accelChanged = true;
		return true;

	}

	@Override
	public synchronized boolean onMagnetChanged(float[] values) {
		if (magnetAlgo != null)
			myNewMagnetValues = magnetAlgo.execute(values);
		else
			myNewMagnetValues = values;
		magnetoChanged = true;
		return true;

	}

	@Override
	public synchronized boolean onOrientationChanged(float[] values) {
		if (orientAlgo != null)
			myNewOrientValues = orientAlgo.execute(values);
		else
			myNewOrientValues = values;
		orientationDataChanged = true;
		return true;

	}

	@Override
	public synchronized boolean update(float timeDelta, Updateable parent) {
		if (magnetoChanged || accelChanged || orientationDataChanged) {
			if (magnetoChanged || accelChanged) {
				// if accel or magnet changed:
				if (accelChanged) {
					accelChanged = false;
					if (accelBufferAlgo != null)
						accelBufferAlgo.execute(myAccelValues,
								myNewAccelValues, timeDelta);
					else
						myAccelValues = myNewAccelValues;
				}
				if (magnetoChanged) {
					magnetoChanged = false;
					if (magnetBufferAlgo != null)
						magnetBufferAlgo.execute(myMagnetValues,
								myNewMagnetValues, timeDelta);
					else
						myMagnetValues = myNewMagnetValues;
				}
				// first calc the unrotated matrix:
				SensorManager.getRotationMatrix(unrotatedMatrix, null,
						myAccelValues, myMagnetValues);
			} else if (orientationDataChanged) {
				orientationDataChanged = false;
				if (orientationBufferAlgo != null)
					orientationBufferAlgo.execute(myOrientValues,
							myNewOrientValues, timeDelta);
				else
					myOrientValues = myNewOrientValues;
				GLUtilityClass.getRotationMatrixFromVector(unrotatedMatrix,
						myOrientValues);
			}

			// then rotate it according to the screen rotation:
			SensorManager.remapCoordinateSystem(unrotatedMatrix,
					SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
					rotationMatrix);

			myTargetCamera.setRotationMatrix(rotationMatrix, 0);
		}
		return true;
	}

	@Override
	public boolean onReleaseTouchMove() {
		myTargetCamera.resetBufferedAngle();
		return true;
	}

}