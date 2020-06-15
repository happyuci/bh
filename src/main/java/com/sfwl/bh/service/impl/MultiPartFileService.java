package com.sfwl.bh.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author huhy
 * @version 1.0
 * @date 2020/5/12 16:52
 */
@Slf4j
@Service
public class MultiPartFileService {

    @Value("${upload.uploadPath}")
    private String uploadPath;
    @Value("${upload.urlPrefix}")
    private String urlPrefix;

    public Mono<String> uploadFile(FilePart filePart) throws IOException {
        if (Objects.isNull(filePart)) {
            return Mono.empty();
        }

        String originalFilename = filePart.filename();
        String prefix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (StringUtils.isBlank(prefix)) {
            prefix = "jpg";
        }
        String newFileName = UUID.randomUUID().toString() + "." + prefix;
        filePart.transferTo(Paths.get(uploadPath + newFileName).toFile());
        return Mono.just(urlPrefix + newFileName);
    }

    /**
     * 为 url 添加前缀
     *
     * @param url
     * @return
     */
    public String getWholeUrl(String url) {
        if (StringUtils.isNotBlank(url) && !url.contains(urlPrefix)) {
            return Arrays.stream(url.split(",")).map(u -> urlPrefix + u).collect(Collectors.joining(","));
        }
        return url;
    }

    /**
     * 删除 url 前缀
     *
     * @param url
     * @return
     */
    public String removeUrlPrefix(String url) {
        return url.replaceAll(urlPrefix, "");
    }
}
