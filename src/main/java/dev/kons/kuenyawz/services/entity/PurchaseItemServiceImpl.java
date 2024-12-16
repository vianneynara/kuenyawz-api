package dev.kons.kuenyawz.services.entity;

import dev.kons.kuenyawz.dtos.product.ProductDto;
import dev.kons.kuenyawz.dtos.purchase.PurchaseItemDto;
import dev.kons.kuenyawz.entities.PurchaseItem;
import dev.kons.kuenyawz.mapper.VariantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseItemServiceImpl implements PurchaseItemService {

	private final ProductService productService;
	private final VariantMapper variantMapper;

	@Override
	public PurchaseItemDto convertToDto(PurchaseItem entity) {
		ProductDto productDto = productService.convertToDtoNoVariant(entity.getVariant().getProduct());
		return PurchaseItemDto.builder()
				.purchaseItemId(entity.getPurchaseItemId())
				.note(entity.getNote())
				.quantity(entity.getQuantity())
				.boughtPrice(entity.getBoughtPrice())
				.variant(variantMapper.fromEntity(entity.getVariant()))
				.product(productDto)
				.build();
	}
}
