package com.github.mdjc.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface PaymentHelper {
	void payBill(long billId, PaymentMethod paymentMethod, ProofOfPaymentExtension extension, byte[] proofOfPaymentContent)
			throws Exception;

	byte[] getProofOfPaymentImage(long billId) throws Exception;

	public default List<PaymentStatus> getAsEnumList(String[] paymentStatus) {
		return Arrays.stream(paymentStatus).map(e -> PaymentStatus.valueOf(e))
				.collect(Collectors.toList());
	}
}
