package com.jirademo.jiraapi.config;

import com.jirademo.jiraapi.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {


  private static final String SECRET_KEY = "d69cbefbea9c71130837a97a355ea62b497c5b9175a671914d30ed86307dd136";

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extracAlltClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extracAlltClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
  }

  private Key getSignInKey() {
    byte[] KeyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(KeyBytes);
  }


  //? Genrate a token in overload


  public String genrateToken(User userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("email", userDetails.getEmail());
    claims.put("name", userDetails.getName());
    claims.put("id", userDetails.getId());
    claims.put("role", userDetails.getRole());
    return genrateToken(claims, userDetails);
  }

  public String genrateToken(Map<String, Object> extraClaims, UserDetails userDetails

  ) {
    return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))//! the date of expire token
            .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact(); //? generate and return the token
  }

  // ? token validation with the userDetail to se its the same token or not
  public boolean isTokenValidate(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
