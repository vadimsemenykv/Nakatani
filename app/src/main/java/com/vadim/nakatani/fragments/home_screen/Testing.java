package com.vadim.nakatani.fragments.home_screen;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vadim.nakatani.R;


/**
 *
 */
public class Testing extends Fragment implements View.OnClickListener {
    private final String TAG = Testing.class.getSimpleName();

    Button buttonTestPatient;
    Button buttonTestAnonymous;
    Button buttonCalibration;

//    private UsbSerialPort mPort;
//    private UsbSerialDriver mDriver;
//    private UsbDevice mDevice;
//    private UsbManager mUsbManager;
//    UsbDeviceConnection mConnection;
//
//    private static final int BUFSIZ = 4096;
////    private ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);
//
//    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
//    private SerialInputOutputManager mSerialIoManager;
//    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
//        @Override
//        public void onRunError(Exception e) {
//            Log.d(TAG, "Runner stopped.");
//        }
//
//        @Override
//        public void onNewData(final byte[] data) {
////            text.append("\nCall on new Data\n\n");
////            text.append(HexDump.dumpHexString(data));
////            updateReceivedData(data);
////            text.append("after call updateRecievedData");
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Testing.this.updateReceivedData(data);
//                }
//            });
////            AsyncTask asyncTask = new AsyncTask<Void, Void, Void>() {
////                @Override
////                protected void onPreExecute() {
////                    super.onPreExecute();
//////                    tvInfo.setText("Begin");
////                    text.append("call on preExec");
////                }
////
////                @Override
////                protected Void doInBackground(Void... params) {
//////                    try {
//////                        TimeUnit.SECONDS.sleep(2);
//////                    } catch (InterruptedException e) {
//////                        e.printStackTrace();
//////                    }
////                    return null;
////                }
////
////                @Override
////                protected void onPostExecute(Void result) {
////                    super.onPostExecute(result);
//////                    tvInfo.setText("End");
////                    text.append("call on postExec");
////                }
////            };
////            asyncTask.execute();
//        }
//    };

    //TODO if need add param description like this
    // @param cardFindAutoCompleteText Text that was entered in autocomplete field

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CardFile
     */
    public static Testing newInstance() {
        Testing fragment = new Testing();
        //TODO add, if need, arguments
//        Bundle args = new Bundle();
//        args.putString(SAVED_TEXT_KEY, cardFindAutoCompleteText);
//        fragment.setArguments(args);
        return fragment;
    }

    public Testing() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //TODO set arguments
//            mCardFindAutoCompleteText = getArguments().getString(SAVED_TEXT_KEY);
        }
//        mUsbManager = (UsbManager) getActivity().getApplicationContext().getSystemService(Context.USB_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_testing, container, false);

        buttonTestPatient = (Button) rootView.findViewById(R.id.button_testing_test_for_patient);
        buttonTestPatient.setOnClickListener(this);
        buttonTestAnonymous = (Button) rootView.findViewById(R.id.button_testing_test_anonymous);
        buttonTestAnonymous.setOnClickListener(this);
        buttonCalibration = (Button) rootView.findViewById(R.id.button_testing_calibration);
        buttonCalibration.setOnClickListener(this);
        return rootView;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        stopIoManager();
//        if (mPort != null) {
//            try {
//                mPort.close();
//            } catch (IOException e) {
//                // Ignore.
//            }
//            mPort = null;
//        }
////        finish();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d(TAG, "Resumed, port=" + mPort);
//        if (mPort == null) {
//            text.setText("No serial device.");
//        } else {
//            mUsbManager = (UsbManager) getActivity().getApplicationContext().getSystemService(Context.USB_SERVICE);
//            UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
//            if (connection == null) {
//                text.setText("Opening device failed");
//                return;
//            }
//
//            try {
//                mPort.open(connection);
////                sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//            } catch (IOException e) {
//                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
//                text.setText("Error opening device: " + e.getMessage());
//                try {
//                    mPort.close();
//                } catch (IOException e2) {
//                    // Ignore.
//                }
//                mPort = null;
//                return;
//            }
////            text.setText("Serial device: " + mPort.getClass().getSimpleName());
//        }
//        onDeviceStateChange();
//    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.button_testing_test_for_patient:
//                findUSBDevices();
                //TODO if patient not set switch to cardFile fragment
                Intent test = new Intent();
                test.setClass(getActivity() , com.vadim.nakatani.activitys.TestActivity.class);
                startActivity(test);
                break;
            case R.id.button_testing_test_anonymous:
//                connectToDevice();
                break;
            case R.id.button_testing_calibration:
                Intent calibration = new Intent();
                calibration.setClass(getActivity() , com.vadim.nakatani.activitys.CalibrationActivity.class);
                startActivity(calibration);
                break;
            default:
                break;
        }
    }

//    private void findUSBDevices() {
//        final List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
//
//        final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
//        for (final UsbSerialDriver driver : drivers) {
//            final List<UsbSerialPort> ports = driver.getPorts();
//            Log.d(getClass().getSimpleName(), String.format("+ %s: %s port%s", driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
//            result.addAll(ports);
//        }
//
//        text.setText("");
//        for (UsbSerialPort usp : result) {
//            UsbSerialDriver driver = usp.getDriver();
//            UsbDevice device = driver.getDevice();
//
//            mPort = usp;
//            mDriver = driver;
//            mDevice = device;
//
//            String msg1 = String.format("Vendor %s Product %s",
//                    HexDump.toHexString((short) device.getVendorId()),
//                    HexDump.toHexString((short) device.getProductId()));
//            String msg2 = driver.getClass().getSimpleName();
//            String msg = msg1 + "\n" + msg2;
//            text.append(msg);
//        }
//
//        UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
//        try {
//            mPort.open(connection);
//            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//        }catch (IOException e) {
////            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
////                    mTitleTextView.setText("Error opening device: " + e.getMessage());
//            try {
//                mPort.close();
//            } catch (IOException e2) {
//                // Ignore.
//            }
//            mPort = null;
//        }
//        onDeviceStateChange();
//    }
//
//    private void connectToDevice() {
////        UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
////        if (mConnection == null) mConnection = mUsbManager.openDevice(mDevice);
//        try {
////            mPort.open(connection);
////            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//            byte[] bytesHello = new byte[] {(byte) 73};
//            mPort.write(bytesHello, 1000);
//
//            /*int len = mPort.read(mReadBuffer.array(), 200);
//            byte[] data = new byte[len];
//            mReadBuffer.get(data, 0, len);
//            updateReceivedData(data);
//            mReadBuffer.clear();*/
////            mPort.close();
//        }catch (IOException e) {
////            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
////                    mTitleTextView.setText("Error opening device: " + e.getMessage());
//            try {
//                mPort.close();
//            } catch (IOException e2) {
//                // Ignore.
//            }
//            mPort = null;
//        }
//
//        //TODO change position of this code in future when we connected to dev we start startIoManager
////        startIoManager();
//    }
//
//    private void sendMeasurementMessage() {
////        UsbDeviceConnection connection = mUsbManager.openDevice(mDevice);
//        try {
////            mPort.open(connection);
////            mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//            byte[] bytesHello = new byte[] {(byte) 0x47};
//            mPort.write(bytesHello, 1000);
//
//            /*int len = mPort.read(mReadBuffer.array(), 200);
//            byte[] data = new byte[len];
//            mReadBuffer.get(data, 0, len);
//            updateReceivedData(data);
//            mReadBuffer.clear();*/
////            mPort.close();
//        }catch (IOException e) {
////            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
////                    mTitleTextView.setText("Error opening device: " + e.getMessage());
//            try {
//                mPort.close();
//            } catch (IOException e2) {
//                // Ignore.
//            }
//            mPort = null;
//        }
//    }
//
//    private void updateReceivedData(byte[] data) {
//        final String message = "\n\nRead " + data.length + " bytes: \n" + HexDump.dumpHexString(data) + "\n\n";
//        text.append(message);
//        scrollView.smoothScrollTo(0, text.getBottom());
//    }
//
//    private void stopIoManager() {
//        if (mSerialIoManager != null) {
//            Log.i(TAG, "Stopping io manager ..");
//            mSerialIoManager.stop();
//            mSerialIoManager = null;
//        }
//    }
//
//    private void startIoManager() {
//        if (mPort != null) {
//            Log.i(TAG, "Starting io manager ..");
//            mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
//            mExecutor.submit(mSerialIoManager);
//
//            text.append("\nStartIOMANAGER from if");
//        }
//        text.append("\nStartIOMANAGER");
//    }
//
//    private void onDeviceStateChange() {
//        stopIoManager();
//        startIoManager();
//    }
}
