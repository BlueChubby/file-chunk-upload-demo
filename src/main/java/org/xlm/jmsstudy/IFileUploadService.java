package org.xlm.jmsstudy;

import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {

    /**
     * 文件上传前检查文件是否存在
     *
     * 根据文件md5得到文件路径
     *
     * @param fileMd5 文件md5值
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param mimetype 文件类型
     * @param fileExt 文件扩展名
     * @return
     */
    Object register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);


    /**
     * 分块文件检查
     * @param fileMd5 文件md5
     * @param chunk 分块文件下标
     * @param chunkSize 分块文件大小
     * @return
     */
    Object checkchunk(String fileMd5, Integer chunk, Integer chunkSize);

    /**
     * 上传分块文件
     * @param file 文件对象
     * @param fileMd5 文件md5
     * @param chunk 分块文件下标
     * @return
     */
    Object uploadchunk(MultipartFile file, String fileMd5, Integer chunk);

    /**
     * 合并分块文件
     * @param fileMd5 文件md5
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param mimetype 文件类型
     * @param fileExt 文件扩展名
     * @return
     */
    Object mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);
}

