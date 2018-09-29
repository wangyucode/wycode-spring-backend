package cn.wycode.web.controller;

import cn.wycode.web.entity.JsonResult;
import cn.wycode.web.service.StorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/public")
@Api(value = "file", description = "文件", tags = "File")
public class FileController {

    private Log log = LogFactory.getLog(FileController.class);

    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ApiOperation(value = "上传文件")
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult<String> handleFileUpload(@RequestParam MultipartFile file) {
        String name = null;
        try {
            name = storageService.storeToTemp(file);
        } catch (IOException e) {
            log.error("文件保存失败", e);
        }
        return JsonResult.Companion.data(name);
    }

}
