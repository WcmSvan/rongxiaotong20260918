package org.example.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前登录用户工具类
 * 从当前请求携带的 token 中解析出登录用户名，供 Controller / Service 复用
 */
public class CurrentUserUtil {

    //工具类不允许实例化
    private CurrentUserUtil() {
    }

    /**
     * 获取当前登录用户的用户名
     *
     * @return 登录用户名；未登录或 token 无效时返回 null
     */
    public static String getCurrentUsername() {
        String token = getCurrentToken();
        if (token == null || token.isEmpty() || !JwtUtil.validateToken(token)) {
            return null;
        }
        return JwtUtil.parseUsername(token);
    }

    /**
     * 获取当前请求携带的 token
     * 优先从 token 头获取，其次从 Authorization: Bearer xxx 获取
     *
     * @return token 字符串；取不到时返回 null
     */
    public static String getCurrentToken() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
        }
        return token;
    }
}