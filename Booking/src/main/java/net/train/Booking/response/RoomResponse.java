package net.train.Booking.response;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomResponse {
    private Long Id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked;
    private String photo;
    private List<BookRoomResponse> bookings;

    //Constructor khi tạo mới room
    public RoomResponse(Long Id, String roomType, BigDecimal roomPrice) {
        this.Id = Id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    //Constructor khi get,update
    public RoomResponse(Long Id, String roomType, BigDecimal roomPrice, boolean isBooked,
                        byte[] photoBytes,List<BookRoomResponse> bookings) {
        this.Id = Id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
        this.bookings = bookings;
    }
}
