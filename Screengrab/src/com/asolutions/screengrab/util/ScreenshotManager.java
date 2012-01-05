package com.asolutions.screengrab.util;

import java.io.File;
import java.io.IOException;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;

public class ScreenshotManager {
	// TODO: Make sure to point this to your actual adb location!
	private static final String ADB_LOCATION = "./adb";

	private static boolean isThreadStopped;

	public ScreenshotManager(final ImageRetrievedListener listener) {
		AndroidDebugBridge.init(false);
		verfiyAdbLocation();
		AndroidDebugBridge.createBridge(ADB_LOCATION, true);

		AndroidDebugBridge.addDeviceChangeListener(new IDeviceChangeListener() {
			@Override
			public void deviceConnected(IDevice device) {
				System.out.println("Device connected");
				try {
					addDevice(device, listener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void deviceDisconnected(IDevice device) {
				System.out.println("Device disconnected");
				removeDevice(device);
			}

			@Override
			public void deviceChanged(IDevice device, int changeMask) {
				System.out.println("Device changed");
			}
		});
	}

	private void verfiyAdbLocation() {
		if (!new File(ADB_LOCATION).exists()) {
			System.err.println("ADB location '" + ADB_LOCATION + "' is not valid. Please change it in "
					+ getClass().getSimpleName());
			System.exit(0);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			AndroidDebugBridge.terminate();
		} catch (Exception e) {
			// Do nothing
		}
		super.finalize();
	}

	private void addDevice(IDevice device, ImageRetrievedListener listener) throws Exception {
		long imageIndex = 0;
		long startTime = 0;

		isThreadStopped = false;

		while (!isThreadStopped) {
			Thread.yield();
			try {
				if (imageIndex == 0) {
					startTime = System.currentTimeMillis();
				}

				double fps = imageIndex / ((System.currentTimeMillis() - startTime) / 1000.0);

				listener.fireEvent(ImageUtils.convertImage(device.getScreenshot()), fps);
				// writeToFile(ImageUtils.convertImage(device.getScreenshot()), System.currentTimeMillis() + ".png",
				// "png");
			} catch (IOException e) {
				System.out.println("IO error. Ignoring");
			} catch (AdbCommandRejectedException e) {
				e.printStackTrace();
				isThreadStopped = true;
			}

			imageIndex++;
		}
	}

	private void removeDevice(IDevice device) {
		isThreadStopped = true;
	}

	public void stopPolling() {
		isThreadStopped = true;
	}
}
