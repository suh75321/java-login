package com.teamsparta.springauth.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api")
public class AuthController {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @GetMapping("/create-cookie")
    public String createCookie(HttpServletResponse res) {
        addCookie("Robbie Auth", res);//공백을 일부러 넣어서 공백 대체법 알아보기

        return "createCookie";
    }//쿠키 생성

    @GetMapping("/get-cookie")
    public String getCookie(@CookieValue(AUTHORIZATION_HEADER) String value) {
        System.out.println("value = " + value);

        return "getCookie : " + value;
    }//쿠키 조회

    public static void addCookie(String cookieValue, HttpServletResponse res) {//Robbie Auth를 받아서 쿠키 생성.
        try {
            cookieValue = URLEncoder.encode(cookieValue, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행
//cookieValue를 인코딩. URL 인코딩은 특수 문자, 공백 등을 안전하게 전송할 수 있는 형식으로 변환하는 과정.
// 여기서 "utf-8"은 인코딩에 사용할 문자셋 공백을  '+'로 변환. 그러나 쿠키에서는 '+'가 올바르게 해석되지 않으므로, 이를 '%20’으로 대체

            //이 부분은 새로운 쿠키를 생성. 쿠키의 이름은 AUTHORIZATION_HEADER 상수에 지정된 값이며,
            // 쿠키의 값은 cookieValue 매개변수에 지정된 값
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue); // Name-Value

            cookie.setPath("/");//쿠키의 경로
            cookie.setMaxAge(30 * 60);//쿠키의 수명. 30분

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);//cookie를 HTTP 응답에 추가. 이렇게 하면 클라이언트는 이 쿠키를 받아서 저장하고,
            // 이후 요청에서 이 쿠키를 서버에 전송할 수 있다.
        } catch (UnsupportedEncodingException e) {//주어진 문자셋이 지원 안하는거면 에러메시지
            throw new RuntimeException(e.getMessage());
        }
    }

    //세션
    @GetMapping("/create-session")
    public String createSession(HttpServletRequest req) {
        // 세션이 존재할 경우 세션 반환, 없을 경우 새로운 세션을 생성한 후 반환
        HttpSession session = req.getSession(true);

        // 세션에 저장될 정보 Name - Value 를 추가합니다. 즉 Robbie Auth를 저장
        session.setAttribute(AUTHORIZATION_HEADER, "Robbie Auth");

        return "createSession";//그리고 createSession을 출력
    }

    @GetMapping("/get-session")
    public String getSession(HttpServletRequest req) {
        // 세션이 존재할 경우 세션 반환, 없을 경우 null 반환
        HttpSession session = req.getSession(false);

        String value = (String) session.getAttribute(AUTHORIZATION_HEADER); // 가져온 세션에 저장된 Value 를 Name 을 사용하여 가져옵니다.
        System.out.println("value = " + value);

        return "getSession : " + value;
    }
}
