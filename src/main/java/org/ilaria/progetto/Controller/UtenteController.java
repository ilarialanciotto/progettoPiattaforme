package org.ilaria.progetto.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progetto.Model.DTO.*;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.UtenteRepository;
import org.ilaria.progetto.Ruolo;
import org.ilaria.progetto.Service.AulaService;
import org.ilaria.progetto.Service.FeedbackService;
import org.ilaria.progetto.Service.PrenotazioneService;
import org.ilaria.progetto.Service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/AuleLibere/home")
@AllArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;
    private final AulaService aulaService;
    private final UtenteRepository utenteRepository;
    private final PrenotazioneService prenotazioneService;
    private final PasswordEncoder passwordEncoder;
    private final FeedbackService feedbackService;

    @PostMapping("/auth/registrazione")
    public ResponseEntity<String> registra(@RequestBody UtenteDTO utente) {
        try {
            utenteService.registra(utente);
            return ResponseEntity.ok("Utente registrato con successo!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UtenteDTO utente) { return utenteService.login(utente); }

    /* ad ogni richiesta di getLista se esistono prenotazioni viene controllato che non ci siano
        prenotazioni scadute e dopo di che mostra le aule libere */

    @GetMapping("/getLista")
    public ResponseEntity<List<AulaDTO>> ListaAule() {
        return ResponseEntity.ok(aulaService.lista());
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> feedback(@RequestBody FeedbackDTO feedback) {
        feedbackService.save(feedback);
        return ResponseEntity.ok("feedback inserito");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody long idPrenotazione) {
        prenotazioneService.delete(idPrenotazione);
        return ResponseEntity.ok("prenotazione cancellata");
    }

    @GetMapping("/getPrenotazioni")
    public ResponseEntity<LinkedList<PrenotazioneDTO>> getPrenotazioni() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Utente utente = utenteRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(prenotazioneService.getPrenotazioni(utente));
    }

    @PostMapping("aggiornaDati")
    public ResponseEntity<String> aggiornaDati(@RequestBody modificheDTO dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email).orElseThrow();
            if(dto.getEmail()==null) dto.setEmail(email);
            if(dto.getPassword()==null) dto.setPassword(utente.getPassword());
            else dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            utenteService.update(utente.getId(),dto.getEmail(),dto.getPassword());
            return ResponseEntity.ok("Credenziali aggiornate");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /* è possibile per uno studente prenotare un laboratorio solo se ha posti in quel momento
      un docente può prenotare un aula a patto che non sia stata prenotata gia da un altro docente */

    @PostMapping("/prenota")
    public ResponseEntity<String> PrenotaLaboratorio(@RequestBody PrenotazioneDTO prenotazione) {
        try{
            if(!prenotazioneService.Prenotabile(prenotazione))
                return ResponseEntity.badRequest().body("aula non libera");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email).orElseThrow();
            if(!aulaService.getAula(prenotazione.getIdlaboratorio()).isLaboratorio() && utente.getRuolo().equals(Ruolo.STUDENTE))
                return ResponseEntity.badRequest().body("non è necessario prenotare un aula che non sia un laboratorio");
            prenotazione.setIdutente(utente.getId());
            if(prenotazioneService.esistonoPrenotazioniData(prenotazione))
                return ResponseEntity.badRequest().body("non puoi prenotare piu aule allo stesso momento");
            if(prenotazione.getDataPrenotazione().isBefore(LocalDateTime.now()))
                return ResponseEntity.badRequest().body("data inserita non valida");
            prenotazioneService.prenota(prenotazione);
            return ResponseEntity.ok("utente ha prenotato l'aula");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
