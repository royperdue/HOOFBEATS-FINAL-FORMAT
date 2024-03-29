package com.hoofbeats.app;

import java.util.UUID;

/**
 * Created by royperdue on 2/6/17.
 */
public final class Config
{
    public static final String GPIO_SENSOR = "gpioSensor";
    public static final String SENSOR_FUSION_BOSCH = "sensorFusionBosch";

    public static final String TIME_SENSOR_FUSION_READING = "timeSensorFusionBoschReading";

    public static final String WORKOUT_ID = "***workoutId***";
    public static final String WORKOUT_START_TIME = "***workoutStartTime***";
    public static final String WORKOUT_START_DATE = "***workoutStartDate***";
    public static final String WORKOUT_TIME_SELECTED = "***workoutTimeSelected***";
    public static final String WORKOUT_DATE_SELECTED = "***workoutDateSelected***";
    public static final String HORSE_ID = "***horseId***";
    public static final String HORSE_SELECTED = "***horseSelected***";
    public static final String SERIALIZED_BOARDS_FILE = "SerializedBoards";
    public static final String FRAGMENT_ARGUMENTS = "fragmentArguments";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final String SELECTED_HORSE_ID = "*****horseSelectedId**";
    public static final String MODULES_CURRENTLY_LOGGING_LH = "*****modulesCurrentlyLoggingLeftHind**";
    public static final String MODULES_CURRENTLY_LOGGING_LF = "*****modulesCurrentlyLoggingLeftFront**";
    public static final String MODULES_CURRENTLY_LOGGING_RH = "*****modulesCurrentlyLoggingRightHind**";
    public static final String MODULES_CURRENTLY_LOGGING_RF = "*****modulesCurrentlyLoggingRightFront**";

    public static final String EXTRA_BLE_DEVICE= "com.mbientlab.bletoolbox.examples.MainActivity.EXTRA_BLE_DEVICE";
    public static final int SCAN_DEVICE = 100;

    public static final UUID METAWEAR_GATT_SERVICE = UUID.fromString("326a9000-85cb-9195-d9dd-464cfbbae75a"),
            METAWEAR_CMD_CHAR = UUID.fromString("326A9001-85CB-9195-D9DD-464CFBBAE75A");
    public static final int NAV_DRAWER_ITEM_INVALID = -1;

    public static final long DEFAULT_SCAN_PERIOD = 5000L;
    public static final int REQUEST_ENABLE_BT = 1, PERMISSION_REQUEST_COARSE_LOCATION = 2;

    public static final int SELECT_FILE_REQ = 1, PERMISSION_REQUEST_READ_STORAGE = 2;
    public static final String EXTRA_URI = "uri", FRAGMENT_KEY = "NavigationActivity.FRAGMENT_KEY",
            DFU_PROGRESS_FRAGMENT_TAG = "NavigationActivity.DFU_PROGRESS_FRAGMENT_TAG";

    public static final String DEFAULT_DEVICE_NAME = "Hoofbeat";
    //public static final String SERVICE_BOUND = "**serviceBound**";

    public static final String TOTAL_DISTANCE_X = "totalDistanceX";
    public static final String TOTAL_DISTANCE_Z = "totalDistanceZ";

    public final static int RSSI_BAR_LEVELS = 5;
    public final static int RSSI_BAR_SCALE = 100 / RSSI_BAR_LEVELS;

    public static final boolean LOGGING_OVERWRITE_MODE = false;
    public static final int GPIO_SAMPLE_PERIOD = 33;
    public static final byte GPIO_PIN = 1;
    public static final byte NUMBER_SAMPLES_GPIO_AVERAGE = 10;

    public static final short ADVERTISING_INTERVAL = 415;
    public static final short TIMEOUT = 0;
    public static final float Y_VALUE_THRESHOLD = 2f;

    public static final byte TX_POWER = 4;

    public static final String AVERAGE_STEP_DURATION_LH = "averageStepDurationLeftHind";
    public static final String AVERAGE_STEP_DURATION_LF = "averageStepDurationLeftFront";
    public static final String AVERAGE_STEP_DURATION_RH = "averageStepDurationRightHind";
    public static final String AVERAGE_STEP_DURATION_RF = "averageStepDurationRightFront";

    public static final String AVERAGE_STANCE_PHASE_DURATION_LH = "averageStancePhaseDurationLeftHind";
    public static final String AVERAGE_STANCE_PHASE_DURATION_LF = "averageStancePhaseDurationLeftFront";
    public static final String AVERAGE_STANCE_PHASE_DURATION_RH = "averageStancePhaseDurationRightHind";
    public static final String AVERAGE_STANCE_PHASE_DURATION_RF = "averageStancePhaseDurationRightFront";

    public static final String AVERAGE_SWING_PHASE_DURATION_LH = "averageSwingPhaseDurationLeftHind";
    public static final String AVERAGE_SWING_PHASE_DURATION_LF = "averageSwingPhaseDurationLeftFront";
    public static final String AVERAGE_SWING_PHASE_DURATION_RH = "averageSwingPhaseDurationRightHind";
    public static final String AVERAGE_SWING_PHASE_DURATION_RF = "averageSwingPhaseDurationRightFront";


    public static final String TEST_PATTERN = "RF-LF-RF-LF-RF-LF-RF-LF-RF-LF-RF-LF";
    public static final String WALK = "LH-LF-RH-RF-LH-LF-RH-RF-LH-LF-RH-RF";
    public static final String TROT_NO_DISSOCIATION = "LH-RF-RH-LF-LH-RF-RH-LF-LH-RF-RH-LF";


    public static final String INFLUX_DB_URL = "influxDBUrl";
    public static final String INFLUX_DB_DATABASE_NAME = "influxDBDatabaseName";
    public static final String INFLUX_DB_USER_NAME = "influxDBUserName";
    public static final String INFLUX_DB_PASSWORD = "influxDBPassword";

    public static final String INFLUX_TAG_KEY_HORSE_NAME = "HorseName";
    public static final String INFLUX_TAG_KEY_HOOF = "Hoof";
    public static final String INFLUX_TAG_KEY_SENSOR = "Sensor";

    public static final String INFLUX_FIELD_KEY_LINEAR_ACCELERATION = "LinearAccelerationKey";
    public static final String INFLUX_FIELD_KEY_FORCE = "ForceKey";

    public static final String INFLUX_SENSOR_FORCE = "Force";
    public static final String INFLUX_SENSOR_LINEAR_ACCELERATION = "LinearAcceleration";
}
