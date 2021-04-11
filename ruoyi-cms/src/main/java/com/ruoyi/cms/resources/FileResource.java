package com.ruoyi.cms.resources;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Aiolos
 * @date 2020/11/9 4:02 上午
 */
@Data
@Component
@PropertySource("classpath:file-dev.properties")
@ConfigurationProperties(prefix = "file")
public class FileResource {

    private String host;

    private String ossHost;

    private String endpoint;

    private String bucketName;

    private String objectName;

    private String prefix;
}
