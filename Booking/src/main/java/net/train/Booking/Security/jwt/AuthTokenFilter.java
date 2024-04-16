package net.train.Booking.Security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.train.Booking.Security.user.HotelUserDetailsService;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    private final String HEADER = "Authorization";
    private final String PREFIX  = "Bearer ";
    @Autowired
    private JwtUtils _jwtUtils;

    @Autowired
    private HotelUserDetailsService _userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
//            if(request.getServletPath().contains("/api")){
//                filterChain.doFilter(request, response);
//                return;
//            }
//            if(request.getServletPath().contains("/swagger-ui") || request.getServletPath().contains("/v3/api-docs")
//            || request.getServletPath().contains("/swagger-ui.html")){
//                filterChain.doFilter(request, response);
//                return;
//            }
            String jwt = parseJwt(request);
            if(jwt != null && _jwtUtils.validateToken(jwt)){//Kiểm tra token có null và validatetoken
                //lấy username theo token trong trường hợp này là email
                String email = _jwtUtils.getUserNameFromToken(jwt);
                //Gán username cho user client
                UserDetails userDetails = _userDetailsService.loadUserByUsername(email);
                //Tạo đối tượng xác thực authentication
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                //Set thêm các thông tin chi tiết(Ip,...) khi xác thực cho phía request, để lấy được thông tin chi tiết của client
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //Xác thực thông tin
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception e){
            logger.error("Cannot set user authentication : {} ", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request){
        String authHeader = request.getHeader(HEADER);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith(PREFIX)){
            return authHeader.substring(7);
        }
        return null;
    }
}
