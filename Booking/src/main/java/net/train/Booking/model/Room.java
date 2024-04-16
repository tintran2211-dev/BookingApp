package net.train.Booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked = false;

    @Lob
    private Blob photo;

    //Tạo relationship 1-n (1 phòng có thể nhiều booking)
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookRoom> bookings;

    public Room() {
        this.bookings = new ArrayList<>();
    }

    //  Hàm addbooking để xử lý khi có request booking phòng
    public void addBooking(BookRoom bookRoom) {
        //Kiểm tra booking đã khởi tạo chưa nếu booking == null chưa khởi tạo thì sẽ khởi tạo là 1 mảng
        //đảm bảo booking không null
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        //thêm bookRoom vào danh sách bookings
        bookings.add(bookRoom);
        //Set room cho bookroom, tức là mỗi khi request đặt phòng thì nó sẽ được liên kết với 1 phòng cụ thể
        bookRoom.setRoom(this);
        //Sau khi bookroom thành công chuyển trạng thái book thành true
        isBooked = true;
        //Sau đó tạo mã xác nhận đặt phòng chuyển cho người dùng
        String bookingCode = RandomStringUtils.randomNumeric(10);
        bookRoom.setBookingConfirmationCode(bookingCode);
    }
}
