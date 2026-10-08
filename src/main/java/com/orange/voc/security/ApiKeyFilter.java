package com.orange.voc.security;

import com.orange.voc.repo.ApiKeyRepository;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyFilter extends OncePerRequestFilter {
    private final ApiKeyRepository keys;
    public ApiKeyFilter(ApiKeyRepository keys) { this.keys = keys; }

    @Override protected boolean shouldNotFilter(HttpServletRequest r) { return !r.getRequestURI().substring(r.getContextPath().length()).startsWith("/api/v1/"); }

    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String raw = req.getHeader("X-API-Key");
        if (raw != null) {
            keys.findByKeyHashAndActiveTrue(Keys.sha256(raw)).ifPresent(k -> SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("api:" + k.getName(), null, AuthorityUtils.createAuthorityList("ROLE_API_CLIENT"))));
        }
        chain.doFilter(req, res);
    }
}
