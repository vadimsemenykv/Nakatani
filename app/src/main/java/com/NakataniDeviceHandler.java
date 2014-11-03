package com;


import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * Created by vadim on 03.11.14.
 */
public class NakataniDeviceHandler{
    private static NakataniDeviceHandler nakataniDeviceHandler;

    private UsbSerialPort mPort;
    private UsbSerialDriver mDriver;
    private UsbDevice mDevice;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mConnection;

    public static synchronized NakataniDeviceHandler getNakataniDeviceHandlerInstance(){
        if(nakataniDeviceHandler == null) nakataniDeviceHandler = new NakataniDeviceHandler();
        return nakataniDeviceHandler;
    }
    private NakataniDeviceHandler(){}


}
