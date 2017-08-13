package com.github.mdjc.security;

import java.util.NoSuchElementException;
import java.util.function.BooleanSupplier;

import org.springframework.security.core.Authentication;

import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.User;

public class WebSecurity {
	private final CondoRepository condoRepo;
	private final OutlayRepository outlayRepo;
	private final BillRepository billRepo;

	public WebSecurity(CondoRepository condoRepo, OutlayRepository outlayRepo, BillRepository billRepo) {
		this.condoRepo = condoRepo;
		this.outlayRepo = outlayRepo;
		this.billRepo = billRepo;
	}

	public boolean checkHasAccessToCondo(Authentication authentication, int condoId) {
		return isAuthenticated(authentication) && check(() -> condoRepo.getBy(condoId, user(authentication)) != null);
	}

	public boolean checkHasAccessToCondoAsManager(Authentication authentication, int condoId) {
		return isAuthenticated(authentication) && isManager(authentication)
				&& checkHasAccessToCondo(authentication, condoId);
	}

	public boolean checkHasAccessToOutlay(Authentication authentication, int outlayId) {
		return isAuthenticated(authentication) && check(() -> outlayRepo.getBy(outlayId, user(authentication)) != null);
	}

	public boolean checkHasAccessToOutlayAsManager(Authentication authentication, int outlayId) {
		return isAuthenticated(authentication) && isManager(authentication)
				&& checkHasAccessToOutlay(authentication, outlayId);
	}

	public boolean checkHasAccessToBill(Authentication authentication, int billId) {
		return isAuthenticated(authentication) && check(() -> billRepo.getBy(billId, user(authentication)) != null);
	}

	public boolean checkHasAccessToBillAsManager(Authentication authentication, int billId) {
		return isAuthenticated(authentication) && isManager(authentication)
				&& checkHasAccessToBill(authentication, billId);
	}

	private boolean isAuthenticated(Authentication authentication) {
		return authentication != null;
	}

	private boolean check(BooleanSupplier supplier) {
		try {
			return supplier.getAsBoolean();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private User user(Authentication authentication) {
		return (User) authentication.getPrincipal();
	}

	private boolean isManager(Authentication authentication) {
		return user(authentication).isManager();
	}
}
