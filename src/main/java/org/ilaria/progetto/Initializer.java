package org.ilaria.progetto;

import ch.qos.logback.core.encoder.EchoEncoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.Entity.Aula;
import org.ilaria.progetto.Model.Entity.Contenuto;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.AulaRepository;
import org.ilaria.progetto.Repository.ContenutoRepository;
import org.ilaria.progetto.Repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Initializer {

    private final AulaRepository aulaRepository;
    private final ContenutoRepository contenutoRepository;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        if(utenteRepository.count()==0) {
            String password = passwordEncoder.encode("password123");
            Utente admin = new Utente(null,"admin","admin@gmail.com",  password ,null,-1,null);
            admin.setRuolo(Ruolo.ADMIN);
            utenteRepository.save(admin);
            if (aulaRepository.count() == 0) {
                loadAule(admin);
            }
            if (contenutoRepository.count() == 0) {
                loadContenuti();
            }
        }
    }

    private void loadAule(Utente admin) {
        List<Aula> aule = List.of(
                new Aula(null, "0A", 1, 1, false, 0, null),
                new Aula(null, "1B", 80, 2, true, 0, admin),
                new Aula(null, "30A", 1, 1, true, 0, admin),
                new Aula(null, "42B2", 100, 0, false, 0, null),
                new Aula(null, "2C", 100, 6, false, 0, null),
                new Aula(null, "3D", 80, 7, true, 0, admin),
                new Aula(null, "11B", 80, 4, true, 0, admin),
                new Aula(null, "12C", 100, 3, false, 0, null),
                new Aula(null, "20A", 150, 1, false, 0, null),
                new Aula(null, "21B", 100, 0, false, 0, null),
                new Aula(null, "22C", 80, 3, true, 0, admin),
                new Aula(null, "23D", 100, 4, false, 0, null),
                new Aula(null, "31B2", 100, 2, false, 0, null),
                new Aula(null, "33D", 130, 6, false, 0, null),
                new Aula(null, "40Z", 150, 5, false, 0, null),
                new Aula(null, "41B0A", 120, 7, false, 0, null),
                new Aula(null, "43C", 150, 1, false, 0, null),
                new Aula(null, "10A", 100, 5, false, 0, null),
                new Aula(null, "13D", 130, 2, false, 0, null),
                new Aula(null, "32C", 50, 0, true, 0, admin)
        );
        aulaRepository.saveAll(aule);
    }

    private void loadContenuti() {
        List<Contenuto> contenuti = List.of(
                new Contenuto(null, "microfono", aulaRepository.findByCubo("0A").orElse(null)),
                new Contenuto(null, "proiettore", aulaRepository.findByCubo("0A").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("0A").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("2C").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("2C").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("12C").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("12C").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("13D").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("13D").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("13D").orElse(null)),

                new Contenuto(null, "pc", aulaRepository.findByCubo("1B").orElse(null)),
                new Contenuto(null, "proettore", aulaRepository.findByCubo("1B").orElse(null)),

                new Contenuto(null, "pc", aulaRepository.findByCubo("3D").orElse(null)),
                new Contenuto(null, "proettore", aulaRepository.findByCubo("3D").orElse(null)),

                new Contenuto(null, "pc", aulaRepository.findByCubo("11B").orElse(null)),
                new Contenuto(null, "proettore", aulaRepository.findByCubo("11B").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("10A").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("10A").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("20A").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("20A").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("20A").orElse(null)),

                new Contenuto(null, "microfono", aulaRepository.findByCubo("21B").orElse(null)),
                new Contenuto(null, "proiettore", aulaRepository.findByCubo("21B").orElse(null)),

                new Contenuto(null, "microfono", aulaRepository.findByCubo("22C").orElse(null)),
                new Contenuto(null, "proettore", aulaRepository.findByCubo("22C").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("23D").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("23D").orElse(null)),

                new Contenuto(null, "pc", aulaRepository.findByCubo("30A").orElse(null)),
                new Contenuto(null, "proettore", aulaRepository.findByCubo("30A").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("31B2").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("31B2").orElse(null)),

                new Contenuto(null, "pc", aulaRepository.findByCubo("32C").orElse(null)),
                new Contenuto(null, "proettore", aulaRepository.findByCubo("32C").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("33D").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("33D").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("40Z").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("40Z").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("40Z").orElse(null)),

                new Contenuto(null, "proiettore", aulaRepository.findByCubo("41B0A").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("41B0A").orElse(null)),
                new Contenuto(null, "microfono", aulaRepository.findByCubo("41B0A").orElse(null)),

                new Contenuto(null, "microfono", aulaRepository.findByCubo("42B2").orElse(null)),
                new Contenuto(null, "proiettore", aulaRepository.findByCubo("42B2").orElse(null)),

                new Contenuto(null, "microfono", aulaRepository.findByCubo("43C").orElse(null)),
                new Contenuto(null, "proiettore", aulaRepository.findByCubo("43C").orElse(null)),
                new Contenuto(null, "prese", aulaRepository.findByCubo("43C").orElse(null))
        );
        contenutoRepository.saveAll(contenuti);
    }

}
