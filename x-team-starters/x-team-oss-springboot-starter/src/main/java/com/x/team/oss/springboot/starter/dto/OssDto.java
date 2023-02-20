package com.x.team.oss.springboot.starter.dto;

/**
 * 描述：OSS struct
 *
 * @author MassAdobe
 * @date Created in 2023/1/30 18:08
 */
public class OssDto {

    /**
     * file only-one identifier
     */
    private String ossIdentifier;
    /**
     * file name
     */
    private String ossName;
    /**
     * file address
     */
    private String ossUrl;

    public OssDto() {
    }

    public OssDto(String ossIdentifier, String ossName, String ossUrl) {
        this.ossIdentifier = ossIdentifier;
        this.ossName = ossName;
        this.ossUrl = ossUrl;
    }

    public String getOssIdentifier() {
        return ossIdentifier;
    }

    public void setOssIdentifier(String ossIdentifier) {
        this.ossIdentifier = ossIdentifier;
    }

    public String getOssName() {
        return ossName;
    }

    public void setOssName(String ossName) {
        this.ossName = ossName;
    }

    public String getOssUrl() {
        return ossUrl;
    }

    public void setOssUrl(String ossUrl) {
        this.ossUrl = ossUrl;
    }

    @Override
    public String toString() {
        return "OssDto{" +
                "ossIdentifier='" + ossIdentifier + '\'' +
                ", ossName='" + ossName + '\'' +
                ", ossUrl='" + ossUrl + '\'' +
                '}';
    }
}
