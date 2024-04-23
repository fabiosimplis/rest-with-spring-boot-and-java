package br.com.erudio.services;

import br.com.erudio.config.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.erudio.exceptions.FileStorageException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServices {


    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServices(FileStorageConfig fileStorageConfig) {

        Path path = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();
        this.fileStorageLocation = path;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e){
            throw new FileStorageException(
                    "Could not create the directoty where the uploaded files will be stored!", e);
        }
    }

    //Método responsável por salvar no disco.
    public String storeFile(MultipartFile file){

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (filename.contains("..")) {
                throw new FileStorageException(
                        "Sorry! Filename contain invalid path sequence " + filename);
            }
            //Cria arquivo vazio e resolve caminho completo até o arquivo
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            //Salva o arquivo
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (Exception e) {
            throw new FileStorageException(
                    "Could not store file " +filename+ " will be store!", e);

        }
    }
}
