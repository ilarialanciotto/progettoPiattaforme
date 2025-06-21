package org.ilaria.progetto.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progetto.Model.DTO.PrenotazioneDTO;
import org.ilaria.progetto.Model.Entity.Prenotazione;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.UtenteRepository;
import org.ilaria.progetto.Service.PrenotazioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@PreAuthorize("hasRole('DOCENTE')")
@RequestMapping("/AuleLibere/home/Docente")
@AllArgsConstructor
public class DocenteController {

    private final PrenotazioneService prenotazioneService;
    private final UtenteRepository utenteRepository;

    /* un docente ha la lista delle prenotazioni dei laboratori di cui è responsabile e che non
     ha ancora approvato, prima viene fatto un controllo per eliminare le prenotazioni scadute*/

    @PreAuthorize("hasRole('DOCENTE')")
    @GetMapping("/getLista")
    public ResponseEntity<List<PrenotazioneDTO>> ListaPrenotazioni() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Utente docente = utenteRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(prenotazioneService.lista(docente.getId()));
    }

    /* un docente accetta la prenotazione */

    @PostMapping("/accettaPrenotazione")
    public ResponseEntity<String> accettaPrenotazione(@RequestBody long IDprenotazione) {
        Prenotazione prenotazione = prenotazioneService.getPrenotazione(IDprenotazione);
        prenotazioneService.accettaPrenotazione(prenotazione);
        return ResponseEntity.ok("accettazione riuscita");
    }

}
