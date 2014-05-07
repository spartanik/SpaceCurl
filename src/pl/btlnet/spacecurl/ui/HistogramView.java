package pl.btlnet.spacecurl.ui;

import java.util.concurrent.TimeUnit;

import pl.btlnet.spacecurl.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HistogramView extends View{

	/**
	 * Used to scale the dp units to pixels
	 */
	private final float DPTOPX_SCALE = getResources().getDisplayMetrics().density;

	/**
	 * Minimum touch target size in DP. 48dp is the Android design recommendation
	 */
	private final float MIN_TOUCH_TARGET_DP = 48;
 
	// Default values
	private static final float DEFAULT_CIRCLE_X_RADIUS = 30f;
	private static final float DEFAULT_CIRCLE_Y_RADIUS = 30f;
	private static final float DEFAULT_POINTER_RADIUS = 7f;
	private static final float DEFAULT_POINTER_HALO_WIDTH = 6f;
	private static final float DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f;
	private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 5f;
	private static final float DEFAULT_START_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
	private static final float DEFAULT_END_ANGLE = 270f; // Geometric (clockwise, relative to 3 o'clock)
	private static final int DEFAULT_MAX = 100;
	private static final int DEFAULT_PROGRESS = 0;
	private static final int DEFAULT_CIRCLE_COLOR = Color.DKGRAY;
	private static final int DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255);
	private static final int DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255);
	private static final int DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255);
	private static final int DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255);
	private static final int DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT;
	private static final int DEFAULT_POINTER_ALPHA = 135;
	private static final int DEFAULT_POINTER_ALPHA_ONTOUCH = 100;
	private static final boolean DEFAULT_USE_CUSTOM_RADII = false;
	private static final boolean DEFAULT_MAINTAIN_EQUAL_CIRCLE = true;
	private static final boolean DEFAULT_MOVE_OUTSIDE_CIRCLE = false;

	/**
	 * {@code Paint} instance used to draw the inactive circle.
	 */
	private Paint mCirclePaint;

	/**
	 * {@code Paint} instance used to draw the circle fill.
	 */
	private Paint mCircleFillPaint;

	/**
	 * {@code Paint} instance used to draw the active circle (represents progress).
	 */
	private Paint mCircleProgressPaint;

	/**
	 * {@code Paint} instance used to draw the glow from the active circle.
	 */
	private Paint mCircleProgressGlowPaint;

	/**
	 * {@code Paint} instance used to draw the center of the pointer.
	 * Note: This is broken on 4.0+, as BlurMasks do not work with hardware acceleration.
	 */
	protected Paint mPointerPaint;

	/**
	 * {@code Paint} instance used to draw the halo of the pointer.
	 * Note: The halo is the part that changes transparency.
	 */
	private Paint mPointerHaloPaint;

	/**
	 * {@code Paint} instance used to draw the border of the pointer, outside of the halo.
	 */
	private Paint mPointerHaloBorderPaint;

	private Paint mWychylenieMaxPaint;
	private Paint mWychylenieMinPaint;
	
	/**
	 * The width of the circle (in pixels).
	 */
	private float mCircleStrokeWidth;

	/**
	 * The X radius of the circle (in pixels).
	 */
	private float mCircleXRadius;

	/**
	 * The Y radius of the circle (in pixels).
	 */
	private float mCircleYRadius;

	/**
	 * The radius of the pointer (in pixels).
	 */
	private float mPointerRadius;

	/**
	 * The width of the pointer halo (in pixels).
	 */
	private float mPointerHaloWidth;

	/**
	 * The width of the pointer halo border (in pixels).
	 */
	private float mPointerHaloBorderWidth;

	/**
	 * Start angle of the CircularSeekBar.
	 * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted 
	 * from the mEndAngle to make the circle function properly.
	 */
	private float mStartAngle;

	/**
	 * End angle of the CircularSeekBar.
	 * Note: If mStartAngle and mEndAngle are set to the same angle, 0.1 is subtracted 
	 * from the mEndAngle to make the circle function properly.
	 */
	private float mEndAngle;

	/**
	 * {@code RectF} that represents the circle (or ellipse) of the seekbar.
	 */
	private RectF mCircleRectF = new RectF();
	
	private RectF mCircleRectF30 = new RectF();
	private RectF mCircleRectF60 = new RectF();

	/**
	 * Holds the color value for {@code mPointerPaint} before the {@code Paint} instance is created.
	 */
	private int mPointerColor = DEFAULT_POINTER_COLOR;

	/**
	 * Holds the color value for {@code mPointerHaloPaint} before the {@code Paint} instance is created.
	 */
	private int mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR;
	
	/**
	 * Holds the color value for {@code mPointerHaloPaint} before the {@code Paint} instance is created.
	 */
	private int mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH;

	/**
	 * Holds the color value for {@code mCirclePaint} before the {@code Paint} instance is created.
	 */
	private int mCircleColor = DEFAULT_CIRCLE_COLOR;

	/**
	 * Holds the color value for {@code mCircleFillPaint} before the {@code Paint} instance is created.
	 */
	private int mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR;

	/**
	 * Holds the color value for {@code mCircleProgressPaint} before the {@code Paint} instance is created.
	 */
	private int mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;

	/**
	 * Holds the alpha value for {@code mPointerHaloPaint}.
	 */
	private int mPointerAlpha = DEFAULT_POINTER_ALPHA;

	/**
	 * Holds the OnTouch alpha value for {@code mPointerHaloPaint}.
	 */
	private int mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH;

	/**
	 * Distance (in degrees) that the the circle/semi-circle makes up.
	 * This amount represents the max of the circle in degrees.
	 */
	private float mTotalCircleDegrees;

	/**
	 * Distance (in degrees) that the current progress makes up in the circle.
	 */
	private float mProgressDegrees;

	/**
	 * {@code Path} used to draw the circle/semi-circle.
	 */
	private Path mCirclePath;
	
	private Path mCirclePath30;
	private Path mCirclePath60;
	
	/**
	 * {@code Path} used to draw the progress on the circle.
	 */
	private Path mCircleProgressPath;

	/**
	 * Max value that this CircularSeekBar is representing.
	 */
	private int mMax;

	/**
	 * Progress value that this CircularSeekBar is representing.
	 */
	private int mProgress;

	/**
	 * If true, then the user can specify the X and Y radii.
	 * If false, then the View itself determines the size of the CircularSeekBar.
	 */
	private boolean mCustomRadii;

	/**
	 * Maintain a perfect circle (equal x and y radius), regardless of view or custom attributes.
	 * The smaller of the two radii will always be used in this case.
	 * The default is to be a circle and not an ellipse, due to the behavior of the ellipse.
	 */
	private boolean mMaintainEqualCircle;

	/**
	 * Represents the counter-clockwise distance from {@code mEndAngle} to the touch angle.
	 * Used when touching the CircularSeekBar.
	 * Currently unused, but kept just in case.
	 */
	@SuppressWarnings("unused")
	private float ccwDistanceFromEnd;

	/**
	 * The width of the circle used in the {@code RectF} that is used to draw it.
	 * Based on either the View width or the custom X radius.
	 */
	private float mCircleWidth;

	/**
	 * The height of the circle used in the {@code RectF} that is used to draw it.
	 * Based on either the View width or the custom Y radius.
	 */
	private float mCircleHeight;

	/**
	 * Represents the progress mark on the circle, in geometric degrees.
	 * This is not provided by the user; it is calculated;
	 */
	private float mPointerPosition;

	/**
	 * Pointer position in terms of X and Y coordinates.
	 */
	private float[] mPointerPositionXY = new float[2];

	/**
	 * Listener.
	 */
	private OnCircularSeekBarChangeListener mOnCircularSeekBarChangeListener;

	/**
	 * Initialize the CircularSeekBar with the attributes from the XML style.
	 * Uses the defaults defined at the top of this file when an attribute is not specified by the user.
	 * @param attrArray TypedArray containing the attributes.
	 */
	private void initAttributes(TypedArray attrArray) {
		mCircleXRadius = (float) (attrArray.getFloat(R.styleable.CircularSeekBar_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS) * DPTOPX_SCALE);
		mCircleYRadius = (float) (attrArray.getFloat(R.styleable.CircularSeekBar_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS) * DPTOPX_SCALE);
		mPointerRadius = (float) (attrArray.getFloat(R.styleable.CircularSeekBar_pointer_radius, DEFAULT_POINTER_RADIUS) * DPTOPX_SCALE);
		mPointerHaloWidth = (float) (attrArray.getFloat(R.styleable.CircularSeekBar_pointer_halo_width, DEFAULT_POINTER_HALO_WIDTH) * DPTOPX_SCALE);
		mPointerHaloBorderWidth = (float) (attrArray.getFloat(R.styleable.CircularSeekBar_pointer_halo_border_width, DEFAULT_POINTER_HALO_BORDER_WIDTH) * DPTOPX_SCALE);
		mCircleStrokeWidth = (float) (attrArray.getFloat(R.styleable.CircularSeekBar_circle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH) * DPTOPX_SCALE);

		String tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_color);
		if (tempColor != null) {
			try {
				mPointerColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mPointerColor = DEFAULT_POINTER_COLOR;
			}
		}

		tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color);
		if (tempColor != null) {
			try {
				mPointerHaloColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR;
			}
		}
		
		tempColor = attrArray.getString(R.styleable.CircularSeekBar_pointer_halo_color_ontouch);
		if (tempColor != null) {
			try {
				mPointerHaloColorOnTouch = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH;
			}
		}

		tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_color);
		if (tempColor != null) {
			try {
				mCircleColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mCircleColor = DEFAULT_CIRCLE_COLOR;
			}
		}

		tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_progress_color);
		if (tempColor != null) {
			try {
				mCircleProgressColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR;
			}
		}

		tempColor = attrArray.getString(R.styleable.CircularSeekBar_circle_fill);
		if (tempColor != null) {
			try {
				mCircleFillColor = Color.parseColor(tempColor);
			} catch (IllegalArgumentException e) {
				mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR;
			}
		}

		mPointerAlpha = Color.alpha(mPointerHaloColor);

		mPointerAlphaOnTouch = attrArray.getInt(R.styleable.CircularSeekBar_pointer_alpha_ontouch, DEFAULT_POINTER_ALPHA_ONTOUCH);
		if (mPointerAlphaOnTouch > 255 || mPointerAlphaOnTouch < 0) {
			mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH;
		}

		mMax = attrArray.getInt(R.styleable.CircularSeekBar_max, DEFAULT_MAX);
		mProgress = attrArray.getInt(R.styleable.CircularSeekBar_progress, DEFAULT_PROGRESS);
		mCustomRadii = attrArray.getBoolean(R.styleable.CircularSeekBar_use_custom_radii, DEFAULT_USE_CUSTOM_RADII);
		mMaintainEqualCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_maintain_equal_circle, DEFAULT_MAINTAIN_EQUAL_CIRCLE);
//		mMoveOutsideCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_move_outside_circle, DEFAULT_MOVE_OUTSIDE_CIRCLE);

		// Modulo 360 right now to avoid constant conversion
		mStartAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_start_angle), DEFAULT_START_ANGLE) % 360f)) % 360f);
		mEndAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_end_angle), DEFAULT_END_ANGLE) % 360f)) % 360f);

		if (mStartAngle == mEndAngle) {
			//mStartAngle = mStartAngle + 1f;
			mEndAngle = mEndAngle - .1f;
		}

	}

	/**
	 * Initializes the {@code Paint} objects with the appropriate styles.
	 */
	private void initPaints() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setDither(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
		mCirclePaint.setStyle(Paint.Style.STROKE);
		mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
		mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

		mCircleFillPaint = new Paint();
		mCircleFillPaint.setAntiAlias(true);
		mCircleFillPaint.setDither(true);
		mCircleFillPaint.setColor(mCircleFillColor);
		mCircleFillPaint.setStyle(Paint.Style.FILL);

		mCircleProgressPaint = new Paint();
		mCircleProgressPaint.setAntiAlias(true);
		mCircleProgressPaint.setDither(true);
		mCircleProgressPaint.setColor(mCircleProgressColor);
		mCircleProgressPaint.setStrokeWidth(mCircleStrokeWidth);
		mCircleProgressPaint.setStyle(Paint.Style.STROKE);
		mCircleProgressPaint.setStrokeJoin(Paint.Join.ROUND);
		mCircleProgressPaint.setStrokeCap(Paint.Cap.ROUND);

		mCircleProgressGlowPaint = new Paint();
		mCircleProgressGlowPaint.set(mCircleProgressPaint);
		mCircleProgressGlowPaint.setMaskFilter(new BlurMaskFilter((5f * DPTOPX_SCALE), BlurMaskFilter.Blur.NORMAL));

		mPointerPaint = new Paint();
		mPointerPaint.setAntiAlias(true);
		mPointerPaint.setDither(true);
		mPointerPaint.setStyle(Paint.Style.FILL);
		mPointerPaint.setColor(mPointerColor);
		mPointerPaint.setStrokeWidth(mPointerRadius);

		mPointerHaloPaint = new Paint();
		mPointerHaloPaint.set(mPointerPaint);
		mPointerHaloPaint.setColor(mPointerHaloColor);
		mPointerHaloPaint.setAlpha(mPointerAlpha);
		mPointerHaloPaint.setStrokeWidth(mPointerRadius + mPointerHaloWidth);

		mPointerHaloBorderPaint = new Paint();
		mPointerHaloBorderPaint.set(mPointerPaint);
		mPointerHaloBorderPaint.setStrokeWidth(mPointerHaloBorderWidth);
		mPointerHaloBorderPaint.setStyle(Paint.Style.STROKE);

		mWychylenieMaxPaint = new Paint();
		mWychylenieMaxPaint.set(mPointerPaint);
		mWychylenieMaxPaint.setColor(mPointerHaloColor);
		mWychylenieMaxPaint.setStyle(Paint.Style.STROKE);
		mWychylenieMaxPaint.setStrokeCap(Paint.Cap.SQUARE);
		mWychylenieMaxPaint.setStrokeWidth((float) (1.5*mPointerRadius + 0.5*mPointerHaloWidth));
		
		mWychylenieMinPaint = new Paint();
		mWychylenieMinPaint.set(mPointerPaint);
		mWychylenieMinPaint.setColor(Color.argb(135, 170, 0, 0));
		mWychylenieMinPaint.setStyle(Paint.Style.STROKE);
		mWychylenieMinPaint.setStrokeCap(Paint.Cap.SQUARE);
		mWychylenieMinPaint.setStrokeWidth((float)(0.5* mPointerRadius + 0.5* mPointerHaloWidth));
		
	}

	/**
	 * Calculates the total degrees between mStartAngle and mEndAngle, and sets mTotalCircleDegrees 
	 * to this value.
	 */
	private void calculateTotalDegrees() {
		mTotalCircleDegrees = (360f - (mStartAngle - mEndAngle)) % 360f; // Length of the entire circle/arc
		if (mTotalCircleDegrees <= 0f) {
			mTotalCircleDegrees = 360f;
		}
	}

	/**
	 * Calculate the degrees that the progress represents. Also called the sweep angle.
	 * Sets mProgressDegrees to that value.
	 */
	private void calculateProgressDegrees() {
		mProgressDegrees = mPointerPosition - mStartAngle; // Verified
		mProgressDegrees = (mProgressDegrees < 0 ? 360f + mProgressDegrees : mProgressDegrees); // Verified
	}

	/**
	 * Calculate the pointer position (and the end of the progress arc) in degrees.
	 * Sets mPointerPosition to that value.
	 */
	private void calculatePointerAngle() {
		float progressPercent = ((float)mProgress / (float)mMax);
		mPointerPosition = (progressPercent * mTotalCircleDegrees) + mStartAngle;
		mPointerPosition = mPointerPosition % 360f;
	}

	private void calculatePointerXYPosition() {
		PathMeasure pm = new PathMeasure(mCircleProgressPath, false);
		boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);
		if (!returnValue) {
			pm = new PathMeasure(mCirclePath, false);
			returnValue = pm.getPosTan(0, mPointerPositionXY, null);
		}
	}

	/**
	 * Initialize the {@code Path} objects with the appropriate values. 
	 */
	private void initPaths() {
		mCirclePath = new Path();
		mCirclePath.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees);

		mCirclePath30 = new Path();
		mCirclePath30.addArc(mCircleRectF30, mStartAngle, mTotalCircleDegrees);
		
		mCirclePath60 = new Path();
		mCirclePath60.addArc(mCircleRectF60, mStartAngle, mTotalCircleDegrees);
		
		mCircleProgressPath = new Path();
		mCircleProgressPath.addArc(mCircleRectF, mStartAngle, mProgressDegrees);
		
	}

	/**
	 * Initialize the {@code RectF} objects with the appropriate values. 
	 */
	private void initRects() {
		mCircleRectF.set(-mCircleWidth, -mCircleHeight, mCircleWidth, mCircleHeight);
		mCircleRectF30.set(-mCircleWidth*1/3, -mCircleHeight*1/3, mCircleWidth*1/3, mCircleHeight*1/3);
		mCircleRectF60.set(-mCircleWidth*2/3, -mCircleHeight*2/3, mCircleWidth*2/3, mCircleHeight*2/3);
	}

	final int rBins = 18;
	final int phiBins = 72;

	private int[][] mWychylenia = new int[rBins][phiBins]; //r, phi
	private long maxCount=0;
	
	public void putWychylenie(float r, float phi){
		int radius = (int) Math.floor(r/5f);
		int angle = (int) Math.floor(phi/5f);
		if(radius>=0 && radius<rBins 
			&& angle>=0 && angle <phiBins){
//			Log.d("TAG", "[r,phi]=\t" + radius + "\t" + angle);
			mWychylenia[radius][angle]++;
		}else{
			Log.d("TAG", "?! [r,phi]=\t"+radius +"\t"+angle);
		}
		invalidate();
	}
	
	
	void drawEmptyPlane(Canvas canvas, Paint p) {
		float baseAngle = -90;

		int colorUpdate = Color.argb(100, 255, 255, 255);
		p.setColor(colorUpdate);
		
		for(int j=0; j<rBins; j++){
			for (int i = 0; i < phiBins; i++) {
				
				float rRatio = j/(float)rBins;
				RectF mRect = new RectF(-mCircleWidth * rRatio,	-mCircleHeight * rRatio, 
						mCircleWidth * rRatio, mCircleHeight * rRatio);

				p.setStrokeWidth((float) (-1 + mCircleWidth / (float)rBins));
				float singleStroke = (360/phiBins)/2;
				canvas.drawArc(mRect, baseAngle + i * (360/phiBins) - singleStroke/2.5f, singleStroke*0.8f, false, p); //co 5 stopni
			}
		}
	}
	
	

	
	void drawAngleArray(int[][] anglesArray, Canvas canvas, Paint p) {
		float baseAngle = -90;
		
		for(int j=0; j<rBins; j++){
			for (int i = 0; i < phiBins; i++) {
				
				if(anglesArray[j][i]==0) continue;
				
				if(anglesArray[j][i] > maxCount){
					maxCount = anglesArray[j][i];
					invalidate();
				}
				
				float frequencyRatio = (float) anglesArray[j][i] / (float) maxCount;
//				Log.e("TAG", "freaq: "+frequencyRatio);
				int colorUpdate = Color.argb((int)(120 + 100*frequencyRatio), 
						(int)(255*(frequencyRatio)), 
						(int)(255*(1-frequencyRatio)), 
						0);
				p.setColor(colorUpdate);
				
				float rRatio = j/(float)rBins;
				RectF mRect = new RectF(-mCircleWidth * rRatio,	-mCircleHeight * rRatio, 
						mCircleWidth * rRatio, mCircleHeight * rRatio);

				p.setStrokeWidth((float) (-1 + mCircleWidth / (float)rBins));
				float singleStroke = (360/phiBins)/2;
				canvas.drawArc(mRect, baseAngle + i * (360/phiBins) - singleStroke/2.5f, singleStroke*0.8f, false, p); //co 5 stopni
			}
		}
	}
	
	
	public void resetCount(){
		mWychylenia = new int[rBins][phiBins];
		maxCount = 0;
		redraw = true;
	}
	
	
	private Bitmap canvasBackground;
	boolean redraw = true;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (canvasBackground == null || redraw) {
		
			canvasBackground = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvasBuffer = new Canvas(canvasBackground);

			canvasBuffer.translate(this.getWidth() / 2, this.getHeight() / 2);
			
			canvasBuffer.drawPath(mCirclePath, mCirclePaint);
			canvasBuffer.drawPath(mCirclePath30, mCirclePaint);
			canvasBuffer.drawPath(mCirclePath60, mCirclePaint);

			drawEmptyPlane(canvasBuffer, mWychylenieMaxPaint);
			
			canvas.drawBitmap(canvasBackground, 0, 0, mWychylenieMaxPaint);
			canvas.translate(this.getWidth() / 2, this.getHeight() / 2);
			drawAngleArray(mWychylenia, canvas, mWychylenieMaxPaint);
			redraw = false;
		}else{
			canvas.drawBitmap(canvasBackground, 0, 0, mWychylenieMaxPaint);
			canvas.translate(this.getWidth() / 2, this.getHeight() / 2);
			drawAngleArray(mWychylenia, canvas, mWychylenieMaxPaint);
		}
		
	}

	/**
	 * Get the progress of the CircularSeekBar.
	 * @return The progress of the CircularSeekBar.
	 */
	public int getProgress() {
		int progress = Math.round((float)mMax * mProgressDegrees / mTotalCircleDegrees);
		return progress;
	}

	/**
	 * Set the progress of the CircularSeekBar.
	 * If the progress is the same, then any listener will not receive a onProgressChanged event.
	 * @param progress The progress to set the CircularSeekBar to.
	 */
	public void setProgress(int progress) {
		if (mProgress != progress) {
			mProgress = progress;
			if (mOnCircularSeekBarChangeListener != null) {
				mOnCircularSeekBarChangeListener.onProgressChanged(this, progress, false);
			}

			recalculateAll();
			invalidate();
		}
	}

	private void setProgressBasedOnAngle(float angle) {
		mPointerPosition = angle;
		calculateProgressDegrees();
		mProgress = Math.round((float)mMax * mProgressDegrees / mTotalCircleDegrees);
	}

	private void recalculateAll() {
		calculateTotalDegrees();
		calculatePointerAngle();
		calculateProgressDegrees();

		initRects();
		initPaths();

		calculatePointerXYPosition();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		if (mMaintainEqualCircle) {
			int min = Math.min(width, height);
			setMeasuredDimension(min, min);
		} else {
			setMeasuredDimension(width, height);
		}

		// Set the circle width and height based on the view for the moment
		mCircleHeight = (float)height / 2f - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
		mCircleWidth = (float)width / 2f - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);

		// If it is not set to use custom
		if (mCustomRadii) {
			// Check to make sure the custom radii are not out of the view. If they are, just use the view values
			if ((mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth) < mCircleHeight) {
				mCircleHeight = mCircleYRadius - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
			}

			if ((mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth) < mCircleWidth) {
				mCircleWidth = mCircleXRadius - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
			}
		}

		if (mMaintainEqualCircle) { // Applies regardless of how the values were determined
			float min = Math.min(mCircleHeight, mCircleWidth);
			mCircleHeight = min;
			mCircleWidth = min;
		}

		recalculateAll();
	}

	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyle, 0);

		initAttributes(attrArray);

		attrArray.recycle();

		initPaints();
	}

	public HistogramView(Context context) {
		super(context);
		init(null, 0);
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public HistogramView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable("PARENT", superState);
		state.putInt("MAX", mMax);
		state.putInt("PROGRESS", mProgress);
		state.putInt("mCircleColor", mCircleColor);
		state.putInt("mCircleProgressColor", mCircleProgressColor);
		state.putInt("mPointerColor", mPointerColor);
		state.putInt("mPointerHaloColor", mPointerHaloColor);
		state.putInt("mPointerHaloColorOnTouch", mPointerHaloColorOnTouch);
		state.putInt("mPointerAlpha", mPointerAlpha);
		state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch);

		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle savedState = (Bundle) state;

		Parcelable superState = savedState.getParcelable("PARENT");
		super.onRestoreInstanceState(superState);

		mMax = savedState.getInt("MAX");
		mProgress = savedState.getInt("PROGRESS");
		mCircleColor = savedState.getInt("mCircleColor");
		mCircleProgressColor = savedState.getInt("mCircleProgressColor");
		mPointerColor = savedState.getInt("mPointerColor");
		mPointerHaloColor = savedState.getInt("mPointerHaloColor");
		mPointerHaloColorOnTouch = savedState.getInt("mPointerHaloColorOnTouch");
		mPointerAlpha = savedState.getInt("mPointerAlpha");
		mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch");
		
		initPaints();

		recalculateAll();
	}


	public void setOnSeekBarChangeListener(OnCircularSeekBarChangeListener l) {
		mOnCircularSeekBarChangeListener = l;
	}

	/**
	* Listener for the CircularSeekBar. Implements the same methods as the normal OnSeekBarChangeListener.
	*/
	public interface OnCircularSeekBarChangeListener {

		public abstract void onProgressChanged(HistogramView circularSeekBar, int progress, boolean fromUser);

		public abstract void onStopTrackingTouch(HistogramView seekBar);

		public abstract void onStartTrackingTouch(HistogramView seekBar);
	}
	
	/**
	 * Sets the circle color.
	 * @param color the color of the circle
	 */
	public void setCircleColor(int color) {
		mCircleColor = color;
		mCirclePaint.setColor(mCircleColor);
		invalidate();
	}
	
	/**
	 * Gets the circle color.
	 * @return An integer color value for the circle
	 */
	public int getCircleColor() {
		return mCircleColor;
	}
	
	/**
	 * Sets the circle progress color.
	 * @param color the color of the circle progress
	 */
	public void setCircleProgressColor(int color) {
		mCircleProgressColor = color;
		mCircleProgressPaint.setColor(mCircleProgressColor);
		invalidate();
	}
	
	/**
	 * Gets the circle progress color.
	 * @return An integer color value for the circle progress
	 */
	public int getCircleProgressColor() {
		return mCircleProgressColor;
	}
	
	/**
	 * Sets the pointer color.
	 * @param color the color of the pointer
	 */
	public void setPointerColor(int color) {
		mPointerColor = color;
		mPointerPaint.setColor(mPointerColor);
		invalidate();
	}
	
	/**
	 * Gets the pointer color.
	 * @return An integer color value for the pointer
	 */
	public int getPointerColor() {
		return mPointerColor;
	}
	
	/**
	 * Sets the pointer halo color.
	 * @param color the color of the pointer halo
	 */
	public void setPointerHaloColor(int color) {
		mPointerHaloColor = color;
		mPointerHaloPaint.setColor(mPointerHaloColor);
		invalidate();
	}
	
	/**
	 * Gets the pointer halo color.
	 * @return An integer color value for the pointer halo
	 */
	public int getPointerHaloColor() {
		return mPointerHaloColor;
	}
	
	/**
	 * Sets the pointer alpha.
	 * @param alpha the alpha of the pointer
	 */
	public void setPointerAlpha(int alpha) {
		if (alpha >=0 && alpha <= 255) {
			mPointerAlpha = alpha;
			mPointerHaloPaint.setAlpha(mPointerAlpha);
			invalidate();
		}
	}
	
	/**
	 * Gets the pointer alpha value.
	 * @return An integer alpha value for the pointer (0..255)
	 */
	public int getPointerAlpha() {
		return mPointerAlpha;
	}
	
	/**
	 * Sets the pointer alpha when touched.
	 * @param alpha the alpha of the pointer (0..255) when touched
	 */
	public void setPointerAlphaOnTouch(int alpha) {
		if (alpha >=0 && alpha <= 255) {
			mPointerAlphaOnTouch = alpha;
		}
	}
	
	/**
	 * Gets the pointer alpha value when touched.
	 * @return An integer alpha value for the pointer (0..255) when touched
	 */
	public int getPointerAlphaOnTouch() {
		return mPointerAlphaOnTouch;
	}

	/**
	 * Sets the circle fill color.
	 * @param color the color of the circle fill
	 */
	public void setCircleFillColor(int color) {
		mCircleFillColor = color;
		mCircleFillPaint.setColor(mCircleFillColor);
		invalidate();
	}
	
	/**
	 * Gets the circle fill color.
	 * @return An integer color value for the circle fill
	 */
	public int getCircleFillColor() {
		return mCircleFillColor;
	}

	/**
	 * Set the max of the CircularSeekBar.
	 * If the new max is less than the current progress, then the progress will be set to zero.
	 * If the progress is changed as a result, then any listener will receive a onProgressChanged event.
	 * @param max The new max for the CircularSeekBar.
	 */
	public void setMax(int max) {
		if (!(max <= 0)) { // Check to make sure it's greater than zero
			if (max <= mProgress) {
				mProgress = 0; // If the new max is less than current progress, set progress to zero
				if (mOnCircularSeekBarChangeListener != null) {
					mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, false);
				}
			}
			mMax = max;
			
			recalculateAll();
			invalidate();
		}
	}
	
	/**
	 * Get the current max of the CircularSeekBar.
	 * @return Synchronized integer value of the max.
	 */
	public synchronized int getMax() {
		return mMax;
	}


	//distance from (x, y) to that point (a, b) in the center
	double distance(float x, float y, float a, float b)
	{
	    return Math.sqrt((x - a) * (x - a) + (y - b) * (y - b));
	}
	
		
//	@Override
//	public boolean onDragEvent(DragEvent event) {
////		this.onTouchEvent(event);
//		return super.onDragEvent(event);
//	}
	float lastUpdateTime=0;
	
	private void useEvent(MotionEvent me){
        float xPosition = me.getX()/(getMeasuredWidth());
        float yPosition = me.getY()/(getMeasuredHeight());
        
        double dist = 200*distance(xPosition, yPosition, 0.5f, 0.5f);
        double phi = 90+Math.toDegrees(
        		Math.atan(
        		(yPosition-0.5f)/(xPosition-0.5f))
        		);
        phi=Math.signum((xPosition-0.5f))<0?phi+180:phi;
        
        Log.d("TAG", "(\t"+xPosition +",\t"+yPosition+"\t) => \t"+dist+" , \t"+phi);
        
//        putWychylenie((float)dist, (float)phi);
		
		final long now = System.nanoTime();
		if (now > lastUpdateTime + TimeUnit.MILLISECONDS.toNanos(200)) {
			invalidate();
			lastUpdateTime = System.nanoTime();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
//        	useEvent(event);
        	break;
        case MotionEvent.ACTION_MOVE:
//        	useEvent(event);
        	break;
        case MotionEvent.ACTION_UP:
        	resetCount();
            break;
        case MotionEvent.ACTION_CANCEL:
            break;
        }
        return true;
//		return super.onTouchEvent(event);
	}
		
}
