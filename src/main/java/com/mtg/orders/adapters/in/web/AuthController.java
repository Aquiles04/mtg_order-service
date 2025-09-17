package com.mtg.orders.adapters.in.web;

import com.mtg.orders.adapters.out.persistence.RefreshTokenRepository;
import com.mtg.orders.adapters.out.persistence.UserRepository;
import com.mtg.orders.domain.model.RefreshToken;
import com.mtg.orders.domain.model.UserAccount;
import com.mtg.orders.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authManager, UserRepository userRepo,
                          RefreshTokenRepository refreshRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.refreshRepo = refreshRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body){
        String username = body.get("username");
        String password = body.get("password");
        if(username == null || password == null) return ResponseEntity.badRequest().body(Map.of("error","username and password required"));
        if(userRepo.findByUsername(username).isPresent()) return ResponseEntity.badRequest().body(Map.of("error","username exists"));
        UserAccount u = new UserAccount(username, passwordEncoder.encode(password), "ROLE_USER");
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("username", u.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        String username = body.get("username");
        String password = body.get("password");
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Optional<UserAccount> maybe = userRepo.findByUsername(username);
        if(maybe.isEmpty()) return ResponseEntity.status(401).build();
        UserAccount u = maybe.get();
        String access = jwtUtil.generateAccessToken(u.getUsername(), Map.of("roles", u.getRoles()));
        String refresh = jwtUtil.generateRefreshToken(u.getUsername());
        RefreshToken rt = new RefreshToken(refresh, Instant.now().plusMillis(Long.parseLong(System.getProperty("jwt.refresh-expiration-ms", "2592000000"))), u);
        // persist refresh token
        refreshRepo.deleteByUserId(u.getId()); // simple: one active refresh token per user
        refreshRepo.save(rt);
        return ResponseEntity.ok(Map.of("accessToken", access, "refreshToken", refresh));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body){
        String token = body.get("refreshToken");
        if(token == null) return ResponseEntity.badRequest().body(Map.of("error","refreshToken required"));
        Optional<RefreshToken> maybe = refreshRepo.findByToken(token);
        if(maybe.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","invalid refresh token"));
        RefreshToken rt = maybe.get();
        if(rt.getExpiresAt().isBefore(Instant.now())){
            refreshRepo.delete(rt);
            return ResponseEntity.status(401).body(Map.of("error","expired refresh token"));
        }
        String username = rt.getUser().getUsername();
        String access = jwtUtil.generateAccessToken(username, Map.of("roles", rt.getUser().getRoles()));
        String refresh = jwtUtil.generateRefreshToken(username);
        // rotate refresh token
        refreshRepo.delete(rt);
        RefreshToken newRt = new RefreshToken(refresh, Instant.now().plusMillis(Long.parseLong(System.getProperty("jwt.refresh-expiration-ms", "2592000000"))), rt.getUser());
        refreshRepo.save(newRt);
        return ResponseEntity.ok(Map.of("accessToken", access, "refreshToken", refresh));
    }
}
