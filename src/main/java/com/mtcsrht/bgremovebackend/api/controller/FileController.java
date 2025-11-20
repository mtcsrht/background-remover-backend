package com.mtcsrht.bgremovebackend.api.controller;

import com.mtcsrht.bgremovebackend.api.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String folder,
                                              @PathVariable String filename){
        String objectKey = folder + "/" + filename;
        Resource resource = fileService.loadAsResource(objectKey);

        System.out.println("Serving: " + objectKey);
        System.out.println("exists=" + (resource != null && resource.exists()));
        System.out.println("readable=" + (resource != null && resource.isReadable()));

        if (resource == null || !resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

}
