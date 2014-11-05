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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import java.util.concurrent.atomic.AtomicInteger;

public class CalibrationActivity extends Activity implements View.OnClickListener {
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
    private static final int HANDLER_MSG_SHOW_DEV_NOT_CON_DIALOG = 7;

    /**
     * KEY to add arguments in bundle
     */
    private static final String RADIO_BUTTON_POSITION = "radio button position";
    private static final String VALUE_HIGH_OLD = "value high old";
    private static final String VALUE_HIGH_NEW = "value high new";
    private static final String VALUE_LOW_OLD = "value low old";
    private static final String VALUE_LOW_NEW = "value low new";
    //TODO Maybe in future will use this to save measurement process state
//    private static final String IS_WAS_IN_MEASUREMENT_PROCESS = "was measurement process";

    //TODO Maybe in future will use this to save measurement process state
//    private AtomicBoolean isWasInMeasurementProcess = new AtomicBoolean(false);
    private AtomicInteger permissionDialogAnswered = new AtomicInteger(0);
    private AtomicInteger isHiMessageWasReceaved = new AtomicInteger(0);
    private volatile boolean isConnected;
    private String measurementPosition = MEASUREMENT_POSITION_LOW;

    private int highCalibrationValue;
    private int lowCalibrationValue;
    private List<Integer> measuredValues = new ArrayList<Integer>();

    RadioButton radioButtonHi;
    RadioButton radioButtonLow;
    Button buttonStart;
    Button buttonOk;
    TextView textHiValueNew;
    TextView textHiValueOld;
    TextView textLowValueNew;
    TextView textLowValueOld;
    TextView textCalibrationHelp;
    ProgressBar progressBar;

    private boolean progresBarDrawerState = false;
    private Thread progresBarDrawer;

    private Handler h;

    private static final int READ_WAIT_MILLIS = 200;
    private static final int BUFSIZ = 4096;
    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);

    private UsbSerialPort mPort;
//    private UsbSerialDriver mDriver;
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
//                            permissionDialogAnswered.set(true);

                            openDevicePort();
                        }
                    } else {
//                        Toast.makeText(context, "permission denied for device " + mDevice, Toast.LENGTH_LONG).show();
                        showToast("Permission denied for device " + mDevice);
                        permissionDialogAnswered.set(2);
//                        permissionDialogAnswered.set(true);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        /**
         * restore arguments from bundle
         */
        measurementPosition = ((savedInstanceState != null) && savedInstanceState.containsKey(RADIO_BUTTON_POSITION)) ? savedInstanceState.getString(RADIO_BUTTON_POSITION) : MEASUREMENT_POSITION_LOW;
        String valueHighOld = ((savedInstanceState != null) && savedInstanceState.containsKey(VALUE_HIGH_OLD)) ? savedInstanceState.getString(VALUE_HIGH_OLD) : "0";
        String valueHighNew = ((savedInstanceState != null) && savedInstanceState.containsKey(VALUE_HIGH_NEW)) ? savedInstanceState.getString(VALUE_HIGH_NEW) : "0";
        String valueLowOld = ((savedInstanceState != null) && savedInstanceState.containsKey(VALUE_LOW_OLD)) ? savedInstanceState.getString(VALUE_LOW_OLD) : "0";
        String valueLowNew = ((savedInstanceState != null) && savedInstanceState.containsKey(VALUE_LOW_NEW)) ? savedInstanceState.getString(VALUE_LOW_NEW) : "0";

        /**
         * initialize UI variables
         */
        buttonStart = (Button) findViewById(R.id.button_calibration_start);
        buttonStart.setOnClickListener(this);
        buttonOk = (Button) findViewById(R.id.button_calibration_ok);
        buttonOk.setOnClickListener(this);
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
                        textCalibrationHelp.setText(R.string.calibration_text_help_high);
                        break;
                    case R.id.radio_button_calibration_low:
                        radioButtonHi.setChecked(false);
                        radioButtonLow.setChecked(true);
                        measurementPosition = MEASUREMENT_POSITION_LOW;
                        textCalibrationHelp.setText(R.string.calibration_text_help_low);
                        break;
                    default:
                        break;
                }
            }
        };
        radioButtonHi.setOnClickListener(radioListener);
        radioButtonLow.setOnClickListener(radioListener);

        textHiValueNew = (TextView) findViewById(R.id.textView_calibration_hi_value_new);
        textHiValueNew.setText(valueHighNew);

        textHiValueOld = (TextView) findViewById(R.id.textView_calibration_hi_value_old);
        textHiValueOld.setText(valueHighOld);

        textLowValueNew = (TextView) findViewById(R.id.textView_calibration_low_value_new);
        textLowValueNew.setText(valueLowNew);

        textLowValueOld = (TextView) findViewById(R.id.textView_calibration_low_value_old);
        textLowValueOld.setText(valueLowOld);

        textCalibrationHelp = (TextView) findViewById(R.id.textView_calibration_help);

        progressBar = (ProgressBar) findViewById(R.id.progressBarCallibration);
        progressBar.setMax(100);

        /**
         * initialize USB manager
         */
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        h = new Handler() {
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
//                    case HANDLER_MSG_SHOW_DEV_NOT_CON_DIALOG:
//                        makeToast("jkkh");
//                        break;
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
                processStartButtonClick();
                break;
            case R.id.button_calibration_ok:
                //TODO here will be saving to database
                break;
            default:
                break;
        }
    }

    private void processStartButtonClick() {
        if (!isConnected) {
            findUSBDevices();
        }

        Thread threadSendMessage = new Thread(new MyRunnable(permissionDialogAnswered) {
            @Override
            public void run() {
                while (true) {
                    if (permissionDialogAnswered.get() == 1 || permissionDialogAnswered.get() == 2) break;
                }
//                interrupter.start();
                if (permissionDialogAnswered.get() == 1) {
                    h.sendEmptyMessage(HANDLER_MSG_START_PROGRESS_BAR);
                    h.sendEmptyMessage(HANDLER_MSG_DISABLE_BUTTON);
                    h.sendEmptyMessage(HANDLER_MSG_DISABLE_RADIO_BUTTONS);

                    if (isHiMessageWasReceaved.get() == 0) {
                        final Thread threadSendHiMessage = new Thread(new MyRunnable(isHiMessageWasReceaved) {
                            @Override
                            public void run() {
//                                    while (true) {
//                                        if (permissionDialogAnswered.get() == 1) break;
//                                    }

                                int i = 0;
                                while (i < 5) {
                                    if (isHiMessageWasReceaved.get() == 1) break;
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(100);
                                        if (isConnected) {
                                            sendHiMessage();
                                        }
                                        i++;
                                    } catch (InterruptedException e) {
                                        //                                e.printStackTrace();
                                    }
                                }
                            }
                        });
                        threadSendHiMessage.start();

                        if (threadSendHiMessage.isAlive()) {
                            try {
                                threadSendHiMessage.join();
                            } catch (InterruptedException e) {
                            }
                        }
                    }

                    //TODO maybe add new thread/process in this to interrupt this both if they will not answer for a long time and enable buttons end etc.

                    if (isHiMessageWasReceaved.get() == 1) {
                        Thread threadSendMeasurementMessage = new Thread(new MyRunnable(isHiMessageWasReceaved) {
                            @Override
                            public void run() {
//                            while (true) {
//                                if (isHiMessageWasReceaved.get() == 1) break;
//                            }

                                int i = 0;
                                while (i < 35) {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(100);
                                    } catch (InterruptedException e) {
                                        //                                e.printStackTrace();
                                    }
                                    if (isConnected) {
                                        sendMeasurementMessage();
//                                        h.post(sendMeasurementMessage);
                                    }
                                    i++;
                                }

//                                h.sendEmptyMessage(HANDLER_MSG_STOP_PROGRESS_BAR);
//                                h.sendEmptyMessage(HANDLER_MSG_ENABLE_BUTTON);
//                                h.sendEmptyMessage(HANDLER_MSG_ENABLE_RADIO_BUTTONS);
                                h.sendEmptyMessage(HANDLER_MSG_UPDATE_MEASUREMENT_TEXT_FIELDS);
                            }
                        });
                        threadSendMeasurementMessage.start();

                        if (threadSendMeasurementMessage.isAlive()) {
                            try {
                                threadSendMeasurementMessage.join();
                            } catch (InterruptedException e) {
                            }
                        }
                    } else {
                        //TODO call dialog that device is not connected and isConnected = false and etc.
//                        h.obtainMessage(HANDLER_MSG_SHOW_DEV_NOT_CON_DIALOG);
                        showToast("Device is not connected");
                    }
                    h.sendEmptyMessage(HANDLER_MSG_STOP_PROGRESS_BAR);
                    h.sendEmptyMessage(HANDLER_MSG_ENABLE_BUTTON);
                    h.sendEmptyMessage(HANDLER_MSG_ENABLE_RADIO_BUTTONS);
                }
            }
        });
        if (mPort != null) {
            threadSendMessage.start();
        } else {
//            Toast.makeText(getApplicationContext(), "Device is not connected", Toast.LENGTH_SHORT).show();
            showToast("Device is not connected");
            isConnected = false;
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
        }
    }

    private class MyRunnable implements Runnable {
        AtomicInteger permissionDialogAnswered;
//        AtomicBoolean isHiMessageWasReceaved;

        public MyRunnable(AtomicInteger ai1/*, AtomicBoolean ab2*/) {
            this.permissionDialogAnswered = ai1;
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
            } catch (IOException e) {
                // Ignore.
            }
            mPort = null;
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        progressBar.setProgress(0);
        /* Saving variables*/
        savedInstanceState.putString(RADIO_BUTTON_POSITION, measurementPosition);
        savedInstanceState.putString(VALUE_HIGH_OLD, textHiValueOld.getText().toString());
        savedInstanceState.putString(VALUE_HIGH_NEW, textHiValueNew.getText().toString());
        savedInstanceState.putString(VALUE_LOW_OLD, textLowValueOld.getText().toString());
        savedInstanceState.putString(VALUE_LOW_NEW, textLowValueOld.getText().toString());
        //TODO Maybe in future will use this to save measurement process state
//        savedInstanceState.putBoolean(IS_WAS_IN_MEASUREMENT_PROCESS, isConnected);
        /* Call at the end*/
        super.onSaveInstanceState(savedInstanceState);
    }

    private synchronized void updateReceivedData(byte[] data) {
//        isHiMessageWasReceaved.set(true);
        final String msg = new String(data);
//        mDumpTextView.append(msg + "\n\n");
        /**
         * process hi message
         */
        if (msg.indexOf(START_RECEIVED_MESSAGE_HI) == 0) {
            if (msg.startsWith("I44V_EPDMini v1.6 Copyright (c)2004-2005")) isHiMessageWasReceaved.set(1);
//            mDumpTextView.append(msg + "\n\n");
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
                }
            }
        }
//        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
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
//                    mDriver = driver;
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
            permissionDialogAnswered.set(1);
        } catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            try {
                mPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            mPort = null;
        }
    }

    private void sendHiMessage() {
//    Runnable sendHiMessage = new Runnable() {
//        @Override
//        public void run() {
        if (mPort != null) {
            try {
                mPort.write(MESSAGE_HI, 1000);
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
        } else {
            isConnected = false;
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
        }
//        }
//    };
    }

        private void sendMeasurementMessage() {
//    Runnable sendMeasurementMessage = new Runnable() {
//        @Override
//        public void run() {
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
            } else {
                isConnected = false;
                isHiMessageWasReceaved.set(0);
                permissionDialogAnswered.set(0);
            }
//        }
//    };
    }

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
        if (mPort != null) {
            if (measuredValues.size() == 35) {
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
            } else {
                showToast("Device is not connected");
            }

        } else {
            isConnected = false;
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
        }
        measuredValues.clear();
    }

    private void enableRadioButtons(boolean action) {
        radioButtonLow.setEnabled(action);
        radioButtonHi.setEnabled(action);
    }

    private void showToast(final String msg)
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(CalibrationActivity.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout linearLayout = (LinearLayout) toast.getView();
                TextView messageTextView = (TextView) linearLayout.getChildAt(0);
                messageTextView.setTextSize(25);
                toast.show();

            }
        });
    }
}
