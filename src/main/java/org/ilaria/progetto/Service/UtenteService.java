package org.ilaria.progetto.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.DTO.AulaDTO;
import org.ilaria.progetto.Security.JwtResponse;
import org.ilaria.progetto.Model.DTO.UtenteDTO;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.UtenteRepository;
import org.ilaria.progetto.Ruolo;
import org.ilaria.progetto.Service.Mapper.UtenteMapper;
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
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtenteMapper utenteMapper;
    private final AuthenticationManager authenitcationManager;
    private final JwtUtils jwtUtils;

    public void registra(UtenteDTO dto) {
        if (utenteRepository.existsByEmail(dto.getEmail())) { throw new RuntimeException("Email già registrata!"); }
        Utente utente = utenteMapper.toEntity(dto);
        utente.setPassword(passwordEncoder.encode(dto.getPassword()));
        utente.setRuolo(dto.getCodiceDocente() != null ? Ruolo.DOCENTE : Ruolo.STUDENTE);
        utenteRepository.save(utente);
    }

    public ResponseEntity<?> login(UtenteDTO dto) {
        Authentication authentication = authenitcationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userPrincipal);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String ruolo = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("NESSUN_RUOLO");
        return ResponseEntity.ok(new JwtResponse(jwt,userDetails.getUsername(), ruolo));
    }

    @Cacheable(value = "Utente", key = "#email")
    public Utente findUtente(String email) {
        return utenteRepository.findByEmail(email).orElseThrow();
    }

    @Cacheable(value = "UtenteID", key = "#iDutente")
    public Utente getUtente(long iDutente) { return utenteRepository.findById(iDutente); }

    @Transactional
    public void update(Long id, String nuovaEmail, String nuovaPassword) {
        utenteRepository.updateDati(id,nuovaEmail,nuovaPassword);
    }

}

