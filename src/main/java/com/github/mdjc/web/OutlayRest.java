package com.github.mdjc.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.mdjc.commons.files.Files;
import com.github.mdjc.domain.ListMeta;
import com.github.mdjc.domain.Outlay;
import com.github.mdjc.domain.OutlayHelper;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.OutlayStats;
import com.github.mdjc.domain.PaginationCriteria;
import com.google.common.collect.ImmutableMap;

@RestController
public class OutlayRest {
	@Autowired
	OutlayRepository repository;

	@Autowired
	OutlayHelper helper;

	@GetMapping(path = "/condos/{condoId}/outlays")
	public Map<String, List<Outlay>> condoOutlays(@PathVariable long condoId,
			@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "0") int limit,
			@RequestParam(defaultValue = "ASC") String order) {
		PaginationCriteria pagCriteria = new PaginationCriteria(offset, limit,
				PaginationCriteria.SortingOrder.valueOf(order.toUpperCase()));
		return ImmutableMap.of("outlays", repository.findBy(condoId, from, to, pagCriteria));
	}

	@PostMapping(path = "/condos/{condoId}/outlays", consumes = { "multipart/form-data" })
	public void addOutlay(@PathVariable long condoId, @RequestPart String category, @RequestPart String amount,
			@RequestPart(required = false) String supplier, @RequestPart(required = false) String comment,
			@RequestPart MultipartFile receiptImg) throws Exception {
		String receiptImgExtension = Files.getExtension(receiptImg.getOriginalFilename()).toUpperCase();
		helper.addOutlay(condoId, category, Double.valueOf(amount), supplier, comment, receiptImgExtension, receiptImg.getBytes());
	}

	@GetMapping(path = "/condos/{condoId}/outlays/meta")
	public Map<String, ListMeta> condoOutlaysMeta(@PathVariable long condoId,
			@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ImmutableMap.of("meta", new ListMeta(repository.countFindBy(condoId, from, to)));
	}

	@GetMapping(path = "outlays/{outlayId}")
	public Map<String, Outlay> outlay(@PathVariable long outlayId) {
		return ImmutableMap.of("outlay", repository.getBy(outlayId));
	}
	
	@DeleteMapping(path = "outlays/{outlayId}")
	public void deleteCondoBill(@PathVariable long outlayId) {
		helper.deleteOutlay(outlayId);
	}

	@GetMapping(path = "outlays/{outlayId}/receipt-img")
	public ResponseEntity<byte[]> getReceiptImage(@PathVariable long outlayId) throws Exception {
		Outlay outlay = repository.getBy(outlayId);
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = helper.getReceiptImage(outlayId);
		headers.setContentType(RestUtils.getContentType(outlay.getReceiptImageExtension()));
		headers.setContentLength(bytes.length);
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@GetMapping(path = "/condos/{condoId}/outlays/stats")
	public Map<String, OutlayStats> condoOutlayStats(@PathVariable long condoId,
			@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return ImmutableMap.of("stats", repository.getStatsBy(condoId, from, to));
	}
}
