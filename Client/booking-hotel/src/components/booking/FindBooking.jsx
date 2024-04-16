import React, { useState } from "react";
import { cancelBooking, getBookingByConfirmationCode } from "../utils/ApiFunctions";
import moment from "moment";

const FindBooking = () => {
  const [confirmationCode, setConfirmationCode] = useState("");
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isDeleted, setIsDeleted] = useState(false);

  const [bookingInfo, setBookingInfo] = useState({
    id: "",
    bookingConfirmationCode: "",
    room: { id: "", roomType: "" },
    roomNumber: "",
    checkInDate: "",
    checkOutDate: "",
    guestFullName: "",
    guestEmail: "",
    numOfAdults: "",
    numOfChildren: "",
    totalNumOfGuest: "",
  });

  const emptyBookingInfo = {
    id: "",
    bookingConfirmationCode: "",
    room: { id: "", roomType: "" },
    roomNumber: "",
    checkInDate: "",
    checkOutDate: "",
    guestFullName: "",
    guestEmail: "",
    numOfAdults: "",
    numOfChildren: "",
    totalNumOfGuest: "",
  };

  const handleInputChange = (e) => {
    setConfirmationCode(e.target.value);
  };

  //Hàm xử lý submitform
  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      //lấy thông tin phòng theo mã xác nhận
      const data = await getBookingByConfirmationCode(confirmationCode);
      setBookingInfo(data);
    } catch (error) {
      //nếu lỗi thì set thông tin phòng thành emptyBookingInfo
      setBookingInfo(emptyBookingInfo);
      //nếu kiểm tra response trả ra lỗi thì trả ra lỗi
      if (error.response && error.response.status === 400) {
        setError(error.response.data.message);
      } else {
        setError(error.message);
      }
    }
    setTimeout(() => {
      setIsLoading(false);
    }, 2000);
  };

  //Hàm xử lý huỷ booking room
  const handleBookingCancellation = async (bookingId) => {
    try {
      await cancelBooking(bookingInfo.id);
      setIsDeleted(true);
      setSuccessMessage("Booking has been cancelled successfully!");
      setBookingInfo(emptyBookingInfo);
      setConfirmationCode("");
      setError(null);
    } catch (error) {
      setError(error.message);
    }
    setTimeout(() => {
      setSuccessMessage("");
      setIsDeleted(false);
    }, 2000);
  };
  return (
    <>
      <div className="container mt-5 d-flex flex-column justify-content-center align-items-center">
        <h2 className="text-center mb-4">Find My Booking</h2>
        <form onSubmit={handleFormSubmit} className="col-md-6">
          <div className="input-group mb-3">
            <input type="text" className="form-control" id="confirmationCode" value={confirmationCode} onChange={handleInputChange} placeholder="Enter the booking confirmation code" />
            <button type="submit" className="btn btn-hotel input-group-text">
              Find booking
            </button>
          </div>
        </form>
        {isLoading ? (
          <div>Finding your booking...</div>
        ) : error ? (
          <div className="text-danger">Error: {error}</div>
        ) : bookingInfo.bookingConfirmationCode ? (
          <div className="col-md-6 mt-4 mb-5">
            <h3>Booking Information</h3>
            <p className="text-success">Confirmation Code: {bookingInfo.bookingConfirmationCode}</p>
            <p>Room Number: {bookingInfo.room.id}</p>
            <p>Room Type: {bookingInfo.room.roomType}</p>
            <p>Check-in Date: {moment(bookingInfo.checkInDate).subtract(1, "month").format("MMM Do, YYYY")}</p>
            <p>Check-out Date: {moment(bookingInfo.checkInDate).subtract(1, "month").format("MMM Do, YYYY")}</p>
            <p>Full Name: {bookingInfo.guestFullName}</p>
            <p>Email Address: {bookingInfo.guestEmail}</p>
            <p>Adults: {bookingInfo.numOfAdults}</p>
            <p>Children: {bookingInfo.numOfChildren}</p>
            <p>Total Guest: {bookingInfo.totalNumOfGuest}</p>
            {!isDeleted && (
              <button onClick={() => handleBookingCancellation(bookingInfo.id)} className="btn btn-danger">
                Cancel Booking
              </button>
            )}
          </div>
        ) : (
          <div>find booking...</div>
        )}
        {isDeleted && <div className="alert alert-success mt-3 fade show">{successMessage}</div>}
      </div>
    </>
  );
};

export default FindBooking;
