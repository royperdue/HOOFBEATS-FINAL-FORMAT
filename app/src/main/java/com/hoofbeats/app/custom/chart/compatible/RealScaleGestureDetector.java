package com.hoofbeats.app.custom.chart.compatible;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ScaleGestureDetector;

@SuppressLint("NewApi")
public class RealScaleGestureDetector extends ScaleGestureDetector
{
    public RealScaleGestureDetector(Context context, final ScaleGestureDetector fakeScaleGestureDetector,
                                    final ScaleGestureDetector.SimpleOnScaleGestureListener fakeListener)
    {
        super(context, new SimpleOnScaleGestureListener()
        {
            @Override
            public boolean onScale(ScaleGestureDetector detector)
            {
                return fakeListener.onScale(fakeScaleGestureDetector);
            }
        });
    }
}
