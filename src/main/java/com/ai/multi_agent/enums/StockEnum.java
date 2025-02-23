package com.ai.multi_agent.enums;


public enum StockEnum {
    APPLE("AAPL");

    private final String ticker;

    StockEnum(String ticker) {
        this.ticker = ticker;
    }

    public String getTicker() {
        return ticker;
    }

}
