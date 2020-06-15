package com.sfwl.bh.controller;

import com.sfwl.bh.service.impl.MultiPartFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private MultiPartFileService multiPartFileService;

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFile(@RequestPart(value = "file") FilePart filePart) throws IOException {
        return multiPartFileService.uploadFile(filePart);
    }
}
