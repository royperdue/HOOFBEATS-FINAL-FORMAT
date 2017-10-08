package com.hoofbeats.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoofbeats.app.adapter.CustomListAdapter;
import com.hoofbeats.app.bluetooth.BleScannerFragment;
import com.hoofbeats.app.fragment.AccelerometerFragment;
import com.hoofbeats.app.fragment.GpioFragment;
import com.hoofbeats.app.fragment.GyroFragment;
import com.hoofbeats.app.fragment.HomeFragment;
import com.hoofbeats.app.fragment.ModuleFragmentBase;
import com.hoofbeats.app.fragment.SensorFusionFragment;
import com.hoofbeats.app.fragment.SettingsFragment;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.DialogUtility;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Debug;
import com.mbientlab.metawear.module.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import bolts.Capture;
import bolts.Continuation;
import bolts.Task;
import no.nordicsemi.android.dfu.DfuBaseService;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class NavigationActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        ServiceConnection, BleScannerFragment.ScannerCommunicationBus, ModuleFragmentBase.FragmentBus, LoaderManager.LoaderCallbacks<Cursor>
{
    public final static String EXTRA_BT_DEVICE = "NavigationActivity.EXTRA_BT_DEVICE";

    private static final int SELECT_FILE_REQ = 1, PERMISSION_REQUEST_READ_STORAGE = 2;
    private static final String EXTRA_URI = "uri", FRAGMENT_KEY = "NavigationActivity.FRAGMENT_KEY",
            DFU_PROGRESS_FRAGMENT_TAG = "NavigationActivity.DFU_PROGRESS_FRAGMENT_TAG";
    private final static Map<Integer, Class<? extends ModuleFragmentBase>> FRAGMENT_CLASSES;
    private final static Map<String, String> EXTENSION_TO_APP_TYPE;
    private final static UUID[] serviceUuids;

    static
    {
        serviceUuids = new UUID[]{
                MetaWearBoard.METAWEAR_GATT_SERVICE,
                MetaWearBoard.METABOOT_SERVICE
        };
    }

    private BtleService.LocalBinder serviceBinder;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private List<MetaWearBoard> metaWearBoards = new ArrayList<>();

    static
    {
        Map<Integer, Class<? extends ModuleFragmentBase>> tempMap = new LinkedHashMap<>();
        tempMap.put(R.id.nav_assign, BleScannerFragment.class);
        tempMap.put(R.id.nav_home, HomeFragment.class);
        tempMap.put(R.id.nav_accelerometer, AccelerometerFragment.class);
        tempMap.put(R.id.nav_gpio, GpioFragment.class);
        tempMap.put(R.id.nav_gyro, GyroFragment.class);
        tempMap.put(R.id.nav_sensor_fusion, SensorFusionFragment.class);
        tempMap.put(R.id.nav_settings, SettingsFragment.class);
        FRAGMENT_CLASSES = Collections.unmodifiableMap(tempMap);

        EXTENSION_TO_APP_TYPE = new HashMap<>();
        EXTENSION_TO_APP_TYPE.put("hex", DfuBaseService.MIME_TYPE_OCTET_STREAM);
        EXTENSION_TO_APP_TYPE.put("bin", DfuBaseService.MIME_TYPE_OCTET_STREAM);
        EXTENSION_TO_APP_TYPE.put("zip", DfuBaseService.MIME_TYPE_ZIP);
    }

    @Override
    public UUID[] getFilterServiceUuids()
    {
        return serviceUuids;
    }

    @Override
    public long getScanDuration()
    {
        return 10000L;
    }

    @Override
    public void onDeviceSelected(BluetoothDevice bluetoothDevice)
    {
        serviceBinder.removeMetaWearBoard(bluetoothDevice);
        MetaWearBoard metaWearBoard = serviceBinder.getMetaWearBoard(bluetoothDevice);
        bluetoothDevices.add(bluetoothDevice);

        final ProgressDialog connectDialog = new ProgressDialog(this);
        connectDialog.setTitle(getString(R.string.title_connecting));
        connectDialog.setMessage(getString(R.string.message_wait));
        connectDialog.setCancelable(false);
        connectDialog.setCanceledOnTouchOutside(false);
        connectDialog.setIndeterminate(true);
        connectDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.label_cancel), (dialogInterface, i) -> metaWearBoard.disconnectAsync());
        connectDialog.show();

        metaWearBoard.connectAsync()
                .continueWithTask(task ->
                {
                    if (task.isCancelled())
                    {
                        return task;
                    }
                    return task.isFaulted() ? reconnect(metaWearBoard) : Task.forResult(null);
                })
                .continueWith(task ->
                {
                    if (!task.isCancelled())
                    {
                        setConnInterval(metaWearBoard.getModule(Settings.class));
                        runOnUiThread(connectDialog::dismiss);

                        metaWearBoards.add(metaWearBoard);
                    }
                    return null;
                });
    }

    public static class ReconnectDialogFragment extends DialogFragment implements ServiceConnection
    {
        private static final String KEY_BLUETOOTH_DEVICE = "NavigationActivity.ReconnectDialogFragment.KEY_BLUETOOTH_DEVICE";

        private ProgressDialog reconnectDialog = null;
        private BluetoothDevice btDevice = null;
        private MetaWearBoard currentMwBoard = null;

        public static ReconnectDialogFragment newInstance(List<BluetoothDevice> bluetoothDevices)
        {
            Bundle args = new Bundle();
            args.putParcelableArrayList(KEY_BLUETOOTH_DEVICE, (ArrayList<? extends Parcelable>) bluetoothDevices);

            ReconnectDialogFragment newFragment = new ReconnectDialogFragment();
            newFragment.setArguments(args);

            return newFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            btDevice = getArguments().getParcelable(KEY_BLUETOOTH_DEVICE);
            getActivity().getApplicationContext().bindService(new Intent(getActivity(), BtleService.class), this, BIND_AUTO_CREATE);

            reconnectDialog = new ProgressDialog(getActivity());
            reconnectDialog.setTitle(getString(R.string.title_reconnect_attempt));
            reconnectDialog.setMessage(getString(R.string.message_wait));
            reconnectDialog.setCancelable(false);
            reconnectDialog.setCanceledOnTouchOutside(false);
            reconnectDialog.setIndeterminate(true);
            reconnectDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.label_cancel), (dialogInterface, i) ->
            {
                currentMwBoard.disconnectAsync();
                getActivity().finish();
            });

            return reconnectDialog;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            currentMwBoard = ((BtleService.LocalBinder) service).getMetaWearBoard(btDevice);
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
        }
    }

    public static class DfuProgressFragment extends DialogFragment
    {
        private ProgressDialog dfuProgress = null;

        public static DfuProgressFragment newInstance(int messageStringId)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("message_string_id", messageStringId);

            DfuProgressFragment newFragment = new DfuProgressFragment();
            newFragment.setArguments(bundle);
            return newFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            dfuProgress = new ProgressDialog(getActivity());
            dfuProgress.setTitle(getString(R.string.title_firmware_update));
            dfuProgress.setCancelable(false);
            dfuProgress.setCanceledOnTouchOutside(false);
            dfuProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dfuProgress.setProgress(0);
            dfuProgress.setMax(100);
            dfuProgress.setMessage(getString(getArguments().getInt("message_string_id")));
            return dfuProgress;
        }

        public void updateProgress(int newProgress)
        {
            if (dfuProgress != null)
            {
                dfuProgress.setProgress(newProgress);
            }
        }
    }

    private static String getExtension(String path)
    {
        int i = path.lastIndexOf('.');
        return i >= 0 ? path.substring(i + 1) : null;
    }

    private final String RECONNECT_DIALOG_TAG = "reconnect_dialog_tag";
    private final Handler taskScheduler = new Handler();
    //private BluetoothDevice btDevice;
    //private MetaWearBoard mwBoard;
    private Fragment currentFragment = null;
    private Uri fileStreamUri;
    private String fileName;
    private DfuServiceInitiator starter;

    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter()
    {
        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal)
        {
            ((DfuProgressFragment) getSupportFragmentManager().findFragmentByTag(DFU_PROGRESS_FRAGMENT_TAG)).updateProgress(percent);
        }

        @Override
        public void onDfuCompleted(String deviceAddress)
        {
            ((DialogFragment) getSupportFragmentManager().findFragmentByTag(DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
            Snackbar.make(NavigationActivity.this.findViewById(R.id.drawer_layout), R.string.message_dfu_success, Snackbar.LENGTH_LONG).show();
            resetConnectionStateHandler(5000L);
        }

        @Override
        public void onDfuAborted(String deviceAddress)
        {
            ((DialogFragment) getSupportFragmentManager().findFragmentByTag(DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
            Snackbar.make(NavigationActivity.this.findViewById(R.id.drawer_layout), R.string.error_dfu_aborted, Snackbar.LENGTH_LONG).show();
            resetConnectionStateHandler(5000L);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message)
        {
            ((DialogFragment) getSupportFragmentManager().findFragmentByTag(DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
            Snackbar.make(NavigationActivity.this.findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG).show();
            resetConnectionStateHandler(5000L);
        }
    };

    private Continuation<Void, Void> reconnectResult = task ->
    {
        ((DialogFragment) getSupportFragmentManager().findFragmentByTag(RECONNECT_DIALOG_TAG)).dismiss();

        if (task.isCancelled())
        {
            finish();
        } else
        {
            for (int i = 0; i < metaWearBoards.size(); i++)
            {
                setConnInterval(metaWearBoards.get(i).getModule(Settings.class));
            }

            ((ModuleFragmentBase) currentFragment).reconnected();
        }

        return null;
    };

    private void attemptReconnect()
    {
        attemptReconnect(0);
    }

    private void attemptReconnect(long delay)
    {
        ReconnectDialogFragment dialogFragment = ReconnectDialogFragment.newInstance(bluetoothDevices);
        dialogFragment.show(getSupportFragmentManager(), RECONNECT_DIALOG_TAG);

        for (int i = 0; i < metaWearBoards.size(); i++)
        {
            if (!metaWearBoards.get(i).isConnected())
            {
                if (delay != 0)
                {
                    int finalI = i;
                    taskScheduler.postDelayed(() -> reconnect(metaWearBoards.get(finalI)).continueWith(reconnectResult), delay);
                } else
                {
                    reconnect(metaWearBoards.get(i)).continueWith(reconnectResult);
                }
            }
        }
    }

    @Override
    public List<BluetoothDevice> getBtDevices()
    {
        return bluetoothDevices;
    }

    @Override
    public void resetConnectionStateHandler(long delay)
    {
        attemptReconnect(delay);
    }

    private boolean addMimeType(Object path)
    {
        if (path instanceof File)
        {
            File filePath = (File) path;

            String mimeType = EXTENSION_TO_APP_TYPE.get(getExtension(filePath.getAbsolutePath()));
            if (mimeType == null)
            {
                mimeType = EXTENSION_TO_APP_TYPE.get(getExtension(fileName));
            }
            if (mimeType.equals(DfuService.MIME_TYPE_OCTET_STREAM))
            {
                starter.setBinOrHex(DfuBaseService.TYPE_APPLICATION, filePath.getAbsolutePath());
            } else
            {
                starter.setZip(filePath.getAbsolutePath());
            }
        } else if (path instanceof Uri)
        {
            Uri uriPath = (Uri) path;

            String mimeType = EXTENSION_TO_APP_TYPE.get(getExtension(uriPath.toString()));
            if (mimeType == null)
            {
                mimeType = EXTENSION_TO_APP_TYPE.get(getExtension(fileName));
            }
            if (mimeType.equals(DfuService.MIME_TYPE_OCTET_STREAM))
            {
                starter.setBinOrHex(DfuBaseService.TYPE_APPLICATION, uriPath);
            } else
            {
                starter.setZip(uriPath);
            }
        } else if (path != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public void initiateDfu(final Object path)
    {
        for (int i = 0; i < bluetoothDevices.size(); i++)
        {
            starter = new DfuServiceInitiator(bluetoothDevices.get(i).getAddress())
                    .setDeviceName(bluetoothDevices.get(i).getName())
                    .setKeepBond(false)
                    .setForceDfu(true);
        }
        // Init packet is required by Bootloader/DFU from SDK 7.0+ if HEX or BIN file is given above.
        // In case of a ZIP file, the init packet (a DAT file) must be included inside the ZIP file.
        //service.putExtra(NordicDfuService.EXTRA_INIT_FILE_PATH, mInitFilePath);
        //service.putExtra(NordicDfuService.EXTRA_INIT_FILE_URI, mInitFileStreamUri);

        if (!addMimeType(path))
        {
            Snackbar.make(NavigationActivity.this.findViewById(R.id.drawer_layout), R.string.error_firmware_path_type, Snackbar.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < metaWearBoards.size(); i++)
        {
            final int finalI = i;
            taskScheduler.post(() ->
            {
                if (path == null)
                {
                    DfuProgressFragment.newInstance(R.string.message_dfu).show(getSupportFragmentManager(), DFU_PROGRESS_FRAGMENT_TAG);

                    Capture<File> firmwareCapture = new Capture<>();

                    metaWearBoards.get(finalI).downloadLatestFirmwareAsync()
                            .onSuccessTask(task ->
                            {
                                firmwareCapture.set(task.getResult());
                                return metaWearBoards.get(finalI).inMetaBootMode() ? metaWearBoards.get(finalI).disconnectAsync() : metaWearBoards.get(finalI).getModule(Debug.class).jumpToBootloaderAsync();
                            })
                            .continueWith(task ->
                            {
                                if (task.isFaulted())
                                {
                                    Snackbar.make(NavigationActivity.this.findViewById(R.id.drawer_layout), task.getError().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                } else
                                {
                                    if (addMimeType(firmwareCapture.get()))
                                    {
                                        starter.start(this, DfuService.class);
                                    } else
                                    {
                                        ((DialogFragment) getSupportFragmentManager().findFragmentByTag(DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
                                        Snackbar.make(NavigationActivity.this.findViewById(R.id.drawer_layout), R.string.error_firmware_path_type, Snackbar.LENGTH_LONG).show();
                                    }
                                }
                                return null;
                            }, Task.UI_THREAD_EXECUTOR);
                } else
                {
                    DfuProgressFragment.newInstance(R.string.message_manual_dfu).show(getSupportFragmentManager(), DFU_PROGRESS_FRAGMENT_TAG);
                    (metaWearBoards.get(finalI).inMetaBootMode() ? metaWearBoards.get(finalI).disconnectAsync() : metaWearBoards.get(finalI).getModule(Debug.class).jumpToBootloaderAsync())
                            .continueWith(ignored -> starter.start(this, DfuService.class));
                }
            });
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        getApplicationContext().unbindService(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mWrapper = (RelativeLayout) findViewById(R.id.wrapper);
        mListView = (ListView) findViewById(R.id.list_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        }

        mToolbarProfile = (RelativeLayout) findViewById(R.id.toolbar_profile);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollView);
        mProfileDetails = (LinearLayout) findViewById(R.id.wrapper_profile_details);
        mTextViewProfileName = (TextView) findViewById(R.id.text_view_profile_name);
        connectButton = (Button) findViewById(R.id.ble_connect_control);
        mButtonProfile = findViewById(R.id.button_profile);
        mButtonProfile.post(new Runnable()
        {
            @Override
            public void run()
            {
                mInitialProfileButtonX = mButtonProfile.getX();
            }
        });
        findViewById(R.id.toolbar_profile_back).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateCloseProfileDetails();
            }
        });

        sScreenWidth = getResources().getDisplayMetrics().widthPixels;
        sProfileImageHeight = getResources().getDimensionPixelSize(R.dimen.height_profile_image);
        sOverlayShape = buildAvatarCircleOverlay();

        initList();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> ((ModuleFragmentBase) currentFragment).showHelpDialog());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null)
        {
            onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_assign));
        } else
        {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);
        }

        getApplicationContext().bindService(new Intent(NavigationActivity.this, BtleService.class), NavigationActivity.this, BIND_AUTO_CREATE);

        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (currentFragment != null)
        {
            getSupportFragmentManager().putFragment(outState, FRAGMENT_KEY, currentFragment);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode != RESULT_OK)
            return;

        fileStreamUri = null;
        switch (requestCode)
        {
            case SELECT_FILE_REQ:
                // and read new one
                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema.
                 * 'File' schema allows us to create a File object and read details from if directly.
                 *
                 * Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */
                if (uri.getScheme().equals("file"))
                {
                    // the direct path to the file has been returned
                    initiateDfu(new File(uri.getPath()));
                } else if (uri.getScheme().equals("content"))
                {
                    fileStreamUri = uri;

                    // file name and size must be obtained from Content Provider
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(EXTRA_URI, uri);
                    getSupportLoaderManager().restartLoader(0, bundle, this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            if (metaWearBoards.size() > 0)
            {
                for (int i = 0; i < metaWearBoards.size(); i++)
                {
                    metaWearBoards.get(i).disconnectAsync();
                }
            }

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_reset:
                if (metaWearBoards.size() > 0)
                {
                    for (int i = 0; i < metaWearBoards.size(); i++)
                    {
                        if (!metaWearBoards.get(i).inMetaBootMode())
                        {
                            metaWearBoards.get(i).getModule(Debug.class).resetAsync()
                                    .continueWith(ignored ->
                                    {
                                        attemptReconnect(0);
                                        return null;
                                    });
                            Snackbar.make(findViewById(R.id.drawer_layout), R.string.message_soft_reset, Snackbar.LENGTH_LONG).show();
                        } else
                        {
                            Snackbar.make(findViewById(R.id.drawer_layout), R.string.message_no_soft_reset, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
                return true;
            case R.id.action_disconnect:
                if (metaWearBoards.size() > 0)
                {
                    for (int i = 0; i < metaWearBoards.size(); i++)
                    {
                        if (!metaWearBoards.get(i).inMetaBootMode())
                        {
                            Settings.BleConnectionParametersEditor editor = metaWearBoards.get(i).getModule(Settings.class).editBleConnParams();
                            if (editor != null)
                            {
                                editor.maxConnectionInterval(125f)
                                        .commit();
                            }
                            metaWearBoards.get(i).getModule(Debug.class).disconnectAsync();
                        } else
                        {
                            metaWearBoards.get(i).disconnectAsync();
                        }
                    }
                }
                //finish();
                return true;
            case R.id.action_manual_dfu:
                if (checkLocationPermission())
                {
                    startContentSelectionIntent();
                }
                break;
            case R.id.action_new_horse:
                startActivity(new Intent(NavigationActivity.this, HorseProfileActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (currentFragment != null)
        {
            transaction.detach(currentFragment);
        }

        String fragmentTag = FRAGMENT_CLASSES.get(id).getCanonicalName();
        currentFragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (currentFragment == null)
        {
            try
            {
                currentFragment = FRAGMENT_CLASSES.get(id).getConstructor().newInstance();
            } catch (Exception e)
            {
                throw new RuntimeException("Cannot instantiate fragment", e);
            }

            transaction.add(R.id.container, currentFragment, fragmentTag);
        }

        transaction.attach(currentFragment).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder)
    {
        serviceBinder = (BtleService.LocalBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {

    }

    public void createMetaWearBoards()
    {
        if (bluetoothDevices.size() > 0)
        {
            for (int i = 0; i < bluetoothDevices.size(); i++)
            {
                MetaWearBoard metaWearBoard = serviceBinder.getMetaWearBoard(bluetoothDevices.get(i));
                metaWearBoard.onUnexpectedDisconnect(status -> attemptReconnect());
                metaWearBoards.add(metaWearBoard);
            }
        } else if (bluetoothDevices.size() == 0)
            DialogUtility.showAlertSnackBarMedium(NavigationActivity.this, getString(R.string.message_no_horseshoes_found_click_scan));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final Uri uri = args.getParcelable(EXTRA_URI);
        /*
         * Some apps, f.e. Google Drive allow to select file that is not on the device. There is no "_data" column handled by that provider. Let's try to obtain all columns and than check
         * which columns are present.
	     */
        //final String[] projection = new String[] { MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATA };
        return new CursorLoader(this, uri, null /*all columns, instead of projection*/, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (data.moveToNext())
        {
            /*
             * Here we have to check the column indexes by name as we have requested for all. The order may be different.
             */
            fileName = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)/* 0 DISPLAY_NAME */);
            //final int fileSize = data.getInt(data.getColumnIndex(MediaStore.MediaColumns.SIZE) /* 1 SIZE */);

            final int dataIndex = data.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (dataIndex != -1)
            {
                initiateDfu(new File(data.getString(dataIndex /*2 DATA */)));
            } else
            {
                initiateDfu(fileStreamUri);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    /**
     * Code for content selection adapted from the nRF Toolbox app by Nordic Semiconductor
     * https://play.google.com/store/apps/details?id=no.nordicsemi.android.nrftoolbox&hl=en
     */
    private void startContentSelectionIntent()
    {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*")
                .addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, SELECT_FILE_REQ);
    }

    @TargetApi(23)
    private boolean checkLocationPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission code taken from Radius Networks
            // http://developer.radiusnetworks.com/2015/09/29/is-your-beacon-app-ready-for-android-6.html

            // Android M Permission check
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_request_permission);
            builder.setMessage(R.string.permission_read_external_storage);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE));
            builder.show();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_READ_STORAGE:
            {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Snackbar.make(findViewById(R.id.drawer_layout), R.string.message_permission_denied, Snackbar.LENGTH_LONG).show();
                } else
                {
                    startContentSelectionIntent();
                }
            }
        }
    }

    @Override
    protected BaseAdapter getAdapter()
    {
        Map<String, Object> profileMap;
        List<Map<String, Object>> profilesList = new ArrayList<>();

        List<Horse> horses = DatabaseUtility.retrieveHorses(NavigationActivity.this);

        if (horses.size() > 0)
        {
            for (int i = 0; i < horses.size(); i++)
            {
                profileMap = new HashMap<>();
                profileMap.put(CustomListAdapter.KEY_HORSE_ID, horses.get(i).getId());
                profileMap.put(CustomListAdapter.KEY_AVATAR, horses.get(i).getProfilePictureURI());
                profileMap.put(CustomListAdapter.KEY_NAME, horses.get(i).getHorseName());
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_SHORT, getString(R.string.lorem_ipsum_short));
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_FULL, getString(R.string.lorem_ipsum_long));
                profilesList.add(profileMap);
            }
        } else
        {
            int[] avatars = {
                    R.drawable.horse1,
                    R.drawable.horse2,
                    R.drawable.horse3,
                    R.drawable.horse4,
                    R.drawable.horse5};
            String[] names = getResources().getStringArray(R.array.array_names);

            for (int i = 0; i < avatars.length; i++)
            {
                profileMap = new HashMap<>();
                profileMap.put(CustomListAdapter.KEY_AVATAR, avatars[i]);
                profileMap.put(CustomListAdapter.KEY_NAME, names[i]);
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_SHORT, getString(R.string.lorem_ipsum_short));
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_FULL, getString(R.string.lorem_ipsum_long));
                profilesList.add(profileMap);
            }
        }

        return new CustomListAdapter(this, R.layout.list_item, profilesList);
    }

    static void setConnInterval(Settings settings)
    {
        if (settings != null)
        {
            Settings.BleConnectionParametersEditor editor = settings.editBleConnParams();
            if (editor != null)
            {
                editor.maxConnectionInterval(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 11.25f : 7.5f)
                        .commit();
            }
        }
    }

    public static Task<Void> reconnect(final MetaWearBoard board)
    {
        return board.connectAsync()
                .continueWithTask(task ->
                {
                    if (task.isFaulted())
                    {
                        return reconnect(board);
                    } else if (task.isCancelled())
                    {
                        return task;
                    }
                    return Task.forResult(null);
                });
    }
}