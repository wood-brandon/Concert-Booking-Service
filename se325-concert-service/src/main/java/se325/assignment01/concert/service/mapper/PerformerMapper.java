package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;

public class PerformerMapper {

    static Performer toDomainModel(PerformerDTO performerDTO){
        Performer fullPerformer = new Performer(performerDTO.getId(),
                performerDTO.getName(),
                performerDTO.getImageName(),
                performerDTO.getGenre(),
                performerDTO.getBlurb());
        return fullPerformer;
    }

    public static PerformerDTO toDto(Performer performer){
        PerformerDTO performerDTO = new PerformerDTO(performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                performer.getBlurb());
        return performerDTO;
    }
}
