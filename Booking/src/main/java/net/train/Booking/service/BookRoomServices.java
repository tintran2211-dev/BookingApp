package net.train.Booking.service;

import lombok.RequiredArgsConstructor;
import net.train.Booking.exception.InvalidBookingRequestException;
import net.train.Booking.exception.ResourceNotFoundException;
import net.train.Booking.model.BookRoom;
import net.train.Booking.model.Room;
import net.train.Booking.repository.BookRoomRepository;
import net.train.Booking.service.IServices.IBookRoomService;
import net.train.Booking.service.IServices.IRoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookRoomServices implements IBookRoomService {
    private final BookRoomRepository _bookingRepository;
    private final IRoomService _roomService;

    @Override
    public List<BookRoom> getAllBookingByRoomId(Long id) {
        return _bookingRepository.findByRoomId(id);
    }

    @Override
    public List<BookRoom> getAllBooking() {
        return _bookingRepository.findAll();
    }

    @Override
    public void cancelBooking(Long bookingId) {
        _bookingRepository.deleteById(bookingId);
    }

    @Override
    public String saveBooking(Long roomId, BookRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Room room = _roomService.getRoomByRoomId(roomId).get();
        List<BookRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if (roomIsAvailable) {
            room.addBooking(bookingRequest);
            _bookingRepository.save(bookingRequest);
        } else {
            throw new InvalidBookingRequestException("Sorry, This room is not availabe for the selected dates;");
        }
        return bookingRequest.getBookingConfirmationCode();
    }

    private boolean roomIsAvailable(BookRoom bookingRequest, List<BookRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

    @Override
    public BookRoom findByBookingConfirmationCode(String confirmationCode) {
        return _bookingRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("No booking found with booking code: " + confirmationCode));
    }

    @Override
    public List<BookRoom> getBookingsByUserEmail(String email) {
        return _bookingRepository.findByGuestEmail(email);
    }
}
