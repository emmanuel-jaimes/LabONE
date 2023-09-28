package com.example.labone;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;

public class HelloController {

    @FXML
    private Button cDegrees;

    @FXML
    private NumberAxis degreeLabel;

    @FXML
    private Button fDegrees;

    @FXML
    private LineChart<?, ?> lineChart;

    @FXML
    void cButtonClick(ActionEvent event) {
        degreeLabel.setLabel("Temperature (C)");
    }

    @FXML
    void fButtonClick(ActionEvent event) {
        degreeLabel.setLabel("Temperature (F)");
    }

}
