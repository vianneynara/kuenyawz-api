package dev.realtards.kuenyawz.mapper;

import dev.realtards.kuenyawz.dtos.account.AccountPatchDto;
import dev.realtards.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.realtards.kuenyawz.dtos.account.AccountSecureDto;
import dev.realtards.kuenyawz.entities.Account;
import org.mapstruct.*;

@Mapper
public interface AccountMapper {

	@Mapping(target = "password", ignore = true)
	Account toEntity(AccountRegistrationDto accountRegistrationDto);

	AccountSecureDto toAccountSecureDto(Account account);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAccountFromPatch(AccountPatchDto dto, @MappingTarget Account account);
}
