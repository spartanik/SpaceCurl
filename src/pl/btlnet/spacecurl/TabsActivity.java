package pl.btlnet.spacecurl;

import pl.btlnet.spacecurl.ui.PlanesView;
import pl.btlnet.spacecurl.ui.RotationView;
import pl.btlnet.spacecurl.ui.ZoomOutPageTransformer;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class TabsActivity extends FragmentActivity implements TabListener, SensorEventListener {
	CollectionPagerAdapter mCollectionPagerAdapter;
	ViewPager mViewPager;

	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private WakeLock mWakeLock;

	private Sensor mRotationVector;
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

		if (actionBar.isShowing())
			actionBar.hide();
		else
			actionBar.show();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();
		
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
				PlanesView planesView = (PlanesView) findViewById(R.id.rotationView);
				planesView.setWychylenieMax(position, progress);
				planesView.invalidate();
			}
		};
		
		try {
			((SeekBar) findViewById(R.id.SeekBar0)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) findViewById(R.id.SeekBar1)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) findViewById(R.id.SeekBar2)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) findViewById(R.id.SeekBar3)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) findViewById(R.id.SeekBar4)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) findViewById(R.id.SeekBar5)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) findViewById(R.id.SeekBar6)).setOnSeekBarChangeListener(progressMonitor);
			((SeekBar) findViewById(R.id.SeekBar7)).setOnSeekBarChangeListener(progressMonitor);
		} catch (Exception e) {
			// TODO: handle exception
		}

		
		
		OnSeekBarChangeListener anglesMonitor = new SeekBar.OnSeekBarChangeListener() {
			
			float phi,theta=0;
			
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {	}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				if(seekBar.getId()==R.id.SeekBarPhi) phi = 3.6f*progress;
				if(seekBar.getId()==R.id.SeekBarTheta) theta = 90-2.5f/10f*progress;
				
				RotationView rotView = (RotationView) findViewById(R.id.calibrationRotationView);
				rotView.updateRotation(phi, theta);
				Log.d("TAG", "Katy: "+phi + " , "+theta);
			}
		};
		
		try {
			((SeekBar) findViewById(R.id.SeekBarPhi)).setOnSeekBarChangeListener(anglesMonitor);
			((SeekBar) findViewById(R.id.SeekBarTheta)).setOnSeekBarChangeListener(anglesMonitor);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		
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

	public static class TabFragment extends Fragment {
		public static final String ARG_OBJECT = "object";

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
			setGender(rootView);
			return rootView;

		}

	}

	

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
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
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR)
			return;
		
		mSensorTimeStamp = event.timestamp;
		mCpuTimeStamp = System.nanoTime();
		final long now = mSensorTimeStamp + (System.nanoTime() - mCpuTimeStamp);
		
//		Log.d("XYZS", dataX + " \t"+dataY+" \t"+dataZ+" \t"+dataS);
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
				SensorManager.SENSOR_DELAY_UI);
	}

	public void stopSensing(SensorManager sm) {
		sm.unregisterListener(this);
	}
	
	
	
	
}