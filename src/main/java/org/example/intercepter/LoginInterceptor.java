package org.example.intercepter;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.common.Result;
import org.example.util.BaseContext;
import org.example.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

//登录拦截器：用于校验用户是否已登录，若未登录则返回未登录状态
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("执行LoginInterceptor拦截器");
        //判断请求是否是controller请求 Header instanceof HandlerInterceptor 判断当前请求是不是controller请求
        if (!(handler instanceof HandlerMethod)){
            System.out.println("不是controller请求");
            return true;//表示放行
        }
        //从请求头中获取token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            System.out.println("token为空");
            //如果token为空，表示用户未登录，返回未登录状态
            return writeUnauthorized(response,"未登录，请先登录");//表示拦截
        }
        //jwt验证（验证token是否有效、是否在8小时有效期内）
        if (!JwtUtil.validateToken(token)) {
            System.out.println("token无效");
            //如果token无效，表示用户未登录，返回未登录状态
            return writeUnauthorized(response,"token无效，请重新登录");//表示拦截
        }

        //从token中解析当前登录用户名与角色，并存入线程上下文，便于后续业务获取当前登录用户
        String userName = JwtUtil.parseUsername(token);
        String role = JwtUtil.parseRole(token);
        BaseContext.setCurrentUser(userName);
        BaseContext.setCurrentRole(role);

        //角色校验：若目标方法或其所在类标注了 @RequireRole，则校验当前角色是否满足要求
        if (handler instanceof HandlerMethod handlerMethod) {
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole == null) {
                requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
            }
            if (requireRole != null && !roleMatched(role, requireRole)) {
                BaseContext.clear();
                try {
                    return writeForbidden(response, "权限不足，无法访问该资源");
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        //请求结束清理线程上下文，防止线程复用导致的内存泄漏与数据串号
        BaseContext.clear();
    }

    //根据 @RequireRole 配置判断当前角色是否匹配
    private boolean roleMatched(String role, RequireRole requireRole) {
        String[] requiredRoles = requireRole.value();
        //没有角色注解
        if (requiredRoles.length == 0) {
            return true;
        }
        if (role == null || role.isEmpty()) {
            return false;
        }
        //如果要求所有角色都匹配，则所有角色都必须满足要求
        if (requireRole.requireAll()) {
            for (String r : requiredRoles) {
                if (!role.equals(r)) {
                    return false;
                }
            }
            return true;
        }
        //如果要求任意角色匹配，则满足要求即可
        for (String r : requiredRoles) {
            if (role.equals(r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 写入未登录状态响应（401）
     */
       private boolean writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.error(message)));
        return false;
    }

    /**
     * 写入权限不足状态响应（403）
     */
    private boolean writeForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.error(message)));
        return false;
    }
}
