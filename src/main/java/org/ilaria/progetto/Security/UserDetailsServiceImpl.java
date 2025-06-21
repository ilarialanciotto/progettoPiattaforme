package org.ilaria.progetto.Security;

import lombok.AllArgsConstructor;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.UtenteRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con l'email: " + username));

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + utente.getRuolo().name())
        );

        return new org.springframework.security.core.userdetails.User(
                utente.getEmail(),
                utente.getPassword(),
                authorities
        );
    }
}
