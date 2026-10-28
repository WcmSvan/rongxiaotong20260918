package org.example.service.impl;

import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FileServiceImpl implements FileService {

    //上传文件在磁盘上的根目录，对应 application.yaml 中 application.upload-path
    @Value("${application.upload-path}")
    private String uploadPath;

    //图片后缀白名单：头像 / 商品图片 / 知识配图
    private static final Set<String> IMAGE_EXTS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp"));

    //贷款材料后缀白名单
    private static final Set<String> FINANCE_EXTS = new HashSet<>(
            Arrays.asList("pdf", "doc", "docx"));

    /**
     * 上传单张图片（头像 / 商品图 / 知识配图 ）
     */
    @Override
    public String uploadImage(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!IMAGE_EXTS.contains(ext)) {
            throw new RuntimeException("文件 [" + file.getOriginalFilename()
                    + "] 仅支持 jpg/jpeg/png/gif/webp 格式");
        }
        return saveFile(file, category, ext);
    }

    /**
     * 上传贷款材料多文件
     */
    @Override
    public List<String> uploadFinanceFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new RuntimeException("上传文件不能为空");
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String ext = getExtension(file.getOriginalFilename());
            if (!FINANCE_EXTS.contains(ext)) {
                throw new RuntimeException("文件 [" + file.getOriginalFilename()
                        + "] 仅支持 pdf/doc/docx 格式");
            }
            urls.add(saveFile(file, "finance", ext));
        }
        if (urls.isEmpty()) {
            throw new RuntimeException("没有有效的上传文件");
        }
        return urls;
    }


    /**
     * 取扩展名（小写）
     */
    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * 计算文件内容 MD5（十六进制 32 位字符串）
     * 用作落盘文件名：同一张图片内容相同 → MD5 相同 → 文件名相同，复用磁盘已存在文件，避免重复保存。
     */
    private String md5Hex(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) != -1) {
                md.update(buf, 0, n);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("计算文件摘要失败：" + e.getMessage());
        }
    }

    /**
     * 落盘到 {uploadPath}/{category}/{md5}.{ext}，并返回可访问 URL
     * 同一内容文件重复上传时，因 MD5 相同、文件名相同，目标文件已存在则跳过写入，磁盘只留一份。
     */
    private String saveFile(MultipartFile file, String category, String ext) {
        //根目录末尾补斜杠
        String root = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        File dir = new File(root + category);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("创建目录失败：" + dir.getAbsolutePath());
        }
        //按文件内容 MD5 命名，实现内容级去重
        String fileName = md5Hex(file) + "." + ext;
        File dest = new File(dir, fileName);
        if (!dest.exists()) {
            try {
                file.transferTo(dest.getCanonicalFile());
            } catch (IOException e) {
                throw new RuntimeException("文件保存失败：" + e.getMessage());
            }
        }
        //返回可直接访问的 URL（由 WebConfig 的 addResourceHandlers 映射 /upload/** 提供）
        return "/upload/" + category + "/" + fileName;
    }
}
