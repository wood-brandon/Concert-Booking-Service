package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Seat;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

//    public static Booking toDomainModel(BookingDTO bookingDTO){
//        Booking booking = new Booking(bookingDTO.getConcertId(), bookingDTO.getDate(),bookingDTO.getSeats());
//        return booking;
//    }

    public static BookingDTO toDto(Booking booking){
        List<SeatDTO> seatsDto = new ArrayList<>();
        for(Seat s: booking.getSeats()){
            seatsDto.add(SeatMapper.toDto(s));
        }
        BookingDTO bookingDTO = new BookingDTO(booking.getConcertId(), booking.getDate(),seatsDto);
        return bookingDTO;
    }
}
