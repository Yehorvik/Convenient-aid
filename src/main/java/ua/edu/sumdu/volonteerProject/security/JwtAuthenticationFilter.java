package ua.edu.sumdu.volonteerProject.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static ua.edu.sumdu.volonteerProject.security.SecurityConstraints.HEADER_STRING;
import static ua.edu.sumdu.volonteerProject.security.SecurityConstraints.TOKEN_PREFIX;

@AllArgsConstructor
@Slf4j
@Component
@Order(1)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwt= getJwtFromRequest(request);
            if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)){
                UUID uuid = jwtTokenProvider.getUserIdFromJwt(jwt);
                JwtUserDetails userDetails = customUserDetailsService.loadUserByUUID(uuid);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch (Exception exception){
            log.error("could not set user authentication in security context while filtering");
            exception.printStackTrace();
        }
        try {
            filterChain.doFilter(request, response);
        }catch (BadCredentialsException e){
            log.error("Bad creds of user");
        }
    }

    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(HEADER_STRING);
        if(StringUtils.hasText(bearerToken)&& bearerToken.startsWith(TOKEN_PREFIX)){
            return bearerToken.substring(7, bearerToken.length());
        }else{
            return null;
        }
    }

}
