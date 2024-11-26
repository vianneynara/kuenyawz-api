package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.entities.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

	TransactionDto toDto(Transaction transaction);
}
