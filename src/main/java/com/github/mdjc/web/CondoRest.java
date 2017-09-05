package com.github.mdjc.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.github.mdjc.domain.Apartment;
import com.github.mdjc.domain.Condo;
import com.github.mdjc.domain.CondoHelper;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.CondoStats;
import com.github.mdjc.domain.ImageExtension;
import com.github.mdjc.domain.User;

@RestController
public class CondoRest {
	private static final int DEFAULT_CONDO_IMG_ID = 0;
	private static final ImageExtension DEFAULT_CONDO_IMG_EXTENSION = ImageExtension.PNG;

	@Autowired
	CondoRepository condoRepo;
	
	@Autowired
	CondoHelper helper;

	@GetMapping(path = "/condos")
	public List<Condo> userCondos(Authentication auth) {
		return condoRepo.getAllByUser((User) auth.getPrincipal());
	}

	@GetMapping(path = "/condos/{condoId}")
	public Condo condo(@PathVariable long condoId) {
		return condoRepo.getBy(condoId);
	}

	@GetMapping(path = "/condos/{condoId}/img")
	public ResponseEntity<byte[]> condoImage(@PathVariable long condoId) throws Exception {
		Condo condo = condoRepo.getBy(condoId);
		HttpHeaders headers = new HttpHeaders();
		byte[] bytes = null;
		if (condo.getImageExtension() != null) {
			bytes = helper.getImage(condoId);
			headers.setContentType(RestUtils.getContentType(condo.getImageExtension()));
		} else {
			bytes = helper.getImage(DEFAULT_CONDO_IMG_ID);
			headers.setContentType(RestUtils.getContentType(DEFAULT_CONDO_IMG_EXTENSION));
		}
		
		headers.setContentLength(bytes.length);
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
	
	@GetMapping(path = "/condos/{condoId}/stats")
	public CondoStats condoStats(@PathVariable long condoId) {
		return condoRepo.getStatsByCondoId(condoId);
	}
	
	@GetMapping(path = "/condos/{condoId}/apartments")
	public List<Apartment> condoApartments(@PathVariable long condoId) {
		return condoRepo.getCondoApartments(condoId);
	}
}