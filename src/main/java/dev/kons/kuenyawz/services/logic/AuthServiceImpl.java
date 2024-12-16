package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.dtos.account.AccountRegistrationDto;
import dev.kons.kuenyawz.dtos.account.AccountSecureDto;
import dev.kons.kuenyawz.dtos.auth.AuthRequestDto;
import dev.kons.kuenyawz.dtos.auth.AuthResponseDto;
import dev.kons.kuenyawz.entities.Account;
import dev.kons.kuenyawz.entities.RefreshToken;
import dev.kons.kuenyawz.exceptions.InvalidPasswordException;
import dev.kons.kuenyawz.exceptions.UnauthorizedException;
import dev.kons.kuenyawz.mapper.AccountMapper;
import dev.kons.kuenyawz.services.entity.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AccountService accountService;
	private final AccountMapper accountMapper;
	private final JWTService jwtService;
	private final RefreshTokenService refreshTokenService;

	@Override
	public AuthResponseDto register(AccountRegistrationDto accountRegistrationDto) {
		Account account = accountService.createAccount(accountRegistrationDto);

		AuthResponseDto authResponseDto = generateTokensThenResponse(account);
		return authResponseDto;
	}

	@Override
	public AuthResponseDto login(AuthRequestDto accountLoginDto) {
		Account account = accountService.getAccount(accountLoginDto.getPhone());

		if (!accountService.passwordMatches(accountLoginDto.getPassword(), account)) {
			throw new InvalidPasswordException();
		}

		AuthResponseDto authResponseDto = generateTokensThenResponse(account);
		return authResponseDto;
	}

	@Override
	public AuthResponseDto refresh(String token) {
		RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(token);
		Account account = accountService.getAccount(refreshToken.getAccountId());

		if (!refreshToken.getAccountId().equals(account.getAccountId())) {
			throw new UnauthorizedException();
		}

		refreshTokenService.revokeRefreshToken(token);

		AuthResponseDto authResponseDto = generateTokensThenResponse(account);
		return authResponseDto;
	}

	@Override
	public void revokeRefreshToken(String token) {
		refreshTokenService.revokeRefreshToken(token);
	}

	@Override
	public AccountSecureDto getUserInfo(String token) {
		String username = jwtService.extractUsername(token);
		Account account = accountService.getAccount(username);

		AccountSecureDto accountSecureDto = accountMapper.fromEntity(account);
		return accountSecureDto;
	}

	@Override
	public AccountSecureDto getUserInfo(Long accountId) {
		Account account = accountService.getAccount(accountId);

		AccountSecureDto accountSecureDto = accountMapper.fromEntity(account);
		return accountSecureDto;
	}

	@Override
	public AccountSecureDto getUserInfo(Authentication auth) {
		try {
			Account account = (Account) auth.getPrincipal();
			AccountSecureDto accountSecureDto = accountMapper.fromEntity(account);
			return accountSecureDto;
		} catch (ClassCastException e) {
			throw new UnauthorizedException();
		}
	}

	@Override
	public AccountSecureDto getCurrentUserInfo() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return getUserInfo(auth);
	}

	@Override
	public boolean validateToken(String token) {
		return jwtService.isAccessToken(token) && !jwtService.isTokenExpired(token);
	}

	private AuthResponseDto generateTokensThenResponse(Account account) {
		String accessToken = jwtService.generateAccessToken(account);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(account);

		AuthResponseDto authResponseDto = AuthResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken.getToken())
			.iat(jwtService.getIssuedAt(accessToken))
			.exp(jwtService.getExpiration(accessToken))
			.build();

		return authResponseDto;
	}
}
