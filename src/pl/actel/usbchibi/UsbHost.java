package pl.actel.usbchibi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import java.util.HashMap;
import java.util.Iterator;

import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbRequest;
import android.hardware.usb.UsbConstants;

public class UsbHost {

	private Context mainContext;
	private UsbManager usbManager;
	private int VID;
	private int PID;
	private static final String ACTION_USB_PERMISSION = "pl.actel.usbchibi.USB_PERMISSION";
	private Thread usbThread;
	private UsbRun usbRun;
	private static final Object[] sSendLock = new Object[]{};
	private byte[] mData = new byte[2];

	public UsbHost(Activity mainActivity, int VID, int PID) {
		// Get main activity context
		mainContext = mainActivity.getApplicationContext();
		usbManager = (UsbManager) mainContext.getSystemService(Context.USB_SERVICE);
		this.VID = VID;
		this.PID = PID;

//		UsbDevice device;
//		PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mainContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
//		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//		mainContext.registerReceiver(mUsbReceiver, filter);
//		usbManager.requestPermission(device, mPermissionIntent);

		// Enumeration
		HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while(deviceIterator.hasNext()){
			UsbDevice device = deviceIterator.next();
			if (device.getVendorId() == this.VID && device.getProductId() == this.PID) {
				// If we don't have permission, create dummy UsbManager and ask for it
				if (!usbManager.hasPermission(device)) {
					UsbManager tmpUsbManager = (UsbManager) mainContext.getSystemService(Context.USB_SERVICE);
					PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mainContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
					IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
					mainContext.registerReceiver(mUsbReceiver, filter);
					tmpUsbManager.requestPermission(device, mPermissionIntent);
				} else {
					// Run main USB thread
					run(device);
				}
			}
		}
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							// Run main USB thread
							run(device);
						}
					}
					else {
//						Log.d(TAG, "permission denied for device " + device);
					}
				}
			}
			else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (device != null) {
					// Detach
					detach(device);
				}
			}
		}
	};

	private void run(UsbDevice device) {
		usbRun = new UsbRun(device);
		usbThread = new Thread(usbRun);
		usbThread.start();
	}

	private void detach(UsbDevice device) {
		if (usbThread != null) {
			try {
				usbThread.join();
			} catch (InterruptedException e) {
			}
		}
		mainContext.unregisterReceiver(mUsbReceiver);
	}

	private class UsbRun implements Runnable {

		private UsbDevice usbDevice;

		private UsbRun(UsbDevice device) {
			usbDevice = device;
		}

		@Override
		public void run() {
			UsbDeviceConnection usbConnection = usbManager.openDevice(usbDevice);
			if (!usbConnection.claimInterface(usbDevice.getInterface(1), true)) {
				return;
			}

			UsbEndpoint epIN = null;
			UsbEndpoint epOUT = null;

			UsbInterface usbIf = usbDevice.getInterface(1);
			for (int i = 0; i < usbIf.getEndpointCount(); i++) {
				if (usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
					if (usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
						epIN = usbIf.getEndpoint(i);
					}
					else {
						epOUT = usbIf.getEndpoint(i);
					}
				}
			}

			while (true) {
				synchronized (sSendLock) {
					try {
						sSendLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				byte send_1 = mData[0];
				byte send_2 = mData[1];
				usbConnection.bulkTransfer(epOUT, new byte[] { send_1, send_2 }, 2, 0);

				usbConnection.bulkTransfer(epIN, mData, 2, 500);

				if (!(mData[0] == 'a' && mData[1] == 'c' && mData[2] == 'k'
						|| mData[0] == 'n' && mData[1] == 'a' && mData[2] == 'k')) {
					usbConnection.bulkTransfer(epOUT, new byte[] { 'x' }, 1, 0);
					usbConnection.bulkTransfer(epOUT, new byte[] { send_1, send_2 }, 2, 0);
				}
			}
		}
	}

	public void send(byte[] data) {
		mData = data;
		synchronized (sSendLock) {
			sSendLock.notify();
		}
	}
}
