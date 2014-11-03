package com.vadim.nakatani.activitys;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.vadim.nakatani.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CalibrationActivity extends Activity implements View.OnClickListener{
    private final String TAG = CalibrationActivity.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private static final int VENDOR_ID = 4292;
    private static final int PRODUCT_ID = 60000;

    private static final String MESSAGE_HI = "I";
    private static final String MESSAGE_GET_MEASURED_VALUE = "G";
    private static final String MESSAGE_GET_PROBE_BUTTON_STATE = "B";
    private static final String MESSAGE_END_PACKAGE_TRANSMIT = "A";
    private static final String END_RECEIVED_MESSAGE = "O";

    private static final String IS_CONNECTED_KEY = "is connected";

    private volatile boolean isConnected;

    RadioButton radioButtonHi;
    RadioButton radioButtonLow;
    Button buttonStart;
    TextView textHiValueNew;
    TextView textHiValueOld;
    TextView textLowValueNew;
    TextView textLowValueOld;

    private TextView mDumpTextView;
    private ScrollView mScrollView;

    private UsbSerialPort mPort;
    private UsbSerialDriver mDriver;
    private UsbDevice mDevice;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mConnection;
    private PendingIntent mPermissionIntent;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    mDevice = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(mDevice != null){
                            onDeviceStateChange();
                            connectToDevice();
                        }
                    }
                    else {
                        Toast.makeText(context, "permission denied for device " + mDevice, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
            CalibrationActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CalibrationActivity.this.updateReceivedData(data);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        isConnected = ((savedInstanceState != null) && savedInstanceState.containsKey(IS_CONNECTED_KEY))?savedInstanceState.getBoolean(IS_CONNECTED_KEY):false;

        buttonStart = (Button) findViewById(R.id.button_calibration_start);
        buttonStart.setOnClickListener(this);
        radioButtonHi = (RadioButton) findViewById(R.id.radio_button_calibration_hi);
        radioButtonLow = (RadioButton) findViewById(R.id.radio_button_calibration_low);
        View.OnClickListener radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.radio_button_calibration_hi:
                        radioButtonHi.setChecked(true);
                        radioButtonLow.setChecked(false);
                        break;
                    case R.id.radio_button_calibration_low:
                        radioButtonHi.setChecked(false);
                        radioButtonLow.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        };
        radioButtonHi.setOnClickListener(radioListener);
        radioButtonLow.setOnClickListener(radioListener);

        textHiValueNew = (TextView) findViewById(R.id.textView_calibration_hi_value_new);
        textHiValueOld = (TextView) findViewById(R.id.textView_calibration_hi_value_old);
        textLowValueNew = (TextView) findViewById(R.id.textView_calibration_low_value_new);
        textLowValueOld = (TextView) findViewById(R.id.textView_calibration_low_value_old);

        mDumpTextView = (TextView) findViewById(R.id.textViewdfdf);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter("android.hardware.usb.action.ACTION_USB_DEVICE_ATTACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

//        if (isConnected) {
//            findUSBDevices();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calibration, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_calibration_start:
                findUSBDevices();

//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        int i = 0;
//                        while (i < 5) {
//                            try {
//                                TimeUnit.MILLISECONDS.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            sendMeasurementMessage();
//                            i++;
//                        }
//                    }
//                });
//                thread.start();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        stopIoManager();
        if (mPort != null) {
            try {
                mPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            mPort = null;
        }
        super.onDestroy();
    }

    /*@Override
    public void onPause() {
        super.onPause();
        stopIoManager();
        if (mPort != null) {
            try {
                mPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            mPort = null;
        }
        finish();
    }*/

    /*@Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + mPort);
        if (mPort == null) {
//            text.setText("No serial device.");
        } else {
            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
            if (connection == null) {
//                text.setText("Opening device failed");
                return;
            }

            try {
                mPort.open(connection);
//                sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
//                text.setText("Error opening device: " + e.getMessage());
                try {
                    mPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mPort = null;
                return;
            }
//            text.setText("Serial device: " + mPort.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }*/

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        /* Saving variables*/
        savedInstanceState.putBoolean(IS_CONNECTED_KEY, isConnected);
        //TODO add saving viewText value
        //TODO add saving measured values int[]
        /* Call at the end*/
        super.onSaveInstanceState(savedInstanceState);
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        final String msg = new String(data);
        if (msg.indexOf("G") == 0) {
            mDumpTextView.append("msg = " + msg + "\n\n");

            int g_index = msg.indexOf("G");
            int o_index = msg.indexOf("O");
            int msg_length = msg.length();

            String msgData = "";
            String msgDataCheck = "";

            if (g_index == 0 && o_index == 7) {
                msgData = new String(msg.substring(g_index + 1, o_index - 2));
                msgDataCheck = new String(msg.substring(o_index - 2, msg_length - 1));
            }
            if (msgData != "" && msgDataCheck != "") {
                String dataHiD = new String(msgData.substring(2, 4));
                String dataLoD = new String(msgData.substring(0, 2));
                msgData = dataHiD + dataLoD;

                int valueHiD = Integer.parseInt(dataHiD, 16);
                int valueLoD = Integer.parseInt(dataLoD, 16);
                int vc = Integer.parseInt(msgDataCheck, 16);
                byte valueCheck = (byte) vc;

                byte verification = (byte) (0 - valueHiD);
                verification = (byte) (verification - valueLoD);

                if (verification == valueCheck) {
                    int value = Integer.parseInt(msgData, 16);
                    textLowValueNew.setText(String.valueOf(value));
                }
            }
        }
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    private void findUSBDevices() {
        mDumpTextView.setText("findUsb");
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
        final List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

        final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            Log.d(getClass().getSimpleName(), String.format("+ %s: %s port%s", driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
            result.addAll(ports);
        }

        boolean findDevice = false;
        for (UsbSerialPort usp : result) {
            UsbSerialDriver driver = usp.getDriver();
            UsbDevice device = driver.getDevice();

            if (device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID) {
                findDevice = true;
//                if (!isConnected) {
                    mPort = usp;
                    mDriver = driver;
                    mDevice = device;

//                if (!isConnected) {
                    mUsbManager.requestPermission(mDevice, mPermissionIntent);
//                }
            }
        }
        if (!findDevice) {
            isConnected = false;
        }
    }

    private void connectToDevice() {
//        UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
//        if (mConnection == null) mConnection = mUsbManager.openDevice(mDevice);
        UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
        try {
            mPort.open(connection);
            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        }catch (IOException e) {
//            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
//                    mTitleTextView.setText("Error opening device: " + e.getMessage());
            try {
                mPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            mPort = null;
        }
        try {
//            mPort.open(connection);
//            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            byte[] bytesHello = new byte[] {(byte) 73};
            mPort.write(bytesHello, 1000);

            /*int len = mPort.read(mReadBuffer.array(), 200);
            byte[] data = new byte[len];
            mReadBuffer.get(data, 0, len);
            updateReceivedData(data);
            mReadBuffer.clear();*/
//            mPort.close();
        }catch (IOException e) {
//            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
//                    mTitleTextView.setText("Error opening device: " + e.getMessage());
            try {
                mPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            mPort = null;
        }

        //TODO change position of this code in future when we connected to dev we start startIoManager
//        startIoManager();
    }

    private void sendMeasurementMessage() {
//        UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
        try {
//            mPort.open(connection);
//            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            byte[] bytesHello = new byte[] {(byte) 0x47};
            mPort.write(bytesHello, 1000);

            /*int len = mPort.read(mReadBuffer.array(), 200);
            byte[] data = new byte[len];
            mReadBuffer.get(data, 0, len);
            updateReceivedData(data);
            mReadBuffer.clear();*/
//            mPort.close();
        }catch (IOException e) {
//            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
//                    mTitleTextView.setText("Error opening device: " + e.getMessage());
            try {
                mPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            mPort = null;
        }
    }
}
