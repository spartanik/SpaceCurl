package pl.btlnet.spacecurl.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import pl.btlnet.spacecurl.R;

public class VerticalSeekBar extends SeekBar{
    private int x, y, z, w;

    @Override
    protected void drawableStateChanged() {
        // TODO Auto-generated method stub
        super.drawableStateChanged();
    }

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
        this.x = w;
        this.y = h;
        this.z = oldw;
        this.w = oldh;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
            int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:

            setSelected(true);
            setPressed(true);
            break;
        case MotionEvent.ACTION_MOVE:
            setProgress(getMax()
                    - (int) (getMax() * event.getY() / getHeight()));
            onSizeChanged(getWidth(), getHeight(), 0, 0);

            break;
        case MotionEvent.ACTION_UP:
            setSelected(false);
            setPressed(false);
            break;

        case MotionEvent.ACTION_CANCEL:
            break;
        }
        return true;
    }

    @Override
    public synchronized void setProgress(int progress) {

        if (progress >= 0)
            super.setProgress(progress);

        else
            super.setProgress(0);
        onSizeChanged(x, y, z, w);

    }
}