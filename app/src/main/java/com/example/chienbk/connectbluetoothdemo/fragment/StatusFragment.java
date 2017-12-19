package com.example.chienbk.connectbluetoothdemo.fragment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chienbk.connectbluetoothdemo.R;
import com.example.chienbk.connectbluetoothdemo.service.BluetoothService;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    public static final String TAG = StatusFragment.class.getSimpleName();

    private EditText editMessage;
    private ListView listMessage;
    private Button btnSendMessage;
    private TextView txtNameOfDevice, txtLabelOfDevice, txtRssival;

    private BluetoothService mService = null;
    private BluetoothDevice mDevice = null;
    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        initViews(view);
        initControls();
        initService();
        return view;
    }

    private void initControls() {
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                String message =  editMessage.getText().toString();
                byte[] value;
                try {
                    //todo something
                    value = message.getBytes("UTF-8");
                    //Send message to module
                    mService.writeRXCharacteristic(value);
                    //Update to view
                    //todo something
                    showMessage(message);
                    editMessage.setText("");

                }catch (UnsupportedEncodingException e){
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    private void initViews(View view) {
        /*String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        mService.connect(deviceAddress);*/
        editMessage = (EditText) view.findViewById(R.id.sendText);
        listMessage = (ListView) view.findViewById(R.id.listMessage);
        btnSendMessage = (Button) view.findViewById(R.id.sendButton);
        txtLabelOfDevice = (TextView) view.findViewById(R.id.deviceLabel);
        txtNameOfDevice = (TextView) view.findViewById(R.id.deviceName);
        txtRssival = (TextView) view.findViewById(R.id.rssival);
    }

    private void showMessage(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(BluetoothService.ACTION_GATT_CONNECTED)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        editMessage.setEnabled(true);
                        btnSendMessage.setEnabled(true);
                        txtNameOfDevice.setText(mDevice.getName()+ " - ready");
                        //listAdapter.add("["+currentDateTimeString+"] Connected to: "+ mDevice.getName());
                        //messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                        //mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(BluetoothService.ACTION_GATT_DISCONNECTED)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        editMessage.setEnabled(false);
                        btnSendMessage.setEnabled(false);
                        txtNameOfDevice.setText("Not Connected");
                        showMessage(currentDateTimeString + " - " + "UART disconnect");
                        //listAdapter.add("["+currentDateTimeString+"] Disconnected to: "+ mDevice.getName());
                        //mState = UART_PROFILE_DISCONNECTED;
                        mService.close();
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
            }
            //*********************//
            if (action.equals(BluetoothService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String text = new String(txValue, "UTF-8");
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            showMessage(currentDateTimeString + " - " + text);
                            //listAdapter.add("["+currentDateTimeString+"] RX: "+text);
                            //messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(BluetoothService.DEVICE_DOES_NOT_SUPPORT_UART)){
                showMessage("Device doesn't support UART. Disconnecting");
                mService.disconnect();
            }


        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        getActivity().unbindService(mServiceConnection);
        mService.stopSelf();
        mService= null;

    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    /**
     * UART service connected/disconnected
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = ((BluetoothService.LocalBinder) iBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                getActivity().finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    /**
     * The method used to init the service
     */
    private void initService() {
        Intent bindIntent = new Intent(getActivity(), BluetoothService.class);
        getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }
}
