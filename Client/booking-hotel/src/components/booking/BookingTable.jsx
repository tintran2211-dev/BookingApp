import React, { useEffect, useState } from "react";
import DateSlider from "../common/DateSlider";

const BookingTable = ({ bookingInfo, handleBookingCancellation }) => {
  const [filteredBookings, setFilteredBookings] = useState(bookingInfo);

  const filterBookings = (startDate, endDate) => {
    let filtered = bookingInfo;
    if (startDate && endDate) {
      filtered = bookingInfo.filter((booking) => {
        const bookingStartDate = new Date(booking.checkInDate[0], booking.checkInDate[1] - 1, booking.checkInDate[2]);
        const bookingEndDate = new Date(booking.checkOutDate[0], booking.checkOutDate[1] - 1, booking.checkOutDate[2]);
        return bookingStartDate <= endDate && bookingEndDate >= startDate;
      });
    }
    setFilteredBookings(filtered);
  };

  function formatDate(inputDate) {
    // Kiểm tra nếu inputDate không phải là một mảng hoặc không có đủ số phần tử
    if (!Array.isArray(inputDate) || inputDate.length !== 3) {
      return "Invalid Date";
    }
    // Trích xuất năm, tháng và ngày từ mảng
    const year = inputDate[0];
    const month = inputDate[1] < 10 ? `0${inputDate[1]}` : inputDate[1];
    const day = inputDate[2] < 10 ? `0${inputDate[2]}` : inputDate[2];
    // Tạo định dạng ngày mới
    return `${day}/${month}/${year}`;
  }

  useEffect(() => {
    setFilteredBookings(bookingInfo);
  }, [bookingInfo]);

  return (
    <section className="p-4">
      <DateSlider onDateChange={filterBookings} onFilterChange={filterBookings} />
      <table className="table table-bordered table-hover shadow">
        <thead>
          <tr>
            <th>S/N</th>
            <th>Booking ID</th>
            <th>Room ID</th>
            <th>Room Type</th>
            <th>Check-In Date</th>
            <th>Check-Out Date</th>
            <th>Guest Name</th>
            <th>Guest Email</th>
            <th>Adults</th>
            <th>Children</th>
            <th>Total Guest</th>
            <th>Confirmation Code</th>
            <th colSpan={2}>Actions</th>
          </tr>
        </thead>
        <tbody className="text-center">
          {filteredBookings.map((booking, index) => (
            <tr key={booking.id}>
              <td>{index + 1}</td>
              <td>{booking.id}</td>
              <td>{booking.room.id}</td>
              <td>{booking.room.roomType}</td>
              <td>{formatDate(booking.checkInDate)}</td>
              <td>{formatDate(booking.checkOutDate)}</td>
              <td>{booking.guestFullName}</td>
              <td>{booking.guestEmail}</td>
              <td>{booking.numOfAdults}</td>
              <td>{booking.numOfChildren}</td>
              <td>{booking.totalNumOfGuest}</td>
              <td>{booking.bookingConfirmationCode}</td>
              <td>
                <button className="btn btn-danger btn-sm" onClick={() => handleBookingCancellation(booking.id)}>
                  Cancel
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {filteredBookings.length === 0 && <p> No booking found for the selected dates</p>}
    </section>
  );
};

export default BookingTable;
