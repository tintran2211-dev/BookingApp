package net.train.Booking.service.IServices;

import net.train.Booking.model.BookRoom;

import java.util.List;

public interface IBookRoomService {
    List<BookRoom> getAllBookingByRoomId(Long id);

    List<BookRoom> getAllBooking();

    void cancelBooking(Long bookingId);

    String saveBooking(Long roomId, BookRoom bookingRequest);

    BookRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookRoom> getBookingsByUserEmail(String email);
}
