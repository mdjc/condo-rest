package com.github.mdjc.domain;

public interface OutlayHelper {
	byte[] getReceiptImage(long outlayId) throws Exception;

	void addOutlay(long condoId, String category, double amount, String supplier, String comment,
			String receiptImgExtension, byte[] bytes) throws Exception;
}
