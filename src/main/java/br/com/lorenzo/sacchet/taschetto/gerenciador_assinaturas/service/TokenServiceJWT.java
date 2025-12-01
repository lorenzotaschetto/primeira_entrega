package br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.service;

import br.com.lorenzo.sacchet.taschetto.gerenciador_assinaturas.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
public class TokenServiceJWT {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "API Gerenciador de Assinaturas";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(Usuario usuario) {

        Instant expiracaoInstant = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
                .plusHours(2)
                .toInstant();

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(usuario.getEmail())

                .claim("id", usuario.getIdUsuario())

                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiracaoInstant))
                .signWith(getSigningKey())
                .compact();
    }

    public String getSubject(String tokenJWT) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(tokenJWT)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            return "";
        }
    }
}