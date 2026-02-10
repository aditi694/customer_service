//package com.bank.customer_service.dto.response;
//
//import com.bank.customer_service.entity.Customer;
//import com.bank.customer_service.enums.CustomerStatus;
//import com.bank.customer_service.enums.KycStatus;
//import java.util.UUID;
//public class CustomerResponse {
//
//    private UUID id;
//    private String fullName;
//    private String email;
//    private String phone;
//    private CustomerStatus status;
//    private KycStatus kycStatus;
//
//    public CustomerResponse(
//            UUID id,
//            String fullName,
//            String email,
//            String phone,
//            CustomerStatus status,
//            KycStatus kycStatus
//    ) {
//        this.id = id;
//        this.fullName = fullName;
//        this.email = email;
//        this.phone = phone;
//        this.status = status;
//        this.kycStatus = kycStatus;
//    }
//
//    public static CustomerResponse from(Customer c) {
//        return new CustomerResponse(
//                c.getId(),
//                c.getFullName(),
//                c.getEmail(),
//                c.getPhone(),
//                c.getStatus(),
//                c.getKycStatus()
//        );
//    }
//}
