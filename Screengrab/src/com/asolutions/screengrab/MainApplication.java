package com.asolutions.screengrab;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.asolutions.screengrab.util.ImageRetrievedListener;
import com.asolutions.screengrab.util.ScreenshotManager;

public class MainApplication extends JFrame {
	private static final long serialVersionUID = 1L;

	private static JLabel picLabel;

	public MainApplication() {
		picLabel = new JLabel();
		getContentPane().add(picLabel, BorderLayout.CENTER);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		final MainApplication mainApplication = new MainApplication();

		mainApplication.setLocation(10, 10);
		mainApplication.setSize(500, 50);
		mainApplication.setTitle("Attach device with debugging enabled");
		mainApplication.setVisible(true);

		final ScreenshotManager screenshotManager = new ScreenshotManager(new ImageRetrievedListener() {
			@Override
			public void fireEvent(BufferedImage image, double fps) {
				fps = Math.round(fps * 100) / 100.0;
				mainApplication.setTitle("FPS: " + fps);

				picLabel.setIcon(new ImageIcon(image));

				mainApplication.pack();
				picLabel.repaint();
				picLabel.revalidate();
			}
		});

		mainApplication.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				screenshotManager.stopPolling();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
