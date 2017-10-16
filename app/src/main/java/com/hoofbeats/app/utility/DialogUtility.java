package com.hoofbeats.app.utility;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hoofbeats.app.BaseActivity;
import com.hoofbeats.app.NavigationActivity;
import com.hoofbeats.app.R;
import com.hoofbeats.app.help.HelpOptionAdapter;
import com.hoofbeats.app.model.Wrapper;
import com.liuguangqiang.cookie.CookieBar;
import com.liuguangqiang.cookie.OnActionClickListener;
import com.mbientlab.metawear.MetaWearBoard;

import java.util.Map;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by royperdue on 1/16/17.
 */
public final class DialogUtility
{
    public static void showInformationSnackBarShort(Activity activity, String message)
    {
        new CookieBar.Builder(activity)
                .setTitle(activity.getString(R.string.title_notice))
                .setMessage(message)
                .setBackgroundColor(R.color.grey_500)
                .setTitleColor(R.color.grey_100)
                .setMessageColor(R.color.white)
                .setDuration(3000)
                .show();
    }

    public static void showNoticeSnackBarShort(Activity activity, String message)
    {
        new CookieBar.Builder(activity)
                .setTitle(activity.getString(R.string.title_notice))
                .setMessage(message)
                .setBackgroundColor(R.color.yellow_500)
                .setTitleColor(R.color.grey_400)
                .setMessageColor(R.color.grey_500)
                .setDuration(3000)
                .show();
    }

    public static void showWarningSnackBarLong(Activity activity, String message)
    {
        new CookieBar.Builder(activity)
                .setTitle(activity.getString(R.string.title_notice))
                .setMessage(message)
                .setBackgroundColor(R.color.red_600)
                .setTitleColor(R.color.grey_100)
                .setMessageColor(R.color.white)
                .setDuration(4000)
                .show();
    }

    public static void showAlertSnackBarMedium(Activity activity, String message)
    {
        new CookieBar.Builder(activity)
                .setTitle(activity.getString(R.string.title_alert))
                .setMessage(message)
                .setDuration(3000)
                .setBackgroundColor(R.color.orange_500)
                .setActionColor(android.R.color.white)
                .setTitleColor(R.color.white)
                .setAction(activity.getString(R.string.label_ok), new OnActionClickListener() {
                    @Override
                    public void onClick() {

                    }
                })
                .show();
    }

    public static void showAlertSnackBarShort(Activity activity, String message)
    {
        new CookieBar.Builder(activity)
                .setTitle(activity.getString(R.string.title_notice))
                .setMessage(message)
                .setBackgroundColor(R.color.orange_500)
                .setTitleColor(R.color.grey_100)
                .setMessageColor(R.color.white)
                .setDuration(2500)
                .show();
    }

    public static void showAlertDialog(String message, Activity activity)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.title_notice);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        builder.show();
    }

    public static void showDownloadFinishedDialog(BaseActivity baseActivity)
    {
        new MaterialDialog.Builder(baseActivity)
                .title(R.string.title_notice)
                .content(R.string.message_download_complete)
                .positiveText(R.string.label_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static MaterialDialog showProgressDialogConnecting(Activity activity, final MetaWearBoard metaWearBoard)
    {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.title_connecting))
                .content(activity.getString(R.string.message_wait))
                .negativeText(activity.getString(R.string.label_cancel))
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .onNegative(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        if (metaWearBoard != null && metaWearBoard.isConnected())
                            metaWearBoard.disconnectAsync();
                    }
                })
                .show();

        return materialDialog;
    }

    public static MaterialDialog showProgressDialogDownload(Activity activity)
    {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.title_downloading))
                .content(activity.getString(R.string.message_wait))
                .cancelable(false)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        return materialDialog;
    }

    public static MaterialDialog showProgressDialogScan(Activity activity)
    {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.title_scanning))
                .content(activity.getString(R.string.message_wait))
                .cancelable(true)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        return materialDialog;
    }

    public static MaterialDialog showProgressDialogUpdating(Activity activity)
    {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.title_updating))
                .content(activity.getString(R.string.message_wait))
                .cancelable(true)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        return materialDialog;
    }

    public static MaterialDialog showProgressDialogScanning(Activity activity)
    {
        final MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .title(activity.getString(R.string.title_scanning))
                .content(activity.getString(R.string.message_wait))
                .cancelable(true)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        return materialDialog;
    }

    public static void showHelpDialog(Activity activity, HelpOptionAdapter helpOptionAdapter)
    {
        if (helpOptionAdapter.getCount() != 0)
        {
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setPositiveButton(R.string.label_ok, null)
                    .setView(R.layout.layout_config_help)
                    .create();
            dialog.show();
            ((ListView) dialog.findViewById(R.id.config_help_list)).setAdapter(helpOptionAdapter);

            helpOptionAdapter.notifyDataSetChanged();
        } else
        {
            new AlertDialog.Builder(activity)
                    .setPositiveButton(R.string.label_ok, null)
                    .setMessage(R.string.message_no_config)
                    .create().show();
        }
    }

   public static void showConfirmDownloadDialog(NavigationActivity navigationActivity)
    {
        new MaterialDialog.Builder(navigationActivity)
                .title(R.string.title_confirm)
                .content(R.string.message_confirm_download)
                .positiveText(R.string.label_ok)
                .negativeText(R.string.label_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        navigationActivity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                            }
                        });

                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showStartLoggingDialog(NavigationActivity navigationActivity)
    {
       new MaterialDialog.Builder(navigationActivity)
                .title(R.string.title_success)
                .content(R.string.message_logging_started)
                .positiveText(R.string.label_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        navigationActivity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                navigationActivity.closeModules();
                                dialog.dismiss();
                            }
                        });
                    }
                }).show();
    }

    public static void updateBatteryIndicator(Activity activity, ImageView batteryIconView, TextView batteryLevelTextView, Map<Wrapper, BluetoothDevice> modules)
    {
        for (final Map.Entry<Wrapper, BluetoothDevice> entry : modules.entrySet())
        {
            if (entry.getKey().getMetaWearBoard() != null)
            {
                if (entry.getKey().getMetaWearBoard().isConnected())
                {
                    entry.getKey().getMetaWearBoard().readBatteryLevelAsync().continueWith(new Continuation<Byte, Object>()
                    {
                        @Override
                        public Object then(Task<Byte> task) throws Exception
                        {
                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    //System.out.println("BATTERY-LEVEL: " + String.format(Locale.US, "%d", task.getResult()));
                                    //batteryLevelTextView.setText(String.format(Locale.US, "%d", task.getResult()));
                                    //batteryLevelTextView.invalidate();

                                   /* if (Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) == 100)
                                        batteryIconView.setBackground(activity.getDrawable(R.drawable.ic_battery_full_light_green_600_24dp));
                                    else if (Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) > 90 && Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) < 100)
                                        batteryIconView.setBackground(activity.getDrawable(R.drawable.ic_battery_90_light_green_600_24dp));
                                    else if (Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) > 80 && Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) <= 90)
                                        batteryIconView.setBackground(activity.getDrawable(R.drawable.ic_battery_80_light_green_600_24dp));
                                    else if (Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) > 60 && Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) <= 80)
                                        batteryIconView.setBackground(activity.getDrawable(R.drawable.ic_battery_60_light_green_600_24dp));
                                    else if (Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) > 50 && Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) < 60)
                                        batteryIconView.setBackground(activity.getDrawable(R.drawable.ic_battery_50_light_green_600_24dp));
                                    else if (Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) > 20 && Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) <= 50)
                                        batteryIconView.setBackground(activity.getDrawable(R.drawable.ic_battery_30_red_500_24dp));
                                    else if (Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) > 0 && Integer.parseInt(String.format(Locale.US, "%d", task.getResult())) <= 20)
                                        batteryIconView.setBackground(activity.getDrawable(R.drawable.ic_battery_20_red_500_24dp));*/
                                }
                            });
                            return null;
                        }
                    });
                }

            }
        }
    }
}
