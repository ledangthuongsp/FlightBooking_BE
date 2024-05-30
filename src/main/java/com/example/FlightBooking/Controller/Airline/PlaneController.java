package com.example.FlightBooking.Controller.Airline;

import com.example.FlightBooking.Models.Planes;
import com.example.FlightBooking.Services.Planes.PlaneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/airlines")
@Tag(name ="CRUD FOR AIRLINE", description = "apis for changing AIRLINE info")
public class PlaneController {
    @Autowired
    private PlaneService planeService;

    @PostMapping("/create-new-plane")
    public ResponseEntity<?> createNewPlane(@RequestParam Long airlineId) {
        try {
            Planes plane = planeService.createPlaneWithSeats(airlineId);
            return ResponseEntity.ok(plane);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating plane: " + e.getMessage());
        }
    }
    // Cai nay la xem thu cai ghe do da duoc dat chua, hay la on hold theo user ID nao
    @GetMapping("/{planeId}/get-seat-status")
    public ResponseEntity<?> getSeatStatuses(@PathVariable Long planeId) {
        try {
            Map<String, Map<String, String>> seatStatuses = planeService.getSeatStatuses(planeId);
            return ResponseEntity.ok(seatStatuses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting seat statuses: " + e.getMessage());
        }
    }

    //Khi nguoi dung booking ve thi hold ve cho nguoi dung
    @PostMapping("/{planeId}/hold")
    public ResponseEntity<?> holdSeats(@PathVariable Long planeId, @RequestBody Set<String> seatNumbers) {
        try {
            boolean success = planeService.holdSeats(planeId, seatNumbers);
            if (success) {
                return ResponseEntity.ok("Seats held successfully.");
            } else {
                return ResponseEntity.status(400).body("One or more seats are not available.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error holding seats: " + e.getMessage());
        }
    }
    // Dat ve may bay
    @PostMapping("/{planeId}/book")
    public ResponseEntity<?> bookSeats(@PathVariable Long planeId, @RequestBody Set<String> seatNumbers) {
        try {
            boolean success = planeService.bookSeats(planeId, seatNumbers);
            if (success) {
                return ResponseEntity.ok("Seats booked successfully.");
            } else {
                return ResponseEntity.status(400).body("One or more seats are not available for booking.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error booking seats: " + e.getMessage());
        }
    }
    @GetMapping("/get-plane-detail-by-planeId")
    public ResponseEntity<?> getDetailPlane(@RequestParam Long planeId)
    {
        try {
            Planes planes = planeService.getDetailPlane(planeId);
            return ResponseEntity.ok(planes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting Plane detail: " + e.getMessage());
        }
    }
    @PostMapping("/reset/{planeId}")
    public ResponseEntity<Planes> resetSeats(@RequestParam Long planeId) throws Exception {
        Planes planes = planeService.resetSeats(planeId);
        return ResponseEntity.ok().body(planes);

    }
}
