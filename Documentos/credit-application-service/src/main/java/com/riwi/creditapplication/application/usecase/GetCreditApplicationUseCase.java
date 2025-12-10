package com.riwi.creditapplication.application.usecase;

import com.riwi.creditapplication.domain.model.CreditApplication;

public interface GetCreditApplicationUseCase {
    CreditApplication execute(Long id);
}
