package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private int id;
    private BigDecimal balance;
    private boolean canWithdraw;
    private List<Transaction> transactions = new ArrayList<>();
    private static int nextTransactionId = 1;

    private BigDecimal rate;
    private int termMonths;
    private LocalDate openDate;
    private LocalDate endDate;
    private LocalDate simulatedDate;
    private BigDecimal totalReward = BigDecimal.ZERO;
    private boolean closed = false;

    public Account(int id, BigDecimal balance, boolean canWithdraw,
               BigDecimal rate, int termMonths) {

    this.id = id;
    this.balance = balance;
    this.canWithdraw = canWithdraw;

    if (
    rate.compareTo(new BigDecimal("17")) != 0 &&
    rate.compareTo(new BigDecimal("17.5")) != 0 &&
    rate.compareTo(new BigDecimal("19")) != 0 &&
    rate.compareTo(new BigDecimal("21")) != 0 &&
    rate.compareTo(new BigDecimal("22.3")) != 0
) {
    throw new IllegalArgumentException(
            "Ставка может быть только 17, 17.5, 19, 21 или 22.3"
    );
}

    this.rate = rate;

    if (termMonths != 3 &&
    termMonths != 6 &&
    termMonths != 9 &&
    termMonths != 12) {

    throw new IllegalArgumentException(
            "Срок депозита может быть только 3, 6, 9 или 12 месяцев"
    );
}
    this.termMonths = termMonths;

    this.openDate = LocalDate.now();
    this.endDate = this.openDate.plusMonths(termMonths);
    this.simulatedDate = this.openDate;

    this.totalReward = BigDecimal.ZERO;
    this.closed = false;
}

    public Account(int id, BigDecimal balance, boolean canWithdraw) {
        this.id = id;
        this.balance = balance;
        this.canWithdraw = canWithdraw;
    }

    public Account(int id, boolean canWithdraw) {
        this.id = id;
        this.balance = BigDecimal.ZERO;
        this.canWithdraw = canWithdraw;
    }

    public BigDecimal getBalance() {
    return balance;
}

public int getId() {
    return id;
}

public BigDecimal getRate() {
    return rate;
}

public int getTermMonths() {
    return termMonths;
}

public LocalDate getOpenDate() {
    return openDate;
}

public LocalDate getEndDate() {
    return endDate;
}

public LocalDate getSimulatedDate() {
    return simulatedDate;
}

public BigDecimal getTotalReward() {
    return totalReward;
}

public boolean isClosed() {
    return closed;
}

public boolean isCanWithdraw() {
    return canWithdraw;
}

    public void deposit(BigDecimal amount) {

        if (closed) {
    System.out.println("Депозит закрыт. Пополнение невозможно.");
    return;
}
    balance = balance.add(amount);

    Transaction transaction = new Transaction(
        nextTransactionId++,
        amount
    );

    transactions.add(transaction);
}

    public void withdraw(BigDecimal amount) {

        if (closed) {
    System.out.println("Депозит закрыт. Снятие невозможно.");
    return;
}

    if (!canWithdraw) {
        System.out.println("С этого депозита снимать деньги нельзя");
        return;
    }

    if (amount.compareTo(balance) > 0) {
        System.out.println("Недостаточно денег на счете");
        return;
    }

    balance = balance.subtract(amount);

    Transaction transaction = new Transaction(
        nextTransactionId++,
        amount.negate()
    );

    transactions.add(transaction);
}

public void transferTo(Account otherAccount, BigDecimal amount) {

    if (!canWithdraw) {
        System.out.println("С этого депозита переводить деньги нельзя");
        return;
    }

    if (amount.compareTo(balance) > 0) {
        System.out.println("Недостаточно денег для перевода");
        return;
    }

    balance = balance.subtract(amount);

    Transaction transaction = new Transaction(
        nextTransactionId++,
        amount.negate()
    );

    transactions.add(transaction);

    otherAccount.deposit(amount);
}

public List<Transaction> getTransactions() {
    return transactions;
}

public BigDecimal simulateMonth() {

    if (closed) {
        System.out.println("Депозит уже закрыт.");
        return BigDecimal.ZERO;
    }

    if (!simulatedDate.isBefore(endDate)) {
        System.out.println("Срок депозита уже закончился.");
        return BigDecimal.ZERO;
    }

    BigDecimal monthlyReward = balance
            .multiply(rate)
            .divide(new BigDecimal("100"))
            .divide(new BigDecimal("12"), 2, java.math.RoundingMode.HALF_UP);

    simulatedDate = simulatedDate.plusMonths(1);

    totalReward = totalReward.add(monthlyReward);

    transactions.add(
        new Transaction(
                nextTransactionId++,
                monthlyReward,
                simulatedDate.atStartOfDay()
        )
);

    return monthlyReward;
}

public void closeDepositEarly() {

    if (closed) {
        System.out.println("Депозит уже закрыт.");
        return;
    }

    if (!simulatedDate.isBefore(endDate)) {
        System.out.println("Срок депозита уже закончился. Это не досрочное закрытие.");
        return;
    }

    if (totalReward.compareTo(BigDecimal.ZERO) > 0) {

       transactions.add(
        new Transaction(
                nextTransactionId++,
                totalReward.negate(),
                simulatedDate.atStartOfDay()
        )
);

        System.out.println(
                "Сгоревшее вознаграждение: " + totalReward + " тг"
        );
    }

    totalReward = BigDecimal.ZERO;
    closed = true;

    System.out.println("Депозит досрочно закрыт.");
}

public void closeDeposit() {

    if (closed) {
        System.out.println("Депозит уже закрыт.");
        return;
    }

    if (simulatedDate.isBefore(endDate)) {
        System.out.println("Срок депозита еще не закончился.");
        System.out.println("Для досрочного закрытия используйте closeDepositEarly().");
        return;
    }

    balance = balance.add(totalReward);

    System.out.println("Депозит успешно завершен.");
    System.out.println("Начисленное вознаграждение: " + totalReward + " тг");
    System.out.println("Итоговый баланс: " + balance + " тг");

    closed = true;
}

}