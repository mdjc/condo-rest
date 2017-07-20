package com.github.mdjc.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.mdjc.commons.files.Files;
import com.github.mdjc.domain.Bill;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.BilltStats;
import com.github.mdjc.domain.CondoBill;
import com.github.mdjc.domain.ListMeta;
import com.github.mdjc.domain.PaginationCriteria;
import com.github.mdjc.domain.BillHelper;
import com.github.mdjc.domain.PaymentMethod;
import com.github.mdjc.domain.PaymentStatus;
import com.github.mdjc.domain.ProofOfPaymentExtension;
import com.google.common.collect.ImmutableMap;

@RestController
public class BillRest {
	@Autowired
	BillRepository billRepo;

	@Autowired
	BillHelper helper;
	
	@GetMapping(path = "/condos/{condoId}/condoBills")
	public Map<String, List<CondoBill>> condoBills(@PathVariable long condoId,
			@RequestParam(required = false) String[] paymentStatus,
			@RequestParam(required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue = "0") int offset, 
			@RequestParam(defaultValue = "0") int limit,
			@RequestParam(defaultValue = "ASC") String order) {
		List<PaymentStatus> statusList = helper.getAsEnumList(paymentStatus);
		PaginationCriteria pagCriteria = new PaginationCriteria(offset, limit,
				PaginationCriteria.SortingOrder.valueOf(order.toUpperCase()));
		return ImmutableMap.of("condo-bills", billRepo.findBy(condoId, statusList, from, to, pagCriteria));
	}
	
	@GetMapping(path = "/condos/{condoId}/condoBills/meta")
	public Map<String, ListMeta> condoBillsMeta(@PathVariable long condoId,
			@RequestParam(required = false) String[] paymentStatus,
			@RequestParam(required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		List<PaymentStatus> statusList = helper.getAsEnumList(paymentStatus);
		return ImmutableMap.of("meta", new ListMeta(billRepo.countFindBy(condoId, statusList, from, to)));
	}
	
	@PostMapping(path = "/condos/{condoId}/condoBills")
	public void addCondoBill(@PathVariable long condoId, @RequestBody CondoBill bill) {
		billRepo.addBill(condoId, bill);
	}	
	
	@GetMapping(path = "/condos/{condoId}/condoBills/{billId}")
	public Map<String, CondoBill> getCondoBill(@PathVariable long billId) {
		return ImmutableMap.of("condo-bill", billRepo.getCondoBilldBy(billId));
	}

	@GetMapping(path = "/condos/{condoId}/bills/stats")
	public Map<String, BilltStats> condoBillStats(@PathVariable long condoId,
			@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ImmutableMap.of("stats", billRepo.getStatsBy(condoId, from, to));
	}

	@GetMapping(path = "condos/{condoId}/residents/{username}/bills")
	public Map<String, List<Bill>> getApartmentBills(@PathVariable long condoId, @PathVariable String username,
			@RequestParam String[] paymentStatus) {
		List<PaymentStatus> statusList = helper.getAsEnumList(paymentStatus);
		return ImmutableMap.of("bills", billRepo.findBy(condoId, username, statusList));
	}

	@GetMapping(path = "condos/{condoId}/residents/{username}/bills/{billId}")
	public Map<String, Bill> getBill(@PathVariable long billId) {
		return ImmutableMap.of("bill", billRepo.getBy(billId));
	}

	@DeleteMapping(path = "bills/{billId}")
	public void deleteCondoBill(@PathVariable long billId) {
		helper.deleteBill(billId);
	}
	
	@PutMapping(path = "bills/{billId}/payment", consumes = { "multipart/form-data" })
	public void putPaymentInfo(@PathVariable long billId, @RequestPart String paymentMethod,
			@RequestPart(required = false) MultipartFile proofOfPaymentPict) throws Exception {
		if (proofOfPaymentPict == null) {
			helper.updateBillPayment(billId, PaymentMethod.valueOf(paymentMethod));
		} else {
			String pictExtension = Files.getExtension(proofOfPaymentPict.getOriginalFilename()).toUpperCase();
			helper.updateBillPayment(billId, PaymentMethod.valueOf(paymentMethod),
					ProofOfPaymentExtension.valueOf(pictExtension), proofOfPaymentPict.getBytes());
		}
	}
	
	@PatchMapping(path = "bills/{billId}/payment")
	public void updatePaymentInfo(@PathVariable long billId, @RequestBody String paymentStatus) throws Exception {
		helper.transitionBillPaymentStatusTo(billId, PaymentStatus.valueOf(paymentStatus));
	}

	@GetMapping(path = "bills/{billId}/payment-img")
	public ResponseEntity<byte[]> getPhoto(@PathVariable long billId) throws Exception {
		Bill bill = billRepo.getBy(billId);
		HttpHeaders headers = new HttpHeaders();

		if (bill.getProofOfPaymentExtension() == null) {
			return new ResponseEntity<>(new byte[] {}, headers, HttpStatus.NOT_FOUND);
		}

		MediaType contentType = null;
		switch (bill.getProofOfPaymentExtension()) {
		case JPG:
			contentType = MediaType.IMAGE_JPEG;
			break;
		case PNG:
			contentType = MediaType.IMAGE_PNG;
			break;
		case GIF:
			contentType = MediaType.IMAGE_GIF;
			break;
		}

		byte[] bytes = helper.getProofOfPaymentImage(billId);
		headers.setContentType(contentType);
		headers.setContentLength(bytes.length);
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
}
