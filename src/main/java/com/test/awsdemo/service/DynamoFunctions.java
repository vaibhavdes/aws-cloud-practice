package com.test.awsdemo.service;

import java.util.List;

import com.test.awsdemo.model.ImageInfo;


public interface DynamoFunctions {
	public void setupDB();
	public List<ImageInfo> allImages();
	public void addImage(ImageInfo e);

}
