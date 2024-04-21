package br.com.erudio.security.jwt;

import br.com.erudio.exceptions.InvalidJwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtTokenFilter extends GenericFilterBean {

    @Autowired
    private JwtTokenProvider tokenProvider;

    public JwtTokenFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            String token = tokenProvider.resolveToken((HttpServletRequest) request);
            var url = req.getRequestURL().toString();

            if (token != null && tokenProvider.validateToken(token)) {
                Authentication auth = tokenProvider.getAuthentication(token);
                if (auth != null) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } else if(!url.contains("/auth/")){
                throw new InvalidJwtAuthenticationException("ERROR - Token is Null!");
            }
        } catch (InvalidJwtAuthenticationException e){

                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                res.getWriter().write("Invalid token " + e.getMessage());
                res.getWriter().flush();
                return;
            }
        chain.doFilter(request, response);
    }

}
