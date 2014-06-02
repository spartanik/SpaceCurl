package pl.btlnet.spacecurl;

import java.util.List;

import pl.btlnet.spacecurl.ui.HistogramView;
import pl.btlnet.spacecurl.ui.PlanesView;
import pl.btlnet.spacecurl.ui.RotationView;
import pl.btlnet.spacecurl.ui.ZoomOutPageTransformer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TabsActivity extends FragmentActivity implements TabListener, SensorEventListener {
	CollectionPagerAdapter mCollectionPagerAdapter;
	ViewPager mViewPager;
	public final String tag = "TAG";
	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private WakeLock mWakeLock;

	private Sensor mRotationSensor;
	private long mSensorTimeStamp;
	private long mCpuTimeStamp;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);

		mCollectionPagerAdapter = new CollectionPagerAdapter(
				getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setCustomView(R.layout.tabs);
		actionBar.setSubtitle("for BTL Polska Sp. z o.o.");
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
					
				});
		
		for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mCollectionPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		// Get an instance of the SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Get an instance of the PowerManager
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		// Create a bright wake lock
		mWakeLock = mPowerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
		actionBar.hide();

	}
	
	public void showHideActionBar(View v){
		final ActionBar actionBar = getActionBar();

		if (actionBar.isShowing()){
			actionBar.hide();
			startSensing(mSensorManager);			
		} else{
			actionBar.show();
			stopSensing(mSensorManager);
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();
		startSensing(mSensorManager);
	}
	
	@Override
	protected void onStop() {
		stopSensing(mSensorManager);
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
	}
	
	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {

	}

	public class CollectionPagerAdapter extends FragmentPagerAdapter {

		public CollectionPagerAdapter(FragmentManager fm) {
			super(fm);
			
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new TabFragment();
			Bundle args = new Bundle();
			args.putInt(TabFragment.ARG_OBJECT, i);
			fragment.setArguments(args);
			return fragment;

		}

		@Override
		public int getCount() {
			
			Resources res = getResources();
			String[] titles = res.getStringArray(R.array.tabs_titles);
			
			return titles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			
			Resources res = getResources();
			String[] titles = res.getStringArray(R.array.tabs_titles);
		
			return titles[position];
		}
	}

	
	RotationView sensorView;
	HistogramView statycznyZamkniete;
	HistogramView statycznyOtwarte;
	
	public class TabFragment extends Fragment {
		public static final String ARG_OBJECT = "object";
		private FragmentActivity myContext;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle args = getArguments();
			int position = args.getInt(ARG_OBJECT);
			int tabLayout = 0;
			switch (position) {
			case 0:
				tabLayout = R.layout.dane;
				break;
			case 1:
				tabLayout = R.layout.statyczny_before;
				break;
			case 2:
				tabLayout = R.layout.statyczny_after;
				break;
			case 3:
				tabLayout = R.layout.plaszczyzny;
				break;
			case 4:
				tabLayout = R.layout.zalecenia;
				break;
			default:
				tabLayout = R.layout.zalecenia;
			}

			
			View rootView = inflater.inflate(tabLayout, container, false);
//			findViews(rootView);
			setGender(rootView);
			return rootView;

		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			Log.e(tag, "Fragment Created");
			super.onCreate(savedInstanceState);
		}
		
		@Override
		public void onResume() {
//			findViews(getView());
//			startSensing(mSensorManager);

			Log.e(tag, "Fragment Resumed");
			super.onResume();
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			Log.e(tag, "Activity Created");

//			findViews(getView());
			super.onActivityCreated(savedInstanceState);
		}
		
		
		@Override
		public void onAttach(Activity activity) {
			myContext=(FragmentActivity) activity;
			super.onAttach(activity);
		}
		
		@Override
		public void onDestroyView() {
			Log.e(tag, "FragmentView Destroyed");
			super.onDestroyView();
		}
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
			Log.e(tag, "Fragment Saved");
			super.onSaveInstanceState(outState);
		}


	}
	
	
	
	
	
	
	
	public void findViews(View rootView){
		
//		if(true) return;
		
		OnSeekBarChangeListener progressMonitor = new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int position = seekBar.getSecondaryProgress();
				
				View parentView = (View)seekBar.getParent().getParent();
				PlanesView planesView = (PlanesView) parentView.findViewById(R.id.rotationView);
				planesView.setWychylenieMax(position, progress);
				planesView.invalidate();
			}
		};
		
		
		
		try {
			((SeekBar) rootView.findViewById(R.id.SeekBar0)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) rootView.findViewById(R.id.SeekBar1)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) rootView.findViewById(R.id.SeekBar2)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) rootView.findViewById(R.id.SeekBar3)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) rootView.findViewById(R.id.SeekBar4)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) rootView.findViewById(R.id.SeekBar5)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) rootView.findViewById(R.id.SeekBar6)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) rootView.findViewById(R.id.SeekBar7)).setOnSeekBarChangeListener(progressMonitor);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			sensorView = (RotationView) rootView.findViewById(R.id.calibrationRotationView);
//			sensorView.invalidate();
		} catch (Exception e) {
			Log.e(tag,"Brak sensorView");
		}
		
		
		try {
			statycznyZamkniete = (HistogramView) rootView.findViewById(R.id.histogramViewBefore);
//			statycznyZamkniete.invalidate();
		} catch (Exception e) {
			Log.e(tag,"Brak statycznyZamkniete");
		}
		
		
		try {
			statycznyOtwarte = (HistogramView) rootView.findViewById(R.id.histogramViewAfter);
//			statycznyOtwarte.invalidate();
		} catch (Exception e) {
			Log.e(tag,"Brak statycznyOtwarte");
		}
		
//		
//		OnSeekBarChangeListener anglesMonitor = new SeekBar.OnSeekBarChangeListener() {
//			
//			float phi,theta=0;
//			
//			
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {	}
//			
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {}
//			
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress,
//					boolean fromUser) {
//				
//				if(seekBar.getId()==R.id.SeekBarPhi) phi = 3.6f*progress;
//				if(seekBar.getId()==R.id.SeekBarTheta) theta = 90-2.5f/10f*progress;
//				
//				View parentView = (View)seekBar.getParent().getParent();
//				RotationView rotView = (RotationView) parentView.findViewById(R.id.calibrationRotationView);
//				rotView.updateRotation(phi, theta);
////				sensorView = rotView;
//				Log.d("TAG", "Katy: "+phi + " , "+theta);
//			}
//		};
		
//		try {
//			((SeekBar) rootView.findViewById(R.id.SeekBarPhi)).setOnSeekBarChangeListener(anglesMonitor);
//			((SeekBar) rootView.findViewById(R.id.SeekBarTheta)).setOnSeekBarChangeListener(anglesMonitor);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}

	}
	
	

	
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
    
//		TabFragment tf = (TabFragment) getSupportFragmentManager().findFragmentById(R.id.pager);
//		findViews(tf.getView());
    
//		startSensing(mSensorManager);
	}
//
	@Override
	public void onAttachFragment(android.app.Fragment fragment) {
		Log.e("TAG", "Attached");
		
		super.onAttachFragment(fragment);
//	    
//		TabFragment tf = (TabFragment) getSupportFragmentManager().findFragmentById(R.id.tab1);
//		tf.findViews(tf.getView());
//		startSensing(mSensorManager);
	}
	

	public static String gender = "female";
	static void setGender(View root){
		
		ImageView front = (ImageView) root.findViewById(R.id.resultBodyFront);
		ImageView back = (ImageView) root.findViewById(R.id.resultBodyBack);
		
		try{
		if(gender.equals("male")){
			front.setImageResource(R.drawable.male_front);
			back.setImageResource(R.drawable.male_back);
		}else{
			front.setImageResource(R.drawable.female_front);
			back.setImageResource(R.drawable.female_back);
		}
		}catch(Exception e){
			
		}
	}
	

	public void chooseMale(View v){
		Log.d("TAG", "male");
		ImageView female = (ImageView) findViewById(R.id.femaleImage);

		Animation goneAnimation = AnimationUtils.loadAnimation(this, R.anim.disappear);
		female.startAnimation(goneAnimation);
		
		female.setVisibility(View.GONE);
		RotationView calibration = (RotationView) findViewById(R.id.calibrationRotationView);
		
		Animation comeAnimation = AnimationUtils.loadAnimation(this, R.anim.appear);
		calibration.startAnimation(comeAnimation);
		calibration.setVisibility(View.VISIBLE);
		gender="male";
	}
	
	public void chooseFemale(View v){
		Log.d("TAG", "female");
		ImageView male = (ImageView) findViewById(R.id.maleImage);
		Animation goneAnimation = AnimationUtils.loadAnimation(this, R.anim.disappear);
		male.startAnimation(goneAnimation);
		male.setVisibility(View.GONE);
		
		RotationView calibration = (RotationView) findViewById(R.id.calibrationRotationView);
		Animation comeAnimation = AnimationUtils.loadAnimation(this, R.anim.appear);
		calibration.startAnimation(comeAnimation);
		calibration.setVisibility(View.VISIBLE);

		gender="female";
	}
	
	
	
	/*
	 * time smoothing constant for low-pass filter 0 ≤ alpha ≤ 1 ; a smaller
	 * value basically means more smoothing See:
	 * http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
	 */
	static final float ALPHA = 0.2f;

	/**
	 * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
	 * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
	 */
	protected float[] lowPass(float[] input, float[] output) {
		if (output == null)
			return input;

		for (int i = 0; i < input.length; i++) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent rawEvent) {
		if (rawEvent.sensor.getType() != Sensor.TYPE_ORIENTATION)
			return;
		
		mSensorTimeStamp = rawEvent.timestamp;
		mCpuTimeStamp = System.nanoTime();

		final long diff = System.nanoTime() - mCpuTimeStamp;
		final long now = mSensorTimeStamp + diff;
		
//		float mSensorValueX = (float) (2 * Math.asin(event.values[0]));
//		final int dataX = (int) (90 * mSensorValueX);
//
//		float mSensorValueY = (float) (2 * Math.asin(event.values[1]));
//		final int dataY = (int) (90 * mSensorValueY);
//		
//		float mSensorValueZ = (float) (2 * Math.asin(event.values[2]));
//		final int dataZ = (int) (90 * mSensorValueZ);
//		
//		float mSensorValueS = (float) (2 * Math.acos(event.values[3]));
//		final int dataS = (int) (90 * mSensorValueS);

		
		float[] smoothValues = rawEvent.values.clone();
		lowPass(rawEvent.values, smoothValues);
		
		int dataZ = (int) (smoothValues[0]);
		final int dataX = (int) smoothValues[1];
		final int dataY = (int) smoothValues[2];
		int dataS = (int) (rawEvent.values[2] - rawEvent.values[1]);
		
		//Poprawka przechylen do tylu
		if (dataS > 0) {
			dataZ = (dataZ + 180) % 360;
			dataS = -dataS;
		}
//		Log.d("XYZS", "["+diff+"] "+dataX + " \t"+dataY+" \t"+dataZ+" \t ("+dataS);
		
		try {
			sensorView = (RotationView) findViewById(R.id.calibrationRotationView);
			sensorView.updateRotation(dataX, dataY);	
			sensorView.invalidate();
		} catch (Exception e) {
//			Log.e(tag,"Brak sensorView");
		}
		
		
		try {
			statycznyZamkniete = (HistogramView) findViewById(R.id.histogramViewBefore);
			statycznyZamkniete.putWychylenie(-dataS, dataZ);
		} catch (Exception e) {
//			Log.e(tag,"Brak statycznyZamkniete");
		}
		
		
		try {
			statycznyOtwarte = (HistogramView) findViewById(R.id.histogramViewAfter);
			statycznyOtwarte.putWychylenie(-dataS, dataZ);
		} catch (Exception e) {
//			Log.e(tag,"Brak statycznyOtwarte");
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void initSensor(SensorManager sm) {
		mRotationSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	public void startSensing(SensorManager sm) {
		if (mRotationSensor == null) {
			initSensor(sm);
		}
		sm.registerListener(this, mRotationSensor,	SensorManager.SENSOR_DELAY_UI);
	}

	public void stopSensing(SensorManager sm) {
		sm.unregisterListener(this);
		mRotationSensor=null;
	}
	
	
	
	
}