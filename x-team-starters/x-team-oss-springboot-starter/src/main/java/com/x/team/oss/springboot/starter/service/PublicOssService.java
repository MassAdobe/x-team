package com.x.team.oss.springboot.starter.service;

import com.x.team.oss.springboot.starter.dto.BatchFileUrlRequestDto;
import com.x.team.oss.springboot.starter.dto.BatchFileUrlResponseDto;
import com.x.team.oss.springboot.starter.dto.StsDto;

import java.io.File;
import java.util.List;

/**
 * 描述：共有OSS接口类
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:11
 */
public interface PublicOssService {

    /**
     * 上传
     */
    void upload(String dirName, String fileName, File file);

    /**
     * 上传
     */
    void upload(String dirName, String fileName, byte[] bytes);

    /**
     * 下载(file)
     */
    void download(String dirName, String fileName, File file);

    /**
     * 下载(InputStream)
     */
    byte[] download(String dirName, String fileName);

    /**
     * 获取文件路径地址
     */
    String getFileUrl(String dirName, String fileName);

    /**
     * 获取文件路径列表
     */
    List<BatchFileUrlResponseDto> getFileUrls(List<BatchFileUrlRequestDto> batchFileUrlRequests);

    /**
     * 获取STS临时TOKEN
     */
    StsDto getSts();
}
