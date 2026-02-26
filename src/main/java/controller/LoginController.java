package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, username);
            pstm.setString(2, password);
            ResultSet resultSet = pstm.executeQuery();

            if (resultSet.next()) {
                System.out.println("✅ Login Successful!");

                String role = resultSet.getString("role");
                UserSession.userRole = role;
                UserSession.username = username;

                System.out.println("User Role: " + role);

                Parent root = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(new Scene(root));

            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Username or Password!").show();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error!").show();
        }
    }
}