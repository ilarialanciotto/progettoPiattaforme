package org.ilaria.progetto.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progetto.Model.DTO.AulaDTO;
import org.ilaria.progetto.Model.DTO.PrenotazioneAuleDTO;
import org.ilaria.progetto.Model.DTO.codiceAulaDTO;
import org.ilaria.progetto.Model.Entity.Prenotazione;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.UtenteRepository;
import org.ilaria.progetto.Service.AulaService;
import org.ilaria.progetto.Service.PrenotazioneService;
import org.ilaria.progetto.Service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedList;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@PreAuthorize("hasRole('STUDENTE')")
@RequestMapping("/AuleLibere/home/Studente")
@AllArgsConstructor
public class StudenteController {

    private final AulaService aulaService;
    private final PrenotazioneService prenotazioneService;
    private final UtenteRepository utenteRepository;
    private final UtenteService utenteService;

    /* uno studente può entrare in un aula libera solo se non è un laboratorio*/
    @PostMapping("/controllaAule")
    public ResponseEntity<LinkedList<PrenotazioneAuleDTO>> controllaAule(@RequestBody long codiceAula) {
        return ResponseEntity.ok(aulaService.prenotazioniAula(codiceAula));
    }

    @PostMapping("/Entra")
    public ResponseEntity<String> SegnalaEntrata(@RequestBody codiceAulaDTO controlloAula) {
        try{
            long idAula= controlloAula.getIdAula();
            int codice= controlloAula.getCodice();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email).orElseThrow();
            AulaDTO aula = aulaService.getAulaDTO(idAula);
            if(aula.isLaboratorio()){
                if(codice!=0 && !prenotazioneService.controllaCodice(idAula,codice,utente.getId()))
                    return ResponseEntity.badRequest().body("codice errato");
                else
                    if(codice==0)
                        return ResponseEntity.badRequest().
                                body("Laboratorio deve essere prenotato prima di entrare");
            }
            if(utente.getIdInAula()!=-1)
                return ResponseEntity.badRequest().body("non puoi entrare in piu aule in contemporanea");
            if(aulaService.libera(idAula)!=0) return ResponseEntity.badRequest().body("aula occupata");
            aulaService.entra(utente,aula);
            return ResponseEntity.ok("utente entrato in aula");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/Esci")
    public ResponseEntity<String> SegnalaUscita(@RequestBody long idAula) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Utente utente = utenteRepository.findByEmail(email).orElseThrow();
            if(utente.getIdInAula()==-1)
                return ResponseEntity.badRequest().body("non puoi uscire senza entrare in un aula");
            aulaService.esci(utente,idAula);
            return ResponseEntity.ok("utente uscito dall' aula");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/controllaCodice")
    public ResponseEntity<String> controlloCodice() {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Utente utente = utenteService.findUtente(email);
            LinkedList<Prenotazione> listaP = prenotazioneService.findbyUtente(utente.getId());
            if(listaP == null || listaP.isEmpty()) return ResponseEntity.badRequest().body("non hai effettuato alcuna prenotazione");
            StringBuilder response = new StringBuilder();
            for(Prenotazione p : listaP)
                if(p.getCodice()!=0) response.append("id: ").
                        append(p.getAula().getCubo()).append(" : ").
                        append(p.getCodice()).append(" : ").
                        append(p.getDataPrenotazione() + "\n");
            if (response.isEmpty()) return ResponseEntity.badRequest().body("ricarica tra poco, non hai ancora ricevuto accettazioni per le tue prenotazioni");
            return ResponseEntity.ok(response.toString());
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
