package org.ilaria.progetto.Controller;

import lombok.AllArgsConstructor;
import org.ilaria.progetto.Model.DTO.ClassroomDTO;
import org.ilaria.progetto.Model.DTO.classroomBookingDTO;
import org.ilaria.progetto.Model.DTO.ClassroomCodeDTO;
import org.ilaria.progetto.Model.Entity.Booking;
import org.ilaria.progetto.Model.Entity.User;
import org.ilaria.progetto.Repository.UserRepository;
import org.ilaria.progetto.Service.ClassroomService;
import org.ilaria.progetto.Service.BookingService;
import org.ilaria.progetto.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("/FreeClassroom/home/Student")
@AllArgsConstructor
public class StudentController {

    private final ClassroomService classroomService;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final UserService userService;

    /* uno studente può entrare in un aula libera solo se non è un laboratorio*/
    @PostMapping("/classroomCheck")
    public ResponseEntity<LinkedList<classroomBookingDTO>> classroomCheck(@RequestBody long classroomCode) {
        return ResponseEntity.ok(classroomService.classroomsBooking(classroomCode));
    }

    @PostMapping("/Enter")
    public ResponseEntity<String> reportEntry(@RequestBody ClassroomCodeDTO classroomCodeDTO) {
        try{
            long classroomID= classroomCodeDTO.getClassroomID();
            int code= classroomCodeDTO.getCode();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            ClassroomDTO classroom = classroomService.getClassroomDTO(classroomID);
            if(classroom.isLaboratory()){
                if(code!=0 && !bookingService.checkCode(classroomID,code, user.getId()))
                    return ResponseEntity.badRequest().body("wrong code");
                else
                    if(code==0)
                        return ResponseEntity.badRequest().
                                body("Laboratory must be booked before entering");
            }
            if(user.getClassroomIDin()!=-1)
                return ResponseEntity.badRequest().body("You cannot enter more than one classroom at the same time");
            if(classroomService.IsFree(classroomID)!=0) return ResponseEntity.badRequest().body("classroom occupied");
            classroomService.enter(user,classroom);
            return ResponseEntity.ok(  "user entered the classroom");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/Exit")
    public ResponseEntity<String> reportExit(@RequestBody long classroomID) {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            if(user.getClassroomIDin()==-1)
                return ResponseEntity.badRequest().body("you can't leave without entering a classroom");
            classroomService.exit(user,classroomID);
            return ResponseEntity.ok("user left the classroom");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/checkCode")
    public ResponseEntity<String> checkCode() {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findUser(email);
            List<Booking> bookingList = bookingService.findByUtente(user.getId());
            if(bookingList == null || bookingList.isEmpty()) return ResponseEntity.badRequest().body("You have not made any reservations");
            StringBuilder response = new StringBuilder();
            for(Booking p : bookingList)
                if(p.getCode()!=0) response.append("id: ").
                        append(p.getClassroom().getCube()).append(" : ").
                        append(p.getCode()).append(" : ").
                        append(p.getBookingDate() + "\n");
            if (response.isEmpty()) return ResponseEntity.badRequest().body("recharge soon, you have not yet received acceptances for your reservations");
            return ResponseEntity.ok(response.toString());
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
