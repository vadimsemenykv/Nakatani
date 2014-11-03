package com.vadim.nakatani.fragments;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vadim.nakatani.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class ShowDataFromUSB extends Fragment implements View.OnClickListener {

    private Context mContext;
    UsbDevice device = null;
    UsbInterface usbInterface = null;
    UsbEndpoint endpointIn = null;
    UsbEndpoint endpointOut = null;
    PendingIntent mPermissionIntent;
    UsbDeviceConnection usbDeviceConnection;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    public static ShowDataFromUSB newInstance(Context mContext) {
        ShowDataFromUSB fragment = new ShowDataFromUSB();
        return fragment;
    }

    public ShowDataFromUSB() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_data_from_usb, container, false);

        Button button = (Button)  rootView.findViewById(R.id.textView_test);
        button.setOnClickListener(this);
        Button button2 = (Button)  rootView.findViewById(R.id.button_calibration_start);
        button2.setOnClickListener(this);
        TextView textView = (TextView) rootView.findViewById(R.id.textDataUSB);
        textView.setText("Text:");

//        UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
//        HashMap<String,UsbDevice> deviceList = manager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//
//        while(deviceIterator.hasNext())
//        {
//            UsbDevice device = deviceIterator.next();
//            CharSequence charSequence = "DeviceName = " + device.getDeviceName() + "\n" +
//                    "DeviceClass = " + device.getDeviceClass() + "\n" +
//                    "DeviceId = " + device.getDeviceId() + "\n" +
//                    "DeviceProtocol = " + device.getDeviceProtocol() + "\n" +
//                    "DeviceSubclass = " + device.getDeviceSubclass() + "\n" +
//                    "InterfaceCount = " + device.getInterfaceCount() + "\n" +
//                    "ProductId = " + device.getProductId() + "\n" +
//                    "VendorId = " + device.getVendorId() + "\n";
//            textView.append(charSequence);
//        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId) {
            case R.id.textView_test:
                viewConnectedUSBDevice();
                break;
            case R.id.button_calibration_start:
                startWork();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        releaseUsb();
        getActivity().unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    private void releaseUsb() {

        Toast.makeText(getActivity(), "releaseUsb()", Toast.LENGTH_LONG).show();

        if (usbDeviceConnection != null) {
            if (usbInterface != null) {
                usbDeviceConnection.releaseInterface(usbInterface);
                usbInterface = null;
            }
            usbDeviceConnection.close();
            usbDeviceConnection = null;
        }

        device = null;
        usbInterface = null;
        endpointIn = null;
        endpointOut = null;
    }

    private final BroadcastReceiver mUsbReceiver =
            new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (ACTION_USB_PERMISSION.equals(action)) {
                        Toast.makeText(getActivity(), "ACTION_USB_PERMISSION", Toast.LENGTH_LONG).show();

                        synchronized (this) {
                            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                if(device != null){
                                    connectUsb();
                                }
                            }
                            else {
                                Toast.makeText(getActivity(), "permission denied for device " + device, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            };

    private void startWork() {
        //register the broadcast receiver
        mPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        getActivity().registerReceiver(mUsbReceiver, filter);

//        registerReceiver(mUsbDeviceReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
//        registerReceiver(mUsbDeviceReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        connectUsb();

    }

    private void connectUsb() {
        searchEndPoint();

        if(usbInterface != null){
            setupUsbComm();
        }
    }

    private void searchEndPoint(){
        usbInterface = null;
        endpointOut = null;
        endpointIn = null;

        //Search device for targetVendorID and targetProductID
       /* if(device == null){
            UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();

                if(device.getVendorId()==targetVendorID){
                    if(device.getProductId()==targetProductID){
                        deviceFound = device;
                    }
                }
            }
        }*/

        if(device == null){
            Toast.makeText(getActivity(), "device not found", Toast.LENGTH_LONG).show();
        }else {
            /*String s = device.toString() + "\n" +
                    "DeviceID: " + device.getDeviceId() + "\n" +
                    "DeviceName: " + device.getDeviceName() + "\n" +
                    "DeviceClass: " + device.getDeviceClass() + "\n" +
                    "DeviceSubClass: " + device.getDeviceSubclass() + "\n" +
                    "VendorID: " + device.getVendorId() + "\n" +
                    "ProductID: " + device.getProductId() + "\n" +
                    "InterfaceCount: " + device.getInterfaceCount();
            textInfo.setText(s);*/

            //Search for UsbInterface with Endpoint of USB_ENDPOINT_XFER_BULK,
            //and direction USB_DIR_OUT and USB_DIR_IN

            for(int i=0; i < device.getInterfaceCount(); i++){
                UsbInterface usbif = device.getInterface(i);

                UsbEndpoint tOut = null;
                UsbEndpoint tIn = null;

                int tEndpointCnt = usbif.getEndpointCount();
                if(tEndpointCnt>=2){
                    for(int j=0; j<tEndpointCnt; j++){
                        if(usbif.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK){
                            if(usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT){
                                tOut = usbif.getEndpoint(j);
                            }else if(usbif.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN){
                                tIn = usbif.getEndpoint(j);
                            }
                        }
                    }

                    if(tOut!=null && tIn!=null){
                        //This interface have both USB_DIR_OUT
                        //and USB_DIR_IN of USB_ENDPOINT_XFER_BULK
                        usbInterface = usbif;
                        endpointOut = tOut;
                        endpointIn = tIn;
                    }
                }

            }

            TextView textView = (TextView) getActivity().findViewById(R.id.textDataUSB);

            if(usbInterface == null){
                textView.setText("No suitable interface found!");
            }else{
                textView.setText(
                        "UsbInterface found: " + usbInterface.toString() + "\n\n" +
                                "Endpoint OUT: " + endpointOut.toString() + "\n\n" +
                                "Endpoint IN: " + endpointIn.toString());
            }
        }
    }

    private boolean setupUsbComm(){

        //for more info, search SET_LINE_CODING and
        //SET_CONTROL_LINE_STATE in the document:
        //"Universal Serial Bus Class Definitions for Communication Devices"
        //at http://adf.ly/dppFt
        final int RQSID_SET_LINE_CODING = 0x20;
        final int RQSID_SET_CONTROL_LINE_STATE = 0x22;

        boolean success = false;

        UsbManager manager = (UsbManager) getActivity().getApplicationContext().getSystemService(Context.USB_SERVICE);
        Boolean permitToRead = manager.hasPermission(device);

        if(permitToRead){
            usbDeviceConnection = manager.openDevice(device);
            if(usbDeviceConnection != null){
                usbDeviceConnection.claimInterface(usbInterface, true);

//                showRawDescriptors(); //skip it if you no need show RawDescriptors

                int usbResult;
//                usbResult = usbDeviceConnection.controlTransfer(
//                        0x21,        //requestType
//                        RQSID_SET_CONTROL_LINE_STATE, //SET_CONTROL_LINE_STATE
//                        0,     //value
//                        0,     //index
//                        null,    //buffer
//                        0,     //length
//                        0);    //timeout
//                Toast.makeText(getActivity(), "controlTransfer(SET_CONTROL_LINE_STATE): " + usbResult, Toast.LENGTH_LONG).show();

                usbResult = usbDeviceConnection.controlTransfer(0x21, 0x22, 0x1, 0, null, 0, 0);
                Toast.makeText(getActivity(), "controlTransfer(SET_CONTROL_LINE_STATE): " + usbResult, Toast.LENGTH_LONG).show();

                //baud rate = 9600
                //8 data bit
                //1 stop bit
                byte[] encodingSetting = new byte[] {(byte)0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08 };
                usbResult = usbDeviceConnection.controlTransfer(
                        0x21,       //requestType
                        RQSID_SET_LINE_CODING,   //SET_LINE_CODING
                        0,      //value
                        0,      //index
                        encodingSetting,  //buffer
                        7,      //length
                        0);     //timeout
                Toast.makeText(getActivity(), "controlTransfer(RQSID_SET_LINE_CODING): " + usbResult, Toast.LENGTH_LONG).show();

                for(int x = 0; x < 256; x++){
                    byte[] bytesHello = new byte[] {(byte) x};
                    usbResult = usbDeviceConnection.bulkTransfer(endpointOut, bytesHello, bytesHello.length, 50);
//                    Toast.makeText(getActivity(), "Send - " + x + " bulkTransfer: " + usbResult, Toast.LENGTH_LONG).show();
                }

            }

        }else{
            manager.requestPermission(device, mPermissionIntent);
            Toast.makeText(getActivity(), "Permission: " + permitToRead, Toast.LENGTH_LONG).show();
        }
        return success;
    }

    private void showRawDescriptors(){
        final int STD_USB_REQUEST_GET_DESCRIPTOR = 0x06;
        final int LIBUSB_DT_STRING = 0x03;

        byte[] buffer = new byte[255];
        int indexManufacturer = 14;
        int indexProduct = 15;
        String stringManufacturer = "";
        String stringProduct = "";

        byte[] rawDescriptors = usbDeviceConnection.getRawDescriptors();

        int lengthManufacturer = usbDeviceConnection.controlTransfer(
                UsbConstants.USB_DIR_IN|UsbConstants.USB_TYPE_STANDARD,   //requestType
                STD_USB_REQUEST_GET_DESCRIPTOR,         //request ID for this transaction
                (LIBUSB_DT_STRING << 8) | rawDescriptors[indexManufacturer], //value
                0,   //index
                buffer,  //buffer
                0xFF,  //length
                0);   //timeout
        try {
            stringManufacturer = new String(buffer, 2, lengthManufacturer-2, "UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }

        int lengthProduct = usbDeviceConnection.controlTransfer(
                UsbConstants.USB_DIR_IN|UsbConstants.USB_TYPE_STANDARD,
                STD_USB_REQUEST_GET_DESCRIPTOR,
                (LIBUSB_DT_STRING << 8) | rawDescriptors[indexProduct],
                0,
                buffer,
                0xFF,
                0);
        try {
            stringProduct = new String(buffer, 2, lengthProduct-2, "UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void viewConnectedUSBDevice() {
        TextView textView = (TextView) getActivity().findViewById(R.id.textDataUSB);
        textView.setText("Devices:");

        UsbManager manager = (UsbManager) getActivity().getApplicationContext().getSystemService(Context.USB_SERVICE);
        HashMap<String,UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        textView.append("manager != null " + (manager!=null) + " num = " + deviceList.size());

        while(deviceIterator.hasNext())
        {
            UsbDevice d = deviceIterator.next();
            CharSequence charSequence =
                    "\nDeviceName = " + d.getDeviceName() + "\n" +
                    "DeviceClass = " + d.getDeviceClass() + "\n" +
                    "DeviceId = " + d.getDeviceId() + "\n" +
                    "DeviceProtocol = " + d.getDeviceProtocol() + "\n" +
                    "DeviceSubclass = " + d.getDeviceSubclass() + "\n" +
                    "InterfaceCount = " + d.getInterfaceCount() + "\n" +
                    "ProductId = " + d.getProductId() + "\n" +
                    "VendorId = " + d.getVendorId() + "\n";
            textView.append(charSequence);
            this.device = d;
        }
    }
}
