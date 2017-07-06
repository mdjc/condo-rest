package com.github.mdjc.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.Bill;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.PaymentStatus;

@RestController
public class ResidentRest {
	@Autowired
	BillRepository billRepo;

	@GetMapping(path = "condos/{condoId}/residents/{username}/bills")
	public Map<String, List<Bill>> getApartmentBills(@PathVariable long condoId, @PathVariable String username,
			@RequestParam String[] paymentStatus) {
		List<PaymentStatus> statusList = Arrays.stream(paymentStatus).map(e -> PaymentStatus.valueOf(e))
				.collect(Collectors.toList());
		Map<String, List<Bill>> map = new HashMap<>();
		map.put("bills", billRepo.getBy(condoId, username, statusList));
		return map;
	}
}
