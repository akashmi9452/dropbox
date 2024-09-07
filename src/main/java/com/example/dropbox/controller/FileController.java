package com.example.dropbox.controller;

import com.example.dropbox.model.FileMetadata;
import com.example.dropbox.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public FileMetadata uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.save(file);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) throws IOException {
        byte[] fileData = fileService.getFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @PutMapping("/{id}")
    public FileMetadata updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return fileService.updateFile(id, file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) throws IOException {
        fileService.deleteFile(id);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping
    public List<FileMetadata> listFiles() {
        return fileService.listFiles();
    }
}
