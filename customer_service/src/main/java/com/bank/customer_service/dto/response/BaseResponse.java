package com.bank.customer_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private T data;
    private ResultInfo resultInfo;

    public BaseResponse(T data, String msg, String code) {
        this.data = data;
        this.resultInfo = new ResultInfo(msg);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultInfo {
        private String resultMsg;
    }
}