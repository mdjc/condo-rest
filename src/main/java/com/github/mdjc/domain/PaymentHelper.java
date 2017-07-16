package com.github.mdjc.domain;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface PaymentHelper {
	byte[] getProofOfPaymentImage(long billId) throws Exception;
	
	void updateBillPayment(long billId, PaymentMethod paymentMethod, ProofOfPaymentExtension extension, byte[] proofOfPaymentContent)
			throws Exception;
	void updateBillPayment(long billId, PaymentMethod paymentMethod) throws IOException;
	void transitionBillPaymentStatusTo(long billId, PaymentStatus status);

	public default List<PaymentStatus> getAsEnumList(String[] paymentStatus) {
		if (paymentStatus == null) {
			return Collections.emptyList();
		}
		
		return Arrays.stream(paymentStatus).map(e -> PaymentStatus.valueOf(e))
				.collect(Collectors.toList());
	}
}
