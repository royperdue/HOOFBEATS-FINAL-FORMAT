package com.hoofbeats.app.fragment;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public abstract class ThreeAxisChartFragment extends SensorFragment
{
    private final ArrayList<Entry> axisDataLH = new ArrayList<>();
    private final ArrayList<Entry> axisDataLF = new ArrayList<>();
    private final ArrayList<Entry> axisDataRH = new ArrayList<>();
    private final ArrayList<Entry> axisDataRF = new ArrayList<>();
    private final String dataType;
    protected float samplePeriod;

    protected void addChartDataLH(float x, float y0)
    {
        LineData chartData = chart.getData();
        chartData.addXValue(String.format(Locale.US, "%.2f", x));

        chartData.addEntry(new Entry(y0, sampleCountLH), 0);

        sampleCountLH++;

        updateChart();
    }

    protected void addChartDataLF(float x, float y1)
    {
        LineData chartData = chart.getData();
        chartData.addXValue(String.format(Locale.US, "%.2f", x));

        chartData.addEntry(new Entry(y1, sampleCountLF), 1);

        sampleCountLF++;

        updateChart();
    }

    protected void addChartDataRH(float x, float y2)
    {
        LineData chartData = chart.getData();
        chartData.addXValue(String.format(Locale.US, "%.2f", x));

        chartData.addEntry(new Entry(y2, sampleCountRH), 2);

        sampleCountRH++;

        updateChart();
    }

    protected void addChartDataRF(float x, float y3)
    {
        LineData chartData = chart.getData();
        chartData.addXValue(String.format(Locale.US, "%.2f", x));

        chartData.addEntry(new Entry(y3, sampleCountRF), 3);

        sampleCountRF++;

        updateChart();
    }

    protected ThreeAxisChartFragment(String dataType, int layoutId, int sensorResId, float min, float max, float sampleFreq)
    {
        super(sensorResId, layoutId, min, max);
        this.dataType = dataType;
        this.samplePeriod = 1 / sampleFreq;
    }

    protected ThreeAxisChartFragment(String dataType, int layoutId, int sensorResId, float min, float max)
    {
        super(sensorResId, layoutId, min, max);
        this.dataType = dataType;
        this.samplePeriod = -1.f;
    }

    @Override
    protected String saveData()
    {
        final String CSV_HEADER = String.format("time,x-%s,y-%s,z-%s%n", dataType, dataType, dataType);
        String filename = String.format(Locale.US, "%s_%tY%<tm%<td-%<tH%<tM%<tS%<tL.csv", getContext().getString(sensorResId), Calendar.getInstance());

        try
        {
            FileOutputStream fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(CSV_HEADER.getBytes());

            LineData data = chart.getLineData();
            LineDataSet xSpinDataSet = data.getDataSetByIndex(0), ySpinDataSet = data.getDataSetByIndex(1),
                    zSpinDataSet = data.getDataSetByIndex(2);
            for (int i = 0; i < data.getXValCount(); i++)
            {
                fos.write(String.format(Locale.US, "%.3f,%.3f,%.3f,%.3f%n", i * samplePeriod,
                        xSpinDataSet.getEntryForXIndex(i).getVal(),
                        ySpinDataSet.getEntryForXIndex(i).getVal(),
                        zSpinDataSet.getEntryForXIndex(i).getVal()).getBytes());
            }
            fos.close();
            return filename;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void resetData(boolean clearData)
    {
        if (clearData)
        {
            sampleCountLH = 0;
            sampleCountLF = 0;
            sampleCountRH = 0;
            sampleCountRF = 0;
            chartXValues.clear();
            axisDataLH.clear();
            axisDataLF.clear();
            axisDataRH.clear();
            axisDataRF.clear();
        }

        ArrayList<LineDataSet> spinAxisData = new ArrayList<>();
        spinAxisData.add(new LineDataSet(axisDataLH, "Left Hind"));
        spinAxisData.get(0).setColor(Color.RED);
        spinAxisData.get(0).setDrawCircles(false);

        spinAxisData.add(new LineDataSet(axisDataLF, "Left Front"));
        spinAxisData.get(1).setColor(Color.GREEN);
        spinAxisData.get(1).setDrawCircles(false);

        spinAxisData.add(new LineDataSet(axisDataRH, "Right Hind"));
        spinAxisData.get(2).setColor(Color.BLUE);
        spinAxisData.get(2).setDrawCircles(false);

        spinAxisData.add(new LineDataSet(axisDataRF, "Right Front"));
        spinAxisData.get(3).setColor(Color.MAGENTA);
        spinAxisData.get(3).setDrawCircles(false);

        LineData data = new LineData(chartXValues);
        for (LineDataSet set : spinAxisData)
        {
            data.addDataSet(set);
        }
        data.setDrawValues(false);
        chart.setData(data);
    }
}
