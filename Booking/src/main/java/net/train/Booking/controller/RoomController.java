package net.train.Booking.controller;


//import io.swagger.v3.oas.annotations.parameters.RequestBody;

import lombok.RequiredArgsConstructor;
import net.train.Booking.exception.PhotoRetrievalExcrption;
import net.train.Booking.exception.ResourceNotFoundException;
import net.train.Booking.model.BookRoom;
import net.train.Booking.model.Room;
import net.train.Booking.response.BookRoomResponse;
import net.train.Booking.response.RoomResponse;
import net.train.Booking.service.IServices.IBookRoomService;
import net.train.Booking.service.IServices.IRoomService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class RoomController {
    private final IRoomService _roomService;
    private final IBookRoomService _bookRoomService;

    //Thêm phòng mới , consumes = {"multipart/form-data"}
    @PostMapping(value = "create-room",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "roomPrice", required = false) BigDecimal roomPrice) throws SQLException, IOException {
        Room saveRoom = _roomService.addNewRoom(roomType,photo, roomPrice);
        RoomResponse roomResponse = new RoomResponse(saveRoom.getId(), saveRoom.getRoomType(),
                saveRoom.getRoomPrice());
        return ResponseEntity.ok(roomResponse);
    }

    //Get kiểu phòng ko trùng lặp
    @GetMapping("get-roomType")
    public List<String> getRoomTypes() {
        return _roomService.getAllRoomTypes();
    }

    //Get tất cả phòng
    @GetMapping("get-allRoom")
    public ResponseEntity<List<RoomResponse>> getAllRoom() throws SQLException {
        List<Room> rooms = _roomService.getAllRoom();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms)
        {
            byte[] photoBytes = _roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0)
            {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok(roomResponses);
    }

    @DeleteMapping("deleteRoom/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") Long Id){
        _roomService.deleteRoom(Id);
        return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "updateRoom/{roomId}",consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long roomId,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) BigDecimal roomPrice,
            @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {
            byte[]  photoBytes = photo != null && !photo.isEmpty()? photo.getBytes() : _roomService.getRoomPhotoByRoomId(roomId);
            Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes):null;
            Room theRoom = _roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
            theRoom.setPhoto(photoBlob);
            RoomResponse roomResponse = getRoomResponse(theRoom);
            return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("getRoomById/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable("roomId") Long roomId){
            Optional<Room> theRoom = _roomService.getRoomByRoomId(roomId);
            return theRoom.map(room -> {
                RoomResponse roomResponse = getRoomResponse(room);
                return ResponseEntity.ok(Optional.of(roomResponse));
            }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @GetMapping("get-roomAvailable")
    public  ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) throws SQLException {
        List<Room> availableRooms = _roomService.getAvailableRooms(checkInDate,checkOutDate,roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for(Room room: availableRooms)
        {
            byte[] photoBytes = _roomService.getRoomPhotoByRoomId(room.getId());
            if(photoBytes != null && photoBytes.length > 0)
            {
                String photoBase64 = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(photoBase64);
                roomResponses.add(roomResponse);
            }
        }
        if (roomResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(roomResponses);
        }
    }

    private RoomResponse getRoomResponse(Room room) {
        List<BookRoom> bookings = getAllBookingByRoomId(room.getId());
        List<BookRoomResponse> bookingInfo = bookings
                .stream()
                .map(booking -> new BookRoomResponse(booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getGuestFullName(),
                        booking.getGuestEmail(),
                        booking.getNumOfAdults(),
                        booking.getNumOfChildren(),
                        booking.getTotalNumOfGuest(),
                        booking.getBookingConfirmationCode()
                        )).toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null){
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }
            catch (SQLException e){
                throw new PhotoRetrievalExcrption("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.isBooked(), photoBytes, bookingInfo);
    }

    private List<BookRoom> getAllBookingByRoomId(Long id) {
        return _bookRoomService.getAllBookingByRoomId(id);
    }
}
