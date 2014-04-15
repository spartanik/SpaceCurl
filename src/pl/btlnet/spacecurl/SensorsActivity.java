/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.btlnet.spacecurl;

import pl.btlnet.spacecurl.ui.RotationView;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.WindowManager;

public class SensorsActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private WakeLock mWakeLock;
	private RotationView rotationView;
	private RotationView rotationSectors;
	private RotationView rotationZ;
	private RotationView rotationScalar;

	private Sensor mRotationVector;
	private long mSensorTimeStamp;
	private long mCpuTimeStamp;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get an instance of the SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get an instance of the PowerManager
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		// Create a bright wake lock
		mWakeLock = mPowerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

		// instantiate our simulation view and set it as the activity's content
		// mSimulationView = new SimulationView(this);
		// setContentView(mSimulationView);
		setContentView(R.layout.kalibracja);

		rotationView = (RotationView) findViewById(R.id.rotationView);
//		rotationSectors = (RotationView) findViewById(R.id.rotationSectors);
//		rotationZ = (RotationView) findViewById(R.id.rotationZ);
//		rotationScalar = (RotationView) findViewById(R.id.rotationScalar);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();

		rotationView.setMax(180);
//		rotationSectors.setMax(180);
//		rotationZ.setMax(180);
//		rotationScalar.setMax(180);
		
		startSensing(mSensorManager);
		
		// rotationView.getProgress();
		// rotationView.setProgress(50);
	}

	@Override
	protected void onPause() {
		super.onPause();

		stopSensing(mSensorManager);
		
		// and release our wake-lock
		mWakeLock.release();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR)
			return;

		float mSensorValueX = (float) (2 * Math.asin(event.values[0]));
		final int dataX = (int) (90 * mSensorValueX);

		float mSensorValueY = (float) (2 * Math.asin(event.values[1]));
		final int dataY = (int) (90 * mSensorValueY);
		
		float mSensorValueZ = (float) (2 * Math.asin(event.values[2]));
		final int dataZ = (int) (90 * mSensorValueZ);
		
		float mSensorValueS = (float) (2 * Math.acos(event.values[2]));
		final int dataS = (int) (90 * mSensorValueS);
		
		mSensorTimeStamp = event.timestamp;
		mCpuTimeStamp = System.nanoTime();
		final long now = mSensorTimeStamp + (System.nanoTime() - mCpuTimeStamp);

		// final int data = (int) (10*event.values[usedValueIndex]);

		rotationView.setProgress(dataX);
//		rotationSectors.setProgress(dataY);
//		rotationZ.setProgress(dataZ);
//		rotationScalar.setProgress(dataS);
		
		Log.d("XYZS", dataX + " \t"+dataY+" \t"+dataZ+" \t"+dataS);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void initSensor(SensorManager sm) {
		mRotationVector = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	}

	public void startSensing(SensorManager sm) {
		if (mRotationVector == null) {
			initSensor(sm);
		}
		sm.registerListener(this, mRotationVector,
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void stopSensing(SensorManager sm) {
		sm.unregisterListener(this);
	}
}
