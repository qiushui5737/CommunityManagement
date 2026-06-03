package com.community.controller;

import com.community.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error(400, "文件不能为空");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return Result.error(400, "文件名无效");

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
            return Result.error(400, "仅支持 jpg/png/gif/webp 格式");
        }

        // 生成唯一文件名，防止覆盖
        String newFileName = UUID.randomUUID() + extension;
        String uploadDir = System.getProperty("user.dir") + "/uploads/repair/";

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs(); // 自动创建目录

        File dest = new File(uploadDir + newFileName);
        file.transferTo(dest);

        // 返回访问路径（前端通过 /uploads/repair/xxx.jpg 访问）
        Map<String, String> res = new HashMap<>();
        res.put("url", "/uploads/repair/" + newFileName);
        return Result.ok(res);
    }
}
