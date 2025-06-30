package org.ilaria.progetto.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.DTO.ClassroomDTO;
import org.ilaria.progetto.Model.DTO.ContentDTO;
import org.ilaria.progetto.Model.DTO.classroomBookingDTO;
import org.ilaria.progetto.Model.Entity.Classroom;
import org.ilaria.progetto.Model.Entity.Booking;
import org.ilaria.progetto.Model.Entity.Content;
import org.ilaria.progetto.Model.Entity.User;
import org.ilaria.progetto.Repository.ClassroomRepository;
import org.ilaria.progetto.Repository.BookingRepository;
import org.ilaria.progetto.Repository.UserRepository;
import org.ilaria.progetto.Role;
import org.ilaria.progetto.Service.Mapper.ClassroomMapper;
import org.ilaria.progetto.Service.Mapper.ContentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;
    private final BookingRepository bookingRepository;
    private final ContentMapper contentMapper;
    private final UserRepository userRepository;

    /* un aula è libera se ha posti al momento della richiesta di lista, se non è stata prenotata per quel tempo da un
     professore. */

    private LinkedList<Classroom> free(){
        LocalDateTime now = LocalDateTime.now();
        LinkedList<Classroom> listClassroom = classroomRepository.findFree();
        LinkedList<Classroom> listClassroomCopy = (LinkedList<Classroom>) listClassroom.clone();
        for (Classroom classroom : listClassroom) {
            List<Booking> bookingList = classroom.getBookings();
            for (Booking b : bookingList) {
                LocalDateTime start = b.getBookingDate();
                LocalDateTime end = start.plusHours(b.getDuration().getHour());
                if (now.toLocalDate().isEqual(start.toLocalDate()))
                    if (now.isAfter(start) && now.isBefore(end) && b.getUser().getRole().equals(Role.TEACHER)){
                        listClassroomCopy.remove(classroom);
                    }
            }
        }
        return listClassroomCopy;
    }

    public List<ClassroomDTO> list() {
        List<ClassroomDTO> listDTO = new LinkedList<>();
        LinkedList<ContentDTO> contentslist = new LinkedList<>();
        for (Classroom C : free()) {
            contentslist = new LinkedList<>();
            ClassroomDTO DTO = classroomMapper.toDto(C);
            if(C.isLaboratory()) DTO.setPersonInCharge(C.getPersonInCharge().getEmail());
            for (Content content:  C.getContents()) {
                ContentDTO DTO2 = contentMapper.toDto(content);
                contentslist.add(DTO2);
            }
            DTO.setContents(contentslist);
            listDTO.add(DTO);
        }
        return listDTO;
    }

    public int IsFree(long classroomID) {
        Classroom classroom = classroomRepository.findById(classroomID);
        if(!free().contains(classroom)) return -1;
        return 0;
    }

    /* se uno studente entra in un aula decrementa il numero di posti disponibili */

    @Transactional
    public void enter(User user, ClassroomDTO classroom) {
        Classroom c = classroomRepository.findByIdWithLock(classroom.getId());
        if(c.isLaboratory()) {
            for(Booking b : c.getBookings()) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime start = b.getBookingDate();
                if(b.getUser().getId().equals(user.getId()))
                    if(now.isBefore(start)) throw new RuntimeException("it's not the booking time");
            }
        }
        userRepository.update(user.getId(),c.getId());
        classroomRepository.update(c.getId(),c.getNumberOfSeats() - 1);
    }

    /* se uno studente esce da un aula aumenta il numero di posti disponibili */

    @Transactional
    public void exit(User user, long classroomID) {
        ClassroomDTO classroom = getClassroomDTO(classroomID);
        Classroom c = classroomMapper.toEntity(classroom);
        if(c.getId()!= user.getClassroomIDin()) throw new RuntimeException("you can't leave a different classroom");
        if(c.isLaboratory()) bookingRepository.delete(classroom.getId(), user.getId());
        userRepository.update(user.getId(),-1);
        classroomRepository.update(c.getId(),classroom.getNumberOfSeats() + 1);
    }

    public Classroom getClassroom(long classroomID) {
        return classroomRepository.findById(classroomID);
    }

    public ClassroomDTO getClassroomDTO(long classroomID) {
        return classroomMapper.toDto(classroomRepository.findById(classroomID));
    }

    @Transactional
    public LinkedList<classroomBookingDTO> classroomsBooking(long classroomID) {
        LinkedList<classroomBookingDTO> list = new LinkedList<>();
        for (Booking b : classroomRepository.findById(classroomID).getBookings()){
            classroomBookingDTO classroomsBooking = new classroomBookingDTO(b.getId() , b.getBookingDate().toLocalDate(),
                    b.getBookingDate().toLocalTime(), b.getDuration());
            if(!b.getUser().getRole().equals(Role.STUDENT))  list.add(classroomsBooking);
        }
        return list;
    }

    @Transactional
    public void personInCharge(long idAula, User user) {
        User admin = userRepository.findByEmail("admin@gmail.com").orElseThrow();
        if (classroomRepository.findById(idAula).getPersonInCharge().getId().equals(admin.getId())) {
            classroomRepository.personInCharge(idAula, user);
        } else throw new RuntimeException("the person in charge for this classroom has already been assigned");
    }

    @Transactional
    public LinkedList<ClassroomDTO> getAdminClassrooms() {
        LinkedList<ClassroomDTO> list = new LinkedList<>();
        for (Classroom c : classroomRepository.findAll())
            if(c.isLaboratory() && c.getPersonInCharge().getEmail().equals("admin@gmail.com") )
                list.add(classroomMapper.toDto(c));
        return list;
    }
}
