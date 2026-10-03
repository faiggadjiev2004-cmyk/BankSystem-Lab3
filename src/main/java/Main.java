import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    // Деньги на счетах
    static BigDecimal mainBalance = new BigDecimal("250000");
    static BigDecimal deposit1 = new BigDecimal("200000");
    static BigDecimal deposit2 = new BigDecimal("150000");

    // Кредит
    static BigDecimal creditAmount = BigDecimal.ZERO;
    static BigDecimal remainingDebt = BigDecimal.ZERO;
    static BigDecimal monthlyPayment = BigDecimal.ZERO;

    static final BigDecimal MAX_CREDIT = new BigDecimal("1000000");
    static final BigDecimal CREDIT_RATE = new BigDecimal("0.30");

    static int creditMonths = 0;
    static boolean creditActive = false;
    static boolean accountBlocked = false;

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n==============================");
            System.out.println("          BANK SYSTEM");
            System.out.println("==============================");

            System.out.println("1. Показать счета");
            System.out.println("2. Изменить баланс для проверки");
            System.out.println("3. Оформить кредит");
            System.out.println("4. Запустить симуляцию кредита");
            System.out.println("0. Выход");

            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    showAccounts();
                    break;

                case 2:
                    changeBalances();
                    break;

                case 3:
                    createCredit();
                    break;

                case 4:
                    simulateCredit();
                    break;

                case 0:
                    System.out.println("Программа завершена.");
                    return;

                default:
                    System.out.println("Неверная команда.");
            }
        }
    }

    // Показать все счета
    static void showAccounts() {

        System.out.println("\n----- СЧЕТА -----");

        System.out.println("Основной счет: " + mainBalance + " тг");
        System.out.println("Депозит №1: " + deposit1 + " тг");
        System.out.println("Депозит №2: " + deposit2 + " тг");

        System.out.println("Статус счета: "
                + (accountBlocked ? "ЗАБЛОКИРОВАН" : "АКТИВЕН"));

        if (creditActive) {

            System.out.println("\n----- КРЕДИТ -----");

            System.out.println("Сумма кредита: "
                    + creditAmount + " тг");

            System.out.println("Остаток долга: "
                    + remainingDebt + " тг");

            System.out.println("Ежемесячный платеж: "
                    + monthlyPayment + " тг");

            System.out.println("Срок: "
                    + creditMonths + " месяцев");

            System.out.println("Процент: 30%");
        } else {
            System.out.println("Активного кредита нет.");
        }
    }

    // Меняем баланс для тестирования
    static void changeBalances() {

        if (accountBlocked) {
            System.out.println("Счет заблокирован.");
            return;
        }

        System.out.print("Введите сумму на основном счете: ");
        mainBalance =
                new BigDecimal(scanner.next());

        System.out.print("Введите сумму депозита №1: ");
        deposit1 =
                new BigDecimal(scanner.next());

        System.out.print("Введите сумму депозита №2: ");
        deposit2 =
                new BigDecimal(scanner.next());

        System.out.println("Баланс изменен.");
    }

    // Оформление кредита
    static void createCredit() {

        if (accountBlocked) {
            System.out.println(
                    "Нельзя оформить кредит. Счет заблокирован.");
            return;
        }

        if (creditActive) {
            System.out.println(
                    "У клиента уже есть активный кредит.");
            return;
        }

        System.out.print(
                "Введите сумму кредита (максимум 1 000 000): ");

        BigDecimal amount =
                new BigDecimal(scanner.next());

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            System.out.println(
                    "Сумма кредита должна быть больше 0.");

            return;
        }

        if (amount.compareTo(MAX_CREDIT) > 0) {

            System.out.println(
                    "ОТКАЗ!");

            System.out.println(
                    "Банк не может выдать больше 1 000 000 тг.");

            return;
        }

        System.out.print(
                "Введите срок кредита в месяцах (например 24): ");

        int months = scanner.nextInt();

        if (months <= 0) {

            System.out.println(
                    "Неверный срок кредита.");

            return;
        }

        creditAmount = amount;
        creditMonths = months;

        // Сумма кредита + 30%
        BigDecimal interest =
                creditAmount.multiply(CREDIT_RATE);

        remainingDebt =
                creditAmount.add(interest)
                        .setScale(2, RoundingMode.HALF_UP);

        monthlyPayment =
                remainingDebt.divide(
                        new BigDecimal(creditMonths),
                        2,
                        RoundingMode.HALF_UP);

        creditActive = true;

        System.out.println("\n==============================");
        System.out.println("       КРЕДИТ ОДОБРЕН");
        System.out.println("==============================");

        System.out.println(
                "Сумма кредита: "
                        + creditAmount + " тг");

        System.out.println(
                "Процентная ставка: 30%");

        System.out.println(
                "Общая сумма к погашению: "
                        + remainingDebt + " тг");

        System.out.println(
                "Срок: "
                        + creditMonths + " месяцев");

        System.out.println(
                "Ежемесячный платеж: "
                        + monthlyPayment + " тг");
    }

    // Симуляция погашения кредита
    static void simulateCredit() {

        if (!creditActive) {

            System.out.println(
                    "Сначала оформите кредит.");

            return;
        }

        System.out.println("\n==============================");
        System.out.println("     СИМУЛЯЦИЯ ПОГАШЕНИЯ");
        System.out.println("==============================");

        for (int month = 1;
             month <= creditMonths;
             month++) {

            System.out.println(
                    "\n---------- МЕСЯЦ "
                            + month
                            + " ----------");

            if (remainingDebt.compareTo(
                    BigDecimal.ZERO) <= 0) {

                System.out.println(
                        "Кредит уже полностью погашен.");

                continue;
            }

            if (accountBlocked) {

                System.out.println(
                        "Счет заблокирован.");

                System.out.println(
                        "Платеж выполнить невозможно.");

                System.out.println(
                        "Остаток долга: "
                                + remainingDebt
                                + " тг");

                continue;
            }

            BigDecimal payment = monthlyPayment;

            // Последний платеж
            if (remainingDebt.compareTo(payment) < 0) {
                payment = remainingDebt;
            }

            // Если денег хватает
            if (mainBalance.compareTo(payment) >= 0) {

                mainBalance =
                        mainBalance.subtract(payment);

                remainingDebt =
                        remainingDebt.subtract(payment);

                String transactionId =
                        UUID.randomUUID().toString();

                System.out.println(
                        "Платеж успешно выполнен.");

                System.out.println(
                        "Transaction ID: "
                                + transactionId);

                System.out.println(
                        "Списано: "
                                + payment
                                + " тг");

                System.out.println(
                        "Баланс основного счета: "
                                + mainBalance
                                + " тг");

                System.out.println(
                        "Остаток кредита: "
                                + remainingDebt
                                + " тг");
            }

            // Если денег НЕ хватает
            else {

                System.out.println(
                        "Недостаточно денег для платежа!");

                System.out.println(
                        "Счет будет заблокирован.");

                blockAccountAndTakeMoney();
            }
        }

        if (remainingDebt.compareTo(
                BigDecimal.ZERO) <= 0) {

            remainingDebt = BigDecimal.ZERO;
            creditActive = false;

            System.out.println("\n==============================");
            System.out.println(
                    "КРЕДИТ ПОЛНОСТЬЮ ПОГАШЕН");
            System.out.println("==============================");
        }

        else {

            System.out.println("\n==============================");
            System.out.println(
                    "СИМУЛЯЦИЯ ЗАВЕРШЕНА");
            System.out.println("==============================");

            System.out.println(
                    "Остаток долга: "
                            + remainingDebt
                            + " тг");

            System.out.println(
                    "Статус счета: "
                            + (accountBlocked
                            ? "ЗАБЛОКИРОВАН"
                            : "АКТИВЕН"));
        }
    }

    // Блокировка счета и списание всех денег
    static void blockAccountAndTakeMoney() {

        accountBlocked = true;

        BigDecimal allMoney =
                mainBalance
                        .add(deposit1)
                        .add(deposit2);

        System.out.println(
                "\nДеньги на основном счете: "
                        + mainBalance
                        + " тг");

        System.out.println(
                "Депозит №1: "
                        + deposit1
                        + " тг");

        System.out.println(
                "Депозит №2: "
                        + deposit2
                        + " тг");

        System.out.println(
                "Всего будет списано: "
                        + allMoney
                        + " тг");

        // Обнуляем все счета
        mainBalance = BigDecimal.ZERO;
        deposit1 = BigDecimal.ZERO;
        deposit2 = BigDecimal.ZERO;

        // Деньги идут в счет кредита
        remainingDebt =
                remainingDebt.subtract(allMoney);

        if (remainingDebt.compareTo(
                BigDecimal.ZERO) < 0) {

            remainingDebt = BigDecimal.ZERO;
        }

        String transactionId =
                UUID.randomUUID().toString();

        System.out.println(
                "Transaction ID блокировки: "
                        + transactionId);

        System.out.println(
                "Все счета обнулены.");

        System.out.println(
                "Счет ЗАБЛОКИРОВАН.");

        System.out.println(
                "Остаток долга: "
                        + remainingDebt
                        + " тг");
    }
}