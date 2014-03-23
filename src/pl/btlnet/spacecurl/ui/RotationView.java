/*
 * 
 * Copyright 2013 Matt Joseph
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * 
 * This custom view/widget was inspired and guided by:
 * 
 * HoloCircleSeekBar - Copyright 2012 Jes�s Manzano
 * HoloColorPicker - Copyright 2012 Lars Werkman (Designed by Marie Schweiz)
 * 
 * Although I did not used the code from either project directly, they were both used as 
 * reference material, and as a result, were extremely helpful.
 */

package pl.btlnet.spacecurl.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

public class RotationView extends CircularSeekBar {

	public RotationView(Context context) {
		super(context);
	}

	public RotationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RotationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	String innerText;
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		innerText = ""+getProgress();
		
		mPointerPaint.setTextSize(this.getWidth()/4);
		mPointerPaint.setTextAlign(Align.CENTER);
		
		Rect textBounds = new Rect();
		mPointerPaint.getTextBounds(innerText, 0, innerText.length(), textBounds);
		
		canvas.drawText(""+getProgress(), 0, textBounds.height()/2, mPointerPaint);
//		Log.d("DRAW", getProgress()+" " +this.getWidth() / 2 +" "+ this.getHeight() / 2);
	}

}