package net.train.Booking.Security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import net.train.Booking.Security.user.HotelUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.expirationInMils}")
    private int jwtExpirationMs;

    public String generateJwtTokenForUser(Authentication authentication){
        //Lấy thông tin người dùng đang được xác thực theo đối tượng HotelUserDetails(id,username, password,authorities)
        HotelUserDetails usePrincipal = (HotelUserDetails) authentication.getPrincipal();
        //Lấy danh sách roles
        List<String> roles = usePrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder() //Sử dụng phương thức Jwts.builder() để tạo JWT
                .setSubject(usePrincipal.getUsername()) //Set chủ thể đại diện cho token cụ thể là username của người dùng
                //Set claim chứa thông tin người dùng cụ thể là role và 1 list các role được lấy ra từ roles
                .claim("roles", roles)//claim là các thông tin bổ sung về người dùng muốn truyền đi trong token
                //Set ngày phát hành token
                .setIssuedAt(new Date())
                //Set thời gian hết hạn của token
                .setExpiration(new Date(new Date().getTime()+ jwtExpirationMs))
                //Set thuật toán mã hoá chuỗi Secret
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
