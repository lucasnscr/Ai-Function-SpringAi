package com.ai.multi_agent.enums;


public enum CryptoEnum {
    BITCOIN("BTC");

    private final String ticker;

    CryptoEnum(String ticker) {
        this.ticker = ticker;
    }

    public String getTicker() {
        return ticker;
    }


}
