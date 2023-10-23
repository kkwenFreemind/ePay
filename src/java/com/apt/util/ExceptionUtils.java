package com.apt.util;

import com.google.gson.JsonObject;

public class ExceptionUtils {

    public JsonObject getResponse(Exception e) {
        JsonObject jsonResp = new JsonObject();
        String code = "999"; //API未知的錯誤
        String errMsg = "API未知的錯誤";
        String exMsg = null;

        if (e instanceof java.net.SocketTimeoutException
                || e instanceof java.net.ConnectException
                || e instanceof java.net.UnknownHostException) {
            code = "201"; //網路連線失敗
            errMsg = "網路連線失敗";
            exMsg = e.getMessage();
        } else if (e instanceof com.apt.util.HttpException) {
            if ("HTTP -1".equals(errMsg)) {
                code = "201"; //網路連線失敗
            } else if ("HTTP 404".equals(errMsg)) {
                code = "201"; //網路連線失敗
            }
            errMsg = "網路連線失敗";
            exMsg = e.getMessage();
        } else if (e instanceof com.apt.util.DataException) {
            code = "203";
            errMsg = "接收回應資料(JSON)異常";
        } else if (e instanceof com.apt.util.MacException) {
            code = "04"; //金鑰安全強度不足
            errMsg = "金鑰安全強度不足";
        }

        jsonResp.addProperty("ResponseCode", code);		//回應碼代碼
        jsonResp.addProperty("ErrorMessage", errMsg);
        if (exMsg != null) {
            jsonResp.addProperty("ExceptionMessage", exMsg); //Exception
        }
        return jsonResp;
    }
}
