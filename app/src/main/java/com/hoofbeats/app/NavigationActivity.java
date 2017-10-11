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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hoofbeats.app.adapter.CustomListAdapter;
import com.hoofbeats.app.adapter.ScannedDeviceInfoAdapter;
import com.hoofbeats.app.bluetooth.BleScannerFragment;
import com.hoofbeats.app.bluetooth.ScannedDeviceInfo;
import com.hoofbeats.app.custom.menu.ContextMenuDialogFragment;
import com.hoofbeats.app.custom.menu.MenuObject;
import com.hoofbeats.app.custom.menu.MenuParams;
import com.hoofbeats.app.custom.menu.interfaces.OnMenuItemClickListener;
import com.hoofbeats.app.custom.menu.interfaces.OnMenuItemLongClickListener;
import com.hoofbeats.app.fragment.ModuleFragmentBase;
import com.hoofbeats.app.fragment.StrideLinearFragment;
import com.hoofbeats.app.model.Horse;
import com.hoofbeats.app.model.Horseshoe;
import com.hoofbeats.app.model.Workout;
import com.hoofbeats.app.model.Wrapper;
import com.hoofbeats.app.utility.BoardVault;
import com.hoofbeats.app.utility.DatabaseUtility;
import com.hoofbeats.app.utility.DialogUtility;
import com.hoofbeats.app.utility.LittleDB;
import com.hoofbeats.app.utility.SaveUtility;
import com.mbientlab.metawear.DataToken;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Debug;
import com.mbientlab.metawear.module.Gpio;
import com.mbientlab.metawear.module.Logging;
import com.mbientlab.metawear.module.SensorFusionBosch;
import com.mbientlab.metawear.module.Settings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class NavigationActivity extends BaseActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener,
        ServiceConnection, BleScannerFragment.ScannerCommunicationBus, ModuleFragmentBase.FragmentBus, LoaderManager.LoaderCallbacks<Cursor>,
        ScannedDeviceInfoAdapter.OnDeviceConfiguredListener, BleScannerFragment.OnLoggingListener
{
    private final static Map<Integer, Class<? extends ModuleFragmentBase>> FRAGMENT_CLASSES;
    private final static Map<String, String> EXTENSION_TO_APP_TYPE;
    private final static UUID[] serviceUuids;
    private Map<Wrapper, BluetoothDevice> modules = new HashMap<>();

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
    private ContextMenuDialogFragment mMenuDialogFragment;

    static
    {
        Map<Integer, Class<? extends ModuleFragmentBase>> tempMap = new LinkedHashMap<>();
        tempMap.put(0, BleScannerFragment.class);
        tempMap.put(1, StrideLinearFragment.class);
        FRAGMENT_CLASSES = Collections.unmodifiableMap(tempMap);

        EXTENSION_TO_APP_TYPE = new HashMap<>();
        EXTENSION_TO_APP_TYPE.put("hex", DfuBaseService.MIME_TYPE_OCTET_STREAM);
        EXTENSION_TO_APP_TYPE.put("bin", DfuBaseService.MIME_TYPE_OCTET_STREAM);
        EXTENSION_TO_APP_TYPE.put("zip", DfuBaseService.MIME_TYPE_ZIP);
    }

    @Override
    public List<BluetoothDevice> getBtDevices()
    {
        return bluetoothDevices;
    }

    @Override
    public Map<Wrapper, BluetoothDevice> getModules()
    {
        return modules;
    }

    @Override
    public void resetConnectionStateHandler(long delay)
    {
        attemptReconnect(delay);
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

        initToolbar();
        initMenuFragment();

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollView);
        mProfileDetails = (LinearLayout) findViewById(R.id.wrapper_profile_details);
        mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        connectButton = (Button) findViewById(R.id.ble_connect_control);

        findViewById(R.id.toolbar_profile_back).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateCloseProfileDetails();
            }
        });
        findViewById(R.id.toolbar_menu).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMenuDialogFragment.show(getSupportFragmentManager(), "ContextMenuDialogFragment");
            }
        });

        startModulesButton = (Button) findViewById(R.id.capture_button);
        startModulesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startModules();
            }
        });

        sScreenWidth = getResources().getDisplayMetrics().widthPixels;
        sProfileImageHeight = getResources().getDimensionPixelSize(R.dimen.height_profile_image);
        sOverlayShape = buildAvatarCircleOverlay();

        initList();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> ((ModuleFragmentBase) currentFragment).showHelpDialog());

        if (savedInstanceState == null)
        {
            addFragment(0);
        } else
        {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, Config.FRAGMENT_KEY);
        }

        getApplicationContext().bindService(new Intent(NavigationActivity.this, BtleService.class), NavigationActivity.this, BIND_AUTO_CREATE);

        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    private void initToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    private void initMenuFragment()
    {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects()
    {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("Configure");
        send.setResource(R.drawable.icn_5);

        MenuObject like = new MenuObject("Linear Data");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icn_5);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Reset");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.icn_5));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Disconnect");
        addFav.setResource(R.drawable.icn_5);

        MenuObject block = new MenuObject("Update");
        block.setResource(R.drawable.icn_5);

        MenuObject newHorse = new MenuObject("New Horse");
        newHorse.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        menuObjects.add(newHorse);
        return menuObjects;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        /*
        * Saves the metawear boards serialized state when the activity closes.
        * */
        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            BoardVault.get().putMetaWearBoard(entry.getValue().getName(), Config.SERIALIZED_BOARDS_FILE,
                    entry.getKey().getMetaWearBoard().getMacAddress(), entry.getKey().getMetaWearBoard());
        }

        System.out.println("SAVED-INSTANCE-STATE-CALLED");

        if (currentFragment != null)
        {
            getSupportFragmentManager().putFragment(outState, Config.FRAGMENT_KEY, currentFragment);
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
            case Config.SELECT_FILE_REQ:
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
                    bundle.putParcelable(Config.EXTRA_URI, uri);
                    getSupportLoaderManager().restartLoader(0, bundle, this);
                }
                break;
            default:
                break;
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
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_new_horse:
                startActivity(new Intent(NavigationActivity.this, HorseProfileActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (mMenuDialogFragment != null && mMenuDialogFragment.isAdded())
        {
            mMenuDialogFragment.dismiss();
        } else
        {
            for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
            {
                BoardVault.get().putMetaWearBoard(entry.getValue().getName(), Config.SERIALIZED_BOARDS_FILE,
                        entry.getKey().getMetaWearBoard().getMacAddress(), entry.getKey().getMetaWearBoard());
            }

            finish();
            super.onBackPressed();
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            if (entry.getKey().getSensorFusionBosch() != null)
            {
                entry.getKey().getSensorFusionBosch().linearAcceleration().stop();
                entry.getKey().getSensorFusionBosch().stop();
            }

            if (entry.getKey().getMetaWearBoard() != null)
            {
                if (entry.getKey().getMetaWearBoard().isConnected())
                {
                    entry.getKey().getMetaWearBoard().tearDown();

                    if (entry.getKey().getMetaWearBoard() != null)
                        entry.getKey().getMetaWearBoard().disconnectAsync();
                }
            }
        }

        getApplicationContext().unbindService(this);
    }

    //************************ Adapter code start **************************

    @Override
    protected BaseAdapter getAdapter()
    {
        Map<String, Object> profileMap;
        List<Map<String, Object>> profilesList = new ArrayList<>();

        List<Horse> horses = DatabaseUtility.retrieveHorses();

        if (horses.size() > 0)
        {
            for (int i = 0; i < horses.size(); i++)
            {
                profileMap = new HashMap<>();

                long horseId = -1;
                horseId = horses.get(i).getId();

                profileMap.put(CustomListAdapter.KEY_HORSE_ID, horseId);
                profileMap.put(CustomListAdapter.KEY_AVATAR, horses.get(i).getProfilePictureURI());
                profileMap.put(CustomListAdapter.KEY_NAME, horses.get(i).getHorseName());
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_SHORT, getString(R.string.lorem_ipsum_short));
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_FULL, getString(R.string.lorem_ipsum_long));
                profilesList.add(profileMap);
            }
        } else
        {
            int[] avatars = {
                    R.mipmap.ic_launcher,};

            for (int i = 0; i < avatars.length; i++)
            {
                profileMap = new HashMap<>();
                profileMap.put(CustomListAdapter.KEY_AVATAR, avatars[i]);
                profileMap.put(CustomListAdapter.KEY_NAME, NavigationActivity.this.getString(R.string.app_name));
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_SHORT, "click New Horse");
                profileMap.put(CustomListAdapter.KEY_DESCRIPTION_FULL, "-");
                profilesList.add(profileMap);
            }
        }

        return new CustomListAdapter(this, R.layout.list_item, profilesList);
    }

    //******************** Adapter code end ****************************

    //********************Interface methods*****************************

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder)
    {
        serviceBinder = (BtleService.LocalBinder) iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {

    }

    @Override
    public void onLogging(BluetoothDevice bluetoothDevice)
    {
        bluetoothDevices.add(bluetoothDevice);
        Horse horse = DatabaseUtility.retrieveHorseForId(LittleDB.get().getLong(Config.SELECTED_HORSE_ID, -1));
        Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(bluetoothDevice.getAddress());

        if (horseshoe.getHoof().equals("Left Hind"))
        {
            MetaWearBoard metaWearBoard = getMetaWearBoard("Left Hind", bluetoothDevice);
            metaWearBoards.add(metaWearBoard);
            connectBoard("Left Hind", null, metaWearBoard);
        } else if (horseshoe.getHoof().equals("Left Front"))
        {
            MetaWearBoard metaWearBoard = getMetaWearBoard("Left Front", bluetoothDevice);
            metaWearBoards.add(metaWearBoard);
            connectBoard("Left Front", null, metaWearBoard);
        } else if (horseshoe.getHoof().equals("Right Hind"))
        {
            MetaWearBoard metaWearBoard = getMetaWearBoard("Right Hind", bluetoothDevice);
            metaWearBoards.add(metaWearBoard);
            connectBoard("Right Hind", null, metaWearBoard);
        } else if (horseshoe.getHoof().equals("Right Front"))
        {
            MetaWearBoard metaWearBoard = getMetaWearBoard("Right Front", bluetoothDevice);
            metaWearBoards.add(metaWearBoard);
            connectBoard("Right Front", null, metaWearBoard);
        }
    }

    private MetaWearBoard getMetaWearBoard(String hoof, BluetoothDevice bluetoothDevice)
    {
        return BoardVault.get().getMetaWearBoard(hoof, serviceBinder.getMetaWearBoard(bluetoothDevice));
    }

    private void connectBoard(String hoof, ScannedDeviceInfo scannedDeviceInfo, MetaWearBoard metaWearBoard)
    {
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
                        metaWearBoard.onUnexpectedDisconnect(status -> attemptReconnect());
                        setConnInterval(metaWearBoard.getModule(Settings.class));
                        runOnUiThread(connectDialog::dismiss);
                        metaWearBoards.add(metaWearBoard);

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (scannedDeviceInfo != null)
                                {
                                    scannedDeviceInfo.setConnected(true);
                                    scannedDeviceInfo.setColorCheckMark();
                                }

                                setupFragment(metaWearBoard, hoof);
                            }
                        });
                    }
                    return null;
                });
    }

    @Override
    public void onDeviceConfigured(View convertView, ScannedDeviceInfo scannedDeviceInfo)
    {
        ImageView connectedCheck = (ImageView) convertView.findViewById(R.id.ble_check_connected);

        BluetoothDevice bluetoothDevice = scannedDeviceInfo.btDevice;
        serviceBinder.removeMetaWearBoard(bluetoothDevice);
        MetaWearBoard metaWearBoard = serviceBinder.getMetaWearBoard(bluetoothDevice);
        bluetoothDevices.add(bluetoothDevice);

        connectBoard(scannedDeviceInfo.getHoof(), scannedDeviceInfo, metaWearBoard);
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
    public void onMenuItemClick(View clickedView, int position)
    {
        Toast.makeText(this, "Clicked on position: " + position, Toast.LENGTH_SHORT).show();

        switch (position)
        {
            case 0:
                mMenuDialogFragment.dismiss();
                break;
            case 1:
                addFragment(0);
                mToolBarTextView.setText("Capture");
                break;
            case 2:
                addFragment(1);
                break;
            case 3:
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
                            DialogUtility.showNoticeSnackBarShort(NavigationActivity.this, getString(R.string.message_soft_reset));
                        } else
                        {
                            DialogUtility.showAlertSnackBarMedium(NavigationActivity.this, getString(R.string.message_no_soft_reset));
                        }
                    }
                }
            case 4:
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
            case 5:
                if (checkLocationPermission())
                {
                    startContentSelectionIntent();
                }
                break;
            case 6:
                startActivity(new Intent(NavigationActivity.this, HorseProfileActivity.class));
                finish();
                break;
        }

    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position)
    {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

    //****************** Interface code end *********************************
    //******************Configure fragments code*****************************
    protected void addFragment(int position)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (currentFragment != null)
        {
            transaction.detach(currentFragment);
        }

        String fragmentTag = FRAGMENT_CLASSES.get(position).getCanonicalName();
        currentFragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (currentFragment == null)
        {
            try
            {
                currentFragment = FRAGMENT_CLASSES.get(position).getConstructor().newInstance();
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
            actionBar.setTitle(currentFragment.getTag());
        }
    }

    private void setupFragment(MetaWearBoard metaWearBoard, String hoof)
    {
        Gpio gpio = metaWearBoard.getModule(Gpio.class);
        SensorFusionBosch sensorFusionBosch = metaWearBoard.getModule(SensorFusionBosch.class);
        Logging logging = metaWearBoard.getModule(Logging.class);
        Settings settings = metaWearBoard.getModule(Settings.class);
        Wrapper wrapper = new Wrapper(gpio, sensorFusionBosch, logging,
                settings, metaWearBoard);

        if (hoof.contains("Left Hind"))
        {
            configureGpioLH(wrapper);
            configureSensorFusionLH(wrapper);

            for (BluetoothDevice bluetoothDevice : bluetoothDevices)
            {
                if (bluetoothDevice.getAddress().equals(metaWearBoard.getMacAddress()))
                {
                    modules.put(wrapper, bluetoothDevice);
                    if (LittleDB.get().getBoolean(Config.MODULES_CURRENTLY_LOGGING, false))
                        startDownload(wrapper, bluetoothDevice);

                    break;
                }
            }
        } else if (hoof.contains("Left Front"))
        {
            configureGpioLF(wrapper);
            configureSensorFusionLF(wrapper);

            for (BluetoothDevice bluetoothDevice : bluetoothDevices)
            {
                if (bluetoothDevice.getAddress().equals(metaWearBoard.getMacAddress()))
                {
                    modules.put(wrapper, bluetoothDevice);
                    if (LittleDB.get().getBoolean(Config.MODULES_CURRENTLY_LOGGING, false))
                        startDownload(wrapper, bluetoothDevice);

                    break;
                }
            }
        } else if (hoof.contains("Right Hind"))
        {
            configureGpioRH(wrapper);
            configureSensorFusionRH(wrapper);

            for (BluetoothDevice bluetoothDevice : bluetoothDevices)
            {
                if (bluetoothDevice.getAddress().equals(metaWearBoard.getMacAddress()))
                {
                    modules.put(wrapper, bluetoothDevice);
                    if (LittleDB.get().getBoolean(Config.MODULES_CURRENTLY_LOGGING, false))
                        startDownload(wrapper, bluetoothDevice);

                    break;
                }
            }
        } else if (hoof.contains("Right Front"))
        {
            configureGpioRF(wrapper);
            configureSensorFusionRF(wrapper);

            for (BluetoothDevice bluetoothDevice : bluetoothDevices)
            {
                if (bluetoothDevice.getAddress().equals(metaWearBoard.getMacAddress()))
                {
                    modules.put(wrapper, bluetoothDevice);
                    if (LittleDB.get().getBoolean(Config.MODULES_CURRENTLY_LOGGING, false))
                        startDownload(wrapper, bluetoothDevice);

                    break;
                }
            }
        }
    }
    // ************************** Configure horseshoes start ****************************

    private void configureSensorFusionLH(Wrapper wrapper)
    {
        System.out.println("LH-CONFIGURED-1");
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        SaveUtility saveUtility = ((SaveUtility) env[0]);
                        saveUtility.setSensor(Config.SENSOR_FUSION_BOSCH);
                        saveUtility.setHoof("Left Hind");
                        saveUtility.setTimestamp(data.timestamp().getTimeInMillis());
                        saveUtility.setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        saveUtility.setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        saveUtility.setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        saveUtility.executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            System.out.println("LH-CONFIGURED-2");
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }

    private void configureSensorFusionLF(Wrapper wrapper)
    {
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        ((SaveUtility) env[0]).setSensor(Config.SENSOR_FUSION_BOSCH);
                        ((SaveUtility) env[0]).setHoof("Left Front");
                        ((SaveUtility) env[0]).setTimestamp(data.timestamp().getTimeInMillis());
                        ((SaveUtility) env[0]).setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        ((SaveUtility) env[0]).setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        ((SaveUtility) env[0]).setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        ((SaveUtility) env[0]).executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }

    private void configureSensorFusionRH(Wrapper wrapper)
    {
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        ((SaveUtility) env[0]).setSensor(Config.SENSOR_FUSION_BOSCH);
                        ((SaveUtility) env[0]).setHoof("Right Hind");
                        ((SaveUtility) env[0]).setTimestamp(data.timestamp().getTimeInMillis());
                        ((SaveUtility) env[0]).setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        ((SaveUtility) env[0]).setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        ((SaveUtility) env[0]).setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        ((SaveUtility) env[0]).executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }

    private void configureSensorFusionRF(Wrapper wrapper)
    {
        wrapper.getSensorFusionBosch().configure()
                .mode(SensorFusionBosch.Mode.NDOF)
                .accRange(SensorFusionBosch.AccRange.AR_16G)
                .gyroRange(SensorFusionBosch.GyroRange.GR_2000DPS)
                .commit();

        wrapper.getSensorFusionBosch().linearAcceleration()
                .addRouteAsync(source -> source.log((data, env) ->
                {
                    if (env != null)
                    {
                        ((SaveUtility) env[0]).setSensor(Config.SENSOR_FUSION_BOSCH);
                        ((SaveUtility) env[0]).setHoof("Right Front");
                        ((SaveUtility) env[0]).setTimestamp(data.timestamp().getTimeInMillis());
                        ((SaveUtility) env[0]).setxValueLinearAcceleration(data.value(Acceleration.class).x());
                        ((SaveUtility) env[0]).setyValueLinearAcceleration(data.value(Acceleration.class).y());
                        ((SaveUtility) env[0]).setzValueLinearAcceleration(data.value(Acceleration.class).z());
                        ((SaveUtility) env[0]).executeSave();
                    }
                }).react(new RouteComponent.Action()
                {
                    @Override
                    public void execute(DataToken token)
                    {
                        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc().read();
                    }
                })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }

    private void configureGpioLH(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                System.out.println("GPIO-READING: " + data.value(Short.class));

                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("Left Hind");
                                ((SaveUtility) env[0]).setTimestamp(data.timestamp().getTimeInMillis());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWithTask(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }

    private void configureGpioLF(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("Left Front");
                                ((SaveUtility) env[0]).setTimestamp(data.timestamp().getTimeInMillis());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }

    private void configureGpioRH(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("Right Hind");
                                ((SaveUtility) env[0]).setTimestamp(data.timestamp().getTimeInMillis());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }

    private void configureGpioRF(Wrapper wrapper)
    {
        wrapper.getGpio().pin(Config.GPIO_PIN).analogAdc()
                .addRouteAsync(source -> source
                        .log((data, env) ->
                        {
                            if (env != null)
                            {
                                ((SaveUtility) env[0]).setSensor(Config.GPIO_SENSOR);
                                ((SaveUtility) env[0]).setHoof("Right Front");
                                ((SaveUtility) env[0]).setTimestamp(data.timestamp().getTimeInMillis());
                                ((SaveUtility) env[0]).setForceValue(data.value(Short.class));
                                ((SaveUtility) env[0]).executeSave();
                            }
                        })).continueWith(task ->
        {
            task.getResult().setEnvironment(0, new SaveUtility(NavigationActivity.this));
            return null;
        });
    }// **************************** Configure horseshoes end **********************************

    private void startModules()
    {
        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            if (entry.getKey().getLogging() != null)
            {
                entry.getKey().getLogging().start(Config.LOGGING_OVERWRITE_MODE);
                System.out.println("LOGGING-STARTED");
            }

            if (entry.getKey().getGpio() != null)
            {
                System.out.println("GPIO-NOT-NULL");

                if (entry.getKey().getSensorFusionBosch() != null)
                {
                    entry.getKey().getSensorFusionBosch().linearAcceleration().start();
                    entry.getKey().getSensorFusionBosch().start();

                    System.out.println("ACCELEROMETER-STARTED");
                }

                Horse horse = null;

                Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(entry.getKey().getMetaWearBoard().getMacAddress());

                if (horseshoe != null)
                {
                    horseshoe.setLogging(true);
                    horseshoe.update();

                    horse = horseshoe.getHorse();
                }

                Workout workout = null;

                List<Workout> workouts = DatabaseUtility.retrieveWorkouts(horse.getId());

                if (workouts.size() == 0)
                {
                    System.out.println("WORKOUT-1");
                    workout = new Workout();
                    workout.setHorseId(horse.getId());
                    workout.setHorse(horse);
                    workout.setDate(new SimpleDateFormat("d-M-yyyy").format(Calendar.getInstance().getTime()));
                    workout.setStartTime(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(Calendar.getInstance().getTime()));
                    MyApplication.getInstance().getDaoSession().getWorkoutDao().insert(workout);
                } else if (workouts.size() > 0)
                {
                    for (int i = 0; i < workouts.size(); i++)
                    {
                        if (workouts.get(i).getDate().equals(new SimpleDateFormat("d-M-yyyy").format(Calendar.getInstance().getTime())))
                        {
                            System.out.println("WORKOUT-2");

                            // UPDATE SO CAN SELECT START TIME FROM DATE/TIME PICKER TO ENSURE CORRECT WORKOUT IS
                            // SELECTED IF HORSE HAS MULTIPLE WORKOUTS FOR SAME DATE.
                            workout = workouts.get(i);
                        }
                    }

                    if (workout == null)
                    {
                        System.out.println("WORKOUT-3");
                        workout = new Workout();
                        workout.setHorseId(horse.getId());
                        workout.setHorse(horse);
                        workout.setDate(new SimpleDateFormat("d-M-yyyy").format(Calendar.getInstance().getTime()));
                        workout.setStartTime(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(Calendar.getInstance().getTime()));
                        MyApplication.getInstance().getDaoSession().getWorkoutDao().insert(workout);
                    }
                }

                System.out.println("WORKOUT-ID: " + workout.getId());

                LittleDB.get().putLong(Config.WORKOUT_ID, workout.getId());
                horse.getWorkouts().add(workout);

                // SET TO FALSE IN THE LOGGING DOWNLOAD SECTION.
                LittleDB.get().putBoolean(Config.MODULES_CURRENTLY_LOGGING, true);

                DialogUtility.showStartLoggingDialog(NavigationActivity.this);
            }
        }
    }

    public void closeModules()
    {
        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            BoardVault.get().putMetaWearBoard(entry.getValue().getName(), Config.SERIALIZED_BOARDS_FILE,
                    entry.getKey().getMetaWearBoard().getMacAddress(), entry.getKey().getMetaWearBoard());

            if (entry.getKey().getMetaWearBoard().isConnected())
            {
                entry.getKey().getMetaWearBoard().tearDown();
                entry.getKey().getMetaWearBoard().disconnectAsync();
            }

            System.out.println("BOARD-SERIALIZED:");
        }

        finish();
    }

    public void startDownload(Wrapper wrapper, BluetoothDevice bluetoothDevice)
    {
        System.out.println("DOWNLOAD-LOG-CALLED");

        if (wrapper.getMetaWearBoard().isConnected())
        {
            MaterialDialog materialDialog = DialogUtility.showProgressDialogDownload(NavigationActivity.this);
            Horseshoe horseshoe = DatabaseUtility.retrieveHorseShoeForMacAddress(bluetoothDevice.getAddress());

            wrapper.getLogging().stop();

            wrapper.getSensorFusionBosch().linearAcceleration().stop();
            wrapper.getSensorFusionBosch().stop();

            horseshoe.setLogging(false);
            horseshoe.update();

            wrapper.getLogging().downloadAsync(100, new Logging.LogDownloadUpdateHandler()
            {
                @Override
                public void receivedUpdate(long nEntriesLeft, long totalEntries)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            System.out.println("ENTRIES-REMAINING: " + nEntriesLeft);
                        }
                    });
                }
            }).continueWithTask(new Continuation<Void, Task<Void>>()
            {
                @Override
                public Task<Void> then(Task<Void> task) throws Exception
                {
                    wrapper.getLogging().clearEntries();
                    wrapper.getMetaWearBoard().getModule(Debug.class).resetAfterGc();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            wrapper.getMetaWearBoard().tearDown();
                            wrapper.getMetaWearBoard().disconnectAsync();
                            LittleDB.get().putBoolean(Config.MODULES_CURRENTLY_LOGGING, false);
                            DialogUtility.showDownloadFinishedDialog(NavigationActivity.this);
                            materialDialog.dismiss();
                        }
                    });
                    return null;
                }
            });
        }
    }

    //********************* Dfu start **********************************

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
            DialogUtility.showAlertSnackBarMedium(NavigationActivity.this, getString(R.string.error_firmware_path_type));
            return;
        }

        for (int i = 0; i < metaWearBoards.size(); i++)
        {
            final int finalI = i;
            taskScheduler.post(() ->
            {
                if (path == null)
                {
                    DfuProgressFragment.newInstance(R.string.message_dfu).show(getSupportFragmentManager(), Config.DFU_PROGRESS_FRAGMENT_TAG);

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
                                    DialogUtility.showWarningSnackBarLong(NavigationActivity.this, task.getError().getLocalizedMessage());
                                } else
                                {
                                    if (addMimeType(firmwareCapture.get()))
                                    {
                                        starter.start(this, DfuService.class);
                                    } else
                                    {
                                        ((DialogFragment) getSupportFragmentManager().findFragmentByTag(Config.DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
                                        DialogUtility.showAlertSnackBarMedium(NavigationActivity.this, getString(R.string.error_firmware_path_type));
                                    }
                                }
                                return null;
                            }, Task.UI_THREAD_EXECUTOR);
                } else
                {
                    DfuProgressFragment.newInstance(R.string.message_manual_dfu).show(getSupportFragmentManager(), Config.DFU_PROGRESS_FRAGMENT_TAG);
                    (metaWearBoards.get(finalI).inMetaBootMode() ? metaWearBoards.get(finalI).disconnectAsync() : metaWearBoards.get(finalI).getModule(Debug.class).jumpToBootloaderAsync())
                            .continueWith(ignored -> starter.start(this, DfuService.class));
                }
            });
        }
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

    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter()
    {
        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal)
        {
            ((DfuProgressFragment) getSupportFragmentManager().findFragmentByTag(Config.DFU_PROGRESS_FRAGMENT_TAG)).updateProgress(percent);
        }

        @Override
        public void onDfuCompleted(String deviceAddress)
        {
            ((DialogFragment) getSupportFragmentManager().findFragmentByTag(Config.DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
            DialogUtility.showNoticeSnackBarShort(NavigationActivity.this, getString(R.string.message_dfu_success));
            resetConnectionStateHandler(5000L);
        }

        @Override
        public void onDfuAborted(String deviceAddress)
        {
            ((DialogFragment) getSupportFragmentManager().findFragmentByTag(Config.DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
            DialogUtility.showAlertSnackBarMedium(NavigationActivity.this, getString(R.string.error_dfu_aborted));
            resetConnectionStateHandler(5000L);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message)
        {
            ((DialogFragment) getSupportFragmentManager().findFragmentByTag(Config.DFU_PROGRESS_FRAGMENT_TAG)).dismiss();
            DialogUtility.showWarningSnackBarLong(NavigationActivity.this, message);
            resetConnectionStateHandler(5000L);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        final Uri uri = args.getParcelable(Config.EXTRA_URI);
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
        startActivityForResult(intent, Config.SELECT_FILE_REQ);
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
            builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Config.PERMISSION_REQUEST_READ_STORAGE));
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
            case Config.PERMISSION_REQUEST_READ_STORAGE:
            {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    DialogUtility.showWarningSnackBarLong(NavigationActivity.this, getString(R.string.message_permission_denied));
                } else
                {
                    startContentSelectionIntent();
                }
            }
        }
    }

    //******************** Dfu end *************************************
    //******************** Reconnect code start ************************
    public static class ReconnectDialogFragment extends DialogFragment implements ServiceConnection
    {
        private static final String KEY_BLUETOOTH_DEVICE = "NavigationActivity.ReconnectDialogFragment.KEY_BLUETOOTH_DEVICE";

        private ProgressDialog reconnectDialog = null;
        private List<BluetoothDevice> bluetoothDevices;
        private List<MetaWearBoard> metaWearBoards = new ArrayList<>();

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
            bluetoothDevices = getArguments().getParcelable(KEY_BLUETOOTH_DEVICE);
            getActivity().getApplicationContext().bindService(new Intent(getActivity(), BtleService.class), this, BIND_AUTO_CREATE);

            reconnectDialog = new ProgressDialog(getActivity());
            reconnectDialog.setTitle(getString(R.string.title_reconnect_attempt));
            reconnectDialog.setMessage(getString(R.string.message_wait));
            reconnectDialog.setCancelable(false);
            reconnectDialog.setCanceledOnTouchOutside(false);
            reconnectDialog.setIndeterminate(true);
            reconnectDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.label_cancel), (dialogInterface, i) ->
            {
                for (int j = 0; j < metaWearBoards.size(); j++)
                    metaWearBoards.get(j).disconnectAsync();

                getActivity().finish();
            });

            return reconnectDialog;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            if (bluetoothDevices != null)
            {
                for (int i = 0; i < bluetoothDevices.size(); i++)
                {
                    metaWearBoards.add(((BtleService.LocalBinder) service).getMetaWearBoard(bluetoothDevices.get(i)));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
        }
    }


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

    public Task<Void> reconnect(final MetaWearBoard board)
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

    //******************* Reconnect code end ***************************
}