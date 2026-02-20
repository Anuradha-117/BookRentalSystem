package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    void btnManageBooksOnAction(ActionEvent event) {
        System.out.println("Clicked Manage Books!");
    }

    @FXML
    void btnCustomersOnAction(ActionEvent event) {
        System.out.println("Clicked Customers!");
    }

    @FXML
    void btnRentalsOnAction(ActionEvent event) {
        System.out.println("Clicked Rentals!");
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {
        System.out.println("Clicked Logout");
    }
}