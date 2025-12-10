package com.riwi.creditapplication.application.usecase;

import com.riwi.creditapplication.domain.model.CreditApplication;
import java.util.List;

public interface GetAffiliateCreditApplicationsUseCase {
    List<CreditApplication> execute(String email);
}
