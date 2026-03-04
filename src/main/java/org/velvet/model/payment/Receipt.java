package org.velvet.model.payment;

public class Receipt {
    private final String receiptText;

    public Receipt(String receiptText) {
        this.receiptText = receiptText;
    }

    public String getReceiptText() {
        return receiptText;
    }
}
