package com.nukkadshops.mark02;

public class VoidRequest {
    public String TransactionNumber;
    public int SequenceNumber;
    public String AllowedPaymentMode;
    public String StoreID;
    public String Amount;
    public int MerchantID;
    public String SecurityToken;
    public int Clientid;
    public int TxnType;
    public long OriginalPlutusTransactionReferenceID;
    public int AutoCancelDurationInMinutes;

    public VoidRequest(String transactionNumber, int sequenceNumber, String allowedPaymentMode,
                       String storeID, String amount, int merchantID, String securityToken,
                       int clientid, int txnType, long originalPTRID, int autoCancelDuration) {
        this.TransactionNumber = transactionNumber;
        this.SequenceNumber = sequenceNumber;
        this.AllowedPaymentMode = allowedPaymentMode;
        this.StoreID = storeID;
        this.Amount = amount;
        this.MerchantID = merchantID;
        this.SecurityToken = securityToken;
        this.Clientid = clientid;
        this.TxnType = txnType;
        this.OriginalPlutusTransactionReferenceID = originalPTRID;
        this.AutoCancelDurationInMinutes = autoCancelDuration;
    }
}