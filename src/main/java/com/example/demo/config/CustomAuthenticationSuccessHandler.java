package com.example.demo.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String redirectURL = httpServletRequest.getContextPath();
        for(final GrantedAuthority grantedAuthority:userDetails.getAuthorities()){
            if (grantedAuthority.getAuthority().equals("ROLE_USER")){
                redirectURL = "/user/index";
            } else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                redirectURL = "/admin/index";
            }
        }
        httpServletResponse.sendRedirect(redirectURL);
    }
}
