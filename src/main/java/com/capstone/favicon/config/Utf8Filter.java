package com.capstone.favicon.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;

import java.io.IOException;

@Component
public class Utf8Filter implements Filter{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.setCharacterEncoding("UTF-8");
        // Content-Type 을 여기서 정하면 안 된다. 응답 타입이 미리 고정되어 JSON 컨버터를 찾지 못하고
        // 모든 REST 응답이 500(HttpMessageNotWritableException)이 된다.
        chain.doFilter(request, response);
    }

}
