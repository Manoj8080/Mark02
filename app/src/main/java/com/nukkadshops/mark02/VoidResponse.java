package com.nukkadshops.mark02;

public class VoidResponse {
    private int responseCode;
    public String responseMessage;
    private long plutusTransactionReferenceId;
    private String additionInfo;

    public VoidResponse(int responseCode,
    String responseMessage,
    long plutusTransactionReferenceId,
    String additionInfo ){

        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.plutusTransactionReferenceId = plutusTransactionReferenceId;
        this.additionInfo = additionInfo;
    }
}
