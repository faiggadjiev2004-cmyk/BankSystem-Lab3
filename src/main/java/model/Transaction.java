package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int id;
    private BigDecimal amount;
    private LocalDateTime time;

    public Transaction(int id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    public Transaction(int id, BigDecimal amount, LocalDateTime time) {
    this.id = id;
    this.amount = amount;
    this.time = time;
}

    public int getId() {
    return id;
}

public BigDecimal getAmount() {
    return amount;
}

public LocalDateTime getTime() {
    return time;
}
}