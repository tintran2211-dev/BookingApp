package net.train.Booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(name = "CheckIn")
    private LocalDate checkInDate;

    @Column(name = "CheckOut")
    private LocalDate checkOutDate;

    @Column(name = "GuestFullName")
    private String guestFullName;

    @Column(name = "GuestEmail")
    private String guestEmail;

    @Column(name = "Adults")
    private int numOfAdults ;

    @Column(name = "Childrens")
    private int numOfChildren;

    @Column(name = "TotalGuest")
    private int totalNumOfGuest;

    @Column(name = "ConfirmationCode")
    private String bookingConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomId")
    private Room room;

    public void calculateTotalNumberOfGuest() {
        this.totalNumOfGuest = this.numOfAdults + numOfChildren;
    }

    public void setNumOfAdults(int numOfAdults) {
        this.numOfAdults = numOfAdults;
        calculateTotalNumberOfGuest();
    }

    public void setNumOfChildren(int numOfChildren) {
        this.numOfChildren = numOfChildren;
        calculateTotalNumberOfGuest();
    }

    public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}
