package org.ilaria.progetto.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progetto.Model.DTO.*;
import org.ilaria.progetto.Model.Entity.User;
import org.ilaria.progetto.Repository.UserRepository;
import org.ilaria.progetto.Role;
import org.ilaria.progetto.Service.ClassroomService;
import org.ilaria.progetto.Service.FeedbackService;
import org.ilaria.progetto.Service.BookingService;
import org.ilaria.progetto.Service.UserService;
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
@RequestMapping("/FreeClassroom/home")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final ClassroomService classroomService;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final PasswordEncoder passwordEncoder;
    private final FeedbackService feedbackService;

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        try {
            userService.register(user);
            return ResponseEntity.ok("User successfully registered");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        return userService.login(user);
    }

    /* ad ogni richiesta di getLista se esistono prenotazioni viene controllato che non ci siano
        prenotazioni scadute e dopo di che mostra le aule libere */

    @GetMapping("/getList")
    public ResponseEntity<List<ClassroomDTO>> classroomList() {
        return ResponseEntity.ok(classroomService.list());
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> feedback(@RequestBody FeedbackDTO feedback) {
        feedbackService.save(feedback);
        return ResponseEntity.ok("feedback entered");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody long bookingID) {
        bookingService.delete(bookingID);
        return ResponseEntity.ok("reservation cancelled");
    }

    @GetMapping("/getBooking")
    public ResponseEntity<LinkedList<BookingDTO>> getBooking() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(bookingService.getBookings(user));
    }

    @PostMapping("updateCredentials")
    public ResponseEntity<String> updateCredentials(@RequestBody modifyDTO dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            if(dto.getEmail()==null) dto.setEmail(email);
            if(dto.getPassword()==null) dto.setPassword(user.getPassword());
            else dto.setPassword(passwordEncoder.encode(dto.getPassword()));
            userService.update(user.getId(),dto.getEmail(),dto.getPassword());
            return ResponseEntity.ok("Credentials updated successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /* è possibile per uno studente prenotare un laboratorio solo se ha posti in quel momento
      un docente può prenotare un aula a patto che non sia stata prenotata gia da un altro docente */

    @PostMapping("/booking")
    public ResponseEntity<String> booking(@RequestBody BookingDTO booking) {
        try{
            System.out.println("oggetto " + booking);
            if(!bookingService.isBookable(booking))
                return ResponseEntity.badRequest().body("Classroom not available");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            if(!classroomService.getClassroom(booking.getLaboratoryID()).isLaboratory() && user.getRole().equals(Role.STUDENT))
                return ResponseEntity.badRequest().body("It is not necessary to book a non-laboratory classroom");
            booking.setUserID(user.getId());
            if(bookingService.existBookingForDate(booking))
                return ResponseEntity.badRequest().body("You cannot book multiple classrooms at the same time");
            if(booking.getBookingDate().isBefore(LocalDateTime.now()))
                return ResponseEntity.badRequest().body("Invalid booking date");
            bookingService.reservation(booking);
            return ResponseEntity.ok("Classroom booked successfully");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
