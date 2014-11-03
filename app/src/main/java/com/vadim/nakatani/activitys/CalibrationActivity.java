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
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.vadim.nakatani.R;

import java.io.IOException;
import java.nio.ByteBuffer;
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

    private static final byte[] MESSAGE_HI = new byte[]{(byte) 'I'};
    private static final byte[] MESSAGE_GET_MEASURED_VALUE = new byte[]{(byte) 'G'};
    private static final byte[] MESSAGE_GET_PROBE_BUTTON_STATE = new byte[]{(byte) 'B'};
    private static final byte[] MESSAGE_END_PACKAGE_TRANSMIT = new byte[]{(byte) 'A'};
    private static final String START_RECEIVED_MESSAGE_HI = "I";
    private static final String START_RECEIVED_MESSAGE_MEASURED_VALUE = "G";
    private static final String START_RECEIVED_MESSAGE_BUTTON_STATE = "B";
    private static final String START_RECEIVED_MESSAGE_END_PACKAGE_TRANSMIT = "A";
    private static final String END_RECEIVED_MESSAGE = "O";

    private static final String MEASUREMENT_POSITION_HI = "high";
    private static final String MEASUREMENT_POSITION_LOW = "low";

    /**
     * KEY to add arguments in bundle
     */
    private static final String IS_CONNECTED_KEY = "is connected";

    private volatile boolean isConnected;
    private boolean isHiMessageWasSend;
    private boolean isVerificatedHiMessage;
    private String measurementPosition = MEASUREMENT_POSITION_LOW;
    private int[] measuredValues = new int[5];

    RadioButton radioButtonHi;
    RadioButton radioButtonLow;
    Button buttonStart;
    TextView textHiValueNew;
    TextView textHiValueOld;
    TextView textLowValueNew;
    TextView textLowValueOld;

    private TextView mDumpTextView;
    private ScrollView mScrollView;

    private Handler h;

    private static final int READ_WAIT_MILLIS = 200;
    private static final int BUFSIZ = 4096;
    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);

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
                            openDevicePort();
                        }
                    }
                    else {
                        Toast.makeText(context, "permission denied for device " + mDevice, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    //TODO delete this block of code
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

        /**
         * restore arguments from bundle
         */
        isConnected = ((savedInstanceState != null) && savedInstanceState.containsKey(IS_CONNECTED_KEY))?savedInstanceState.getBoolean(IS_CONNECTED_KEY):false;

        /**
         * initialize UI variables
         */
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
                        measurementPosition = MEASUREMENT_POSITION_HI;
                        break;
                    case R.id.radio_button_calibration_low:
                        radioButtonHi.setChecked(false);
                        radioButtonLow.setChecked(true);
                        measurementPosition = MEASUREMENT_POSITION_LOW;
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

        /**
         * initialize USB manager
         */
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        h = new Handler();
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
                if (!isConnected) {
                    findUSBDevices();
                }

//                if (isConnected && !isHiMessageWasSend) {
//                    if (mPort != null) {
//                        try {
//                            int i = 0;
//                            while ( i < 10) {
//                                sendHiMessage();
//                                TimeUnit.MILLISECONDS.sleep(400);
//                                if (isHiMessageWasSend) i = 100;
//                                i++;
//                            }
//                            if (!isHiMessageWasSend) {
//                                //TODO here must be dialog
//                                Toast.makeText(getApplicationContext(), "Не получилось подключится к прибору + \n" +
//                                        "Проверьте подключение", Toast.LENGTH_SHORT).show();
//                                isConnected = false;
//                            }
//                        } catch (InterruptedException e) {
////                            e.printStackTrace();
//                        }
//                    }else {
//                        isConnected = false;
//                    }
//                }

//                buttonStart.setEnabled(false);
                //TODO add check mPort != null else isConnected = false
                //TODO add check if port == null here or in sendMeasurementMessage() method;
                //TODO change this on handler/OnUI......run to use buttonStart.setEnabled(true) in the end of run;
                Thread threadSendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int i = 0;
                        //TODO add some check if to long no any answer
                        while (i < 50) {
                            if (!isConnected) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(100);
                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
                                }
                                i++;
                            }
                            if (isConnected) break;
                        }
                        i = 0;
                        while (i < 5) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(100);
                            } catch (InterruptedException e) {
//                                e.printStackTrace();
                            }
                            if (isConnected) {
//                                sendHiMessage();
                                h.post(sendHiMessage);
                            }
                            i++;
                        }
                    }
                });
                threadSendMessage.start();
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        /* Saving variables*/
        savedInstanceState.putBoolean(IS_CONNECTED_KEY, isConnected);
        //TODO add saving viewText value
        //TODO add saving measured values int[]
        //TODO add saving hi or low measure position of radioButton
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
//        stopIoManager();
//        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        //TODO add hi message process
        //TODO if hi message - set isConnected true
        //TODO change hardcoded values onto variables
//        isConnected = true;
        isHiMessageWasSend = true;
        final String msg = new String(data);
        mDumpTextView.append(msg + "\n\n");
        if (msg.indexOf("G") == 0) {
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
//                    mDumpTextView.append(String.valueOf(value) + "\n\n");
                }
            }
        }
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
    }

    private void findUSBDevices() {
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
                if (!isConnected) {
                    mPort = usp;
                    mDriver = driver;
                    mDevice = device;

                    mUsbManager.requestPermission(mDevice, mPermissionIntent);
                }
            }
        }
        if (!findDevice) {
            isConnected = false;
        }
    }

    private void openDevicePort() {
        mConnection = mUsbManager.openDevice(mDevice);
        try {
            mPort.open(mConnection);
            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            isConnected = true;
        }catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            try {
                mPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            mPort = null;
        }
    }

//    private void sendHiMessage() {
    Runnable sendHiMessage = new Runnable() {
        @Override
        public void run() {
            try {
                mPort.write(MESSAGE_HI,1000);
                int len = mPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
                final byte[] data = new byte[len];
                mReadBuffer.get(data, 0, len);
                updateReceivedData(data);
                mReadBuffer.clear();
            }catch (IOException e) {
                try {
                    mPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mPort = null;
            }
        }
    };
//        try {
//            mPort.write(MESSAGE_HI,1000);
//            int len = mPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
//            final byte[] data = new byte[len];
//            mReadBuffer.get(data, 0, len);
//            updateReceivedData(data);
//            mReadBuffer.clear();
//        }catch (IOException e) {
//            try {
//                mPort.close();
//            } catch (IOException e2) {
//                // Ignore.
//            }
//            mPort = null;
//        }
//    }

    private void sendMeasurementMessage() {
        try {
            mPort.write(MESSAGE_GET_MEASURED_VALUE, 1000);
        }catch (IOException e) {
            try {
                mPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            mPort = null;
        }
    }
}
