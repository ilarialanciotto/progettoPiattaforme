package org.ilaria.progetto.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.Entity.User;
import org.ilaria.progetto.Role;
import org.ilaria.progetto.Security.JwtResponse;
import org.ilaria.progetto.Model.DTO.UserDTO;
import org.ilaria.progetto.Repository.UserRepository;
import org.ilaria.progetto.Service.Mapper.UserMapper;
import org.ilaria.progetto.Security.JwtUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenitcationManager;
    private final JwtUtils jwtUtils;

    public void register(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) { throw new RuntimeException("Email already registered"); }
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getTeacherCode() != null ? Role.TEACHER : Role.STUDENT);
        userRepository.save(user);
    }

    public ResponseEntity<?> login(UserDTO dto) {
        Authentication authentication = authenitcationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userPrincipal);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String ruolo = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("NO_ROLE");
        return ResponseEntity.ok(new JwtResponse(jwt,userDetails.getUsername(), ruolo));
    }

    @Cacheable(value = "User", key = "#email")
    public User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @Cacheable(value = "UserID", key = "#userID")
    public User getUser(long userID) { return userRepository.findById(userID); }

    @Transactional
    public void update(Long id, String newEmail, String newPassword) {
        userRepository.updateDates(id,newEmail,newPassword);
    }

}

