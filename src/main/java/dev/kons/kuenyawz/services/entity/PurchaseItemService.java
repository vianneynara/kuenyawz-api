package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.purchase.PurchaseItemDto;
import dev.kons.kuenyawz.entities.PurchaseItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface PurchaseItemService {
	/**
	 * Converts a PurchaseItem entity to a PurchaseItemDto
	 *
	 * @param entity {@link PurchaseItem} entity to be converted
	 * @return {@link PurchaseItemDto} converted from the entity
	 */
	@Transactional(readOnly = true)
	PurchaseItemDto convertToDto(PurchaseItem entity);
}
