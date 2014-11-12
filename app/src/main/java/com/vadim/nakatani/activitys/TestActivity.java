package com.vadim.nakatani.activitys;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.concurrent.atomic.AtomicInteger;

public class TestActivity extends Activity implements View.OnClickListener{
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

    /**
     * messages for handler
     */
    private static final int HANDLER_MSG_ENABLE_BUTTON_START = 0;
    private static final int HANDLER_MSG_DISABLE_BUTTON_START = 1;
    private static final int HANDLER_UPDATE_DATA = 2;

    /**
     * KEY to add arguments in bundle
     */


    private AtomicBoolean isWasInMeasurementProcess = new AtomicBoolean(false);
    private AtomicInteger permissionDialogAnswered = new AtomicInteger(0);
    private AtomicInteger isHiMessageWasReceaved = new AtomicInteger(0);
    private AtomicBoolean isConnected = new AtomicBoolean(false);

    //TODO in future read from db
    private int highCalibrationValue = 1018;
    private int lowCalibrationValue = 3;
    private AtomicInteger nowMeasuredValue = new AtomicInteger(0);;
    private AtomicInteger maxMeasuredValue = new AtomicInteger(0);;

    private Button buttonStart;
    private Button buttonStop;
    private Button buttonPrev;
    private Button buttonNext;
    private Button buttonReset;
    private Button buttonClear;
    private Button buttonCalibration;
    private Button buttonDone;

    private ProgressBar progressBarValueNow;
    private ProgressBar progressBarValueMax;

    private TextView textViewValueNow;
    private TextView textViewValueMax;

    private TextView textViewPatientName;
    private TextView textViewMeasurementDate;
    private TextView textViewMeasurementTime;

    private ImageView imageView;

    private EditText editText_H1_L;
    private EditText editText_H1_R;
    private EditText editText_H2_L;
    private EditText editText_H2_R;
    private EditText editText_H3_L;
    private EditText editText_H3_R;
    private EditText editText_H4_L;
    private EditText editText_H4_R;
    private EditText editText_H5_L;
    private EditText editText_H5_R;
    private EditText editText_H6_L;
    private EditText editText_H6_R;

    private EditText editText_F1_L;
    private EditText editText_F1_R;
    private EditText editText_F2_L;
    private EditText editText_F2_R;
    private EditText editText_F3_L;
    private EditText editText_F3_R;
    private EditText editText_F4_L;
    private EditText editText_F4_R;
    private EditText editText_F5_L;
    private EditText editText_F5_R;
    private EditText editText_F6_L;
    private EditText editText_F6_R;

    List<EditText> editTextPointsList = new ArrayList<EditText>();
    List<Drawable> imageSrcPointsList = new ArrayList<Drawable>();
    private AtomicInteger editTextPointIndex = new AtomicInteger(0);

    private Handler h;

    private static final int READ_WAIT_MILLIS = 200;
    private static final int BUFSIZ = 4096;
    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);

    private UsbSerialPort mPort;
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
        setContentView(R.layout.activity_test);

        Drawable h1 = getResources().getDrawable(R.drawable.h_1);
        Drawable h2 = getResources().getDrawable(R.drawable.h_2);
        Drawable h3 = getResources().getDrawable(R.drawable.h_3);
        Drawable f1 = getResources().getDrawable(R.drawable.f_1);
        Drawable f2 = getResources().getDrawable(R.drawable.f_2);
        Drawable f3 = getResources().getDrawable(R.drawable.f_3);
        /**
         * add drawable to list in order like points in their list
         */
        imageSrcPointsList.add(h1);
        imageSrcPointsList.add(h1);
        imageSrcPointsList.add(h1);
        imageSrcPointsList.add(h2);
        imageSrcPointsList.add(h2);
        imageSrcPointsList.add(h3);
        imageSrcPointsList.add(h1);
        imageSrcPointsList.add(h1);
        imageSrcPointsList.add(h1);
        imageSrcPointsList.add(h2);
        imageSrcPointsList.add(h2);
        imageSrcPointsList.add(h3);

        imageSrcPointsList.add(f1);
        imageSrcPointsList.add(f2);
        imageSrcPointsList.add(f1);
        imageSrcPointsList.add(f3);
        imageSrcPointsList.add(f3);
        imageSrcPointsList.add(f2);
        imageSrcPointsList.add(f1);
        imageSrcPointsList.add(f2);
        imageSrcPointsList.add(f1);
        imageSrcPointsList.add(f3);
        imageSrcPointsList.add(f3);
        imageSrcPointsList.add(f2);

        imageView = (ImageView) findViewById(R.id.imageViewTest);
        imageView.setImageDrawable(imageSrcPointsList.get(editTextPointIndex.get()));

        textViewValueNow = (TextView) findViewById(R.id.textViewValueNow);
        textViewValueMax = (TextView) findViewById(R.id.textViewValueMax);

        textViewPatientName = (TextView) findViewById(R.id.textViewValueMax);
        textViewMeasurementDate = (TextView) findViewById(R.id.textViewValueMax);
        textViewMeasurementTime = (TextView) findViewById(R.id.textViewValueMax);

        editText_H1_L = (EditText) findViewById(R.id.editText_H1_L);
        editText_H2_L = (EditText) findViewById(R.id.editText_H2_L);
        editText_H3_L = (EditText) findViewById(R.id.editText_H3_L);
        editText_H4_L = (EditText) findViewById(R.id.editText_H4_L);
        editText_H5_L = (EditText) findViewById(R.id.editText_H5_L);
        editText_H6_L = (EditText) findViewById(R.id.editText_H6_L);
        editText_H1_R = (EditText) findViewById(R.id.editText_H1_R);
        editText_H2_R = (EditText) findViewById(R.id.editText_H2_R);
        editText_H3_R = (EditText) findViewById(R.id.editText_H3_R);
        editText_H4_R = (EditText) findViewById(R.id.editText_H4_R);
        editText_H5_R = (EditText) findViewById(R.id.editText_H5_R);
        editText_H6_R = (EditText) findViewById(R.id.editText_H6_R);

        editText_F1_L = (EditText) findViewById(R.id.editText_F1_L);
        editText_F2_L = (EditText) findViewById(R.id.editText_F2_L);
        editText_F3_L = (EditText) findViewById(R.id.editText_F3_L);
        editText_F4_L = (EditText) findViewById(R.id.editText_F4_L);
        editText_F5_L = (EditText) findViewById(R.id.editText_F5_L);
        editText_F6_L = (EditText) findViewById(R.id.editText_F6_L);
        editText_F1_R = (EditText) findViewById(R.id.editText_F1_R);
        editText_F2_R = (EditText) findViewById(R.id.editText_F2_R);
        editText_F3_R = (EditText) findViewById(R.id.editText_F3_R);
        editText_F4_R = (EditText) findViewById(R.id.editText_F4_R);
        editText_F5_R = (EditText) findViewById(R.id.editText_F5_R);
        editText_F6_R = (EditText) findViewById(R.id.editText_F6_R);

        editTextPointsList.add(editText_H1_L);
        editTextPointsList.add(editText_H2_L);
        editTextPointsList.add(editText_H3_L);
        editTextPointsList.add(editText_H4_L);
        editTextPointsList.add(editText_H5_L);
        editTextPointsList.add(editText_H6_L);
        editTextPointsList.add(editText_H1_R);
        editTextPointsList.add(editText_H2_R);
        editTextPointsList.add(editText_H3_R);
        editTextPointsList.add(editText_H4_R);
        editTextPointsList.add(editText_H5_R);
        editTextPointsList.add(editText_H6_R);
        editTextPointsList.add(editText_F1_L);
        editTextPointsList.add(editText_F2_L);
        editTextPointsList.add(editText_F3_L);
        editTextPointsList.add(editText_F4_L);
        editTextPointsList.add(editText_F5_L);
        editTextPointsList.add(editText_F6_L);
        editTextPointsList.add(editText_F1_R);
        editTextPointsList.add(editText_F2_R);
        editTextPointsList.add(editText_F3_R);
        editTextPointsList.add(editText_F4_R);
        editTextPointsList.add(editText_F5_R);
        editTextPointsList.add(editText_F6_R);
        //TODO add click listener to edit text for changing index of point when user will manual click in field

        buttonStart = (Button) findViewById(R.id.button_test_start);
        buttonStart.setOnClickListener(this);
        buttonStop = (Button) findViewById(R.id.button_test_stop);
        buttonStop.setOnClickListener(this);
        buttonNext = (Button) findViewById(R.id.button_test_next);
        buttonNext.setOnClickListener(this);
        buttonPrev = (Button) findViewById(R.id.button_test_prev);
        buttonPrev.setOnClickListener(this);
        buttonReset = (Button) findViewById(R.id.button_test_reset);
        buttonReset.setOnClickListener(this);
        buttonClear = (Button) findViewById(R.id.button_test_clear);
        buttonClear.setOnClickListener(this);
        buttonCalibration = (Button) findViewById(R.id.button_test_calibration);
        buttonCalibration.setOnClickListener(this);
        buttonDone = (Button) findViewById(R.id.button_test_done);
        buttonDone.setOnClickListener(this);

        progressBarValueNow = (ProgressBar) findViewById(R.id.progressBarTestNow);
        progressBarValueMax = (ProgressBar) findViewById(R.id.progressBarTestMax);
        progressBarValueNow.setMax(100);
        progressBarValueMax.setMax(100);

        /**
         * initialize USB manager
         */
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case HANDLER_MSG_ENABLE_BUTTON_START:
                        buttonStart.setEnabled(true);
                        buttonStop.setEnabled(false);
                        break;
                    case HANDLER_MSG_DISABLE_BUTTON_START:
                        buttonStart.setEnabled(false);
                        buttonStop.setEnabled(true);
                        break;
                    case HANDLER_UPDATE_DATA:
                        updateMeasurementFields();
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
        getMenuInflater().inflate(R.menu.test, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_test_start:
                processStartButtonClick();
                break;
            case R.id.button_test_stop:
                processButtonStopClick();
                break;
            case R.id.button_test_next:
                processButtonNextClick();
                break;
            case R.id.button_test_prev:
                processButtonPrevClick();
                break;
            case R.id.button_test_reset:
                processButtonResetClick();
                break;
            case R.id.button_test_clear:
                processButtonClearClick();
                break;
            case R.id.button_test_calibration:
                processButtonCalibrationClick();
                break;
            case R.id.button_test_done:
                processButtonDoneClick();
                break;
            default:
                break;
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
//        savedInstanceState.putBoolean(IS_WAS_IN_MEASUREMENT_PROCESS, isConnected);
        /* Call at the end*/
        super.onSaveInstanceState(savedInstanceState);
    }

    private void processStartButtonClick() {
        if (isConnected.get() == false) {
            findUSBDevices();
        }

        Thread threadSendMessage = new Thread(new MyRunnable(permissionDialogAnswered) {
            @Override
            public void run() {
                while (true) {
                    if (permissionDialogAnswered.get() == 1 || permissionDialogAnswered.get() == 2) break;
                }
                if (permissionDialogAnswered.get() == 1) {
                    h.sendEmptyMessage(HANDLER_MSG_DISABLE_BUTTON_START);

                    if (isHiMessageWasReceaved.get() == 0) {
                        final Thread threadSendHiMessage = new Thread(new MyRunnable(isHiMessageWasReceaved) {
                            @Override
                            public void run() {
                                int i = 0;
                                while (i < 5) {
                                    if (isHiMessageWasReceaved.get() == 1) break;
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(100);
                                        if (isConnected.get() == true) {
                                            sendHiMessage();
                                        }
                                        i++;
                                    } catch (InterruptedException e) {
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

                    if (isHiMessageWasReceaved.get() == 1) {
                        isWasInMeasurementProcess.set(true);

                        Thread threadSendMeasurementMessage = new Thread(new MyRunnable(isHiMessageWasReceaved) {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        if (isWasInMeasurementProcess.get() == false) break;
                                        boolean ic = isConnected.get();
                                        if (!ic){
                                            showToast("Device is not connected");
                                            isWasInMeasurementProcess.set(false);
                                            break;
                                        }
                                        sendMeasurementMessage();
                                        sendProbeButtonStateMessage();
                                        sendEndPackageMessage();
                                        h.sendEmptyMessage(HANDLER_UPDATE_DATA);
                                        TimeUnit.MILLISECONDS.sleep(100);
                                    }
                                } catch (InterruptedException e) {
                                }
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
                        showToast("Device is not connected");
                        isWasInMeasurementProcess.set(false);
                        resetData();
                    }
                    h.sendEmptyMessage(HANDLER_MSG_ENABLE_BUTTON_START);

                }
            }
        });
        if (mPort != null) {
            threadSendMessage.start();
        } else {
            showToast("Device is not connected");
            resetData();
            isConnected.set(false);
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
            isWasInMeasurementProcess.set(false);
        }
    }

    private class MyRunnable implements Runnable {
        AtomicInteger permissionDialogAnswered;
        public MyRunnable(AtomicInteger ai) { this.permissionDialogAnswered = ai; }
        @Override
        public void run() {}
    }

    private void processButtonStopClick() {
        isWasInMeasurementProcess.set(false);
        textViewValueNow.setText("0");
        progressBarValueNow.setProgress(0);
    }

    private void processButtonNextClick() {
        if (editTextPointIndex.get() < 23) editTextPointIndex.set(editTextPointIndex.get() + 1);
        imageView.setImageDrawable(imageSrcPointsList.get(editTextPointIndex.get()));
        EditText et = editTextPointsList.get(editTextPointIndex.get());
        et.requestFocus();
        resetData();
        if (editTextPointIndex.get() == 23) {
            buttonNext.setEnabled(false);
            buttonPrev.setEnabled(true);
        }
    }

    private void processButtonPrevClick() {
        if (editTextPointIndex.get() > 0) editTextPointIndex.set(editTextPointIndex.get() - 1);
        imageView.setImageDrawable(imageSrcPointsList.get(editTextPointIndex.get()));
        EditText et = editTextPointsList.get(editTextPointIndex.get());
        et.requestFocus();
        resetData();
        if (editTextPointIndex.get() == 0) {
            buttonNext.setEnabled(true);
            buttonPrev.setEnabled(false);
        }
    }

    private void processButtonResetClick() {
        EditText et = editTextPointsList.get(editTextPointIndex.get());
        et.setText("0");
        resetData();
    }

    private void processButtonClearClick() {
        for (EditText e : editTextPointsList) e.setText("0");
        resetData();
    }

    private void processButtonCalibrationClick() {

    }

    private void processButtonDoneClick() {

    }

    private void resetData() {
        progressBarValueNow.setProgress(0);
        progressBarValueMax.setProgress(0);
        textViewValueNow.setText("0");
        textViewValueMax.setText("0");
        nowMeasuredValue.set(0);
        maxMeasuredValue.set(0);
    }

    private synchronized void processReceivedData(byte[] data) {
        final String msg = new String(data);
        boolean getData = false;
        /**
         * process hi message
         */
        if (msg.indexOf(START_RECEIVED_MESSAGE_HI) == 0) {
            if (msg.startsWith("I44V_EPDMini v1.6 Copyright (c)2004-2005")) {
                isHiMessageWasReceaved.set(1);
                getData = true;
            }
        }
        /**
         * process measurement message
         */
        if (msg.indexOf(START_RECEIVED_MESSAGE_MEASURED_VALUE) == 0) {
            getData = true;
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
                    value = (int) (((double)(value - lowCalibrationValue) / (highCalibrationValue - lowCalibrationValue)) * 100);
                    nowMeasuredValue.set(value);
                    if (maxMeasuredValue.get() < value) maxMeasuredValue.set(value);
                }
            }
        }
        /**
         * process probe button info
         */
        if (msg.indexOf(START_RECEIVED_MESSAGE_BUTTON_STATE) == 0) {
            getData = true;
        }
        /**
         * process end package message
         */
        if (msg.indexOf(START_RECEIVED_MESSAGE_END_PACKAGE_TRANSMIT) == 0) {
            getData = true;
        }
        if (!getData) isConnected.set(false);
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
                if (isConnected.get() == false) {
                    mPort = usp;
                    mDevice = device;

                    mUsbManager.requestPermission(mDevice, mPermissionIntent);
                }
            }
        }
        if (!findDevice) {
            isConnected.set(false);
        }
    }

    private void openDevicePort() {
        mConnection = mUsbManager.openDevice(mDevice);
        try {
            mPort.open(mConnection);
            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            isConnected.set(true);
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
        if (mPort != null) {
            try {
                mPort.write(MESSAGE_HI, 1000);
                int len = mPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
                final byte[] data = new byte[len];
                mReadBuffer.get(data, 0, len);
                processReceivedData(data);
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
            isConnected.set(false);
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
            isWasInMeasurementProcess.set(false);
        }
    }

    private void sendMeasurementMessage() {
        if (mPort != null) {
            try {
                mPort.write(MESSAGE_GET_MEASURED_VALUE, 1000);
                int len = mPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
                final byte[] data = new byte[len];
                mReadBuffer.get(data, 0, len);
                processReceivedData(data);
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
            isConnected.set(false);
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
            isWasInMeasurementProcess.set(false);
        }
    }

    private void sendProbeButtonStateMessage() {
        if (mPort != null) {
            try {
                mPort.write(MESSAGE_GET_PROBE_BUTTON_STATE, 1000);
                int len = mPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
                final byte[] data = new byte[len];
                mReadBuffer.get(data, 0, len);
                processReceivedData(data);
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
            isConnected.set(false);
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
            isWasInMeasurementProcess.set(false);
        }
    }

    private void sendEndPackageMessage() {
        if (mPort != null) {
            try {
                mPort.write(MESSAGE_END_PACKAGE_TRANSMIT, 1000);
                int len = mPort.read(mReadBuffer.array(), READ_WAIT_MILLIS);
                final byte[] data = new byte[len];
                mReadBuffer.get(data, 0, len);
                processReceivedData(data);
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
            isConnected.set(false);
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
            isWasInMeasurementProcess.set(false);
        }
    }

    private void updateMeasurementFields() {
        if (mPort != null) {
            textViewValueNow.setText(String.valueOf(nowMeasuredValue.get()));
            textViewValueMax.setText(String.valueOf(maxMeasuredValue.get()));
            progressBarValueNow.setProgress(nowMeasuredValue.get());
            progressBarValueMax.setProgress(maxMeasuredValue.get());
            EditText ed = editTextPointsList.get(editTextPointIndex.get());
            ed.setText(String.valueOf(maxMeasuredValue.get()));
        } else {
            showToast("Device is not connected");
            resetData();
            isConnected.set(false);
            isHiMessageWasReceaved.set(0);
            permissionDialogAnswered.set(0);
            isWasInMeasurementProcess.set(false);
        }
    }

    private void showToast(final String msg)
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(TestActivity.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout linearLayout = (LinearLayout) toast.getView();
                TextView messageTextView = (TextView) linearLayout.getChildAt(0);
                messageTextView.setTextSize(25);
                toast.show();

            }
        });
    }
}
