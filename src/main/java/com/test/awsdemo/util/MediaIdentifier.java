package com.test.awsdemo.util;

import org.springframework.http.MediaType;

public class MediaIdentifier {

	public static MediaType contentType(String keyname) {
	    String[] arr = keyname.split("\\.");
	    String type = arr[arr.length-1];
	    switch(type) {
	      case "txt": return MediaType.TEXT_PLAIN;
	      case "png": return MediaType.IMAGE_PNG;
	      case "jpg": return MediaType.IMAGE_JPEG;
	      default: return MediaType.APPLICATION_OCTET_STREAM;
	    }
	  }
}
