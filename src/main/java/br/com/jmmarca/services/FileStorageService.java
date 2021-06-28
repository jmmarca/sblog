package br.com.jmmarca.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.jmmarca.core.errors.FileStorageException;
import br.com.jmmarca.property.FileStorageProperties;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    private Path fullPath;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException(
                    "Não foi possível criar o diretório onde os arquivos carregados serão armazenados.", ex);
        }
    }

    public Path getFullPath() {
        return fullPath;
    }

    public void setFullPath(Path fullPath) {
        this.fullPath = fullPath;
    }

    public String storeFile(MultipartFile file, String extraPath) {
        Path pathUpload = null;
        try {
            Path newPath = this.fileStorageLocation.resolve(extraPath);
            this.setFullPath(newPath);
            Files.createDirectories(newPath);
            pathUpload = newPath;
        } catch (Exception ex) {
            throw new FileStorageException(
                    "Não foi possível criar o diretório onde os arquivos carregados serão armazenados.", ex);
        }
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Erro: O nome do arquivo contém caracteres inválidos" + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = pathUpload.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Não foi possível enviar o arquivo " + fileName + ". Tente novamente!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File não encontrado" + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File não encontrado" + fileName, ex);
        }
    }
}