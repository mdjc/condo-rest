package com.github.mdjc.impl;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.mdjc.domain.CondoHelper;

public class DefaultCondoHelper implements CondoHelper {
	private final String condoImagesDir;
	
	public DefaultCondoHelper(String condoImagesDir) {
		this.condoImagesDir = condoImagesDir;
	}

	@Override
	public byte[] getImage(long condoId) throws Exception {
		return Files.readAllBytes(Paths.get(condoImagesDir, String.valueOf(condoId)));
	}
}
