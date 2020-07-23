package com.test.awsdemo.dao;

import com.test.awsdemo.model.ImageInfo;

public interface ImageDao {
	
	public ImageInfo createEntry(ImageInfo ii);
	public ImageInfo updateEntry(ImageInfo ii);
	public ImageInfo fetchEntry(String userId);
	public void deleteEntry(String userId);

}
