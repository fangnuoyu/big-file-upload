package com.fny.big.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.tomcat.jni.FileInfo;

@Mapper
public interface FileInfoMapper {
    
    @Select("SELECT id, file_sign as fileSign, file_name as fileName, file_size as fileSize, upload_status as uploadStatus FROM file_info WHERE file_sign = #{fileSign}")
    FileInfo findByFileSign(@Param("fileSign") String fileSign);

    @Insert("INSERT INTO file_info (file_sign, file_name, file_size, upload_status) VALUES (#{fileSign}, #{fileName}, #{fileSize}, #{uploadStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertFileInfo(FileInfo fileInfo);

    @Update("UPDATE file_info SET upload_status = #{uploadStatus} WHERE id = #{id}")
    void updateUploadStatus(@Param("id") Long id, @Param("uploadStatus") Boolean uploadStatus);
}