package cn.wycode.web.service.impl;

import cn.wycode.web.service.StorageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    private Log log = LogFactory.getLog(StorageServiceImpl.class);

//    private static Path rootPath = Paths.get("/apache-tomcat-8.5.14/webapps/upload/");
//    private static Path tempPath = Paths.get("/apache-tomcat-8.5.14/webapps/upload/temp/");

    private static final Path rootPath = Paths.get("/var/www/upload/");
    private static final Path tempPath = Paths.get("/var/www/upload/temp/");

    public StorageServiceImpl() {
        log.info(tempPath.toAbsolutePath());
        createFolderIfNotExist();
    }

    private void createFolderIfNotExist() {
        try {
            Files.createDirectories(tempPath);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }


    @Override
    public String storeToTemp(MultipartFile file) throws IOException {
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date()) + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new StorageException("File is empty： " + fileName);
        }
        Files.copy(file.getInputStream(), tempPath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Override
    public void moveTempFileToFolder(String fileName, String folderName) throws IOException {
        Path dir = rootPath.resolve(folderName);
        Path tempFilePath = tempPath.resolve(fileName);
        Files.createDirectories(dir);
        Files.move(tempFilePath, dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Path load(String filename) {
        return rootPath.resolve(filename);
    }

    @Override
    public Stream<Path> loadAll() throws IOException {
        return Files.walk(rootPath, 3)
                .filter(path -> !path.equals(rootPath))
                .map(rootPath::relativize);
    }

    @Override
    public Path loadTemp(String filename) {
        return tempPath.resolve(filename);
    }

    public class StorageException extends RuntimeException {
        private static final long serialVersionUID = 2430191988074222554L;

        public StorageException(String message) {
            super(message);
        }

        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public class StorageFileNotFoundException extends StorageException {
        private static final long serialVersionUID = -7119518537629449580L;

        public StorageFileNotFoundException(String message) {
            super(message);
        }

        public StorageFileNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
