package org.xlm.jmsstudy;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class FileUploadServiceImpl implements IFileUploadService {

    //@Value("${fileUploadPath}")
    String uploadPath = "D:\\Java\\CodeProject\\jms-study\\src\\test";


    @Override
    public Object register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {

        //检查文件在磁盘上是否存在

        //文件所属目录的路径
        String fileFolderPath = this.getFileFolderPath(fileMd5);

        //文件路径
        String filePath = this.getFilePath(fileMd5, fileExt);

        File file = new File(filePath);
        //文件是否存在
        boolean exists = file.exists();

        //TODO 检查数据库中文件信息是否存在

        //文件不存,检查文件所在目录是否存在，不存在则创建
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }

        HashMap map = new HashMap<String, Integer>();
        map.put("ok", 1);

        return map;
    }


    @Override
    public Object checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {

        //得到分块文件的所在目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);

        //得到分块文件
        File chunkFile = new File(chunkFileFolderPath + chunk);

        //检查分块文件是否存在
        if (chunkFile.exists()) {
            //块文件存在
            return BaseUtil.back(1, "");
        } else {
            //块文件不存在
            return BaseUtil.back(0, "");
        }

    }


    @Override
    public Object uploadchunk(MultipartFile file, String fileMd5, Integer chunk) {


        //得到分块文件目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);

        //得到分块文件路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        //检查分块目录，不存在则要自动创建
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            boolean mkdirs = chunkFileFolder.mkdirs();
        }

        //得到上传文件的输入流
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(new File(chunkFilePath));
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return BaseUtil.back(1, "");

    }


    @Override
    public Object mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {


        //得到分块文件所属目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);

        //得到分块文件列表
        File[] files = chunkFileFolder.listFiles();
        List<File> fileList = Arrays.asList(files);

        //创建合并文件路径
        String filePath = this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);

        //执行合并
        mergeFile = this.mergeFile(fileList, mergeFile);
        if (mergeFile == null) {
            //合并文件失败
            BaseUtil.back(0, "");
        }


        //校验文件的md5值是否和前端传入的md5一致
        boolean checkFileMd5 = this.checkFileMd5(mergeFile, fileMd5);
        if (!checkFileMd5) {
            //校验文件失败
            BaseUtil.back(0, "");
        }

        //TODO 将文件的信息写入数据库
        String file_id = fileMd5;
        String file_name = fileName;
        String file_name_ext = fileMd5 + "." + fileExt;
        //文件路径保存相对路径
        String file_path = fileMd5.substring(0, 1) + File.separator + fileMd5.substring(1, 2) + File.separator + fileMd5 +File.separator + fileMd5 + "." + fileExt;
        ;
        Long file_size = fileSize;
        String file_type = mimetype;
        String file_ext = fileExt;


        return BaseUtil.back(1, "");
    }

    /**
     * 校验文件的完整性
     *
     * @param mergeFile
     * @param md5
     * @return
     */
    private boolean checkFileMd5(File mergeFile, String md5) {

        try {
            //创建文件输入流
            FileInputStream inputStream = new FileInputStream(mergeFile);
            //得到文件的md5
            String md5Hex = DigestUtils.md5Hex(inputStream);

            //和传入的md5比较
            if (md5.equalsIgnoreCase(md5Hex)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }


    /**
     * 文件合并
     *
     * @param chunkFileList
     * @param mergeFile
     * @return
     */
    private File mergeFile(List<File> chunkFileList, File mergeFile) {
        try {
            //如果合并文件已存在则删除，否则创建新文件
            if (mergeFile.exists()) {
                mergeFile.delete();
            } else {
                //创建一个新文件
                mergeFile.createNewFile();
            }

            //对块文件进行排序
            Collections.sort(chunkFileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                        return 1;
                    }
                    return -1;

                }
            });
            //创建一个写对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
            byte[] b = new byte[1024];
            for (File chunkFile : chunkFileList) {
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                }
                raf_read.close();
            }
            raf_write.close();
            return mergeFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取文件所属目录路径
     *
     * @param fileMd5 文件md5值
     * @return
     */
    private String getFileFolderPath(String fileMd5) {
        return uploadPath + fileMd5.substring(0, 1) + File.separator + fileMd5.substring(1, 2) + File.separator + fileMd5 + File.separator;
    }

    /**
     * 获取文件的路径
     *
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePath(String fileMd5, String fileExt) {
        return uploadPath + fileMd5.substring(0, 1) + File.separator + fileMd5.substring(1, 2) + File.separator + fileMd5 + File.separator + fileMd5 + "." + fileExt;
    }


    /**
     * 获取分块文件所在目录路径
     *
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return uploadPath + fileMd5.substring(0, 1) + File.separator + fileMd5.substring(1, 2) + File.separator+ fileMd5 + File.separator + "chunk" + File.separator;
    }
}
