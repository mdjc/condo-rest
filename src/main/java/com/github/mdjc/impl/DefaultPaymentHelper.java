package com.github.mdjc.impl;

import java.io.IOException;
import java.nio.file.Paths;

import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.PaymentHelper;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentStatus;
import com.github.mdjc.domain.ProofOfPaymentExtension;

public class DefaultPaymentHelper implements PaymentHelper {
	private final BillRepository billRepository;
	private final String billsProofOfPaymentDir;

	public DefaultPaymentHelper(String billsProofOfPaymentDir, BillRepository billRepository) {
		this.billsProofOfPaymentDir = billsProofOfPaymentDir;
		this.billRepository = billRepository;
	}

	@Override
	public void payBill(long billId, PaymentMethod paymentMethod, ProofOfPaymentExtension proofOfPaymentExt,
			byte[] proofOfPaymentContent) throws IOException {
		java.nio.file.Files.write(Paths.get(billsProofOfPaymentDir, String.valueOf(billId)), proofOfPaymentContent);
		billRepository.updatePaymentInfo(billId, PaymentStatus.PAID_AWAITING_CONFIRMATION, paymentMethod, proofOfPaymentExt);
	}

	@Override
	public byte[] getProofOfPaymentImage(long billId) throws IOException {
		return java.nio.file.Files.readAllBytes(Paths.get(billsProofOfPaymentDir, String.valueOf(billId)));
	}
}
