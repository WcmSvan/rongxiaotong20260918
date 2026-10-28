package org.example.config;

import org.example.intercepter.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//注册登录拦截器，并对登录/注册等公开接口放行；同时配置跨域与静态资源映射
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //上传文件在磁盘上的根目录，对应 application.yaml 中的 application.upload-path
    @Value("${application.upload-path}")
    private String uploadPath;

    //跨域配置，允许前端页面直接调用后端接口
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) //携带凭证
                .maxAge(3600);  //缓存3600s
    }

    //静态资源映射：/upload/** URL → 磁盘 d:/rongxiaotong/file/ 下子目录（avatar/order/finance）
    //前端拿到上传返回的 URL 后，直接 GET 即可访问到图片/文件，无需登录
    //把磁盘 `application.upload-path` 目录映射为 `/upload/**` URL，前端能直接 GET 图片 / 文件。
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //末尾补斜杠，确保拼接子目录时路径正确
        String location = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  //拦截所有请求
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/checkIsRegistered",
                        "/expert/discusslist",

                        "/order/All",
                        "/order/selectAllcontents",
                        "/order/selectAllneeds",
                        "/order/searchAllNeedsByKeys",
                        "/order/searchAllGoodsByKeys",

                        "/expert/discusslist",
                        "/expert/expertlist/**",
                        "/expert/knowledgelist/page/**",
                        "/expert/searchknowledge/page/**",
                        "/expert/knowledge/**",
                        "/expert/questionlist/page/**",
                        "/expert/searchquestion/page/**",
                        "/expert/selectQuestion/**",

                        "/finance/selectbank",
                        "/finance/selectFinaceUser/**",
                        "/question/findExpertUser/**",

                        //上传后的静态文件（图片/贷款文件）允许匿名直接访问，否则前端拿不到 URL
                        "/upload/**",

                        "/doc.html",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/error"
                );
    }
}
