package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CameraFrontSightView extends View
{
	private static final String TAG = "CameraFrontSightView";
	protected int mWidth;
	protected int mHeight;
	private Paint mPaint = new Paint();
	private boolean mDoScaling = false;
	private boolean mDoFading = false;
	private long mTime = 0L;
	private int mHalfWidth;
	private int mHalfHeight;
	private int mLineWidth;
	private ViewGroup.LayoutParams mLayoutParams;
	
	public CameraFrontSightView(Context context)
	{
		super(context);
	}
	
	public CameraFrontSightView(Context context, AttributeSet attr)
	{
		super(context, attr);
	}
	
	public CameraFrontSightView(Context context, AttributeSet attr, int style)
	{
		super(context, attr, style);
	}
	
	public final void startDraw()
	{
		setVisibility(View.VISIBLE);
		mDoScaling = true;
		mDoFading = false;
		mTime = System.currentTimeMillis();
		invalidate();
	}

	public final void init(int w, int h)
	{
		mWidth = dp2Px(w);
		mHeight = dp2Px(h);
		mLayoutParams = getLayoutParams();
		if (mLayoutParams != null)
		{
			mLayoutParams.width = mWidth;
			mLayoutParams.height = mHeight;
		}
		setLayoutParams(mLayoutParams);
		mHalfWidth = (mWidth / 2);
		mHalfHeight = (mHeight / 2);
		mLineWidth = dp2Px(1);
		mPaint.setColor(-8393929);
		mPaint.setStrokeWidth(mLineWidth);
	}
	@Override
	public void draw(Canvas canvas)
	{
		canvas.translate(mHalfWidth / 2, mHalfHeight / 2);
		long timeElapse = System.currentTimeMillis() - mTime;
		if (timeElapse > 200L)
		{
		mDoScaling = false;
		}
		if (timeElapse > 1100L)
		{
		mDoFading = true;
		}
		if (timeElapse > 1300L)
		{
		setVisibility(View.GONE);
		return;
		}
		if (mDoScaling)
		{
		float f1 = 1.0F + (float)(200L - timeElapse) / 200.0F;
		canvas.scale(f1, f1, mHalfWidth / 2, mHalfHeight / 2);
		float f2 = 2.0F - f1;
		mPaint.setAlpha((int)(f2 * 255.0F));
		}
		if (mDoFading)
		{
			float f = (1300F - timeElapse) / 200F;
			mPaint.setAlpha((int)(f * 255));
		}
		canvas.drawLine(0.0F, 0.0F, mHalfWidth, 0.0F, mPaint);
		canvas.drawLine(0.0F, 0.0F, 0.0F, mHalfHeight, mPaint);
		canvas.drawLine(mHalfWidth, 0.0F, mHalfWidth, mHalfHeight, mPaint);
		canvas.drawLine(0.0F, mHalfHeight, mHalfWidth, mHalfHeight, mPaint);
		canvas.drawLine(0.0F, mHalfHeight / 2, mHalfWidth / 10, mHalfHeight / 2, mPaint);
		canvas.drawLine(mHalfWidth, mHalfHeight / 2, 9 * mHalfWidth / 10, mHalfHeight / 2, mPaint);
		canvas.drawLine(mHalfWidth / 2, 0.0F, mHalfWidth / 2, mHalfHeight / 10, mPaint);
		canvas.drawLine(mHalfWidth / 2, mHalfHeight, mHalfWidth / 2, 9 * mHalfHeight / 10, mPaint);
		invalidate();
	}

	private int dp2Px(float dp)
	{
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int)(dp * scale + 0.5f);
	}
}
