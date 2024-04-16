package net.train.Booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import net.train.Booking.exception.InvalidBookingRequestException;
import net.train.Booking.exception.ResourceNotFoundException;
import net.train.Booking.model.BookRoom;
import net.train.Booking.model.Room;
import net.train.Booking.response.BookRoomResponse;
import net.train.Booking.response.RoomResponse;

import net.train.Booking.service.IServices.IBookRoomService;
import net.train.Booking.service.IServices.IRoomService;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class BookRoomController {
    private final IBookRoomService _bookingService;
    private final IRoomService _roomService;

    @GetMapping("get-allBooking")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookRoomResponse>> getAllBooking() {
        List<BookRoom> bookings = _bookingService.getAllBooking();
        List<BookRoomResponse> bookingResponses = new ArrayList<>();
        for (BookRoom booking : bookings) {
            BookRoomResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }


    @GetMapping("getBookingByConfirmationCode/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            BookRoom booking = _bookingService.findByBookingConfirmationCode(confirmationCode);
            BookRoomResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("create-booking/{roomId}")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookRoom bookingRequest) {
        try {
            String confirmationCode = _bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully, Your booking confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("get-HistoryUserBooking/{email}")
    public ResponseEntity<List<BookRoomResponse>> getBookingByUserEmail(@PathVariable String email){
        List<BookRoom> bookRooms = _bookingService.getBookingsByUserEmail(email);
        List<BookRoomResponse> bookRoomResponses = new ArrayList<>();
        for(BookRoom bookRoom: bookRooms){
            BookRoomResponse bookRoomResponse = getBookingResponse(bookRoom);
            bookRoomResponses.add(bookRoomResponse);
        }
        return ResponseEntity.ok(bookRoomResponses);
    }

    @DeleteMapping("delete-booking/{bookingId}")
    public void cancelBooking(@PathVariable("bookingId") Long bookingId) {
        _bookingService.cancelBooking(bookingId);
    }

    private BookRoomResponse getBookingResponse(BookRoom booking) {
        Room theRoom = _roomService.getRoomByRoomId(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice()
        );
        return new BookRoomResponse(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(), room
        );
    }
}
