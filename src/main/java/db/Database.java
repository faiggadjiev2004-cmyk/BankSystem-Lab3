package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import model.Account;
import model.Customer;
import java.sql.Timestamp;
import model.Transaction;
import java.sql.ResultSet;

public class Database {

    private static final String URL = "jdbc:h2:./data/bankdb";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    public static void createTables() {

        String customersTable = """
                CREATE TABLE IF NOT EXISTS customers (
                    id INT PRIMARY KEY,
                    first_name VARCHAR(100),
                    last_name VARCHAR(100),
                    email VARCHAR(150) UNIQUE
                )
                """;

        String accountsTable = """
                CREATE TABLE IF NOT EXISTS accounts (
                    id INT PRIMARY KEY,
                    customer_id INT,
                    balance DECIMAL(19,2),
                    can_withdraw BOOLEAN
                )
                """;

        String transactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INT PRIMARY KEY,
                    account_id INT,
                    amount DECIMAL(19,2),
                    time TIMESTAMP
                )
                """;

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {

            statement.execute(customersTable);
            statement.execute(accountsTable);
            statement.execute(transactionsTable);

            System.out.println("Таблицы базы данных созданы");

        } catch (SQLException e) {
            System.out.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    public static void saveCustomer(Customer customer) {

        String sql = """
                MERGE INTO customers
                (id, first_name, last_name, email)
                KEY(id)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, customer.getId());
            statement.setString(2, customer.getFirstName());
            statement.setString(3, customer.getLastName());
            statement.setString(4, customer.getEmail());

            statement.executeUpdate();

            System.out.println("Клиент сохранен в базу данных");

        } catch (SQLException e) {
            System.out.println(
                    "Ошибка сохранения клиента: " + e.getMessage()
            );
        }
    }

    public static void saveAccount(Account account, int customerId) {

    String sql = """
            MERGE INTO accounts
            (id, customer_id, balance, can_withdraw)
            KEY(id)
            VALUES (?, ?, ?, ?)
            """;

    try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {

        statement.setInt(1, account.getId());
        statement.setInt(2, customerId);
        statement.setBigDecimal(3, account.getBalance());
        statement.setBoolean(4, account.isCanWithdraw());

        statement.executeUpdate();

        System.out.println(
                "Счет " + account.getId() + " сохранен в базу данных"
        );

    } catch (SQLException e) {
        System.out.println(
                "Ошибка сохранения счета: " + e.getMessage()
        );
    }
}

public static void saveTransactions(Account account) {

    String sql = """
            MERGE INTO transactions
            (id, account_id, amount, time)
            KEY(id)
            VALUES (?, ?, ?, ?)
            """;

    try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {

        for (Transaction transaction : account.getTransactions()) {

            statement.setInt(1, transaction.getId());
            statement.setInt(2, account.getId());
            statement.setBigDecimal(3, transaction.getAmount());
            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(transaction.getTime())
            );

            statement.addBatch();
        }

        statement.executeBatch();

        System.out.println(
                "Транзакции счета " + account.getId() + " сохранены"
        );

    } catch (SQLException e) {
        System.out.println(
                "Ошибка сохранения транзакций: " + e.getMessage()
        );
    }
}
public static void showAccounts() {

    String sql = "SELECT * FROM accounts";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        System.out.println("=== СЧЕТА В БАЗЕ ДАННЫХ ===");

        while (resultSet.next()) {

            System.out.println(
                    "ID: " + resultSet.getInt("id")
                    + " | Клиент: " + resultSet.getInt("customer_id")
                    + " | Баланс: " + resultSet.getBigDecimal("balance")
                    + " | Снятие: " + resultSet.getBoolean("can_withdraw")
            );
        }

    } catch (SQLException e) {
        System.out.println(
                "Ошибка чтения счетов: " + e.getMessage()
        );
    }
}
public static void showCustomers() {

    String sql = "SELECT * FROM customers";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        System.out.println("=== КЛИЕНТЫ В БАЗЕ ДАННЫХ ===");

        while (resultSet.next()) {
            System.out.println(
                    "ID: " + resultSet.getInt("id")
                    + " | Имя: " + resultSet.getString("first_name")
                    + " | Фамилия: " + resultSet.getString("last_name")
                    + " | Email: " + resultSet.getString("email")
            );
        }

    } catch (SQLException e) {
        System.out.println("Ошибка чтения клиентов: " + e.getMessage());
    }
}
public static void showTransactions() {

    String sql = "SELECT * FROM transactions ORDER BY id";

    try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
    ) {

        System.out.println("=== ТРАНЗАКЦИИ В БАЗЕ ДАННЫХ ===");

        while (resultSet.next()) {
            System.out.println(
                    "ID: " + resultSet.getInt("id")
                    + " | Счет: " + resultSet.getInt("account_id")
                    + " | Сумма: " + resultSet.getBigDecimal("amount")
                    + " | Время: " + resultSet.getTimestamp("time")
            );
        }

    } catch (SQLException e) {
        System.out.println("Ошибка чтения транзакций: " + e.getMessage());
    }
}
}