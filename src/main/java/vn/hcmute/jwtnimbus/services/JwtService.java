package vn.hcmute.jwtnimbus.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.hcmute.jwtnimbus.entity.User;
import vn.hcmute.jwtnimbus.exceptions.JwtAuthenticationException;

import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final byte[] secretKeyBytes;
    private final long jwtExpirationMs;

    public JwtService(
            @Value("${security.jwt.secret-key}") String base64SecretKey,
            @Value("${security.jwt.expiration-time}") long jwtExpirationMs
    ) {
        this.secretKeyBytes = Base64.getDecoder().decode(base64SecretKey);
        this.jwtExpirationMs = jwtExpirationMs;

        if (this.secretKeyBytes.length < 32) {
            throw new IllegalArgumentException("HS256 secret key must be at least 32 bytes (Base64 decode).");
        }
    }

    public long getExpirationTime() {
        return jwtExpirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issueTime(now)
                .expirationTime(exp);

        // extra claims giống slide (user_id, fullName, images)
        if (userDetails instanceof User u) {
            claims.claim("user_id", u.getId());
            claims.claim("full_name", u.getFullName());
            claims.claim("images", u.getImages());
        }

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claims.build()
        );

        try {
            JWSSigner signer = new MACSigner(secretKeyBytes);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new JwtAuthenticationException("Cannot sign JWT", e);
        }
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        JWTClaimsSet claims = getClaims(token);

        boolean usernameOk = claims.getSubject() != null
                && claims.getSubject().equals(userDetails.getUsername());

        boolean notExpired = claims.getExpirationTime() != null
                && claims.getExpirationTime().after(new Date());

        return usernameOk && notExpired;
    }

    private JWTClaimsSet getClaims(String token) {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new JwtAuthenticationException("JWT is malformed", e);
        }

        boolean signatureOk;
        try {
            JWSVerifier verifier = new MACVerifier(secretKeyBytes);
            signatureOk = signedJWT.verify(verifier);
        } catch (JOSEException e) {
            throw new JwtAuthenticationException("JWT signature verification failed", e);
        }

        if (!signatureOk) {
            throw new JwtAuthenticationException("JWT signature is invalid");
        }

        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            if (claims.getExpirationTime() != null && claims.getExpirationTime().before(new Date())) {
                throw new JwtAuthenticationException("JWT token has expired");
            }
            return claims;
        } catch (ParseException e) {
            throw new JwtAuthenticationException("Cannot read JWT claims", e);
        }
    }
}