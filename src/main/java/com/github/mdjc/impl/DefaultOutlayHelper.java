package com.github.mdjc.impl;

import java.nio.file.Paths;

import com.github.mdjc.domain.OutlayHelper;

public class DefaultOutlayHelper implements OutlayHelper{
	private final String outlaysReceiptImagesDir;
	
	public DefaultOutlayHelper(String outlaysReceiptImagesDir) {
		this.outlaysReceiptImagesDir = outlaysReceiptImagesDir;
	}

	@Override
	public byte[] getReceiptImage(long outlayId) throws Exception {
		return java.nio.file.Files.readAllBytes(Paths.get(outlaysReceiptImagesDir, String.valueOf(outlayId)));
	}
}
