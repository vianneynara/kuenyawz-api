package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.purchase.TransactionDto;
import dev.kons.kuenyawz.entities.Transaction;

public interface TransactionMapper {

	TransactionDto toDto(Transaction transaction);
}
