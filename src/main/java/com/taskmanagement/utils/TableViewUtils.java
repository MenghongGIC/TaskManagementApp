package com.taskmanagement.utils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

public class TableViewUtils {

    private TableViewUtils(){}

    public static <T> void setupColumn(TableColumn<T, String> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    }
    public static <T> void setupColumn(TableColumn<T, String> column,
                                       java.util.function.Function<T, String> valueExtractor) {
        column.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueExtractor.apply(cellData.getValue())));
    }
    public static <T> void refreshTable(TableView<T> tableView, List<T> data) {
        tableView.getItems().clear();
        if (data != null && !data.isEmpty()) {
            tableView.getItems().addAll(data);
        }
    }

    public static <T> void setColumnWidths(List<TableColumn<T, ?>> columns, double... widths) {
        for (int i = 0; i < columns.size() && i < widths.length; i++) {
            columns.get(i).setPrefWidth(widths[i]);
        }
    }

    public static <T> void setEqualColumnWidths(TableView<T> tableView) {
        int columnCount = tableView.getColumns().size();
        if (columnCount > 0) {
            double width = tableView.getPrefWidth() / columnCount;
            tableView.getColumns().forEach(col -> col.setPrefWidth(width));
        }
    }

    public static <T> void disableColumnReordering(TableView<T> tableView) {
        tableView.getColumns().forEach(col -> col.setReorderable(false));
    }

    public static <T> void makeResizable(List<TableColumn<T, ?>> columns) {
        columns.forEach(col -> col.setResizable(true));
    }

    public static <T> T getSelected(TableView<T> tableView) {
        return tableView.getSelectionModel().getSelectedItem();
    }

    public static <T> void selectRow(TableView<T> tableView, int index) {
        if (index >= 0 && index < tableView.getItems().size()) {
            tableView.getSelectionModel().select(index);
        }
    }
    public static <T> void clearSelection(TableView<T> tableView) {
        tableView.getSelectionModel().clearSelection();
    }
    public static <T> boolean isEmpty(TableView<T> tableView) {
        return tableView.getItems().isEmpty();
    }

    public static <T> int getItemCount(TableView<T> tableView) {
        return tableView.getItems().size();
    }
}
