package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;

import java.sql.*;

public class ManageUsersController {

    @FXML private JFXTextField txtUsername;

    @FXML private JFXPasswordField txtPassword;

    @FXML private JFXComboBox<String> cmbRole;

    @FXML private TableView<User> tblUsers;

    @FXML private TableColumn<User, Integer> colId;

    @FXML private TableColumn<User, String> colUsername;

    @FXML private TableColumn<User, String> colPassword;

    @FXML private TableColumn<User, String> colRole;


    private int selectedUserId = 0;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        cmbRole.setItems(FXCollections.observableArrayList("admin", "user"));

        loadTable();

        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUserId = newSelection.getId();
                txtUsername.setText(newSelection.getUsername());
                txtPassword.setText(newSelection.getPassword());
                cmbRole.setValue(newSelection.getRole());
            }
        });
    }


    @FXML
    void btnAddOnAction(ActionEvent event) {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        String role = cmbRole.getValue();

        if (user.isEmpty() || pass.isEmpty() || role == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement check = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            check.setString(1, user);
            if (check.executeQuery().next()) {
                new Alert(Alert.AlertType.ERROR, "Username already taken!").show();
                return;
            }

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
            pstm.setString(1, user);
            pstm.setString(2, pass);
            pstm.setString(3, role);

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "User Added!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }


    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        User selectedUser = tblUsers.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            new Alert(Alert.AlertType.WARNING, "Select a user to update!").show();
            return;
        }

        if (!util.UserSession.username.equalsIgnoreCase("admin")
                && selectedUser.getRole().equalsIgnoreCase("admin")) {

            // Allow admns to only update themselves
            if (!selectedUser.getUsername().equalsIgnoreCase(util.UserSession.username)) {
                new Alert(Alert.AlertType.ERROR, "Only the Main Admin can update other Admins!").show();
                return;
            }
        }

        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        String role = cmbRole.getValue();

        if (user.isEmpty() || pass.isEmpty() || role == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE users SET username=?, password=?, role=? WHERE id=?");
            pstm.setString(1, user);
            pstm.setString(2, pass);
            pstm.setString(3, role);
            pstm.setInt(4, selectedUser.getId());

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "User Updated!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }


    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        User selectedUser = tblUsers.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            new Alert(Alert.AlertType.WARNING, "Select a user to delete!").show();
            return;
        }

        if (selectedUser.getUsername().equalsIgnoreCase("admin")) {
            new Alert(Alert.AlertType.ERROR, "The Main Admin account cannot be deleted!").show();
            return;
        }

        if (!util.UserSession.username.equalsIgnoreCase("admin")
                && selectedUser.getRole().equalsIgnoreCase("admin")) {
            new Alert(Alert.AlertType.ERROR, "Only the Main Admin can delete other Admins!").show();
            return;
        }


        if (selectedUser.getUsername().equalsIgnoreCase(util.UserSession.username)) {
            new Alert(Alert.AlertType.ERROR, "You cannot delete your own account while logged in!").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM users WHERE id=?");
            pstm.setInt(1, selectedUser.getId());

            if (pstm.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "User Deleted!").show();
                loadTable();
                clearFields();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private void loadTable() {
        ObservableList<User> list = FXCollections.observableArrayList();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM users");
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
            tblUsers.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtUsername.clear();
        txtPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        selectedUserId = 0;
    }
}