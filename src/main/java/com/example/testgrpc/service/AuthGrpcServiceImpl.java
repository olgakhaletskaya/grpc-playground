package com.example.testgrpc.service;


import com.example.testgrpc.authentication.jwt.JwtAuthProvider;
import com.example.testgrpc.proto.AuthServiceGrpc;
import com.example.testgrpc.proto.JwtRequest;
import com.example.testgrpc.proto.JwtToken;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@GrpcService
public class AuthGrpcServiceImpl extends AuthServiceGrpc.AuthServiceImplBase {

    @Value("${jwt.secret.key}")
    String jwtSecretKey;

    private final JwtAuthProvider jwtAuthProvider;
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);


    public AuthGrpcServiceImpl(JwtAuthProvider jwtAuthProvider) {
        this.jwtAuthProvider = jwtAuthProvider;
    }

//    @Override
//    public void authorize(JwtRequest request, StreamObserver<JwtToken> responseObserver) {
//        Authentication authenticate = jwtAuthProvider.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
//        Instant now = Instant.now();
//        Instant expiration = now.plus(15, ChronoUnit.MINUTES);
//        String authorities = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
//        responseObserver.onNext(JwtToken.newBuilder().setJwtToken(Jwts.builder()
//                .setSubject((String) authenticate.getPrincipal())
//                .claim("auth", authorities)
//                .setIssuedAt(Date.from(now))
//                .setExpiration(Date.from(expiration))
//                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
//                .compact()).build());
//
//        responseObserver.onCompleted();
//    }



    @Override
    public void authorize(JwtRequest request, StreamObserver<JwtToken> responseObserver) {
        try {
            // Authenticate the user with username and password
            Authentication authentication = authenticateUser(request.getUserName(), request.getPassword());

            // Generate the JWT token
            String jwtToken = generateJwtToken(authentication);

            // Send the token as a response
            responseObserver.onNext(JwtToken.newBuilder().setJwtToken(jwtToken).build());
        } catch (AuthenticationException e) {
            responseObserver.onError(e); // Handle authentication failure
        } finally {
            responseObserver.onCompleted();
        }
    }

    // Separate method to authenticate the user
    private Authentication authenticateUser(String username, String password) {
        return jwtAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    // Separate method to generate the JWT token
    private String generateJwtToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiration = now.plus(15, ChronoUnit.MINUTES); // Token expiration time

        // Get user authorities as a comma-separated string
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

    // Ensure the key is large enough for the algorithm (HS512)
    Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
            .setSubject((String) authentication.getPrincipal())
            .claim("auth", authorities)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .signWith(key, SignatureAlgorithm.HS512)  // Use the proper key and algorithm
            .compact();
}
}
