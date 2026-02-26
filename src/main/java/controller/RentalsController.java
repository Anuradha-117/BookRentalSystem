package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Rental;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalsController {

    @FXML
    private JFXButton btnIssue;

    @FXML
    private JFXButton btnReturn;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private TableColumn<Rental, Integer> colBookId;

    @FXML
    private TableColumn<Rental, Integer> colCustomerId;

    @FXML
    private TableColumn<Rental, Double> colFine;

    @FXML
    private TableColumn<Rental, String> colIssueDate;

    @FXML
    private TableColumn<Rental, Integer> colRentalId;

    @FXML
    private TableColumn<Rental, String> colReturnDate;

    @FXML
    private Label lblFine;

    @FXML
    private TableView<Rental> tblRentals;

    @FXML
    private JFXTextField txtBookId;

    @FXML
    private JFXTextField txtCustomerId;

    @FXML
    private JFXTextField txtRentalId;

    @FXML
    private TextField txtSearch;

    public void initialize() {
        colRentalId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));

        colFine.setCellFactory(column -> new TableCell<Rental, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item == 0.0) {
                    setText("");
                } else {
                    setText("Rs. " + item);
                }
            }
        });

        loadTable();

        tblRentals.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtRentalId.setText(String.valueOf(newSelection.getId()));
            }
        });


        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            loadTable(newValue);
        });
    }


    @FXML
    void btnIssueOnAction(ActionEvent event) {
        String bookIdText = txtBookId.getText();
        String custIdText = txtCustomerId.getText();

        if (bookIdText.isEmpty() || custIdText.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter Book ID and Customer ID!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            int bookId = Integer.parseInt(bookIdText);
            int custId = Integer.parseInt(custIdText);

            PreparedStatement checkStock = connection.prepareStatement("SELECT quantity FROM books WHERE id = ?");
            checkStock.setInt(1, bookId);
            ResultSet resultSet = checkStock.executeQuery();

            if (resultSet.next()) {
                if (resultSet.getInt("quantity") <= 0) {
                    new Alert(Alert.AlertType.ERROR, "Book is out of stock!").show();
                    return;
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Book ID!").show();
                return;
            }

            PreparedStatement checkCust = connection.prepareStatement("SELECT id FROM customers WHERE id = ?");
            checkCust.setInt(1, custId);
            if (!checkCust.executeQuery().next()) {
                new Alert(Alert.AlertType.ERROR, "Invalid Customer ID!").show();
                return;
            }

            String sql = "INSERT INTO rentals (book_id, customer_id, issue_date) VALUES (?, ?, ?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setInt(1, bookId);
            pstm.setInt(2, custId);
            pstm.setDate(3, Date.valueOf(LocalDate.now()));

            if (pstm.executeUpdate() > 0) {
                updateStock(bookId, -1);
                new Alert(Alert.AlertType.INFORMATION, "Book Issued Successfully!").show();
                loadTable();
                clearFields();
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "IDs must be numbers!").show();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnReturnOnAction(ActionEvent event) {
        String rentalIdText = txtRentalId.getText();

        if (rentalIdText.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter Rental ID!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            int rentalId = Integer.parseInt(rentalIdText);

            String sqlGet = "SELECT * FROM rentals WHERE id = ?";
            PreparedStatement pstmGet = connection.prepareStatement(sqlGet);
            pstmGet.setInt(1, rentalId);
            ResultSet resultSet = pstmGet.executeQuery();

            if (resultSet.next()) {
                if (resultSet.getDate("return_date") != null) {
                    new Alert(Alert.AlertType.WARNING, "This book is already returned!").show();
                    return;
                }

                int bookId = resultSet.getInt("book_id");
                LocalDate issueDate = resultSet.getDate("issue_date").toLocalDate();
                LocalDate returnDate = LocalDate.now();

                long days = ChronoUnit.DAYS.between(issueDate, returnDate);
                double fine = (days > 0) ? (days * 15.0) : 0.0;
                lblFine.setText("Fine Amount : Rs. " + fine);

                String sqlUpdate = "UPDATE rentals SET return_date = ?, fine = ? WHERE id = ?";
                PreparedStatement pstmUpdate = connection.prepareStatement(sqlUpdate);
                pstmUpdate.setDate(1, Date.valueOf(returnDate));
                pstmUpdate.setDouble(2, fine);
                pstmUpdate.setInt(3, rentalId);
                pstmUpdate.executeUpdate();

                updateStock(bookId, 1);

                new Alert(Alert.AlertType.INFORMATION, "Book Returned! Fine: " + fine).show();
                loadTable();
                txtRentalId.clear();
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Rental ID!").show();
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Rental ID must be a number!").show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Rental selectedRental = tblRentals.getSelectionModel().getSelectedItem();

        if (selectedRental == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a rental to delete!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            //  the book isn't returned yet, we  add it back to stock before deleting the rental record to fix the stock count.
            // If it returned, we don't do that cause it already returned.
            if (selectedRental.getReturnDate() == null) {
                updateStock(selectedRental.getBookId(), 1);
            }

            PreparedStatement pstm = connection.prepareStatement("DELETE FROM rentals WHERE id = ?");
            pstm.setInt(1, selectedRental.getId());

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Rental Record Deleted!").show();
                loadTable();
                clearFields();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private void updateStock(int bookId, int amount) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement("UPDATE books SET quantity = quantity + ? WHERE id = ?");
        pstm.setInt(1, amount);
        pstm.setInt(2, bookId);
        pstm.executeUpdate();
    }

    private void loadTable() {
        loadTable("");
    }

    private void loadTable(String searchText) {
        ObservableList<Rental> list = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM rentals WHERE CAST(id AS CHAR) LIKE ? OR CAST(book_id AS CHAR) LIKE ? OR CAST(customer_id AS CHAR) LIKE ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, "%" + searchText + "%");
            pstm.setString(2, "%" + searchText + "%");
            pstm.setString(3, "%" + searchText + "%");

            ResultSet resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                list.add(new Rental(
                        resultSet.getInt("id"),
                        resultSet.getInt("book_id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getDate("issue_date").toLocalDate(),
                        resultSet.getDate("return_date") != null ? resultSet.getDate("return_date").toLocalDate() : null,
                        resultSet.getDouble("fine")
                ));
            }
            tblRentals.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtBookId.clear();
        txtCustomerId.clear();
        txtRentalId.clear();
        lblFine.setText("Fine Amount :");
    }
}