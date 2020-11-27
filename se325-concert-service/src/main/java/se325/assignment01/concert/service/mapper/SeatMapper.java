package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Seat;

public class SeatMapper {

    public static SeatDTO toDto(Seat seat){
        SeatDTO seatDTO = new SeatDTO(seat.getLabel(), seat.getPrice());
        return seatDTO;
    }
}
