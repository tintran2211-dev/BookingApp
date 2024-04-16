import React, { useEffect, useState } from "react";
import { Form, FormControl, Button } from "react-bootstrap";
import { bookRoom, getRoomById } from "../utils/ApiFunctions";
import { useNavigate, useParams } from "react-router-dom";
import moment from "moment";
import BookingSummary from "./BookingSummary";
import { useAuth } from "../auth/AuthProvider";

export const BookingForm = () => {
  //Set trạng thái các biến bằng useState
  const [validated, setValidated] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [roomPrice, setRoomPrice] = useState(0);

  const currentUser = localStorage.getItem("userId");
  const [booking, setBooking] = useState({
    guestFullName: "",
    guestEmail: currentUser,
    checkInDate: "",
    checkOutDate: "",
    numOfAdults: "",
    numOfChildren: "",
  });

  const [roomInfo, setRoomInfo] = useState({
    photo: "",
    roomType: "",
    roomPrice: "",
  });

  //Khai báo 1 biến roomID với hook useParams để lấy tham số động trong Url
  const { roomId } = useParams();
  const navigate = useNavigate();

  //Hàm lấy giá phòng bằng ID
  const getRoomPriceById = async (roomId) => {
    try {
      const response = await getRoomById(roomId);
      setRoomPrice(response.roomPrice);
    } catch (error) {
      throw new Error(error);
    }
  };

  //Sử dụng UseEffect để lấy giá phòng khi roomId thay đổi
  useEffect(() => {
    getRoomPriceById(roomId);
  }, [roomId]);

  //Hàm xử lý thay đổi đầu vào
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setBooking({ ...booking, [name]: value });
    setErrorMessage("");
  };

  //Hàm tính tiền phòng theo số ngày book phòng
  const calculatePayment = () => {
    const checkInDate = moment(booking.checkInDate);
    const checkOutDate = moment(booking.checkOutDate);
    const diffInDays = checkOutDate.diff(checkInDate, "days");
    const paymentPerDay = roomPrice ? roomPrice : 0;
    return diffInDays * paymentPerDay;
  };
  // const calculatePayment = () => {
  // 	const checkInDate = moment(booking.checkInDate)
  // 	const checkOutDate = moment(booking.checkOutDate)
  // 	const diffInDays = checkOutDate.diff(checkInDate, "days")
  // 	const paymentPerDay = roomPrice ? roomPrice : 0
  // 	return diffInDays * paymentPerDay
  // }

  //Hàm tính tổng số người
  const isGuestCountValid = () => {
    const adultCount = parseInt(booking.numOfAdults);
    const childrenCount = parseInt(booking.numOfChildren);
    const totalCount = adultCount + childrenCount;
    return totalCount >= 1 && adultCount >= 1;
  };

  //Hàm kiểm tra ngày checkout khi book phòng
  //Ngày checkout không được bằng hoặc sơm hơn ngày checkin
  const isCheckOutDateValid = () => {
    if (!moment(booking.checkOutDate).isSameOrAfter(moment(booking.checkInDate))) {
      setErrorMessage("Check-out date must come before check-in date");
      return false;
    } else {
      setErrorMessage("");
      return true;
    }
  };

  //Hàm xử lý submit
  const handleSubmit = (e) => {
    //sử dụng e.preventDefault() để ngăn chặn cách xử lý mặc định của trình duyệt khi xảy ra sự kiện
    e.preventDefault();
    const form = e.currentTarget;
    //Kiểm tra điều kiện nếu 1 trong các hàm không hợp lệ thì thực hiện e.stopPropagation()
    if (form.checkValidity() === false || !isGuestCountValid() || !isCheckOutDateValid()) {
      //sử dụng e.stopPropagation() để ngăn chặn cho các sự kiện lan toả lên các phần tử mẹ của phần tử ở đó diễn ra sự kiện
      e.stopPropagation();
    } else {
      setIsSubmitted(true);
    }
    setValidated(true);
  };

  //Hàm xử lý sự kiện book phòng sau khi book thành công sẽ trả về một mã xác nhận book phòng
  const handleFormSubmit = async () => {
    try {
      const confirmationCode = await bookRoom(roomId, booking);
      setIsSubmitted(true);
      navigate("/booking-success", { state: { message: confirmationCode } });
    } catch (error) {
      setErrorMessage(error.message);
      navigate("/booking-success", { state: { error: errorMessage } });
    }
  };
  return (
    <>
      <div className="container mb-5">
        <div className="row">
          <div className="col-md-6">
            <div className="card card-body mt-5">
              <h4 className="card-title">Reserve Room</h4>
              <Form noValidate validated={validated} onSubmit={handleSubmit}>
                <Form.Group>
                  <Form.Label htmlFor="guestFullName" className="hotel-color">
                    FullName:
                  </Form.Label>
                  <FormControl required type="text" id="guestFullName" name="guestFullName" value={booking.guestFullName} placeholder="Enter your full name" onChange={handleInputChange}></FormControl>
                  <Form.Control.Feedback type="invalid">Please enter your fullname.</Form.Control.Feedback>
                </Form.Group>
                <Form.Group>
                  <Form.Label htmlFor="guestEmail" className="hotel-color">
                    Email:
                  </Form.Label>
                  <FormControl required type="email" id="guestEmail" name="guestEmail" 
// @ts-ignore
                  value={booking.guestEmail} placeholder="Enter your email" onChange={handleInputChange}></FormControl>
                  <Form.Control.Feedback type="invalid">Please enter a valid email address.</Form.Control.Feedback>
                </Form.Group>
                <fieldset style={{ border: "2px" }}>
                  <legend>Lodging Period</legend>
                  <div className="row">
                    <div className="col-6">
                      <Form.Label htmlFor="checkInDate" className="hotel-color">
                        Check-in date
                      </Form.Label>
                      <FormControl required type="date" id="checkInDate" name="checkInDate" value={booking.checkInDate} placeholder="check-in date" min={moment().format("MMM Do, YYYY")} onChange={handleInputChange}></FormControl>
                      <Form.Control.Feedback type="invalid">Please select a check in date.</Form.Control.Feedback>
                    </div>
                    <div className="col-6">
                      <Form.Label htmlFor="checkOutDate" className="hotel-color">
                        Check-out date
                      </Form.Label>
                      <FormControl required type="date" id="checkOutDate" name="checkOutDate" value={booking.checkOutDate} placeholder="check-out date" min={moment().format("MMM Do, YYYY")} onChange={handleInputChange}></FormControl>
                      <Form.Control.Feedback type="invalid">Please select a check out date.</Form.Control.Feedback>
                    </div>
                    {errorMessage && <p className="error-message text-danger">{errorMessage}</p>}
                  </div>
                </fieldset>
                <fieldset>
                  <legend>Number of Guest</legend>
                  <div className="row">
                    <div className="col-6">
                      <Form.Label htmlFor="numOfAdults" className="hotel-color">
                        Adult :
                      </Form.Label>
                      <FormControl required type="number" id="numOfAdults" name="numOfAdults" value={booking.numOfAdults} placeholder="0" min={1} onChange={handleInputChange}></FormControl>
                      <Form.Control.Feedback type="invalid">Please select at least 1 adult.</Form.Control.Feedback>
                    </div>
                    <div className="col-6">
                      <Form.Label htmlFor="numOfChildren" className="hotel-color">
                        Children :
                      </Form.Label>
                      <FormControl required type="number" id="numOfChildren" name="numOfChildren" value={booking.numOfChildren} placeholder="0" min={0} onChange={handleInputChange}></FormControl>
                      <Form.Control.Feedback type="invalid">Please select at least 1 adult.</Form.Control.Feedback>
                    </div>
                  </div>
                </fieldset>
                <div className="fom-group mt-2 mb-2">
                  <button type="submit" className="btn btn-hotel">
                    Continue
                  </button>
                </div>
              </Form>
            </div>
          </div>
          <div className="col-md-4">{isSubmitted && <BookingSummary booking={booking} payment={calculatePayment()} onConfirm={handleFormSubmit} isFormValid={validated} />}</div>
        </div>
      </div>
    </>
  );
};
export default BookingForm;
