package com.riwi.creditapplication.application.usecase;

import com.riwi.creditapplication.domain.model.Affiliate;

public interface GetAffiliateByEmailUseCase {
    Affiliate execute(String email);
}
