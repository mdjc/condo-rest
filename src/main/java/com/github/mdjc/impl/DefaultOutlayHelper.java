package com.github.mdjc.impl;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.ImageExtension;
import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayCategory;
import com.github.mdjc.domain.OutlayHelper;
import com.github.mdjc.domain.OutlayRepository;

public class DefaultOutlayHelper implements OutlayHelper {
	private final String outlaysReceiptImagesDir;
	private final OutlayRepository outlayRepo;
	private final CondoRepository condoRepo;

	public DefaultOutlayHelper(String outlaysReceiptImagesDir, OutlayRepository outlayRepository,
			CondoRepository condoRepository) {
		this.outlaysReceiptImagesDir = outlaysReceiptImagesDir;
		this.outlayRepo = outlayRepository;
		this.condoRepo = condoRepository;
	}

	@Override
	public byte[] getReceiptImage(long outlayId) throws Exception {
		return java.nio.file.Files.readAllBytes(Paths.get(outlaysReceiptImagesDir, String.valueOf(outlayId)));
	}

	@Transactional
	@Override
	public void addOutlay(long condoId, String category, double amount, String supplier, String comment,
			String receiptImgExtension, byte[] receiptImgContent) throws IOException {
		Outlay outlay = new Outlay(OutlayCategory.valueOf(category), amount, supplier, comment,
				ImageExtension.valueOf(receiptImgExtension), LocalDate.now());
		long outlayId = outlayRepo.add(condoId, outlay);
		java.nio.file.Files.write(Paths.get(outlaysReceiptImagesDir, String.valueOf(outlayId)), receiptImgContent);
		condoRepo.refreshBalanceWithOutlay(outlayId, -1);
	}

	@Transactional
	@Override
	public void deleteOutlay(long outlayId) {
		condoRepo.refreshBalanceWithOutlay(outlayId, 1);
		outlayRepo.delete(outlayId);
	}
}
