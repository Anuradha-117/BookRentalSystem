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
import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageCustomersController {

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private TableColumn<Customer, Integer> colId;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colPhone;

    @FXML
    private TableView<Customer> tblCustomers;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtPhone;

    @FXML
    private TextField txtSearch;

    private int selectedCustomerId = 0;

    public void initialize() {
        txtPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPhone.setText(newValue.replaceAll("[^\\d]", ""));
            }

            if (txtPhone.getText().length() > 10) {
                String limitedText = txtPhone.getText().substring(0, 10);
                txtPhone.setText(limitedText);
            }
        });

        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\d.*")) {
                txtName.setText(oldValue);
            }
        });

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadTable();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        if (selectedCustomerId == 0) {
            new Alert(Alert.AlertType.WARNING, "Select a Customer to delete!").show();
            return;
        }
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "DELETE FROM Customers WHERE id=?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setInt(1, selectedCustomerId);

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Deleted!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String name = txtName.getText();
        String phone = txtPhone.getText();

        if (name.isEmpty() || phone.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields!").show();
            return;
        }

        if (phone.length() < 10) {
            new Alert(Alert.AlertType.WARNING, "Phone number must be 10 digits!").show();
            return;
        }

        if (isCustomerExist(name)){
            new Alert(Alert.AlertType.WARNING,"This customer already exists!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customers (name,phone) VALUES (?,?)");
            pstm.setString(1, name);
            pstm.setString(2, phone);

            if (pstm.executeUpdate()>0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Added!").show();
                loadTable();
                clearFields();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (selectedCustomerId == 0){
            new Alert(Alert.AlertType.WARNING,"Select a customer to update!").show();
            return;
        }

        String name = txtName.getText();
        String phone = txtPhone.getText();

        if (name.isEmpty() || phone.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields!").show();
            return;
        }

        if (phone.length() < 10) {
            new Alert(Alert.AlertType.WARNING, "Phone number must be 10 digits!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE Customers SET name = ?,phone = ? WHERE id = ?");
            pstm.setString(1, name);
            pstm.setString(2, phone);
            pstm.setInt(3, selectedCustomerId);

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Updated!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }

    }

    private void loadTable() {
        ObservableList<Customer> list = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Customers");

            while (resultSet.next()) {
                list.add(new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone")
                ));
            }
            tblCustomers.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isCustomerExist(String name) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT id FROM Customers WHERE name=?");
            pstm.setString(1, name);
            ResultSet resultSet = pstm.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
    }

    public void handleMouseAction(MouseEvent mouseEvent) {
        Customer customer = tblCustomers.getSelectionModel().getSelectedItem();
        if (customer != null ){
            selectedCustomerId = customer.getId();
            txtName.setText(customer.getName());
            txtPhone.setText(customer.getPhone());
        }
    }

    public void txtSearchOnKeyReleased(KeyEvent keyEvent) {
        try {
            String search = txtSearch.getText();
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customers WHERE name LIKE ? OR phone LIKE ? OR CAST(id AS CHAR) LIKE ?");

            pstm.setString(1, "%" + search + "%");
            pstm.setString(2, "%" + search + "%");
            pstm.setString(3, "%" + search + "%");

            ResultSet resultSet = pstm.executeQuery();

            ObservableList<Customer> list = FXCollections.observableArrayList();
            while (resultSet.next()) {
                list.add(new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone")
                ));
            }
            tblCustomers.setItems(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
