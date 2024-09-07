package com.example.dropbox.service;

import com.example.dropbox.model.FileMetadata;
import com.example.dropbox.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private final Path root = Paths.get("uploads");
    private final FileMetadataRepository fileMetadataRepository;

    public FileService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public FileMetadata save(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Long size = file.getSize();
        String fileType = file.getContentType();

        Files.copy(file.getInputStream(), this.root.resolve(fileName));

        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(fileName);
        metadata.setSize(size);
        metadata.setFileType(fileType);
        metadata.setCreatedAt(LocalDateTime.now());

        return fileMetadataRepository.save(metadata);
    }

    public byte[] getFile(Long id) throws IOException {
        Optional<FileMetadata> metadata = fileMetadataRepository.findById(id);
        if (metadata.isPresent()) {
            Path filePath = this.root.resolve(metadata.get().getFileName());
            return Files.readAllBytes(filePath);
        } else {
            throw new RuntimeException("File not found");
        }
    }

    public List<FileMetadata> listFiles() {
        return fileMetadataRepository.findAll();
    }

    public void deleteFile(Long id) throws IOException {
        Optional<FileMetadata> metadata = fileMetadataRepository.findById(id);
        if (metadata.isPresent()) {
            Path filePath = this.root.resolve(metadata.get().getFileName());
            Files.delete(filePath);
            fileMetadataRepository.deleteById(id);
        } else {
            throw new RuntimeException("File not found");
        }
    }

    public FileMetadata updateFile(Long id, MultipartFile file) throws IOException {
        deleteFile(id);
        return save(file);
    }
}
