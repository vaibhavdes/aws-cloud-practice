package com.test.awsdemo.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.Bucket;
import com.test.awsdemo.model.ImageInfo;
import com.test.awsdemo.service.DynamoFunctions;
import com.test.awsdemo.service.S3Bucket;
import com.test.awsdemo.service.S3FileOperation;
import com.test.awsdemo.util.MediaIdentifier;


@RestController
@CrossOrigin(maxAge = 3600)
public class TestController {

	@Value("${bucket.name}")
	String bucketName;
	
	@Autowired
	S3Bucket bucket;
	
	@Autowired
	S3FileOperation fileOperation;
	
	@Autowired
	DynamoFunctions dynamo;

	@GetMapping("/")
	public String helloworld() {
		return "Hello World";
	}
	
	@PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map<String,String> uploadFile(@RequestPart(value = "file", required = false) MultipartFile files) throws IOException {
	    fileOperation.uploadFile(bucketName,files.getOriginalFilename(),files);
        Map<String,String> result = new HashMap<String, String>();
        result.put("key",files.getOriginalFilename());
        return result;
    }

    @GetMapping(path = "/download")
    public ResponseEntity<byte[]> uploadFile(@RequestParam(value = "file") String file,@RequestParam(value = "bucket") String bName) throws IOException {
        byte[] data = fileOperation.getFile(file,bName);
        
        if(data == null) {
        	return ResponseEntity.noContent().build();
        }
        
        ByteArrayResource resource = new ByteArrayResource(data);
    	
        return ResponseEntity
                .ok()
                //.contentLength(data.length)
                //.header("Content-type", "application/octet-stream")
                .contentType(MediaIdentifier.contentType(file))
                .header("Content-disposition", "attachment; filename=\"" + file + "\"")
                .body(resource.getByteArray());
    }
    
	@GetMapping(value="/images", produces = {"application/json"})
	List<ImageInfo> getImages(){
		return dynamo.allImages();
	}
	
	@GetMapping(value="/bucket",  produces = {"application/json"})
	List<Bucket> buckets(){
		return bucket.getAllBuckets();
	}
	
	
}
