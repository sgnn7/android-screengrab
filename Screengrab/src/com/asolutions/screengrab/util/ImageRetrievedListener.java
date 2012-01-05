package com.asolutions.screengrab.util;

import java.awt.image.BufferedImage;

public interface ImageRetrievedListener {
	public void fireEvent(BufferedImage image, double fps);
}
