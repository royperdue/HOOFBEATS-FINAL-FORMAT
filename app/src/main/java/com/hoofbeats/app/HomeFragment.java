package com.hoofbeats.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoofbeats.app.help.HelpOptionAdapter;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.module.Led;
import com.mbientlab.metawear.module.Led.Color;

public class HomeFragment extends ModuleFragmentBase
{
    private Led ledModule;
    private int switchRouteId = -1;

    public static class MetaBootWarningFragment extends DialogFragment
    {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            return new AlertDialog.Builder(getActivity()).setTitle(R.string.title_warning)
                    .setPositiveButton(R.string.label_ok, null)
                    .setCancelable(false)
                    .setMessage(R.string.message_metaboot)
                    .create();
        }
    }

    public HomeFragment()
    {
        super(R.string.navigation_fragment_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.led_red_on).setOnClickListener(view1 ->
        {
            configureChannel(ledModule.editPattern(Color.RED));
            ledModule.play();
        });
        view.findViewById(R.id.led_green_on).setOnClickListener(view12 ->
        {
            configureChannel(ledModule.editPattern(Color.GREEN));
            ledModule.play();
        });
        view.findViewById(R.id.led_blue_on).setOnClickListener(view13 ->
        {
            configureChannel(ledModule.editPattern(Color.BLUE));
            ledModule.play();
        });

    }

    private void setupDfuDialog(AlertDialog.Builder builder, int msgResId)
    {
        builder.setTitle(R.string.title_firmware_update)
                .setPositiveButton(R.string.label_yes, (dialogInterface, i) -> fragBus.initiateDfu(null))
                .setNegativeButton(R.string.label_no, null)
                .setCancelable(false)
                .setMessage(msgResId)
                .show();
    }

    @Override
    protected void boardReady() throws UnsupportedModuleException
    {
        setupFragment(getView());
    }

    @Override
    protected void fillHelpOptionAdapter(HelpOptionAdapter adapter)
    {

    }

    @Override
    public void reconnected()
    {
        setupFragment(getView());
    }

    private void configureChannel(Led.PatternEditor editor)
    {
        final short PULSE_WIDTH = 1000;
        editor.highIntensity((byte) 31).lowIntensity((byte) 31)
                .highTime((short) (PULSE_WIDTH >> 1)).pulseDuration(PULSE_WIDTH)
                .repeatCount((byte) -1).commit();
    }

    private void setupFragment(final View v)
    {
        final String METABOOT_WARNING_TAG = "metaboot_warning_tag";


    }
}
