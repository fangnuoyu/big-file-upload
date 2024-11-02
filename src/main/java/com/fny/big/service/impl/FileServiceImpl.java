package com.fny.big.service.impl;

import com.fny.big.mapper.FileChunkMapper;
import com.fny.big.mapper.FileInfoMapper;
import com.fny.big.pojo.dto.UploadFileChunkReq;
import com.fny.big.pojo.po.FileChunk;
import com.fny.big.pojo.po.FileInfo;
import com.fny.big.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Name FileServiceImpl
 * @Description FileServiceImpl
 * @Author HRT
 * @Date 2024/11/2 11:30
 * @Version 1.0.0
 **/
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private FileChunkMapper fileChunkMapper;

    private final String bigFileUploadDirPath = "bigFileUploadDir"; // 文件上传目录

    /**
     * 根据文件签名&上传状态查询文件
     * @param fileSign 文件签名
     * @return
     */
    @Override
    public FileInfo loadFileByFileSign(String fileSign) {
        FileInfo fileInfo = fileInfoMapper.findByFileSign(fileSign);
        if( Objects.isNull(fileInfo) || !fileInfo.getUploadStatus() ) {
            return null;
        }
        return fileInfo;
    }

    /**
     * 根据文件签名查询分片列表
     * @param fileSign 文件签名
     * @return
     */
    @Override
    public List<Integer> listUploadedChunks(String fileSign) {
        FileInfo fileInfo = fileInfoMapper.findByFileSign(fileSign);
        if(Objects.isNull(fileInfo)) return new ArrayList<>();

        List<FileChunk> fileChunkList = fileChunkMapper.findUploadedChunksByFileId(fileInfo.getId());
        fileChunkList = fileChunkList.stream().filter(fileChunk -> fileChunk.getUploadStatus()).collect(Collectors.toList());
        return fileChunkList.stream().map(FileChunk::getChunkIndex).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean uploadChunk(UploadFileChunkReq uploadFileChunkReq) {

        String fileSign = uploadFileChunkReq.getFileSign();
        String fileName = uploadFileChunkReq.getFileName();
        Long fileSize = uploadFileChunkReq.getFileSize();
        MultipartFile uplaodChunkFile = uploadFileChunkReq.getChunkFile();
        Long uploadChunkSize = uploadFileChunkReq.getChunkSize();
        Integer uplaodChunkIndex = uploadFileChunkReq.getChunkIndex();
        Integer uplaodChunkTotal = uploadFileChunkReq.getChunkTotal();

        FileInfo fileInfo = fileInfoMapper.findByFileSign(fileSign);
        if(Objects.isNull(fileInfo)) {
            fileInfo = new FileInfo();
            fileInfo.setFileSign(fileSign);
            fileInfo.setFileName(fileName);
            fileInfo.setFileSize(fileSize);
            fileInfo.setUploadStatus(false);
            fileInfoMapper.insertFileInfo(fileInfo);
        }

        FileChunk existingChunk = fileChunkMapper.findByFileIdAndChunkIndex(fileInfo.getId(), uplaodChunkIndex);
        if (existingChunk != null && existingChunk.getUploadStatus()) {
            return true;
        }

        try {
            // 创建分片存储目录
            String chunkFileUploadDirPath = Paths.get(bigFileUploadDirPath, fileSign + "_chunks").toAbsolutePath().toString();
            File chunkFileUploadDir = new File(chunkFileUploadDirPath);
            if ( !chunkFileUploadDir.exists() ) chunkFileUploadDir.mkdirs();

            // 将当前分片写入文件系统
            File chunkFile = new File(chunkFileUploadDir, uplaodChunkIndex + "");
            uplaodChunkFile.transferTo(chunkFile);

            // 记录或更新分片信息
            insertOrUpdateFileChunk(existingChunk, fileInfo, uplaodChunkIndex, uploadChunkSize);
            // 合并分片
            mergeChunkFile(fileInfo.getId(), fileSign, fileName, uplaodChunkTotal);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;
    }

    private void insertOrUpdateFileChunk(FileChunk fileChunk, FileInfo fileInfo, Integer chunkIndex, Long chunkSize) {
        if(fileChunk == null) {
            FileChunk finishFileChunk = new FileChunk();
            finishFileChunk.setFileId(fileInfo.getId());
            finishFileChunk.setChunkIndex(chunkIndex);
            finishFileChunk.setChunkSize(chunkSize);
            finishFileChunk.setUploadStatus(true);
            fileChunkMapper.insertFileChunk(finishFileChunk);
        } else {
            fileChunkMapper.updateChunkStatus(fileInfo.getId(), true);
        }
    }

    private void mergeChunkFile(Long fileId, String fileSign, String fileName, Integer uplaodChunkTotal) {
        Integer chunkTotal = fileChunkMapper.countUploadedChunks(fileId);
        // 分片还没有上传完, 不用合并
        if (chunkTotal != uplaodChunkTotal) {
            return;
        }
        // 所有的分片已上传完, 执行合并操作
        try(RandomAccessFile raf = new RandomAccessFile(Paths.get(bigFileUploadDirPath, fileName).toAbsolutePath().toString(), "rw")) {
            for (int i = 0; i < uplaodChunkTotal; i++) {
                File chunkFile = new File(Paths.get(bigFileUploadDirPath, fileSign + "_chunks", i + "").toAbsolutePath().toString());
                byte[] bytes = Files.readAllBytes(chunkFile.toPath());
                raf.write(bytes);
                chunkFile.delete();
            }
            File chunkDir = new File(Paths.get(bigFileUploadDirPath, fileSign + "_chunks").toAbsolutePath().toString());
            chunkDir.delete();
            // 更新文件上传状态
            fileInfoMapper.updateUploadStatus(fileId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
