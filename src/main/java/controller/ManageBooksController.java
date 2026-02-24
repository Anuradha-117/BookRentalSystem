package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageBooksController {

    public JFXButton btnAdd;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;
    public TextField txtSearch;
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
        txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQuantity.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        txtCategory.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\d.*")) {
                txtCategory.setText(oldValue);
            }
        });
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
        String title = txtTitle.getText();
        if (isBookExist(title)) {
            new Alert(Alert.AlertType.WARNING, "This book already exists!").show();
            return;
        }
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO books (title, author, category, quantity) VALUES (?,?,?,?)");
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
            PreparedStatement pstm = connection.prepareStatement("UPDATE books SET title=?, author=?, category=?, quantity=? WHERE id=?");
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
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM books WHERE id=?");
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

    public void handleMouseAction(MouseEvent mouseEvent) {
        Book book = tblBooks.getSelectionModel().getSelectedItem();

        if (book != null) {
            selectedBookId = book.getId();
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthor());
            txtCategory.setText(book.getCategory());
            txtQuantity.setText(String.valueOf(book.getQuantity())); // Fixed name
        }
    }

    public void txtSearchOnKeyReleased(KeyEvent keyEvent) {
        try {
            String search = txtSearch.getText();
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? OR CAST(id AS CHAR) LIKE ?");

            pstm.setString(1, "%" + search + "%");
            pstm.setString(2, "%" + search + "%");
            pstm.setString(3, "%" + search + "%");
            pstm.setString(4, "%" + search + "%");

            ResultSet resultSet = pstm.executeQuery();

            ObservableList<Book> list = FXCollections.observableArrayList();
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

    private boolean isBookExist(String title) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT id FROM books WHERE title=?");
            pstm.setString(1, title);
            ResultSet resultSet = pstm.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}