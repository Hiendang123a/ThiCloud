package com.example.society.Security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    private static final String SIGNER_KEY = "vNY9sgAw7HYG+ACDFtDtj6INQf5gafQDF4AGLGRcJY3+iHVml3q75CQWbkojnpIp";

    public String generateAccessToken(String userID) {
        return generateToken(userID, 1); // 1 giờ
    }

    public String generateRefreshToken(String userID) {
        return generateToken(userID, 7); // 7 giờ
    }

    private String generateToken(String userID, int hours) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userID)
                .issuer("DangBaHien")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(hours, ChronoUnit.HOURS).toEpochMilli()))
                .build();
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS512), new Payload(claimsSet.toJSONObject()));
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create Token", e);
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            return signedJWT.verify(verifier) && signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());
        } catch (ParseException | JOSEException e) {
            log.error("Invalid Token", e);
            return false;
        }
    }

    public String getUserIDFromToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            log.error("Error extracting username from token", e);
            return null;
        }
    }
}
