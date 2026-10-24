package com.quoc.webspire.network;

public class CouponDto {
    private String code;
    private int discountPercent;
    private String description;
    private boolean redeemed;

    public CouponDto(String code, int discountPercent, String description) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.description = description;
        this.redeemed = false;
    }

    public String getCode() { return code; }
    public int getDiscountPercent() { return discountPercent; }
    public String getDescription() { return description; }
    public boolean isRedeemed() { return redeemed; }
    public void setRedeemed(boolean redeemed) { this.redeemed = redeemed; }
}
