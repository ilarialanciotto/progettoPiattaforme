package org.ilaria.progetto.Service;

import lombok.RequiredArgsConstructor;
import org.ilaria.progetto.Model.DTO.BookingDTO;
import org.ilaria.progetto.Model.Entity.Booking;
import org.ilaria.progetto.Model.Entity.User;
import org.ilaria.progetto.Repository.BookingRepository;
import org.ilaria.progetto.Repository.UserRepository;
import org.ilaria.progetto.Role;
import org.ilaria.progetto.Service.Mapper.BookingMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ClassroomService classroomService;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final CacheManager cacheManager;
    private final UserRepository userRepository;

    public List<BookingDTO> list(Long id, Role role) {
        List<BookingDTO> list = new LinkedList<>();
        List<Booking> bookinglist = new LinkedList<>();
        if(role== Role.ADMIN) bookinglist= bookingRepository.findBookingAdmin(id, Role.ADMIN);
        else bookinglist= bookingRepository.findBookingTeacher(id, Role.STUDENT);
        for(Booking p : bookinglist){
            BookingDTO DTO = bookingMapper.toDto(p);
            DTO.setUserID(p.getUser().getId());
            DTO.setLaboratoryID(p.getClassroom().getId());
            list.add(DTO);
        }
        return list;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void bookingCheck() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expired = bookingRepository.findAll();
        for (Booking b : expired)
            if(b.getBookingDate().plusHours(b.getDuration().getHour()).isBefore(now)){
                bookingRepository.delete(b);
            }
    }

    /* una prenotazione accetta ha accettata pari a true e nel momento in cui viene accetta viene fornito
     un codice per l'ingresso  */


    @Transactional
    @CacheEvict(value = "userCodes",  key = "#booking.user.id",  allEntries = true)
    public void approveBooking(Booking booking) {
        booking.setApproved(true);
        bookingRepository.accept(booking.getId(), booking.isApproved());
        booking.setCode(new SecureRandom().nextInt(900000) + 100000);
        bookingRepository.update(booking.getCode(), booking.getId());
    }

    public boolean isBookable(BookingDTO prenotazione) {
        return classroomService.IsFree(prenotazione.getLaboratoryID())==0;
    }

    public void reservation(BookingDTO bookingDTO) {
        Booking booking = bookingMapper.toEntity(bookingDTO);
        booking.setUser(userService.getUser(bookingDTO.getUserID()));
        booking.setClassroom(classroomService.getClassroom(bookingDTO.getLaboratoryID()));
        if(booking.getBookingDate().minusDays(1).isBefore(LocalDateTime.now()))
            throw new RuntimeException("the classroom must be booked at least one day in advance");
        System.out.println("tutto ok 2");
        cacheManager.getCache("userCodes").evict(booking.getUser().getId());
        System.out.println("tutto ok 1" + booking);
        bookingRepository.save(booking);
        System.out.println("tutto ok 3");
    }

    public Booking getBooking(long bookingID) { return bookingRepository.findById(bookingID); }

    public boolean checkCode(long classroomID, int code, long id) {
        return !bookingRepository.check(classroomID,code,id).isEmpty();
    }

    @Cacheable(value = "userCodes", key = "#id")
    public List<Booking> findByUtente(long id) {
        return userRepository.findById(id).getBookings();
    }

    public boolean existBookingForDate(BookingDTO booking) {
        LocalDateTime start2 = booking.getBookingDate();
        LocalDateTime end2 = start2.plusHours(booking.getDuration().getHour());
        for (Booking b : userRepository.findById(booking.getUserID()).getBookings()) {
            LocalDateTime start1 = b.getBookingDate();
            LocalDateTime end1 = start1.plusHours(b.getDuration().getHour());
            if (!(end1.isBefore(start2) || end1.equals(start2) || end2.isBefore(start1) || end2.equals(start1))) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void delete(long bookingID) {
        Booking b = bookingRepository.findById(bookingID);
        if (b != null) {
            long userID = b.getUser().getId();
            bookingRepository.delete(bookingID);
            cacheManager.getCache("userCodes").evict(userID);
        }
    }

    public LinkedList<BookingDTO> getBookings(User user) {
        LinkedList<BookingDTO> list = new LinkedList<>();
        for (Booking b : user.getBookings())
            list.add(bookingMapper.toDto(b));
        return list;
    }
}
