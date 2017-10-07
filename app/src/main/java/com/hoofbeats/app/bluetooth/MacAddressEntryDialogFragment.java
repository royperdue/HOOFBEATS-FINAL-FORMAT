package com.hoofbeats.app.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hoofbeats.app.R;

import java.util.Locale;


public class MacAddressEntryDialogFragment extends DialogFragment
{
    private BleScannerFragment.ScannerCommunicationBus commBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity owner= getActivity();
        if (!(owner instanceof BleScannerFragment.ScannerCommunicationBus)) {
            throw new ClassCastException(String.format(Locale.US, "%s %s", owner.toString(),
                    owner.getString(com.mbientlab.bletoolbox.scanner.R.string.error_scanner_listener)));
        }

        commBus= (BleScannerFragment.ScannerCommunicationBus) owner;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mac_address_entry, container);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        final EditText macAddressString= (EditText) view.findViewById(R.id.mac_address_string);
        final TextView invalidMacAddressText= (TextView) view.findViewById(R.id.invalid_mac_address_text);

        view.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BluetoothManager btManager= (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);

                try {
                    String macAddress= macAddressString.getText().toString().toUpperCase();
                    BluetoothDevice remoteDevice = btManager.getAdapter().getRemoteDevice(macAddress);
                    commBus.onDeviceSelected(remoteDevice);
                    dismiss();
                } catch (IllegalArgumentException ignored) {
                    invalidMacAddressText.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
