package com.teamsparta.springauth.jwt;


import com.teamsparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";// Bearer라고 하고 한칸 띄어야함
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey. application.properties에 넣었던 그것과 연결
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct//이 메소드는 JwtUtil 객체가 생성된 후에 실행. 이 메소드는 비밀 키를 Base64 디코딩하여
    // java.security.Key 객체를 생성하고, 이 객체를 key 필드에 저장
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();//현재 날짜를 가져옴

        return BEARER_PREFIX +
                Jwts.builder()//새로운 JWT를 생성하기 위한 JwtBuilder 객체를 생성
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();// JWT를 문자열로 변환
    }

    // JWT Cookie 에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {//
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value를 받아 새 쿠키 생성
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());//에러 발생시 메시지
        }
    }
    // JWT 토큰 substring
    public String substringToken(String tokenValue) {// tokenValue가 null이 아니고, 공백이 아닌 텍스트를 포함하고 있는지 확인
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);//tokenValue에서 "Bearer " 접두사를 제거하고, 실제 토큰 값을 반환
            // substring(7)은 문자열의 7번째 인덱스부터 끝까지의 부분 문자열을 반환
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");//토큰이 유효하지 않으면 에러
    }//- StringUtils.hasText를 사용하여 공백, null을 확인하고 startsWith을 사용하여 토큰의 시작값이 Bearer이 맞는지 확인
//맞다면 순수 JWT를 반환하기 위해 substring을 사용하여 Bearer을 잘라냅니다.

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);//주어진 JWT를 파싱하고 검증
            return true;//JWT가 유효하면 true 아니면 에러메시지들
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }
    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }//주어진 jwt를 검증하고

}
