package controller;

import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageBooksController {

    @FXML
    private JFXTextField txtTitle;

    @FXML
    private JFXTextField txtAuthor;

    @FXML
    private JFXTextField txtCategory;

    @FXML
    private JFXTextField txtQuantity;

    @FXML
    private TableView<Book> tblBooks;

    @FXML
    private TableColumn<Book, Integer> colID;

    @FXML
    private TableColumn<Book, String> colTitle;

    @FXML
    private TableColumn<Book, String> colAuthor;

    @FXML
    private TableColumn<Book, String> colCategory;

    @FXML
    private TableColumn<Book, Integer> colQuantity;


    private int selectedBookId = 0;

    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadTable();
    }

    private void loadTable() {
        ObservableList<Book> list = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM books");

            while (resultSet.next()) {
                list.add(new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity")
                ));
            }
            tblBooks.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO books (title, author, category, quantity) VALUES (?,?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, txtTitle.getText());
            pstm.setString(2, txtAuthor.getText());
            pstm.setString(3, txtCategory.getText());
            pstm.setInt(4, Integer.parseInt(txtQuantity.getText()));

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Book Added!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Quantity must be a number!").show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (selectedBookId == 0) {
            new Alert(Alert.AlertType.WARNING, "Select a book to update!").show();
            return;
        }
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "UPDATE books SET title=?, author=?, category=?, quantity=? WHERE id=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, txtTitle.getText());
            pstm.setString(2, txtAuthor.getText());
            pstm.setString(3, txtCategory.getText());
            pstm.setInt(4, Integer.parseInt(txtQuantity.getText()));
            pstm.setInt(5, selectedBookId);

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Book Updated!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        if (selectedBookId == 0) {
            new Alert(Alert.AlertType.WARNING, "Select a book to delete!").show();
            return;
        }
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM books WHERE id=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setInt(1, selectedBookId);

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Book Deleted!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtAuthor.clear();
        txtCategory.clear();
        txtQuantity.clear();
        selectedBookId = 0;
    }
}