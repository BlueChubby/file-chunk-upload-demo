package org.xlm.jmsstudy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private IFileUploadService fileUploadService;

    //文件上传前的注册
    @PostMapping("/register")
    public Object register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        return fileUploadService.register(fileMd5, fileName, fileSize, mimetype, fileExt);
    }

    //分块检查
    @PostMapping("/checkchunk")
    public Object checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        return fileUploadService.checkchunk(fileMd5, chunk, chunkSize);
    }

    //上传分块
    @PostMapping("/uploadchunk")
    public Object uploadchunk(MultipartFile file, String fileMd5, Integer chunk) {
        return fileUploadService.uploadchunk(file, fileMd5, chunk);
    }

    //合并文件
    @PostMapping("/mergechunks")
    public Object mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        return fileUploadService.mergechunks(fileMd5, fileName, fileSize, mimetype, fileExt);
    }
}
