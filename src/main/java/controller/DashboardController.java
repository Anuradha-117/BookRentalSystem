package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.UserSession;

import java.io.IOException;
import java.net.URL;

public class DashboardController {

    public JFXButton btnManageUsers;
    @FXML
    private AnchorPane contentArea;

    @FXML
    void btnManageBooksOnAction(ActionEvent event) throws IOException {
        loadUI("/view/manage_books_form.fxml");
    }

    @FXML
    void btnCustomersOnAction(ActionEvent event) throws IOException {
        loadUI("/view/manage_customers_form.fxml");
    }

    @FXML
    void btnRentalsOnAction(ActionEvent event) throws IOException {
        loadUI("/view/rentals_form.fxml");
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login_form.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void loadUI(String fxmlPath) throws IOException {
        contentArea.getChildren().clear();
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            System.out.println("Error: FXML file not found at " + fxmlPath);
            return;
        }

        Parent load = FXMLLoader.load(resource);
        contentArea.getChildren().add(load);
    }

    @FXML
    public void initialize() {
        try {
            loadUI("/view/manage_books_form.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!"admin".equalsIgnoreCase(UserSession.userRole)) {
            btnManageUsers.setVisible(false);
            btnManageUsers.setDisable(true);
        }
    }

    public void btnManageUsersOnAction(ActionEvent actionEvent) throws IOException {
        loadUI("/view/manage_users_form.fxml");
    }
}