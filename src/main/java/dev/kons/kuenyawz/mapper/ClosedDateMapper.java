package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.closeddate.ClosedDatePostDto;
import dev.kons.kuenyawz.entities.ClosedDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface ClosedDateMapper {
    
    @Mapping(target = "closedDateId", ignore = true)
    @Mapping(target = "date", expression = "java(parseDate(dto.getDate()))")
    @Mapping(target = "closureType", expression = "java(parseClosureType(dto.getType()))")
    ClosedDate toEntity(ClosedDatePostDto dto);

    default LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
    }

    default ClosedDate.ClosureType parseClosureType(String type) {
        return ClosedDate.ClosureType.fromString(type);
    }

    default String formatDate(Date date) {
        return DateTimeFormatter.ISO_DATE.format(
            date.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        );
    }
}