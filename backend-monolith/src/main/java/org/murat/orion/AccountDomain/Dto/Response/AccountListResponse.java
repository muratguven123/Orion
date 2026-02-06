package org.murat.orion.AccountDomain.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountListResponse {

    private List<AccountResponse> accounts;
    private int totalCount;
}
