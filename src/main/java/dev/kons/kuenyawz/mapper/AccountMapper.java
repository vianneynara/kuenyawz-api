package dev.kons.kuenyawz.mapper;

import dev.kons.kuenyawz.dtos.account.AccountPatchDto;
import dev.kons.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.kons.kuenyawz.dtos.account.AccountSecureDto;
import dev.kons.kuenyawz.entities.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {

	@Mapping(target = "password", ignore = true)
	Account toEntity(AccountRegistrationDto accountRegistrationDto);

	AccountSecureDto fromEntity(Account account);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Account updateAccountFromPatch(AccountPatchDto dto, @MappingTarget Account account);
}
