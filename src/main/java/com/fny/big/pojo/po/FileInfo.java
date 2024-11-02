package com.fny.big.pojo.po;

import lombok.Data;

@Data
public class FileInfo {
    // 文件id
    private Long id;
    // 文件签名
    private String fileSign;
    // 文件名称
    private String fileName;
    // 文件大小
    private Long fileSize;
    // 上传状态
    private Boolean uploadStatus;
}
