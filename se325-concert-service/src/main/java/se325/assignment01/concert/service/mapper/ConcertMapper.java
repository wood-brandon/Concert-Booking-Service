package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConcertMapper {

    public static Concert toDomainModel(ConcertDTO concertDTO){
        Concert fullConcert= new Concert(concertDTO.getId(),
                concertDTO.getTitle(),
                concertDTO.getImageName(),
                concertDTO.getBlurb());

        Set<LocalDateTime> datesSet = new HashSet<>(concertDTO.getDates());
        fullConcert.setDates(datesSet);

        // map performers by converting each dto performer to domain performer
        List<PerformerDTO> dtoPerformers = concertDTO.getPerformers();
        List<Performer> performersList = new ArrayList<>();

        for (PerformerDTO temp: dtoPerformers){
            Performer performer = PerformerMapper.toDomainModel(temp);
            performersList.add(performer);
        }

        fullConcert.setPerformers(performersList);
        return fullConcert;
    }

    public static ConcertDTO toDto(Concert concert){
        ConcertDTO concertDTO = new ConcertDTO(concert.getId(),
                concert.getTitle(),
                concert.getImageName(),
                concert.getBlurb());

        List<LocalDateTime> datesList = new ArrayList<>(concert.getDates());
        concertDTO.setDates(datesList);

        // map performers by converting each dto performer to domain performer
        List<Performer> performers = concert.getPerformers();
        List<PerformerDTO> performersDTOList = new ArrayList<>();
        for (Performer temp: performers){
            PerformerDTO performerDTO = PerformerMapper.toDto(temp);
            performersDTOList.add(performerDTO);
        }

        concertDTO.setPerformers(performersDTOList);

        return concertDTO;
    }

    public static ConcertSummaryDTO toSummaryDto(Concert concert){
        ConcertSummaryDTO concertSummaryDTO = new ConcertSummaryDTO(concert.getId(),concert.getTitle(),concert.getImageName());
        return concertSummaryDTO;
    }
}
