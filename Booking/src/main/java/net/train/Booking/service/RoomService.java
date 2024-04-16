package net.train.Booking.service;

import lombok.RequiredArgsConstructor;
import net.train.Booking.exception.InternalServerException;
import net.train.Booking.exception.ResourceNotFoundException;
import net.train.Booking.model.Room;
import net.train.Booking.repository.RoomRepository;
import net.train.Booking.service.IServices.IRoomService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {
    private final RoomRepository _roomRepository;


    @Override
    public Room addNewRoom( String roomType,MultipartFile file, BigDecimal roomPrice) throws IOException, SQLException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!file.isEmpty()){
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }
        return _roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return _roomRepository.findDistinctRoomType();
    }

    @Override
    public List<Room> getAllRoom() {
        return _roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long id) throws SQLException {
        Optional<Room> theRoom = _roomRepository.findById(id);
        if (theRoom.isEmpty())
        {
            throw new ResourceNotFoundException("Sorry, Room not found!");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if(photoBlob != null)
        {
            return photoBlob.getBytes(1,(int) photoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long Id) {
        Optional<Room> theRoom = _roomRepository.findById(Id);
        if(theRoom.isPresent()){
            _roomRepository.deleteById(Id);
        }
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) {
        Room room = _roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found"));
        if (roomType != null) room.setRoomType(roomType);
        if (roomPrice != null) room.setRoomPrice(roomPrice);
        if (photoBytes != null && photoBytes.length >0){
            try{
                room.setPhoto(new SerialBlob(photoBytes));
            }catch (SQLException ex){
                throw new InternalServerException("Error updating room");
            }
        }
        return _roomRepository.save(room);
    }

    @Override
    public Optional<Room> getRoomByRoomId(Long roomId) {
        return Optional.of(_roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return _roomRepository.findAvailableRoomsByDatesAndType( checkInDate,  checkOutDate,  roomType);
    }
}
