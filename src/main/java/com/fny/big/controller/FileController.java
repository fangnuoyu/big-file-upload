package com.fny.big.controller;

import com.fny.big.pojo.dto.UploadFileChunkReq;
import com.fny.big.pojo.po.FileInfo;
import com.fny.big.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Name FileController
 * @Description FileController
 * @Author HRT
 * @Date 2024/11/2 11:02
 * @Version 1.0.0
 **/
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 检查文件是否存在
     * @param fileSign 文件签名
     */
    @GetMapping("/load-file-by-file-sign")
    public FileInfo loadFileByFileSign(@RequestParam("fileSign") String fileSign) {
        return fileService.loadFileByFileSign(fileSign);
    }

    /**
     * 列出上传分片索引
     * @param fileSign 文件签名
     */
    @GetMapping("/list-uploaded-chunks")
    public List<Integer> listUploadedChunks(@RequestParam("fileSign") String fileSign) {
        return fileService.listUploadedChunks(fileSign);
    }

    /**
     * 上传分片文件
     * @param uploadFileChunkReq
     * @return
     */
    @PostMapping("/upload-chunk")
    public Boolean uploadChunk(UploadFileChunkReq uploadFileChunkReq) {
        return fileService.uploadChunk(uploadFileChunkReq);
    }

}
