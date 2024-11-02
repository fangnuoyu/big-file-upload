package com.fny.big.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileChunkMapper {

    // 查询已上传的分片，返回所有成功上传的分片记录
    @Select("SELECT id,file_id as FileId,chunk_index as chunkIndex,chunk_size as chunkSize, upload_status as uploadStatus FROM file_chunk WHERE file_id = #{fileId} AND upload_status = TRUE")
    List<FileChunk> findUploadedChunksByFileId(@Param("fileId") Long fileId);

    // 查询指定索引的分片
    @Select("SELECT id,file_id as FileId,chunk_index as chunkIndex,chunk_size as chunkSize, upload_status as uploadStatus FROM file_chunk WHERE file_id = #{fileId} AND chunk_index = #{chunkIndex}")
    FileChunk findByFileIdAndChunkIndex(@Param("fileId") Long fileId, @Param("chunkIndex") int chunkIndex);

    // 插入分片信息
    @Insert("INSERT INTO file_chunk (file_id, chunk_index, chunk_size, upload_status) VALUES (#{fileId}, #{chunkIndex}, #{chunkSize}, #{uploadStatus})")
    void insertFileChunk(FileChunk fileChunk);

    // 更新分片的上传状态
    @Update("UPDATE file_chunk SET upload_status = #{uploadStatus} WHERE id = #{id}")
    void updateChunkStatus(@Param("id") Long id, @Param("uploadStatus") Boolean uploadStatus);

    // 统计文件已上传的分片数量
    @Select("SELECT COUNT(*) FROM file_chunk WHERE file_id = #{fileId} AND upload_status = TRUE")
    Integer countUploadedChunks(@Param("fileId") Long fileId);
}