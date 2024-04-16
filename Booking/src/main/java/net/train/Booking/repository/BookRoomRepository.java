package net.train.Booking.repository;

import net.train.Booking.model.BookRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRoomRepository extends JpaRepository<BookRoom, Long> {
    List<BookRoom> findByRoomId(Long roomId);
    Optional<BookRoom> findByBookingConfirmationCode(String confirmationCode);

    List<BookRoom> findByGuestEmail(String email);
}
