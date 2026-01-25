package com.taskmanagement.utils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

/**
 * Utility class for common TableView operations.
 * Provides convenient methods for configuring columns, managing data, and handling selections.
 */
public class TableViewUtils {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private TableViewUtils() {
        // Utility class, no instantiation
    }

    // ============ Column Setup Methods ============

    /**
     * Setup a column with property value factory using property name.
     *
     * @param <T> the type of data in the table
     * @param column the column to configure
     * @param propertyName the JavaFX property name to bind to
     */
    public static <T> void setupColumn(TableColumn<T, String> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    }

    /**
     * Setup a column with custom value factory.
     *
     * @param <T> the type of data in the table
     * @param column the column to configure
     * @param valueExtractor function to extract value from row data
     */
    public static <T> void setupColumn(TableColumn<T, String> column,
                                       java.util.function.Function<T, String> valueExtractor) {
        column.setCellValueFactory(cellData ->
                new SimpleStringProperty(valueExtractor.apply(cellData.getValue())));
    }

    // ============ Data Management Methods ============

    /**
     * Refresh table view with new data.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to refresh
     * @param data the new data to display (null-safe)
     */
    public static <T> void refreshTable(TableView<T> tableView, List<T> data) {
        tableView.getItems().clear();
        if (data != null && !data.isEmpty()) {
            tableView.getItems().addAll(data);
        }
    }

    // ============ Column Width Methods ============

    /**
     * Set preferred column widths.
     *
     * @param <T> the type of data in the table
     * @param columns the columns to resize
     * @param widths the desired widths in pixels
     */
    public static <T> void setColumnWidths(List<TableColumn<T, ?>> columns, double... widths) {
        for (int i = 0; i < columns.size() && i < widths.length; i++) {
            columns.get(i).setPrefWidth(widths[i]);
        }
    }

    /**
     * Configure equal column widths based on table width.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to configure
     */
    public static <T> void setEqualColumnWidths(TableView<T> tableView) {
        int columnCount = tableView.getColumns().size();
        if (columnCount > 0) {
            double width = tableView.getPrefWidth() / columnCount;
            tableView.getColumns().forEach(col -> col.setPrefWidth(width));
        }
    }

    /**
     * Disable column reordering.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to configure
     */
    public static <T> void disableColumnReordering(TableView<T> tableView) {
        tableView.getColumns().forEach(col -> col.setReorderable(false));
    }

    /**
     * Make columns resizable.
     *
     * @param <T> the type of data in the table
     * @param columns the columns to make resizable
     */
    public static <T> void makeResizable(List<TableColumn<T, ?>> columns) {
        columns.forEach(col -> col.setResizable(true));
    }

    // ============ Selection Methods ============

    /**
     * Get selected item from table.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to query
     * @return the selected item, or null if no selection
     */
    public static <T> T getSelected(TableView<T> tableView) {
        return tableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Select row by index.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to modify
     * @param index the row index to select
     */
    public static <T> void selectRow(TableView<T> tableView, int index) {
        if (index >= 0 && index < tableView.getItems().size()) {
            tableView.getSelectionModel().select(index);
        }
    }

    /**
     * Clear selection.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to modify
     */
    public static <T> void clearSelection(TableView<T> tableView) {
        tableView.getSelectionModel().clearSelection();
    }

    // ============ Query Methods ============

    /**
     * Check if table is empty.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to check
     * @return true if table has no items
     */
    public static <T> boolean isEmpty(TableView<T> tableView) {
        return tableView.getItems().isEmpty();
    }

    /**
     * Get item count in table.
     *
     * @param <T> the type of data in the table
     * @param tableView the table to query
     * @return number of items in the table
     */
    public static <T> int getItemCount(TableView<T> tableView) {
        return tableView.getItems().size();
    }
}
