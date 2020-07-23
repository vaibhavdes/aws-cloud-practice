package com.test.awsdemo.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3FileOperation {
	public void uploadFile(String bucketName, String fileName, MultipartFile file);
	public void deleteFile(String bucketName, String fileName);
	public byte[] getFile(String key, String bucketName);
	public void copyFile(String srcBucket, String srcFileName,String destBucket,String destFileName);

}
