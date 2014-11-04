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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.vadim.nakatani.R;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CalibrationActivity extends Activity implements View.OnClickListener{
    //TODO add database support
    private final String TAG = CalibrationActivity.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.vadim.nakatani.activitys.CalibrationActivity";

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

    private static final String MEASUREMENT_POSITION_HIGH = "high";
    private static final String MEASUREMENT_POSITION_LOW = "low";

    private static final int HANDLER_MSG_ENABLE_BUTTON = 0;
    private static final int HANDLER_MSG_DISABLE_BUTTON = 1;
    private static final int HANDLER_MSG_START_PROGRESS_BAR = 2;
    private static final int HANDLER_MSG_STOP_PROGRESS_BAR = 3;
    private static final int HANDLER_MSG_UPDATE_MEASUREMENT_TEXT_FIELDS = 4;
    private static final int HANDLER_MSG_ENABLE_RADIO_BUTTONS = 5;
    private static final int HANDLER_MSG_DISABLE_RADIO_BUTTONS = 6;

    /**
     * KEY to add arguments in bundle
     */
    private static final String IS_CONNECTED_KEY = "is connected";


    private AtomicBoolean permissionDialogAnswered = new AtomicBoolean(false);
    private volatile boolean isConnected;
    private AtomicBoolean isHiMessageWasReceaved = new AtomicBoolean(false);
    private String measurementPosition = MEASUREMENT_POSITION_LOW;

    private int highValue;
    private int lowValue;
    private List<Integer> measuredValues = new ArrayList<Integer>();

    RadioButton radioButtonHi;
    RadioButton radioButtonLow;
    Button buttonStart;
    TextView textHiValueNew;
    TextView textHiValueOld;
    TextView textLowValueNew;
    TextView textLowValueOld;
    ProgressBar progressBar;

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
                    mDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (mDevice != null) {
                            permissionDialogAnswered.set(true);
                            openDevicePort();
                        }
                    } else {
                        Toast.makeText(context, "permission denied for device " + mDevice, Toast.LENGTH_LONG).show();
                        permissionDialogAnswered.set(true);
                    }
                }
            }
        }
    };

    private boolean progresBarDrawerState = false;
    private Thread progresBarDrawer;

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
                        measurementPosition = MEASUREMENT_POSITION_HIGH;
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

        progressBar = (ProgressBar) findViewById(R.id.progressBarCallibration);
        progressBar.setMax(100);

//        startProgressBar(true);

        mDumpTextView = (TextView) findViewById(R.id.textViewdfdf);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        /**
         * initialize USB manager
         */
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        h = new Handler(){
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                switch (msg.what) {
                    case HANDLER_MSG_ENABLE_BUTTON:
                        buttonStart.setEnabled(true);
                        break;
                    case HANDLER_MSG_DISABLE_BUTTON:
                        buttonStart.setEnabled(false);
                        break;
                    case HANDLER_MSG_START_PROGRESS_BAR:
                        startProgressBar(true);
                        break;
                    case HANDLER_MSG_STOP_PROGRESS_BAR:
                        startProgressBar(false);
                        break;
                    case HANDLER_MSG_UPDATE_MEASUREMENT_TEXT_FIELDS:
                        updateMeasurementFields();
                        break;
                    case HANDLER_MSG_ENABLE_RADIO_BUTTONS:
                        enableRadioButtons(true);
                        break;
                    case HANDLER_MSG_DISABLE_RADIO_BUTTONS:
                        enableRadioButtons(false);
                        break;
                    default:
                        break;
                }
            }
        };
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

                //TODO process if device will be detach

                Thread threadSendMessage = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        h.sendEmptyMessage(HANDLER_MSG_START_PROGRESS_BAR);
                        h.sendEmptyMessage(HANDLER_MSG_DISABLE_BUTTON);
                        h.sendEmptyMessage(HANDLER_MSG_DISABLE_RADIO_BUTTONS);

                        if (!isHiMessageWasReceaved.get()) {
                            Thread threadSendHiMessage = new Thread(new MuRunnable(permissionDialogAnswered) {
                                @Override
                                public void run() {
                                    while (true) {
                                        if (permissionDialogAnswered.get() == true) break;
                                    }

                                    int i = 0;
                                    while (i < 5) {
                                        if (isHiMessageWasReceaved.get() == true) break;
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(100);
                                        } catch (InterruptedException e) {
        //                                e.printStackTrace();
                                        }
                                        if (isConnected) {
                                            h.post(sendHiMessage);
                                        }
                                        i++;
                                    }
                                }
                            });
                            threadSendHiMessage.start();

                            if (threadSendHiMessage.isAlive()) {
                                try{
                                    threadSendHiMessage.join();
                                }catch(InterruptedException e){}
                            }
                        }


                        //TODO maybe add new thread/process in this to interrupt this both if they will not answer for a long time and enable buttons end etc.

                        Thread threadSendMeasurementMessage = new Thread(new MuRunnable(isHiMessageWasReceaved) {
                            @Override
                            public void run() {
                                while (true) {
                                    if (isHiMessageWasReceaved.get() == true) break;
                                }

                                int i = 0;
                                while (i < 20) {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(100);
                                    } catch (InterruptedException e) {
        //                                e.printStackTrace();
                                    }
                                    if (isConnected) {
                                        h.post(sendMeasurementMessage);
                                    }
                                    i++;
                                }

                                h.sendEmptyMessage(HANDLER_MSG_STOP_PROGRESS_BAR);
                                h.sendEmptyMessage(HANDLER_MSG_ENABLE_BUTTON);
                                h.sendEmptyMessage(HANDLER_MSG_ENABLE_RADIO_BUTTONS);
                                h.sendEmptyMessage(HANDLER_MSG_UPDATE_MEASUREMENT_TEXT_FIELDS);
                            }
                        });
                        threadSendMeasurementMessage.start();
                    }
                });
                threadSendMessage.start();
                break;
            default:
                break;
        }
    }

    private class MuRunnable implements Runnable{
        AtomicBoolean permissionDialogAnswered;
//        AtomicBoolean isHiMessageWasReceaved;

        public MuRunnable(AtomicBoolean ab1/*, AtomicBoolean ab2*/) {
            this.permissionDialogAnswered = ab1;
//            this.isHiMessageWasReceaved = ab2;
        }

        @Override
        public void run() {

        }
    }

    @Override
    protected void onDestroy() {
        if (mPort != null) {
            try {
                mPort.close();
                //TODO reset progress bar
                progressBar.setProgress(0);
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
//        savedInstanceState.putBoolean(IS_CONNECTED_KEY, isConnected);
        //TODO add saving viewText value
        //TODO add saving measured values int[]
        //TODO add saving hi or low measure position of radioButton

        /* Call at the end*/
        super.onSaveInstanceState(savedInstanceState);
    }

    private synchronized void updateReceivedData(byte[] data) {
        isHiMessageWasReceaved.set(true);
        final String msg = new String(data);
//        mDumpTextView.append(msg + "\n\n");
        /**
         * process hi message
         */
        if (msg.indexOf(START_RECEIVED_MESSAGE_HI) == 0) {
            if (msg.equals("I44V_IPDMini v1.6 Copyright(c)2004-2005 Valeriy V. Vishnyak, MEDINTECHO")) isHiMessageWasReceaved.set(true);
            mDumpTextView.append(msg + "\n\n");
        }
        /**
         * process measurement message
         */
        if (msg.indexOf(START_RECEIVED_MESSAGE_MEASURED_VALUE) == 0) {
            int g_index = msg.indexOf(START_RECEIVED_MESSAGE_MEASURED_VALUE);
            int o_index = msg.lastIndexOf(END_RECEIVED_MESSAGE);
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
                    measuredValues.add(value);
//                    textLowValueNew.setText(String.valueOf(value));
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
            if (mPort != null) {
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
        }
    };

//    private void sendMeasurementMessage() {
    Runnable sendMeasurementMessage = new Runnable() {
        @Override
        public void run() {
            if (mPort != null) {
                try {
                    mPort.write(MESSAGE_GET_MEASURED_VALUE, 1000);
                    int len = mPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
                    final byte[] data = new byte[len];
                    mReadBuffer.get(data, 0, len);
                    updateReceivedData(data);
                    mReadBuffer.clear();
                } catch (IOException e) {
                    try {
                        mPort.close();
                    } catch (IOException e2) {
                        // Ignore.
                    }
                    mPort = null;
                }
            }
        }
    };

    private void startProgressBar(boolean action) {
        if (action) {
            progresBarDrawer = new Thread(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(50);
                            progressBar.setProgress(i);
                            i += 3;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            progressBar.setProgress(0);
                            break;
                        }
                        if (i > 100) i = 0;
                    }
                }
            });
            progresBarDrawer.start();
        } else {
            progresBarDrawer.interrupt();
        }
        progresBarDrawerState = action;
    }

    private void updateMeasurementFields() {
        if ( measurementPosition.equals(MEASUREMENT_POSITION_LOW)) {
            int val = 0;
            for (Integer i : measuredValues) {
                val = (val < i)?i:val;
            }
            textLowValueNew.setText(String.valueOf(val));
            textLowValueOld.setText(String.valueOf(val));
        }
        if ( measurementPosition.equals(MEASUREMENT_POSITION_HIGH)) {
            int val = 0;
            for (Integer i : measuredValues) {
                val = (val < i)?i:val;
            }
            textHiValueNew.setText(String.valueOf(val));
            textHiValueOld.setText(String.valueOf(val));
        }
    }

    private void enableRadioButtons(boolean action) {
        radioButtonLow.setEnabled(action);
        radioButtonHi.setEnabled(action);
    }
}
