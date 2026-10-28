package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.util.Date;

//封装JWT生成和解析的方法
public class JwtUtil {
   // 密钥字符串（需 >=32 字符以满足 HS256 要求）
    private static final String SECRET_KEY = "rongxiaotong_secret_key_lsy_ljl_2026";
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    // token 有效期：8 小时
    private static final long EXPIRATION = 8 * 60 * 60 * 1000L;

    //生成JWT（携带用户名和角色，有效期8小时）
    public static String generateToken(String userName, String role) {
        return Jwts.builder()
                .setSubject(userName)//设置主题
                .claim("userName", userName)//设置用户
                .claim("role", role == null ? "" : role)//设置角色
                .setIssuedAt(new Date())//设置发行时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))//设置过期时间
                .signWith(SIGNING_KEY)//设置签名密钥
                .compact();//生成JWT字符串
    }
         //解析JWT字符串,返回值为Claims类型
        public static Claims parseJWT(String token) {
             return Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();//返回payload部分
         }
        

        //解析token，从token中获取用户名
        public static String parseUsername(String token) {
             return parseJWT(token).get("userName", String.class);
        }
        //解析token，从token中获取角色
        public static String parseRole(String token) {
             return parseJWT(token).get("role", String.class);
        }   

        //验证token是否有效（含8小时有效期校验）
        public static boolean validateToken(String token) {
            try {
                parseJWT(token);
                return true;
            } catch (Exception e) {
                return false;
            }
    }
}
















