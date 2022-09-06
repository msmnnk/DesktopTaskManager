package smnnk.controller;

import smnnk.model.Model;
import smnnk.model.Task;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tornadofx.control.DateTimePicker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Controller {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private Model model;

    @FXML
    private TableView<Task> mainTable = new TableView<>();

    @FXML
    private TableColumn<Task, String> titleColumn = new TableColumn<>();

    @FXML
    private TableColumn<Task, String> timeColumn = new TableColumn<>();

    @FXML
    private TableColumn<Task, String> statusColumn = new TableColumn<>();

    @FXML
    private Label tableExc;

    @FXML
    private DateTimePicker fromField = new DateTimePicker();

    @FXML
    private DateTimePicker toField = new DateTimePicker();

    @FXML
    private TextField titleField;

    @FXML
    private DateTimePicker timeField = new DateTimePicker();

    @FXML
    private Label filterExc;

    @FXML
    private ComboBox<String> statusList;

    @FXML
    private Label editExc;

    @FXML
    void initialize() {
        offErrors();
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        timeColumn.setCellValueFactory(param -> {
            Task task = param.getValue();
            LocalDateTime time = task.getTime();
            String str = time.format(dateTimeFormatter);
            return new SimpleObjectProperty<>(str);
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        ObservableList<String> statuses = FXCollections.observableList(Task.getStatuses());
        statusList.setItems(statuses);
        dateStyle();
        try {
            model = Model.getInstance();
        } catch (SQLException | ClassNotFoundException e) {
            onError(tableExc, "Database connection error.");
            e.printStackTrace();
        }
        refreshTable();
    }

    @FXML
    void selectColumn() {
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task temp = mainTable.getSelectionModel().getSelectedItem();
            titleField.setText(temp.getTitle());
            timeField.setDateTimeValue(temp.getTime());
            statusList.setValue(temp.getStatus());
        }
    }

    @FXML
    void addButtonAction() {
        offErrors();
        Task temp;
        if (!textFieldIsEmpty(titleField) && timeField.getDateTimeValue() != null) {
           if (statusList.getValue() == null || statusIsEqual(statusList)) {
               if (statusList.getValue() != null) {
                   temp = new Task(titleField.getText(), timeField.dateTimeValueProperty().getValue(), statusList.getValue());
               } else {
                   temp = new Task(titleField.getText(), timeField.dateTimeValueProperty().getValue());
               }
               try {
                   model.insert(temp);
                   unselectColumn();
                   refreshTable();
               } catch (SQLException e) {
                   onError(editExc, "SQl error.");
                   e.printStackTrace();
               }
           } else {
               onError(editExc, "Wrong status.");
           }
        } else {
            onError(editExc, "Fields are empty.");
        }
    }

    @FXML
    void editButtonAction() {
        offErrors();
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            Task editTask = new Task(titleField.getText(), timeField.dateTimeValueProperty().getValue(), statusList.getValue());
            Task notEditTask = mainTable.getSelectionModel().getSelectedItem();
            try {
                model.edit(editTask, notEditTask);
                unselectColumn();
                refreshTable();
            } catch (SQLException e) {
                onError(editExc, "SQl error.");
                e.printStackTrace();
            }
        } else {
            onError(editExc, "Not selected row to edit.");
        }
    }

    @FXML
    void removeButtonAction() {
        offErrors();
        if (mainTable.getSelectionModel().getSelectedItem() != null) {
            try {
                model.remove(mainTable.getSelectionModel().getSelectedItem());
                unselectColumn();
                refreshTable();
            } catch (SQLException e) {
                onError(editExc, "SQL error.");
                e.printStackTrace();
            }
        } else {
            onError(editExc, "Not selected row to delete.");
        }
    }

    @FXML
    void filterButtonAction() {
        offErrors();
        if (toField.getDateTimeValue() != null && fromField.getDateTimeValue() != null) {
            if (!fromField.getValue().isAfter(toField.getValue())) {
                try {
                    loadTable(model.filter(toField.dateTimeValueProperty().getValue(),
                            fromField.dateTimeValueProperty().getValue()));
                } catch (SQLException e) {
                    onError(filterExc, "SQL error.");
                    e.printStackTrace();
                }
            } else {
                onError(filterExc, "\"From\" is greater then \"To\".");
            }
        } else {
            onError(filterExc, "Fields are empty.");
        }
    }

    @FXML
    void resetButtonAction() {
        refreshTable();
        unselectColumn();
        toField.setDateTimeValue(null);
        fromField.setDateTimeValue(null);
    }

    void loadTable(ResultSet set) throws SQLException {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        while (set.next()) {
            Task temp = new Task(set.getString(1), set.getTimestamp(2).toLocalDateTime(),  set.getString(3).toUpperCase());
            taskList.add(temp);
        }
        mainTable.setItems(taskList);
    }

    void refreshTable() {
        offErrors();
        try {
            loadTable(model.fullTable());
        } catch (SQLException e) {
            onError(tableExc, "Data load error.");
            e.printStackTrace();
        }
    }

    void unselectColumn() {
        mainTable.getSelectionModel().clearSelection();
        titleField.clear();
        timeField.setDateTimeValue(null);
        statusList.setValue(null);
        statusList.getSelectionModel().clearSelection();
        statusList.setPromptText("Choose status...");
    }

    void onError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    void offErrors() {
        tableExc.setVisible(false);
        editExc.setVisible(false);
        filterExc.setVisible(false);
    }

    boolean textFieldIsEmpty(TextField textField) {
        if (textField.getText() == null) {
            return true;
        }
        else {
            return textField.getText().isBlank();
        }
    }

    boolean statusIsEqual(ComboBox<String> statusList) {
        if (!statusList.getValue().isBlank()) {
            for (String temp : Task.getStatuses()) {
                if (temp.equals(statusList.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    void dateStyle() {
        timeField.setFormat("dd.MM.yyyy HH:mm");
        toField.setFormat("dd.MM.yyyy HH:mm");
        fromField.setFormat("dd.MM.yyyy HH:mm");
    }
}