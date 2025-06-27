package org.ilaria.progetto.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.DTO.PrenotazioneDTO;
import org.ilaria.progetto.Model.Entity.Prenotazione;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.AulaRepository;
import org.ilaria.progetto.Repository.PrenotazioneRepository;
import org.ilaria.progetto.Ruolo;
import org.ilaria.progetto.Service.Mapper.PrenotazioneMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class PrenotazioneService {

    private final PrenotazioneRepository prenotazioneRepository;
    private final AulaService aulaService;
    private final PrenotazioneMapper prenotazioneMapper;
    private final UtenteService utenteService;

    public List<PrenotazioneDTO> lista(Long id) {
        List<PrenotazioneDTO> list = new LinkedList<>();
        for(Prenotazione p : prenotazioneRepository.findPrenotazione(id)){
            PrenotazioneDTO pDTO = prenotazioneMapper.toDto(p);
            pDTO.setIdutente(p.getUtente().getId());
            pDTO.setIdlaboratorio(p.getAula().getId());
            list.add(pDTO);
        }
        return list;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void controllaPrenotazioni() {
        LocalDateTime now = LocalDateTime.now();
        List<Prenotazione> scadute = prenotazioneRepository.findAll();
        for (Prenotazione p : scadute)
            if(p.getDataPrenotazione().plusHours(p.getDurata().getHour()).isBefore(now)){
                prenotazioneRepository.delete(p);
            }
    }

    /* una prenotazione accetta ha accettata pari a true e nel momento in cui viene accetta viene fornito
     un codice per l'ingresso  */


    @Transactional
    @CacheEvict(value = "codiciUtente",  key = "#prenotazione.utente.id",  allEntries = true)
    public void accettaPrenotazione(Prenotazione prenotazione) {
        prenotazione.setApprovata(true);
        prenotazioneRepository.accetta(prenotazione.getId(),prenotazione.isApprovata());
        prenotazione.setCodice(new SecureRandom().nextInt(900000) + 100000); // da 100000 a 999999
        prenotazioneRepository.update(prenotazione.getCodice(),prenotazione.getId());
    }

    public boolean Prenotabile(PrenotazioneDTO prenotazione) {
        return aulaService.libera(prenotazione.getIdlaboratorio())==0;
    }

    public void prenota(PrenotazioneDTO prenotazioneDTO) {
        Prenotazione prenotazione = prenotazioneMapper.toEntity(prenotazioneDTO);
        prenotazione.setUtente(utenteService.getUtente(prenotazioneDTO.getIdutente()));
        prenotazione.setAula(aulaService.getAula(prenotazioneDTO.getIdlaboratorio()));
        if(prenotazione.getDataPrenotazione().minusDays(1).isBefore(LocalDateTime.now()))
            throw new RuntimeException("l'aula deve essere prenotata almeno un giorno prima");
        prenotazioneRepository.save(prenotazione);
    }

    public Prenotazione getPrenotazione(long iDprenotazione) { return prenotazioneRepository.findById(iDprenotazione); }

    public boolean controllaCodice(long idAula, int codice, long id) {
        return !prenotazioneRepository.controllo(idAula,codice,id).isEmpty();
    }

    @Cacheable(value = "codiciUtente", key = "#id")
    public LinkedList<Prenotazione> findbyUtente(long id) {
        return prenotazioneRepository.findbyUtente(id);
    }

    public boolean esistonoPrenotazioniData(PrenotazioneDTO prenotazione) {
        LocalDateTime start2 = prenotazione.getDataPrenotazione();
        LocalDateTime end2 = start2.plusHours(prenotazione.getDurata().getHour());
        for (Prenotazione p : prenotazioneRepository.findbyUtente(prenotazione.getIdutente())) {
            LocalDateTime start1 = p.getDataPrenotazione();
            LocalDateTime end1 = start1.plusHours(p.getDurata().getHour());
            if (!(end1.isBefore(start2) || end1.equals(start2) || end2.isBefore(start1) || end2.equals(start1))) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void delete(long idPrenotazione) {
        prenotazioneRepository.delete(idPrenotazione);
    }

    public LinkedList<PrenotazioneDTO> getPrenotazioni(Utente utente) {
        LinkedList<PrenotazioneDTO> list = new LinkedList<>();
        for (Prenotazione p : prenotazioneRepository.findbyUtente(utente.getId()))
            list.add(prenotazioneMapper.toDto(p));
        return list;
    }
}
