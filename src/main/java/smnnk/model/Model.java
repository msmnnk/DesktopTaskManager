package smnnk.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Model {
    private final Connection connection;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static Model instance;
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_URL = "jdbc:mysql://localhost/java_odz";

    public static Model getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    private Model() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void insert(Task task) throws SQLException {
        connection.createStatement().execute("insert into tasks (name, time, status) values ('"
                + task.getTitle() + "', " + getTimeSQL(task.getTime()) + ", '" + task.getStatus() + "') ");
    }

    public void edit(Task edit, Task nonEdit) throws SQLException {
        connection.createStatement().execute("update tasks set name = '" + edit.getTitle()
                + "', time = " + getTimeSQL(edit.getTime()) + ", status = '" + edit.getStatus()
                + "' where name = '" + nonEdit.getTitle() + "' and time = " + getTimeSQL(nonEdit.getTime())
                + "and status = '" + nonEdit.getStatus() + "'");
    }

    public void remove(Task task) throws SQLException {
        connection.createStatement().execute("delete from tasks where name = '" + task.getTitle()
                + "' and time = " + getTimeSQL(task.getTime()) + " and status = '" + task.getStatus() + "'");
    }

    public ResultSet filter(LocalDateTime to, LocalDateTime from) throws SQLException {
        return connection.createStatement().executeQuery("select name, time, status from tasks "
                + "where (time >= " + getTimeSQL(from) + " and time < " + getTimeSQL(to) + ")");
    }

    public ResultSet fullTable() throws SQLException {
        return connection.createStatement().executeQuery("select name, time, status from tasks");
    }

    private String getTimeSQL(LocalDateTime time) {
        return "str_to_date('" + time.format(dateTimeFormatter) + "', '%d.%m.%Y %H:%i')";
    }
}
