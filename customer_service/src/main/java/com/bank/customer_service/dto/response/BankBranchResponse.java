package com.bank.customer_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankBranchResponse {

    private String ifscCode;
    private String bankName;
    private String branchName;
    private String city;
    private String address;
}
