package com.github.mdjc.impl;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.transaction.annotation.Transactional;

import com.github.mdjc.domain.Bill;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.InvalidStatusChange;
import com.github.mdjc.domain.PaymentHelper;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentStatus;
import com.github.mdjc.domain.ProofOfPaymentExtension;

public class DefaultPaymentHelper implements PaymentHelper {
	private final String billsProofOfPaymentDir;
	private final BillRepository billRepository;
	private final CondoRepository condoRepository;

	public DefaultPaymentHelper(String billsProofOfPaymentDir, BillRepository billRepository,
			CondoRepository condoRepository) {
		this.billsProofOfPaymentDir = billsProofOfPaymentDir;
		this.billRepository = billRepository;
		this.condoRepository = condoRepository;
	}

	@Override
	public byte[] getProofOfPaymentImage(long billId) throws IOException {
		return java.nio.file.Files.readAllBytes(Paths.get(billsProofOfPaymentDir, String.valueOf(billId)));
	}

	@Override
	public void updateBillPayment(long billId, PaymentMethod paymentMethod, ProofOfPaymentExtension proofOfPaymentExt,
			byte[] proofOfPaymentContent) throws IOException {
		java.nio.file.Files.write(Paths.get(billsProofOfPaymentDir, String.valueOf(billId)), proofOfPaymentContent);
		billRepository.updatePaymentInfo(billId, PaymentStatus.PAID_AWAITING_CONFIRMATION, paymentMethod,
				proofOfPaymentExt);
	}

	@Override
	public void updateBillPayment(long billId, PaymentMethod paymentMethod) throws IOException {
		billRepository.updatePaymentInfo(billId, PaymentStatus.PAID_AWAITING_CONFIRMATION, paymentMethod);
	}

	@Override
	public void transitionBillPaymentStatusTo(long billId, PaymentStatus status) {
		Bill bill = billRepository.getBy(billId);

		if (bill.getPaymentStatus() == PaymentStatus.PAID_AWAITING_CONFIRMATION) {
			switch (status) {
			case PAID_CONFIRMED:
				confirmBillPayment(billId);
				break;
			case REJECTED:
				rejectBillPayment(billId);
				break;
			default:
				throw new InvalidStatusChange(
						String.format("Invalid status change for a %s bill. Valid status are: %s and %s ",
								PaymentStatus.PAID_AWAITING_CONFIRMATION, PaymentStatus.PAID_CONFIRMED,
								PaymentStatus.REJECTED));
			}
		}
	}

	@Transactional
	private void confirmBillPayment(long billId) {
		condoRepository.refreshBalanceWithBill(billId, 1);
		billRepository.updatePaymentInfo(billId, PaymentStatus.PAID_CONFIRMED);
	}

	private void rejectBillPayment(long billId) {
		billRepository.updatePaymentInfo(billId, PaymentStatus.REJECTED);
	}
}
