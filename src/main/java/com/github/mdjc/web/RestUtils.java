package com.github.mdjc.web;

import org.springframework.http.MediaType;

import com.github.mdjc.domain.ImageExtension;

public class RestUtils {
	public static MediaType getContentType(ImageExtension imgExtension) {
		switch (imgExtension) {
		case JPG:
			return MediaType.IMAGE_JPEG;
		case PNG:
			return MediaType.IMAGE_PNG;
		case GIF:
			return MediaType.IMAGE_GIF;
		}

		return null;
	}
}
