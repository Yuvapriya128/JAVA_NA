package org.example.springdatajpademo.Ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        logger.debug("=== JWT FILTER PROCESSING ===");
        logger.debug("Request path: {}", requestPath);
        logger.debug("Authorization header present: {}", authHeader != null);

        // IMPORTANT: JwtFilter should NOT interfere with /api/ecom/auth/login
        // It should permit the request to proceed to the AuthController
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No valid Bearer token found in Authorization header");
            logger.debug("Proceeding to next filter in chain");
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(7);
        logger.debug("JWT token extracted (first 20 chars): {}...", jwtToken.substring(0, Math.min(20, jwtToken.length())));

        String email;

        try {
            logger.debug("Extracting username/email from JWT token");
            email = jwtUtil.extractUsername(jwtToken);
            logger.debug("Email extracted from token: {}", email);
        } catch (Exception ex) {
            logger.error("Failed to extract email from JWT token", ex);
            logger.debug("Proceeding to next filter in chain");
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Validating JWT token for email: {}", email);

            try {
                logger.debug("=== USERDETAILS SERVICE CALL ===");
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                logger.debug("UserDetails loaded successfully for email: {}", email);
                logger.debug("UserDetails username: {}", userDetails.getUsername());
                logger.debug("UserDetails authorities: {}", userDetails.getAuthorities());
                logger.debug("UserDetails authorities count: {}", userDetails.getAuthorities().size());
                userDetails.getAuthorities().forEach(auth ->
                    logger.debug("  - Authority: {}", auth.getAuthority())
                );

                if (jwtUtil.isTokenValid(jwtToken, userDetails)) {
                    logger.debug("JWT token is valid");

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    logger.info("===== AUTHENTICATION SET IN SECURITY CONTEXT =====");
                    logger.info("Principal: {}", authenticationToken.getPrincipal());
                    logger.info("Principal type: {}", authenticationToken.getPrincipal().getClass().getSimpleName());
                    logger.info("Username/Email: {}", authenticationToken.getName());
                    logger.info("Authorities: {}", authenticationToken.getAuthorities());
                    logger.info("Is Authenticated: {}", authenticationToken.isAuthenticated());
                    logger.info("SecurityContext after setting auth: {}", SecurityContextHolder.getContext().getAuthentication());
                } else {
                    logger.warn("JWT token is invalid for email: {}", email);
                }
            } catch (Exception ex) {
                logger.error("Error during JWT validation", ex);
            }
        }

        logger.debug("Proceeding to next filter in chain");
        filterChain.doFilter(request, response);
    }
}

