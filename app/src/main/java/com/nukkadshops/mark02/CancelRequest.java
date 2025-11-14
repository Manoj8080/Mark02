package com.nukkadshops.mark02;

public class CancelRequest {
    public int MerchantID;
    public String SecurityToken;
    public String StoreID;
    public int Clientid;
    public long PlutusTransactionReferenceID;
    public String Amount;
    public boolean TaketToHomeScreen;

    public CancelRequest(int merchantID, String token, String storeID, int clientID, long ptrid, String amount, boolean takeToHomeScreen) {
        this.MerchantID = merchantID;
        this.SecurityToken = token;
        this.StoreID = storeID;
        this.Clientid = clientID;
        this.PlutusTransactionReferenceID = ptrid;
        this.Amount = amount;
        this.TaketToHomeScreen = takeToHomeScreen;
    }
}