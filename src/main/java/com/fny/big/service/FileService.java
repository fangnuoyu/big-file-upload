package com.fny.big.service;

import com.fny.big.pojo.dto.UploadFileChunkReq;
import com.fny.big.pojo.po.FileInfo;

import java.util.List;

/**
 * @Name FileService
 * @Description FileService
 * @Author HRT
 * @Date 2024/11/2 11:24
 * @Version 1.0.0
 **/
public interface FileService {

    FileInfo loadFileByFileSign(String fileSign);

    List<Integer> listUploadedChunks(String fileSign);

    Boolean uploadChunk(UploadFileChunkReq uploadFileChunkReq);
}
