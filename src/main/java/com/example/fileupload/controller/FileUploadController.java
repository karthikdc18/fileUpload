package com.example.fileupload.controller;

import com.example.fileupload.entity.FileEntity;
import com.example.fileupload.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileRepository fileRepository;

    // POST method to handle file uploads
    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Save file to database
            FileEntity fileEntity = new FileEntity(file.getOriginalFilename(), file.getBytes());
            fileRepository.save(fileEntity);

            return new ResponseEntity<>("File uploaded and stored successfully: " + file.getOriginalFilename(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET method to retrieve a file by its ID
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        return fileRepository.findById(id)
                .map(fileEntity -> ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + fileEntity.getFileName() + "\"")
                        .body(fileEntity.getData()))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
