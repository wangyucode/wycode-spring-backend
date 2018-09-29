package cn.wycode.web.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    /**
     * 保存到临时文件夹temp
     *
     * @return 保存的文件名
     */
    String storeToTemp(MultipartFile file) throws IOException;

    /**
     * 将临时文件保存到文件夹
     *
     * @param fileName   文件名
     * @param folderName 挪动到文件夹的位置
     */
    void moveTempFileToFolder(String fileName, String folderName) throws IOException;

    Path load(String filename);

    Stream<Path> loadAll() throws IOException;

    Path loadTemp(String filename);
}
