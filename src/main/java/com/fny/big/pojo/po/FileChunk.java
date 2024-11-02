package com.fny.big.pojo.po;

import lombok.Data;

@Data
public class FileChunk {
    // 分片id
    private Long id;
    // 文件id
    private Long fileId;
    // 分片索引
    private Integer chunkIndex;
    // 分片大小
    private Long chunkSize;
    // 上传状态
    private Boolean uploadStatus;
}
