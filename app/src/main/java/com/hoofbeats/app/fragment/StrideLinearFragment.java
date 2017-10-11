package com.hoofbeats.app.fragment;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.hoofbeats.app.Config;
import com.hoofbeats.app.R;
import com.hoofbeats.app.custom.chart.plot2d;
import com.hoofbeats.app.help.HelpOption;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.hoofbeats.app.model.Reading;
import com.hoofbeats.app.model.Workout;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.LittleDB;
import com.mbientlab.metawear.UnsupportedModuleException;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StrideLinearFragment extends SingleDataSensorFragment {
    private static final int TEMP_SAMPLE_PERIOD = 33;
    private static final boolean f1294D = true;
    double Avgspeed;
    private long StepD = 0;
    private TextView f1295X;
    private TextView f1296Y;
    private TextView f1297Z;
    byte[] ack = new byte[5];
    double avg = 0.0d;
    private TextView avgdur;
    private TextView avgsl;
    private TextView avgspeed;
    int bytes;
    Calendar f1298c;
    boolean check = false;
    double cos_phi;
    private int counter = 0;
    public int ctr = 0;
    double[][] f1299d = ((double[][]) Array.newInstance(Double.TYPE, new int[]{4, 4}));
    double[] delta = new double[]{0.0d, 0.0d, 0.0d};
    DecimalFormat df = new DecimalFormat("0.000");
    DecimalFormat df1 = new DecimalFormat("0.00");
    DecimalFormat df2 = new DecimalFormat("00");
    private TextView dis;
    double distance = 0.0d;
    double distance1 = 0.0d;
    double[][] dp = ((double[][]) Array.newInstance(Double.TYPE, new int[]{4, 4}));
    double[][] dr = ((double[][]) Array.newInstance(Double.TYPE, new int[]{4, 4}));
    double[] dx = new double[4];
    String filename;
    Calendar filenameDate;
    double[] final_data = new double[3];

    private plot2d graphLH;
    private plot2d graphLF;
    private plot2d graphRH;
    private plot2d graphRF;

    int f1300i;
    int f1301j;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private Button mPlotButton;
    private Button mStopButton;
    boolean plotdraw = false;
    SimpleDateFormat sdf;
    private TextView sduration;
    double sin_phi;
    private TextView slenghth;
    private TextView speedno;
    double speednow = 0.0d;
    private long startTime = 0;
    int step_counter;
    private ImageButton stepdetails;
    private Chronometer stopwatch;
    public String str2;
    private TextView sview;
    private Calendar t_origin;
    byte[] temp = new byte[4];
    long timeInMilliseconds = 0;
    long timeSec;
    long timeSec1 = 0;
    long timeSec2 = 0;
    long timeSec3 = 0;
    long timeSec6 = 0;
    long timeSwapBuff = 0;
    private long timeprint;
    private int timer;
    private TextView timerValue;

    private Vibrator vib;
    double[] x_sw = new double[4];
    float[] xvalue = new float[]{-1.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f};
    public float[] xvalues = new float[1024];
    float[] yvalue = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    public float[] yvalues = new float[1024];

    public List<Double> avgStepDurList;
    public List<Double> avgStepLenList;
    public List<Double> cumulativeDistList;
    private String fileSep;
    private int numOfSteps;
    private String outputDir;
    private String outputFileName;
    public List<Integer> sNoList;
    public double stepDurAvg;
    public List<Double> stepDurList;
    public double stepLenAvg;
    public List<Double> stepLenList;
    public List<Double> timeList;
    private Map<Integer, List<Double>> unexpectedData;
    public List<Float> xAxisList;
    public List<Float> yAxisList;
    public List<Float> zAxisList;
    protected LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
    protected LinearLayout graphView;


    protected List<Workout> workouts = null;

    public StrideLinearFragment() {
        super(R.string.navigation_fragment_stride_linear, "adc", R.layout.fragment_stride_linear, Config.GPIO_SAMPLE_PERIOD / 1000.f, 0, 1023);

        this.unexpectedData = null;
        this.stepLenAvg = 0.0d;
        this.stepDurAvg = 0.0d;
        this.numOfSteps = 0;
        this.outputDir = "";
        this.outputFileName = "";
        this.fileSep = "/";
        this.sNoList = new ArrayList();
        this.xAxisList = new ArrayList();
        this.yAxisList = new ArrayList();
        this.zAxisList = new ArrayList();
        this.timeList = new ArrayList();
        this.stepDurList = new ArrayList();
        this.stepLenList = new ArrayList();
        this.cumulativeDistList = new ArrayList();
        this.avgStepDurList = new ArrayList();
        this.avgStepLenList = new ArrayList();
        this.unexpectedData = new HashMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("ON-CREATE-VIEW");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("ON-VIEW-CREATED");

        //graphView = (LinearLayout) view.findViewById(R.id.graph);
        //graphView.setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException {
        System.out.println("---BOARD-READY");

    }

    @Override
    protected void setup() {
        workouts = DatabaseUtility.retrieveWorkouts(LittleDB.get().getLong(Config.SELECTED_HORSE_ID, -1));

        this.mConversationArrayAdapter = new ArrayAdapter(getActivity(), R.layout.message);
        this.f1298c = Calendar.getInstance();
        this.filenameDate = Calendar.getInstance();
        this.sdf = new SimpleDateFormat("HHmmss");

        this.filenameDate = Calendar.getInstance();
        this.sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        this.str2 = this.sdf.format(this.filenameDate.getTime());
        this.sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        double[] dArr = this.x_sw;
        double[] dArr2 = this.x_sw;
        double[] dArr3 = this.x_sw;
        this.x_sw[3] = 0.0d;
        dArr3[2] = 0.0d;
        dArr2[1] = 0.0d;
        dArr[0] = 0.0d;


        List<Reading> readings = workouts.get(0).getReadings();
        Collections.sort(readings, (a, b) -> a.getTimestamp() < b.getTimestamp() ? -1 : a.getTimestamp() == b.getTimestamp() ? 0 : 1);

        for (int i = 0; i < readings.size(); i++)
        {
            System.out.println("X-----" + readings.get(i).getXValueLinearAcceleration());
            System.out.println("Y-----" + readings.get(i).getYValueLinearAcceleration());
            System.out.println("Z-----" + readings.get(i).getZValueLinearAcceleration());

            this.dx[0] = (double) readings.get(i).getXValueLinearAcceleration();
            this.dx[1] = (double) readings.get(i).getYValueLinearAcceleration();
            this.dx[2] = (double) readings.get(i).getZValueLinearAcceleration();

            stepwise_dr_tu();

            if ((i + 1) < readings.size())
            {
                timeSec = readings.get(i + 1).getTimestamp() - readings.get(i).getTimestamp();

                if (timeSec != StrideLinearFragment.this.timeSec1)
                {
                    this.timeSec1 = timeSec;
                }
                if (this.distance1 >= 0.05)
                {
                    this.timeSec3 = this.timeSec1 - this.timeSec2;
                    this.timeSec6 += this.timeSec3;
                    this.timeSec2 = this.timeSec1;
                    //long timeSec5 = this.f1298c.getTimeInMillis() - this.filenameDate.getTimeInMillis();
                    this.step_counter++;
                    DecimalFormat df1 = new DecimalFormat("0.00");
                    DecimalFormat df2 = new DecimalFormat("000");
                    this.avg = this.distance / ((double) this.step_counter);
                    this.speednow = (this.distance1 * 3.6d) / ((double) (this.timeSec3 / 1000));
                    this.Avgspeed = (this.distance * 3.6d) / ((double) (this.timeSec6 / 1000));
                    this.StepD = this.timeSec6 / ((long) this.step_counter);

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //Printdata();
                            StrideLinearFragment.this.mConversationArrayAdapter.add("  " + StrideLinearFragment.this.step_counter + ". x= " + df1.format(StrideLinearFragment.this.final_data[0]) + "m   y= " + df1.format(StrideLinearFragment.this.final_data[1]) + "m   z= " + df1.format(StrideLinearFragment.this.final_data[2]) + "m" + " " + "Speed = " + df1.format(StrideLinearFragment.this.speednow) + "m");
                            DecimalFormat df = new DecimalFormat("0.000");
                            String Str = df.format(StrideLinearFragment.this.final_data[0]) + " " + df.format(StrideLinearFragment.this.final_data[1]) + " " + df.format(StrideLinearFragment.this.final_data[2]) + " " + (StrideLinearFragment.this.timeSec6 / 1000) + "." + df2.format(StrideLinearFragment.this.timeSec6 % 1000) + " " + df.format(StrideLinearFragment.this.distance) + "\n";
                            //writetofile(StrideLinearFragment.this.filename, Str);

                            String[] xyztd = Str.split("\\s+");
                            float x = toDouble(xyztd[0].trim());
                            float y = toDouble(xyztd[1].trim());
                            float z = (float) toDouble(xyztd[2].trim());
                            double stepLen = (double) toDouble(xyztd[3].trim());
                            double stepDur = (double) toDouble(xyztd[4].trim());

                            xAxisList.add(Float.valueOf(x));
                            yAxisList.add(Float.valueOf(z));
                            zAxisList.add(Float.valueOf(z));
                            stepLenList.add(Double.valueOf(stepLen));
                            stepDurList.add(Double.valueOf(stepDur));
                            stepLenAvg += stepLen;
                            stepDurAvg += stepDur;
                            numOfSteps++;


                            LineData chartData = chart.getData();
                            if (startTime == -1) {
                                chartData.addXValue("0");
                                startTime= System.currentTimeMillis();
                            } else {
                                chartData.addXValue(String.format(Locale.US, "%.2f", x));
                            }

                            chartData.addEntry(new Entry(y, sampleCount), 0);

                            sampleCount++;

                            updateChart();

                            System.out.println(Str);

                            // this.sview = (TextView) view.findViewById(R.id.stepcount);
                            // this.dis = (TextView) view.findViewById(R.id.dis);
                            // this.avgspeed = (TextView) view.findViewById(R.id.avgspeed);
                            // this.f1295X = (TextView) view.findViewById(R.id.X);
                            // this.f1296Y = (TextView) view.findViewById(R.id.Y);
                            // this.f1297Z = (TextView) view.findViewById(R.id.Z);
                            // this.timerValue = (TextView) view.findViewById(R.id.timer);

                            /*StrideLinearFragment.this.xvalues = getXAxisData();
                            StrideLinearFragment.this.yvalues = getYAxisData();


                            StrideLinearFragment.this.graphLH = new plot2d(getActivity(), StrideLinearFragment.this.xvalues, StrideLinearFragment.this.yvalues, 1);


                            StrideLinearFragment.this.graphView.addView(StrideLinearFragment.this.graphLH, StrideLinearFragment.this.layoutParams);
                            StrideLinearFragment.this.graphLH.invalidate();

                            StrideLinearFragment.this.plotdraw = StrideLinearFragment.f1294D;*/
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void clean() {
    }

    @Override
    protected void resetData(boolean clearData) {
        super.resetData(clearData);
    }

    public void stepwise_dr_tu()
    {
        this.sin_phi = (double) ((float) Math.sin(this.x_sw[3]));
        this.cos_phi = (double) ((float) Math.cos(this.x_sw[3]));
        this.delta[0] = (this.cos_phi * this.dx[0]) - (this.sin_phi * this.dx[1]);
        this.delta[1] = (this.sin_phi * this.dx[0]) + (this.cos_phi * this.dx[1]);
        this.delta[2] = this.dx[2];
        double[] dArr = this.x_sw;
        dArr[0] = dArr[0] + this.delta[0];
        dArr = this.x_sw;
        dArr[1] = dArr[1] + this.delta[1];
        dArr = this.x_sw;
        dArr[2] = dArr[2] + this.delta[2];
        dArr = this.x_sw;
        dArr[3] = dArr[3] + this.dx[3];
        this.final_data[0] = this.x_sw[0];
        this.final_data[1] = this.x_sw[1];
        this.final_data[2] = this.x_sw[2];
        this.distance1 = Math.sqrt(((this.delta[0] * this.delta[0]) + (this.delta[1] * this.delta[1])) + (this.delta[2] * this.delta[2]));
        this.distance += Math.sqrt((this.delta[0] * this.delta[0]) + (this.delta[1] * this.delta[1]));
    }

    private float toDouble(String values)
    {
        try
        {
            return Float.parseFloat(values.trim());
        } catch (Exception e)
        {
            return Float.NaN;
        }
    }

    public float[] getXAxisData()
    {
        float[] tmpArr = new float[(this.xAxisList.size() + 1)];
        tmpArr[0] = 0.0f;
        for (int i = 0; i < this.xAxisList.size(); i++)
        {
            tmpArr[i + 1] = ((Float) this.xAxisList.get(i)).floatValue() * -1.0f;
        }
        return tmpArr;
    }

    public float[] getYAxisData()
    {
        float[] tmpArr = new float[(this.yAxisList.size() + 1)];
        tmpArr[0] = 0.0f;
        for (int i = 0; i < this.yAxisList.size(); i++)
        {
            tmpArr[i + 1] = ((Float) this.yAxisList.get(i)).floatValue();
        }
        return tmpArr;
    }

    public void Printdata()
    {
        this.dis.setText(" " + this.df1.format(this.distance));
        this.avgspeed.setText(" " + this.df1.format(this.Avgspeed));
        this.sview.setText(" " + this.step_counter);
        this.f1295X.setText(" " + this.df1.format(this.final_data[0]));
        this.f1295X.invalidate();
        this.f1296Y.setText(" " + this.df1.format(this.final_data[1]));
        this.f1296Y.invalidate();
        this.f1297Z.setText(" " + this.df1.format(this.final_data[2]));
        this.f1297Z.invalidate();
    }

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter) {
        adapter.add(new HelpOption(R.string.config_name_gpio_pin, R.string.config_desc_gpio_pin));
        adapter.add(new HelpOption(R.string.config_name_gpio_read_mode, R.string.config_desc_gpio_read_mode));
        adapter.add(new HelpOption(R.string.config_name_output_control, R.string.config_desc_output_control));
        adapter.add(new HelpOption(R.string.config_name_pull_mode, R.string.config_desc_pull_mode));
    }
}
