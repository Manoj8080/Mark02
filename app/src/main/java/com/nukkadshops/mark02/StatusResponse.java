package com.nukkadshops.mark02;

import java.util.List;

public class StatusResponse {

    public int ResponseCode;
    public String ResponseMessage;
    public long PlutusTransactionReferenceID;
    public List<TransactionData> TransactionData;

    // Optional: add a toString() for easy logging
    @Override
    public String toString() {
        return "TxnResponse{" +
                "ResponseCode=" + ResponseCode +
                ", ResponseMessage='" + ResponseMessage + '\'' +
                ", PlutusTransactionReferenceID=" + PlutusTransactionReferenceID +
                ", TransactionData=" + TransactionData +
                '}';
    }

    // Nested class for TransactionData
    public static class TransactionData {
        public String Tag;
        public String Value;

        @Override
        public String toString() {
            return "{" + Tag + "='" + Value + '\'' + '}';
        }
    }
}