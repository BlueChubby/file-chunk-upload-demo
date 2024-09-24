package org.xlm.jmsstudy;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//@SpringBootTest
class JmsStudyApplicationTests {

    @Test
    void contextLoads() throws IOException {

        // 源文件
        File sourceFile = Paths.get("E:\\地平线4DLC.zip").toFile();

        //块文件目录
        String chunkFileFolder = "E:\\chunks\\";

        //定义块文件大小
        long chunkFileSize = 20 * 1024 * 1024;
        //块数
        long chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);
        //创建读文件的对象
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

        //缓冲区
        byte[] b = new byte[1024];
        for (long i = 0; i < chunkFileNum; i++) {
            //分片文件路径
            File chunkFile = Paths.get(chunkFileFolder + "chunk" + i).toFile();

            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
            int len = -1;

            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
                //如果块文件的大小达到2M开始写下一块
                if (chunkFile.length() >= chunkFileSize) {
                    break;
                }
            }
            raf_write.close();
        }
        raf_read.close();

    }

    @Test
    public void testMergeFile() throws IOException {
        //块文件目录
        String chunkFileFolderPath = "E:\\chunks\\";
        //块文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        //分块文件列表
        File[] files = chunkFileFolder.listFiles();
        //转成集合，便于排序  将块文件排序，按名称升序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if(Integer.parseInt(o1.getName().replace("chunk",""))>Integer.parseInt(o2.getName().replace("chunk",""))){
                    return 1;
                }
                return -1;

            }
        });

        //合并文件
        File mergeFile = new File("E:\\chunks\\test_merge.zip");
        //创建新文件
        boolean newFile = mergeFile.createNewFile();

        //创建写对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");

        byte[] b = new byte[1024];
        for(File chunkFile:fileList){
            //创建一个读块文件的对象
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
            int len = -1;
            while((len = raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
    }


}
