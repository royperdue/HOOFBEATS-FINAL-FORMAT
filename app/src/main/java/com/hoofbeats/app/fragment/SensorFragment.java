package com.hoofbeats.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Switch;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.hoofbeats.app.R;
import com.mbientlab.metawear.Route;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class SensorFragment extends ModuleFragmentBase
{
    protected final ArrayList<String> chartXValues = new ArrayList<>();
    protected LineChart chart;
    protected int sampleCount;
    protected long prevUpdate = -1;

    protected float min, max;
    protected Route streamRoute = null;

    private byte globalLayoutListenerCounter = 0;
    private final int layoutId;

    private final Handler chartHandler = new Handler();

    protected SensorFragment(int sensorResId, int layoutId, float min, float max)
    {
        super(sensorResId);
        this.layoutId = layoutId;
        this.min = min;
        this.max = max;
    }

    protected void updateChart()
    {
        long current = Calendar.getInstance().getTimeInMillis();
        if (prevUpdate == -1 || (current - prevUpdate) >= 33)
        {
            chartHandler.post(() ->
            {
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();

                moveViewToLast();
            });

            prevUpdate = current;
        }
    }

    private void moveViewToLast()
    {
        chart.setVisibleXRangeMinimum(120);
        chart.setVisibleXRangeMaximum(120);
        chart.moveViewToX(Math.max(0f, chartXValues.size() - 1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setRetainInstance(true);

        View v = inflater.inflate(layoutId, container, false);
        final View scrollView = v.findViewById(R.id.scrollView);
        if (scrollView != null && chart != null)
        {
            globalLayoutListenerCounter = 1;
            scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    LineChart.LayoutParams params = chart.getLayoutParams();
                    params.height = scrollView.getHeight();
                    chart.setLayoutParams(params);

                    globalLayoutListenerCounter--;
                    if (globalLayoutListenerCounter < 0)
                    {
                        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

        return v;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        chart = (LineChart) view.findViewById(R.id.data_chart);

        initializeChart();
        resetData(false);
        chart.invalidate();
        chart.setDescription(null);
/*
        Button clearButton= (Button) view.findViewById(R.id.layout_two_button_left);
        clearButton.setOnClickListener(view1 -> refreshChart(true));
        clearButton.setText(R.string.label_clear);



        Button saveButton= (Button) view.findViewById(R.id.layout_two_button_right);
        saveButton.setText(R.string.label_save);
        saveButton.setOnClickListener(view12 -> {
            String filename = saveData();

            if (filename != null) {
                File dataFile = getActivity().getFileStreamPath(filename);
                Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.mbientlab.metawear.app.fileprovider", dataFile);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, filename);
                intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(intent, "Saving Data"));
            }
        });*/

        ((Switch) view.findViewById(R.id.sample_control)).setOnCheckedChangeListener((compoundButton, b) ->
        {
            if (b)
            {
                if (chart != null)
                    moveViewToLast();

                setup();
            } else
            {
                if (chart != null)
                {
                    chart.setVisibleXRangeMinimum(1);
                    chart.setVisibleXRangeMaximum(sampleCount);
                    clean();
                    if (streamRoute != null)
                    {
                        streamRoute.remove();
                        streamRoute = null;
                    }
                }
            }
        });
    }

    protected void refreshChart(boolean clearData)
    {
        chart.resetTracking();
        chart.clear();
        resetData(clearData);
        chart.invalidate();
        chart.fitScreen();
    }

    protected void initializeChart()
    {
        ///< configure axis settings
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMaxValue(10);
        leftAxis.setAxisMinValue(0);
        chart.getAxisRight().setEnabled(false);
    }

    protected abstract void setup();

    protected abstract void clean();

    protected abstract String saveData();

    protected abstract void resetData(boolean clearData);
}
