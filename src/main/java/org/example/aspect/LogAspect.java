package org.example.aspect;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.annotation.Log;
import org.example.pojo.entity.OperationLog;
import org.example.service.OperationLogService;
import org.example.util.BaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.StringJoiner;

/**
 * 操作日志切面：拦截所有标注了 @Log 注解的 Controller 方法，
 * 自动记录操作人、模块、操作、请求信息、参数、耗时与结果并落库
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private OperationLogService operationLogService;

    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        long start = System.currentTimeMillis(); //记录耗时

        OperationLog opLog = new OperationLog()
                .setModule(log.module())
                .setOperation(log.operation())
                //当前登录用户信息来自登录拦截器写入的 ThreadLocal，不信任前端传参
                .setUserName(BaseContext.getCurrentUser())
                .setRole(BaseContext.getCurrentRole())
                .setCreateTime(new Date());

        //从当前请求上下文获取请求方式、URL 与 IP
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            opLog.setHttpMethod(request.getMethod())
                    .setUrl(request.getRequestURI())
                    .setIp(request.getRemoteAddr());
        }

        //序列化入参，过滤掉文件流、请求/响应对象等无法序列化的类型
        opLog.setParams(parseArgs(joinPoint.getArgs()));

        Object result;
        try {
            result = joinPoint.proceed();
            opLog.setStatus(1);//成功
        } catch (Throwable e) {
            opLog.setStatus(0);//失败
            opLog.setErrorMsg(e.getMessage());
            throw e;//原样抛出，不影响原有异常处理
        } finally {
            //登录接口的上下文是在 proceed 内部（业务层登录成功后）才写入的，保存前补取一次
            if (opLog.getUserName() == null) {
                opLog.setUserName(BaseContext.getCurrentUser());
                opLog.setRole(BaseContext.getCurrentRole());
            }
            opLog.setCostTime(System.currentTimeMillis() - start);
            try {
                operationLogService.save(opLog);
            } catch (Exception ex) {
                //日志落库失败只打印告警，不影响正常业务
                logger.warn("操作日志保存失败: {}", ex.getMessage());
            }
        }
        return result;
    }

    //将入参序列化为 JSON 字符串，跳过 MultipartFile 与 Servlet 相关类型
    private String parseArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(", ");
        for (Object arg : args) {
            if (arg == null
                    || arg instanceof MultipartFile
                    || arg instanceof HttpServletRequest
                    || arg instanceof jakarta.servlet.http.HttpServletResponse) {
                continue;
            }
            try {
                String json = JSON.toJSONString(arg);
                //参数过长截断，避免撑爆数据库字段
                if (json.length() > 2000) {
                    json = json.substring(0, 2000) + "...(截断)";
                }
                joiner.add(json);
            } catch (Exception e) {
                joiner.add(String.valueOf(arg));
            }
        }
        String params = joiner.toString();
        return params.isEmpty() ? null : params;
    }
}
