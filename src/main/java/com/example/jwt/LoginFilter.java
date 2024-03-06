package com.example.jwt;


import com.example.Dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;


@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    public LoginFilter(AuthenticationManager authenticationManager,JWTUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    //0. UsernamePasswordAuthenticationFilter 커스텀하는거임 formlogin 에서는 기본적으로 활성화 되어있음.
    //1. 로그인을 할때 가로챔
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //1.클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("0.Login attempt: Username: {}, password : {}", username,password);


        //2.스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
        Authentication result = authenticationManager.authenticate(authToken);

        log.info("1.Authentication status: {}", result.isAuthenticated() ? "SUCCESS" : "FAILURE");

        //3.token에 담은 검증을 위한 AuthenticationManager클래스 authenticate 메서드에 authToken 전달 => 성공,실패 둘중하나 전달
        return result;
    }


    //2. 로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal(); //() 타입변경
        String username = customUserDetails.getUsername();

        log.info("2.LoginFilter Login successful for user: {}, JWT Token generated and added to response headers", username);
        log.info("3.Login successful Username: {}", ((CustomUserDetails) authentication.getPrincipal()).getUsername());



        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        log.info("4.Role: {}", role);

        String token = jwtUtil.createJwt(username, role, 60*60*10L);

        log.info("5.JWT Token generated: {}", token);

        log.info("6.JWT Token added to response headers for user: {}", ((CustomUserDetails) authentication.getPrincipal()).getUsername());

        response.addHeader("Authorization", "Bearer " + token);

        log.info("7.JWT Token added to response headers");


    }

    //3. 로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);

        log.info("실패");
    }





}