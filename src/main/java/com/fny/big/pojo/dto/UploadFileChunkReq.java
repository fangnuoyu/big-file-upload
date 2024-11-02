package com.fny.big.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Name UploadFileChunkReq
 * @Description UploadFileChunkReq
 * @Author HRT
 * @Date 2024/11/2 11:18
 * @Version 1.0.0
 **/
@Data
public class UploadFileChunkReq {

    // 分片文件
    private MultipartFile chunkFile;

    // 文件签名
    private String fileSign;

    // 文件名称
    private String fileName;

    // 文件大小
    private Long fileSize;

    // 分片大小
    private Long chunkSize;

    // 分片索引
    private Integer chunkIndex;

    // 分片总数
    private Integer chunkTotal;
}
