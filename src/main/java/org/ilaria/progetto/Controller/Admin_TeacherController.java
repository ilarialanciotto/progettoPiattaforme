package org.ilaria.progetto.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progetto.Model.DTO.ClassroomDTO;
import org.ilaria.progetto.Model.DTO.BookingDTO;
import org.ilaria.progetto.Model.Entity.Booking;
import org.ilaria.progetto.Model.Entity.User;
import org.ilaria.progetto.Repository.UserRepository;
import org.ilaria.progetto.Service.ClassroomService;
import org.ilaria.progetto.Service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/FreeClassroom/home/Admin")
@AllArgsConstructor
public class Admin_TeacherController {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ClassroomService classroomService;

    /* un docente ha la lista delle prenotazioni dei laboratori di cui è responsabile e che non
     ha ancora approvato, prima viene fatto un controllo per eliminare le prenotazioni scadute*/

    @PreAuthorize("hasRole('TEACHER') or  hasRole('ADMIN')")
    @GetMapping("/getList")
    public ResponseEntity<List<BookingDTO>> bookingList() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User teacher = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(bookingService.list(teacher.getId(),teacher.getRole()));
    }

    /* un docente accetta la prenotazione */

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/approveBooking")
    public ResponseEntity<String> bookingAccept(@RequestBody long bookingID) {
        Booking booking = bookingService.getBooking(bookingID);
        bookingService.approveBooking(booking);
        return ResponseEntity.ok("Booking approved successfully");
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/Teacher/personInCharge")
    public ResponseEntity<String> personInCharge(@RequestBody long classroomID) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User teacher = userRepository.findByEmail(email).orElseThrow();
        classroomService.personInCharge(classroomID, teacher);
        return ResponseEntity.ok("Responsibility assigned successfully");
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/Teacher/getLaboratory")
    public ResponseEntity<List<ClassroomDTO>> classroomList() {
        return ResponseEntity.ok(classroomService.getAdminClassrooms());
    }

}
