package org.ilaria.progetto.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.DTO.AulaDTO;
import org.ilaria.progetto.Model.DTO.ContenutoDTO;
import org.ilaria.progetto.Model.DTO.PrenotazioneAuleDTO;
import org.ilaria.progetto.Model.Entity.Aula;
import org.ilaria.progetto.Model.Entity.Contenuto;
import org.ilaria.progetto.Model.Entity.Prenotazione;
import org.ilaria.progetto.Model.Entity.Utente;
import org.ilaria.progetto.Repository.AulaRepository;
import org.ilaria.progetto.Repository.ContenutoRepository;
import org.ilaria.progetto.Repository.PrenotazioneRepository;
import org.ilaria.progetto.Repository.UtenteRepository;
import org.ilaria.progetto.Ruolo;
import org.ilaria.progetto.Service.Mapper.AulaMapper;
import org.ilaria.progetto.Service.Mapper.ContenutoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final AulaMapper aulaMapper;
    private final ContenutoRepository contenutoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final ContenutoMapper contenutoMapper;
    private final UtenteRepository utenteRepository;

    /* un aula è libera se ha posti al momento della richiesta di lista, se non è stata prenotata per quel tempo da un
     professore. */

    private LinkedList<Aula> libere (){
        LocalDateTime now = LocalDateTime.now();
        LinkedList<Aula> listaAula = aulaRepository.findLibere();
        LinkedList<Aula> listaAulaCopy = (LinkedList<Aula>) listaAula.clone();
        for (Aula aula : listaAula) {
            LinkedList<Prenotazione> Plist = prenotazioneRepository.esiste(aula.getId());
            for (Prenotazione p : Plist) {
                LocalDateTime inizio = p.getDataPrenotazione();
                LocalDateTime fine = inizio.plusHours(p.getDurata().getHour());
                if (now.toLocalDate().isEqual(inizio.toLocalDate()))
                    if (now.isAfter(inizio) && now.isBefore(fine) && p.getUtente().getRuolo().equals(Ruolo.DOCENTE)){
                        listaAulaCopy.remove(aula);
                    }
            }
        }
        return listaAulaCopy;
    }

    public List<AulaDTO> lista() {
        List<AulaDTO> listaDTO = new LinkedList<>();
        LinkedList<ContenutoDTO> clist = new LinkedList<>();
        for (Aula A : libere()) {
            clist = new LinkedList<>();
            AulaDTO DTO = aulaMapper.toDto(A);
            if(A.isLaboratorio()) DTO.setResponsabile(A.getResponsabile().getEmail());
            for (Contenuto c:  contenutoRepository.findByAula(A)) {
                ContenutoDTO DTO2 = contenutoMapper.toDto(c);
                clist.add(DTO2);
            }
            DTO.setContenuti(clist);
            listaDTO.add(DTO);
        }
        return listaDTO;
    }

    public int libera(long idAula) {
        Aula aula = aulaRepository.findById(idAula);
        if(!libere().contains(aula)) return -1;
        return 0;
    }

    /* se uno studente entra in un aula decrementa il numero di posti disponibili */

    @Transactional
    public void entra(Utente utente, AulaDTO aula) {
        Aula a = aulaRepository.findByIdWithLock(aula.getId());
        if(a.isLaboratorio()) {
            for(Prenotazione p : prenotazioneRepository.esiste(aula.getId())) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime inizio = p.getDataPrenotazione();
                if(p.getUtente().getId().equals(utente.getId()))
                    if(now.isBefore(inizio)) throw new RuntimeException("non è l'orario di prenotazione");
            }
        }
        utenteRepository.update(utente.getId(),a.getId());
        aulaRepository.update(a.getId(),a.getNumeroPosti() - 1);
    }

    /* se uno studente esce da un aula aumenta il numero di posti disponibili */

    @Transactional
    public void esci(Utente utente,long idAula) {
        AulaDTO aula = getAulaDTO(idAula);
        Aula a = aulaMapper.toEntity(aula);
        if(a.getId()!=utente.getIdInAula()) throw new RuntimeException("non puoi uscire da un aula diversa");
        if(a.isLaboratorio()) prenotazioneRepository.delete(aula.getId(),utente.getId());
        utenteRepository.update(utente.getId(),-1);
        aulaRepository.update(a.getId(),aula.getNumeroPosti() + 1);
    }

    public Aula getAula(long idAula) {
        return aulaRepository.findById(idAula);
    }

    public AulaDTO getAulaDTO(long idAula) {
        return aulaMapper.toDto(aulaRepository.findById(idAula));
    }

    @Transactional
    public LinkedList<PrenotazioneAuleDTO> prenotazioniAula(long idAula) {
        LinkedList<PrenotazioneAuleDTO> list = new LinkedList<>();
        for (Prenotazione p : prenotazioneRepository.esiste(idAula)){
            PrenotazioneAuleDTO pa = new PrenotazioneAuleDTO(p.getId() , p.getDataPrenotazione().toLocalDate(),
                    p.getDataPrenotazione().toLocalTime(), p.getDurata());
            if(p.getUtente().getRuolo().equals(Ruolo.DOCENTE))  list.add(pa);
        }
        return list;
    }
}
