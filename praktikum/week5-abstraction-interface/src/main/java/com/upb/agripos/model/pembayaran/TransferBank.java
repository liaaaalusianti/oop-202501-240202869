package com.upb.agripos.model.pembayaran;

import com.upb.agripos.model.kontrak.Validatable;
import com.upb.agripos.model.kontrak.Receiptable;

public class TransferBank extends Pembayaran implements Validatable, Receiptable {

    private static final double BIAYA_ADMIN = 3500;

    public TransferBank(String invoiceNo, double total) {
        super(invoiceNo, total);
    }

    @Override
    public double biaya() {
        return BIAYA_ADMIN;
    }

    @Override
    public boolean validasi() {
        // simulasi validasi transfer (OTP / rekening)
        return true;
    }

    @Override
    public boolean prosesPembayaran() {
        return validasi();
    }

    @Override
    public String cetakStruk() {
        return "INVOICE " + invoiceNo
                + " | TOTAL+ADMIN: " + String.format("%,.2f", totalBayar())
                + " | TRANSFER BANK"
                + " | STATUS: " + (prosesPembayaran() ? "BERHASIL" : "GAGAL");
    }
}
