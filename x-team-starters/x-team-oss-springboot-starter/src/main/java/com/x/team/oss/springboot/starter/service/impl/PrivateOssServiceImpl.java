package com.x.team.oss.springboot.starter.service.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.x.team.common.constants.HttpConstants;
import com.x.team.common.enums.ErrorCodeMsg;
import com.x.team.common.utils.JsonUtils;
import com.x.team.oss.springboot.starter.dto.BatchFileUrlRequestDto;
import com.x.team.oss.springboot.starter.dto.BatchFileUrlResponseDto;
import com.x.team.oss.springboot.starter.dto.StsDto;
import com.x.team.oss.springboot.starter.config.OssPrivateConfig;
import com.x.team.oss.springboot.starter.service.PrivateOssService;
import com.google.common.io.ByteStreams;
import com.x.team.redis.springboot.starter.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 描述：私有OSS接口实现类
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:10
 */
@Slf4j
@Service
public class PrivateOssServiceImpl implements PrivateOssService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private OssPrivateConfig ossPrivateConfig;

    private static final String THUMBNAIL = "style/gaussian_pic_thumbnail";
    private static final String STS_TOKEN = "oss_sts_private";
    private static final Long STS_TOKEN_EXPIRE_TIME = 60 * 30L;
    private static final Long STS_REDIS_TOKEN_EXPIRE_TIME = 60 * 25L;

    /**
     * 上传
     */
    @Override
    public void upload(String dirName, String fileName, File file) {
        OSS ossClient = this.getClient();
        try {
            ossClient.putObject(new PutObjectRequest(this.ossPrivateConfig.getBucketName(), dirName + fileName, file));
        } catch (Exception ex) {
            throw new RuntimeException(ErrorCodeMsg.OSS_ERROR.getEnMessage());
        } finally {
            this.closeClient(ossClient);
        }
    }

    /**
     * 上传
     */
    @Override
    public void upload(String dirName, String fileName, byte[] bytes) {
        OSS ossClient = this.getClient();
        try {
            ossClient.putObject(new PutObjectRequest(this.ossPrivateConfig.getBucketName(), dirName + fileName, new ByteArrayInputStream(bytes)));
        } catch (Exception ex) {
            throw new RuntimeException(ErrorCodeMsg.OSS_ERROR.getEnMessage());
        } finally {
            this.closeClient(ossClient);
        }
    }

    /**
     * 下载(file)
     */
    @Override
    public void download(String dirName, String fileName, File file) {
        OSS ossClient = this.getClient();
        try {
            ossClient.getObject(new GetObjectRequest(this.ossPrivateConfig.getBucketName(), dirName + fileName), file);
        } catch (Exception ex) {
            throw new RuntimeException(ErrorCodeMsg.OSS_ERROR.getEnMessage());
        } finally {
            this.closeClient(ossClient);
        }
    }

    /**
     * 下载(InputStream)
     */
    @SuppressWarnings("all")
    @Override
    public byte[] download(String dirName, String fileName) {
        OSS ossClient = this.getClient();
        OSSObject ossObject = null;
        InputStream inputStream = null;
        try {
            ossObject = ossClient.getObject(this.ossPrivateConfig.getBucketName(), dirName + fileName);
            inputStream = ossObject.getObjectContent();
            return ByteStreams.toByteArray(inputStream);
        } catch (Exception ex) {
            throw new RuntimeException(ErrorCodeMsg.OSS_ERROR.getEnMessage());
        } finally {
            if (Objects.nonNull(ossObject)) {
                try {
                    ossObject.close();
                } catch (IOException e) {
                    log.error("【private-oss】: 关闭流失败");
                }
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("【private-oss】: 关闭流失败");
            }
            this.closeClient(ossClient);
        }
    }

    /**
     * 获取文件路径地址
     */
    @Override
    public String getFileUrl(String dirName, String fileName, Integer expireSecond) {
        OSS client = this.getClient();
        try {
            Date expiration = new Date(System.currentTimeMillis() + expireSecond * 1000L);
            URL url = client.generatePresignedUrl(this.ossPrivateConfig.getBucketName(), dirName + fileName, expiration);
            return url.toString();
        } finally {
            this.closeClient(client);
        }
    }

    /**
     * 获取缩略图文件路径地址
     */
    @Override
    public String getSmallFileUrl(String dirName, String fileName, Integer expireSecond) {
        OSS client = this.getClient();
        try {
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(this.ossPrivateConfig.getBucketName(), dirName + fileName, HttpMethod.GET);
            req.setExpiration(new Date(System.currentTimeMillis() + expireSecond * 1000L));
            req.setProcess(THUMBNAIL);
            URL url = client.generatePresignedUrl(req);
            return url.toString();
        } finally {
            this.closeClient(client);
        }
    }

    /**
     * 获取文件路径列表
     */
    @SuppressWarnings("all")
    @Override
    public List<BatchFileUrlResponseDto> getFileUrls(List<BatchFileUrlRequestDto> batchFileUrlRequests, Integer expireSecond) {
        if (!CollectionUtils.isEmpty(batchFileUrlRequests)) {
            OSS client = this.getClient();
            Date expiration;
            if (Objects.nonNull(expireSecond) && 0 < expireSecond) {
                expiration = new Date(System.currentTimeMillis() + expireSecond * 1000L);
            } else {
                expiration = new Date(System.currentTimeMillis() + 600 * 1000L);
            }
            try {
                List<BatchFileUrlResponseDto> rtn = new ArrayList<>(batchFileUrlRequests.size());
                for (BatchFileUrlRequestDto batchFileUrlRequest : batchFileUrlRequests) {
                    URL url = client.generatePresignedUrl(this.ossPrivateConfig.getBucketName(), batchFileUrlRequest.getDirName() + batchFileUrlRequest.getFileName(), expiration);
                    rtn.add(BatchFileUrlResponseDto.builder()
                            .dirName(batchFileUrlRequest.getDirName())
                            .fileName(batchFileUrlRequest.getFileName())
                            .ossId(batchFileUrlRequest.getOssId())
                            .url(url.toString())
                            .build());
                }
                return rtn;
            } finally {
                this.closeClient(client);
            }
        }
        return null;
    }

    /**
     * 获取缩略图文件路径列表
     */
    @SuppressWarnings("all")
    @Override
    public List<BatchFileUrlResponseDto> getSmallFileUrls(List<BatchFileUrlRequestDto> batchFileUrlRequests, Integer expireSecond) {
        if (!CollectionUtils.isEmpty(batchFileUrlRequests)) {
            OSS client = this.getClient();
            Date expiration;
            if (Objects.nonNull(expireSecond) && 0 < expireSecond) {
                expiration = new Date(System.currentTimeMillis() + expireSecond * 1000L);
            } else {
                expiration = new Date(System.currentTimeMillis() + 600 * 1000L);
            }
            try {
                List<BatchFileUrlResponseDto> rtn = new ArrayList<>(batchFileUrlRequests.size());
                for (BatchFileUrlRequestDto batchFileUrlRequest : batchFileUrlRequests) {
                    GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(this.ossPrivateConfig.getBucketName(), batchFileUrlRequest.getDirName() + batchFileUrlRequest.getFileName(), HttpMethod.GET);
                    req.setExpiration(expiration);
                    req.setProcess(THUMBNAIL);
                    URL url = client.generatePresignedUrl(req);
                    rtn.add(BatchFileUrlResponseDto.builder()
                            .dirName(batchFileUrlRequest.getDirName())
                            .fileName(batchFileUrlRequest.getFileName())
                            .ossId(batchFileUrlRequest.getOssId())
                            .url(url.toString())
                            .build());
                }
                return rtn;
            } finally {
                this.closeClient(client);
            }
        }
        return null;
    }

    /**
     * 获取STS临时TOKEN
     */
    @SuppressWarnings("all")
    @Override
    public synchronized StsDto getSts() {
        if (this.redisClient.hasKey(STS_TOKEN)) {
            String o = (String) this.redisClient.get(STS_TOKEN);
            try {
                return JsonUtils.OBJECT_MAPPER.readValue(o, StsDto.class);
            } catch (JsonProcessingException e) {
                log.error("【private-oss】(sts): 获取STS-TOKEN失败, error: {}", e.getMessage());
                throw new RuntimeException(ErrorCodeMsg.OSS_ERROR.getEnMessage());
            }
        } else {
            try {
                IClientProfile profile = DefaultProfile.getProfile(this.ossPrivateConfig.getRegionId(), this.ossPrivateConfig.getAccessKeyId(), this.ossPrivateConfig.getAccessKeySecret());
                DefaultAcsClient client = new DefaultAcsClient(profile);
                final AssumeRoleRequest request = new AssumeRoleRequest();
                request.setSysMethod(MethodType.POST);
                request.setRoleArn(this.ossPrivateConfig.getRoleArn());
                request.setRoleSessionName(this.ossPrivateConfig.getRoleSessionName());
                // 如果policy为空，则用户将获得该角色下所有权限。
                request.setPolicy(this.ossPrivateConfig.getPolicy());
                // 设置临时访问凭证的有效时间为600秒。
                request.setDurationSeconds(STS_TOKEN_EXPIRE_TIME);
                final AssumeRoleResponse response = client.getAcsResponse(request);
                StsDto build = StsDto.builder()
                        .endPoint(this.ossPrivateConfig.getEndpoint())
                        .expiration(response.getCredentials().getExpiration())
                        .accessKeyId(response.getCredentials().getAccessKeyId())
                        .accessKeySecret(response.getCredentials().getAccessKeySecret())
                        .securityToken(response.getCredentials().getSecurityToken())
                        .requestId(response.getRequestId())
                        .regionId(this.ossPrivateConfig.getRegionId())
                        .bucket(this.ossPrivateConfig.getBucketName())
                        .build();
                try {
                    this.redisClient.set(STS_TOKEN, JsonUtils.OBJECT_MAPPER.writeValueAsString(build), STS_REDIS_TOKEN_EXPIRE_TIME);
                } catch (JsonProcessingException e) {
                    log.error("【private-oss】(sts): 获取STS-TOKEN失败, error: {}", e.getMessage());
                    throw new RuntimeException(ErrorCodeMsg.OSS_ERROR.getEnMessage());
                }
                return build;
            } catch (ClientException e) {
                log.error("【private-oss】(sts): 获取STS-TOKEN失败, error: {}, request-id: {}", e.getMessage(), e.getRequestId());
                throw new RuntimeException(ErrorCodeMsg.OSS_ERROR.getEnMessage());
            }
        }
    }

    private OSS getClient() {
        StsDto sts = this.getSts();
        return new OSSClientBuilder().build(HttpConstants.HTTPS_HEADER + this.ossPrivateConfig.getEndpoint(), sts.getAccessKeyId(), sts.getAccessKeySecret(), sts.getSecurityToken());
    }

    private void closeClient(OSS ossClient) {
        if (!Objects.isNull(ossClient)) {
            ossClient.shutdown();
        }
    }
}
