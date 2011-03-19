package com.android.licai.view;
import java.util.HashMap;
import java.util.Map;

import com.android.licai.R;
import com.android.licai.data.Constant;

import android.content.Context;
import android.graphics.*;
import android.util.*;
import android.view.MotionEvent;
import android.view.View;

public class StatView extends View{

//	Canvas canvas;
	public StatView(Context context) {
		super(context);
		initStatView();
	}
	
	public StatView(Context context,AttributeSet atrs){
		super(context,atrs);
		initStatView();
	}
	
	public StatView(Context context,AttributeSet attrs,int defaultStyle){
		super(context,attrs,defaultStyle);
		initStatView();
	}
	
	private void initStatView() {
		this.setFocusable(true);
		
//		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		circlePaint.setColor(R.drawable.solid_red);
//		circlePaint.setStrokeWidth(10);
//		circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		bear = 0;
	}
	
	float startX,startY,endX,endY;
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                break;
            /*case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;*/
            case MotionEvent.ACTION_MOVE:
                endX = x;
                endY = y;
                bear += (startX - endX);
                invalidate();
                break;
        }
        return true;
	}
	
	@Override
	protected void onMeasure(int width,int height){
		int measureWidth = measure(width);
		int measureHeight= measure(height);
		
		int d = Math.min(measureWidth, measureHeight);
		
		setMeasuredDimension(d,d);
	}
	
	private int measure(int spec){
		/*int rt = 0;
		
		int mode = MeasureSpec.getMode(spec);
		int size = MeasureSpec.getSize(spec);
		
		if(mode == MeasureSpec.UNSPECIFIED){
			rt = 200;
		}else{
			rt = 200;
		}
		return rt;*/
		return 200;
	}
	
//	private Paint circlePaint;
	String []str = getResources().getStringArray(R.array.items);

	@Override
	protected void onDraw(Canvas canvas){
		
		//canvas = c;
		px = getMeasuredWidth()/2;
		py = getMeasuredHeight()/2;
		
		raduis = Math.min(px,py);
		
		if(map == null || map.size() == 0){
			Paint mPaint = new Paint();  
	        mPaint.setAntiAlias(true);
	        mPaint.setColor(Color.WHITE&0x00ffffff);
			canvas.drawCircle(px, py, raduis, mPaint);
			super.onDraw(canvas);
			return ;
		}
		
		double [] d = new double[str.length];
		double sum = 0;
		Double rt   = null;
		int i = 0;
		while(i<str.length){
			d[i] = (rt = (Double)map.get(str[i])) == null?0:rt;
			sum += d[i];
			i++;
		}
		
		i = 0;
		Paint mPaint = new Paint();  
        mPaint.setAntiAlias(true);
        double start,to;
        start = 0;
        to = 0;
        canvas.rotate(bear,raduis,raduis);
		while(i<str.length)
		{
			mPaint.setColor(Constant.color[i]);
			start +=to;
			to = (d[i] * 360)/sum;
			if(to > 0)	canvas.drawArc(new RectF(0,0,px*2,py*2), (float)start, (float)to, true, mPaint);
			i++;
		}
		
		//canvas.drawCircle(px, py, radius, circlePaint);
		/*RectF rect = new RectF(0,0,100,100);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(R.drawable.white);
		p.setStrokeWidth(0);
		canvas.drawRoundRect(rect, px, py, p);*/
		
        //canvas.drawCircle(px, py, 20, mPaint);
        //canvas.drawArc(new RectF(0,0,40,40), 0, 30, true, mPaint);
        //canvas.drawCircle(px, py, raduis, mPaint);
		//map = null;
		
        super.onDraw(canvas);
	}
	
	int px,py,raduis;

	
	Map<String, Object> map;
	private float bear;
	
	//Ðý×ª
	public void setBear(float _bear){
		bear = _bear;
	}
	
	public float getBear(){
		return bear;
	}
	
	public void drawMyCircle(Map<String, Object> m){
		map = m;
		invalidate();
	}
}
