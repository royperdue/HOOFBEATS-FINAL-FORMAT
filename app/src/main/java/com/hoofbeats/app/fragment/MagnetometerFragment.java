
package com.hoofbeats.app.fragment;

import com.hoofbeats.app.R;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.mbientlab.metawear.AsyncDataProducer;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.data.MagneticField;
import com.mbientlab.metawear.module.MagnetometerBmm150;

public class MagnetometerFragment extends ThreeAxisChartFragment {
    private static final float B_FIELD_RANGE= 2500f, MAG_ODR= 25.f;

    private MagnetometerBmm150 magnetometer = null;

    public MagnetometerFragment() {
        super("field", R.layout.fragment_sensor, R.string.navigation_fragment_magnetometer, -B_FIELD_RANGE, B_FIELD_RANGE, MAG_ODR);
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException {
        //magnetometer = mwBoard.getModuleOrThrow(MagnetometerBmm150.class);
    }

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter) {

    }

    @Override
    protected void setup() {
        magnetometer.configure()
                .outputDataRate(MagnetometerBmm150.OutputDataRate.ODR_25_HZ)
                .commit();

        final float period = 1 / MAG_ODR;
        final AsyncDataProducer producer = magnetometer.packedMagneticField() == null ?
                magnetometer.packedMagneticField() :
                magnetometer.magneticField();
        producer.addRouteAsync(source -> source.stream((data, env) -> {
            final MagneticField value = data.value(MagneticField.class);
            addChartData(value.x() * 1000000f, value.y() * 1000000f, value.z() * 1000000f, period);
        })).continueWith(task -> {
            streamRoute = task.getResult();

            magnetometer.magneticField().start();
            magnetometer.start();

            return null;
        });
    }

    @Override
    protected void clean() {
        magnetometer.stop();
        (magnetometer.packedMagneticField() == null ?
                magnetometer.packedMagneticField() :
                magnetometer.magneticField()
        ).stop();
    }
}
