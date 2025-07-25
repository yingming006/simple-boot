package com.example.simple.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储相关的配置属性类。
 * 映射 application.yml 中以 "file" 开头的配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    /**
     * 文件存储类型, 可选值: local, aliyun-oss, tencent-cos, huawei-obs
     */
    private String storageType = "local";

    /**
     * 本地存储的根路径
     */
    private String rootPath;

    /**
     * 文件访问的URL前缀
     */
    private String accessUrlPrefix;

    /**
     * 云存储服务商配置 (嵌套类)
     */
    private Provider provider = new Provider();

    @Data
    public static class Provider {
        private AliyunOss aliyunOss = new AliyunOss();
        private TencentCos tencentCos = new TencentCos();
        private HuaweiObs huaweiObs = new HuaweiObs();

        @Data
        public static class AliyunOss {
            private String endpoint;
            private String accessKeyId;
            private String accessKeySecret;
            private String bucketName;
        }

        @Data
        public static class TencentCos {
            private String region;
            private String secretId;
            private String secretKey;
            private String bucketName;
        }

        @Data
        public static class HuaweiObs {
            private String endpoint;
            private String accessKeyId;
            private String accessKeySecret;
            private String bucketName;
        }
    }
}