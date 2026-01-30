package com.upb.agripos.model;

/**
 * Ringkasan checkout sederhana
 */
public class CheckoutSummary {
    private final double subtotal;
    private final double tax;
    private final double total;
    private final int distinctItemCount;
    private final int totalQuantity;

    public CheckoutSummary(double subtotal, double tax, double total, int distinctItemCount, int totalQuantity) {
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.distinctItemCount = distinctItemCount;
        this.totalQuantity = totalQuantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTax() {
        return tax;
    }

    public double getTotal() {
        return total;
    }

    public int getDistinctItemCount() {
        return distinctItemCount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}