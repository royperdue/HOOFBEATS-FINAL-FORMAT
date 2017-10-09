package com.hoofbeats.app.custom.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;

import java.text.DecimalFormat;

public class plot2d extends View
{
    private int axes = 1;
    private float locxAxis;
    private float locyAxis;
    private float maxx;
    private float maxy;
    private float minx;
    private float miny;
    private Paint paint;
    private Paint paintdot;
    private int vectorLength;
    private float[] xvalues;
    private float[] yvalues;

    public plot2d(Context context, float[] xvalues, float[] yvalues, int axes)
    {
        super(context);
        this.xvalues = xvalues;
        this.yvalues = yvalues;
        this.axes = axes;
        this.vectorLength = xvalues.length;
        this.paint = new Paint();
        this.paintdot = new Paint();
        getAxes(xvalues, yvalues);
    }

    protected void onDraw(Canvas canvas)
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float canvasWidth = (float) metrics.widthPixels;
        int canvasHeight = (int) (((double) ((float) metrics.heightPixels)) * 0.886d);
        String xAxis = "X";
        String yAxis = "Y";
        this.paint.setStrokeWidth(5.0f);
        this.paintdot.setStrokeWidth(10.0f);
        canvas.drawARGB(255, 191, 196, 199);
        if (this.axes != 0)
        {
            this.paint.setTextAlign(Align.CENTER);
            this.paint.setTextSize(24.0f);
            float xaxis = this.maxx - this.minx;
            float yaxis = this.maxy - this.miny;
            int locxAxisInPixels;
            int locyAxisInPixels;
            int[] xvaluesInPixels;
            int[] yvaluesInPixels;
            int i;
            DecimalFormat df;
            float temp;
            Canvas canvas2;
            if (((double) yaxis) >= ((double) xaxis) * 1.575d)
            {
                locxAxisInPixels = toPixelInt((float) canvasHeight, yaxis, this.minx, this.locxAxis);
                locyAxisInPixels = toPixelInt((float) canvasHeight, yaxis, this.miny, this.locyAxis);
                xvaluesInPixels = toPixel((float) canvasHeight, yaxis, this.minx, this.xvalues);
                yvaluesInPixels = toPixel((float) canvasHeight, yaxis, this.miny, this.yvalues);
                this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
                for (i = 1; i <= 10; i++)
                {
                    df = new DecimalFormat("0.00");
                    temp = (10.0f * (this.minx + ((((float) (i - 1)) * (this.maxy - this.miny)) / ((float) 6)))) / 10.0f;
                    canvas.drawText("" + df.format((double) temp), (float) toPixelInt((float) canvasHeight, yaxis, this.minx, temp), (float) (canvasHeight - 20), this.paint);
                    canvas2 = canvas;
                    canvas2.drawText("|", (float) toPixelInt((float) canvasHeight, yaxis, this.minx, temp), (float) (canvasHeight + 7), this.paint);
                    canvas2 = canvas;
                    canvas2.drawText("|", (float) toPixelInt((float) canvasHeight, yaxis, this.minx, temp), 2.0f, this.paint);
                    temp = (10.0f * (this.miny + ((((float) (i - 1)) * (this.maxy - this.miny)) / ((float) 6)))) / 10.0f;
                    canvas.drawText("" + df.format((double) temp), 40.0f, ((float) canvasHeight) - ((float) toPixelInt((float) canvasHeight, yaxis, this.miny, temp)), this.paint);
                    canvas2 = canvas;
                    canvas2.drawText("--", 0.0f, ((float) canvasHeight) - ((float) toPixelInt((float) canvasHeight, yaxis, this.miny, temp)), this.paint);
                    canvas2 = canvas;
                    canvas2.drawText("--", canvasWidth, ((float) canvasHeight) - ((float) toPixelInt((float) canvasHeight, yaxis, this.miny, temp)), this.paint);
                }
                for (i = 0; i < this.vectorLength - 1; i++)
                {
                    this.paintdot.setColor(Color.BLUE);
                    this.paint.setColor(Color.BLUE);
                    canvas.drawPoint((float) xvaluesInPixels[i], (float) (canvasHeight - yvaluesInPixels[i]), this.paintdot);
                    canvas.drawLine((float) xvaluesInPixels[i], (float) (canvasHeight - yvaluesInPixels[i]), (float) xvaluesInPixels[i + 1], (float) (canvasHeight - yvaluesInPixels[i + 1]), this.paint);
                    canvas.drawPoint((float) xvaluesInPixels[i + 1], (float) (canvasHeight - yvaluesInPixels[i + 1]), this.paintdot);
                }
                return;
            }
            locxAxisInPixels = toPixelInt(canvasWidth, xaxis, this.minx, this.locxAxis);
            locyAxisInPixels = toPixelInt(canvasWidth, xaxis, this.miny, this.locyAxis);
            xvaluesInPixels = toPixel(canvasWidth, xaxis, this.minx, this.xvalues);
            yvaluesInPixels = toPixel(canvasWidth, xaxis, this.miny, this.yvalues);
            this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            for (i = 1; i <= 10; i++)
            {
                df = new DecimalFormat("0.00");
                temp = (10.0f * (this.minx + ((((float) (i - 1)) * (this.maxx - this.minx)) / ((float) 6)))) / 10.0f;
                canvas.drawText("" + df.format((double) temp), (float) toPixelInt((float) canvasHeight, yaxis, this.minx, temp), (float) (canvasHeight - 20), this.paint);
                canvas2 = canvas;
                canvas2.drawText("|", (float) toPixelInt((float) canvasHeight, yaxis, this.minx, temp), (float) (canvasHeight + 7), this.paint);
                canvas2 = canvas;
                canvas2.drawText("|", (float) toPixelInt((float) canvasHeight, yaxis, this.minx, temp), 2.0f, this.paint);
                temp = (10.0f * (this.miny + ((((float) (i - 1)) * (this.maxx - this.minx)) / ((float) 6)))) / 10.0f;
                canvas.drawText("" + df.format((double) temp), 40.0f, ((float) canvasHeight) - ((float) toPixelInt((float) canvasHeight, yaxis, this.miny, temp)), this.paint);
                canvas2 = canvas;
                canvas2.drawText("--", 0.0f, ((float) canvasHeight) - ((float) toPixelInt((float) canvasHeight, yaxis, this.miny, temp)), this.paint);
                canvas2 = canvas;
                canvas2.drawText("--", canvasWidth, ((float) canvasHeight) - ((float) toPixelInt((float) canvasHeight, yaxis, this.miny, temp)), this.paint);
            }
            for (i = 0; i < this.vectorLength - 1; i++)
            {
                this.paintdot.setColor(Color.BLUE);
                this.paint.setColor(Color.BLUE);
                canvas.drawPoint((float) xvaluesInPixels[i], (float) (canvasHeight - yvaluesInPixels[i]), this.paintdot);
                canvas.drawLine((float) xvaluesInPixels[i], (float) (canvasHeight - yvaluesInPixels[i]), (float) xvaluesInPixels[i + 1], (float) (canvasHeight - yvaluesInPixels[i + 1]), this.paint);
                canvas.drawPoint((float) xvaluesInPixels[i + 1], (float) (canvasHeight - yvaluesInPixels[i + 1]), this.paintdot);
            }
        }
    }

    private int[] toPixel(float pixels, float min, float max, float[] value)
    {
        double[] p = new double[value.length];
        int[] pint = new int[value.length];
        for (int i = 0; i < value.length; i++)
        {
            p[i] = (0.05d * ((double) pixels)) + ((((double) ((value[i] - max) / min)) * 0.9d) * ((double) pixels));
            pint[i] = (int) p[i];
        }
        return pint;
    }

    private void getAxes(float[] xvalues, float[] yvalues)
    {
        this.minx = getMin(xvalues);
        this.miny = getMin(yvalues);
        this.maxx = getMax(xvalues);
        this.maxy = getMax(yvalues);
        if (this.minx >= 0.0f)
        {
            this.locyAxis = this.minx;
        } else if (this.minx >= 0.0f || this.maxx < 0.0f)
        {
            this.locyAxis = this.maxx;
        } else
        {
            this.locyAxis = 0.0f;
        }
        if (this.miny >= 0.0f)
        {
            this.locxAxis = this.miny;
        } else if (this.miny >= 0.0f || this.maxy < 0.0f)
        {
            this.locxAxis = this.maxy;
        } else
        {
            this.locxAxis = 0.0f;
        }
    }

    private int toPixelInt(float pixels, float min, float max, float value)
    {
        return (int) ((0.05d * ((double) pixels)) + ((((double) ((value - max) / min)) * 0.9d) * ((double) pixels)));
    }

    private float getMax(float[] v)
    {
        float largest = v[0];
        for (int i = 0; i < v.length; i++)
        {
            if (v[i] > largest)
            {
                largest = v[i];
            }
        }
        return largest;
    }

    private float getMin(float[] v)
    {
        float smallest = v[0];
        for (int i = 0; i < v.length; i++)
        {
            if (v[i] < smallest)
            {
                smallest = v[i];
            }
        }
        return smallest;
    }
}
