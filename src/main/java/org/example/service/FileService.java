package org.example.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传服务：头像 / 商品图片 /农业知识配图 / 贷款文件
 * 上传成功后返回可直接访问的 URL（如 /upload/avatar/xxx.jpg），
 * 由调用方写入对应实体的字段（User.avatar / Order.picture / Finance.fileInfo）。
 */
public interface FileService {

    /**
     * 上传单张头像/商品图片/农业知识配图（jpg/jpeg/png/gif/webp）
     * @param file 前端 multipart 表单文件
     * @param category 子目录名：avatar / order / knowledge
     * @return 可访问 URL，如 /upload/avatar/uuid.jpg
     */
    String uploadImage(MultipartFile file, String category);

    /**
     * 上传贷款材料（多文件，pdf/doc/docx）
     * @param files 前端 multipart 表单多个同名 files 字段
     * @return 多个 URL 列表，调用方拼成 JSON 数组字符串存入 Finance.fileInfo
     */
    List<String> uploadFinanceFiles(MultipartFile[] files);
}
