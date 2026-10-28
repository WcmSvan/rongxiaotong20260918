package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传接口：头像 / 商品图片 /农业知识配图 / 贷款材料
 * 上传成功返回可直接访问的 URL，由前端写入对应业务字段。
 */
@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fileService;

    //上传头像（单文件，jpg/jpeg/png/gif/webp）
    @Operation(summary = "上传头像")
    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file, "avatar");
        return Result.success(url, "头像上传成功");
    }

    //上传商品图片（单文件，jpg/jpeg/png/gif/webp）
    @Operation(summary = "上传商品图片")
    @PostMapping("/uploadOrder")
    public Result uploadOrder(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file, "order");
        return Result.success(url, "商品图片上传成功");
    }

    //上传农业知识配图（单文件，jpg/jpeg/png/gif/webp）
    @Operation(summary = "上传农业知识配图")
    @PostMapping("/uploadKnowledge")
    public Result uploadKnowledge(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file, "knowledge");
        return Result.success(url, "知识配图上传成功");
    }

    //上传贷款材料（多文件，pdf/doc/docx）
    //返回多个 URL 列表，前端拼成 JSON 数组字符串写入 Finance.fileInfo 字段
    //Apifox 测试方法：form-data 中多行同名字段 files，每行选一个 File
    @Operation(summary = "上传贷款材料（多文件）")
    @PostMapping("/uploadFinance")
    public Result uploadFinance(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = fileService.uploadFinanceFiles(files);
        return Result.success(urls, "贷款材料上传成功");
    }
}
