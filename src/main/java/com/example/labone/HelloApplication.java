package com.example.labone;

//api for phone messaging
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Timer;


public class HelloApplication extends Application {

    //twilio setup
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_ACCOUNT_AUTH");
    public static final String ACCOUNT_PHONE = System.getenv("TWILIO_ACCOUNT_PHONE");
    public boolean smsSent = false;
    public Timer timer = new Timer("timer");
    public TimerTask task;
    final int DATA_POINTS = 300; //number of data points
    public String UNIT = "C"; //default unit set to celsius
    public boolean isCelsius = true; //checks current temp unit
    public double max = 90.0;  //current max
    public double min = 45.0;   //current min
    public String phoneNum = "8554526694";  //current p.number
    //fx components
    BorderPane layout = new BorderPane();
    FlowPane flow = new FlowPane(Orientation.VERTICAL);
    public Label minTemp = new Label();
    public Label maxTemp = new Label();
    public Label phoneDisplay = new Label();
    public TextField minUpdate = new TextField();
    public TextField maxUpdate = new TextField();
    public TextField phoneUpdate = new TextField();
    public TextField info = new TextField();
    public Button updateButton = new Button();
    public Button unitButton = new Button();
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    public static int counter = 0;

    public static List<Double> xPoints = new ArrayList<>();
    private ScheduledExecutorService executorService;
    private BufferReader buffer = new BufferReader();

    public HelloApplication() throws FileNotFoundException {
    }


    @Override
    public void start(Stage stage) throws Exception {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Lab 1");
//        stage.setScene(scene);
//        stage.show();

        stage.setTitle("Lab 1");
        xAxis.setLabel("Time ago (s)");
        unitButton.setOnAction(actionEvent -> convert());
        updateButton.setOnAction(actionEvent -> buttonAction());

        //axis
        xAxis.setAutoRanging(false);
        xAxis.setAnimated(false);
        xAxis.setForceZeroInRange(true);
        xAxis.setLowerBound(0.0);
        xAxis.setUpperBound(300.00);
        yAxis.setAutoRanging(false);
        yAxis.setAnimated(false);
        yAxis.setForceZeroInRange(true);
        yAxis.setTickUnit(5.0);
        yAxis.setLowerBound(10.0);
        yAxis.setUpperBound(50.0);
        yAxis.setSide(Side.RIGHT);
        //line chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Temperature Overview");
        lineChart.setAnimated(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        lineChart.getData().add(series);
        unitButton.setText("Convert to C");
        unitButton.setPrefSize(150,100);
        updateButton.setText("Update");
        updateButton.setPrefSize(150,100);
        info.setEditable(false);
        info.setPrefSize(150,100);
        info.setFont(Font.font("Verdana", FontWeight.BLACK, 50));
        minTemp.setFont(Font.font("Verdana", FontWeight.MEDIUM, 30));
        minTemp.setText("Minumum Temperature in F: " + min);
        maxTemp.setFont(Font.font("Verdana", FontWeight.MEDIUM, 30));
        maxTemp.setText("Maximum Temperature in F: " + max);
        phoneDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
        phoneDisplay.setText("Current phone number: " + phoneNum);
        minUpdate.setPromptText("Enter new min value in F");
        maxUpdate.setPromptText("Enter new max value in F");
        phoneUpdate.setPromptText("Enter new phone number");
        xPoints.add(21.00);

        ScheduledExecutorService executorService1;
        executorService1 = Executors.newSingleThreadScheduledExecutor();
        executorService1.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (series.getData().size() > DATA_POINTS) {
                    series.getData().remove(0);
                    xPoints.remove(0);
                }
                String ttemp;
                Double dtemp = -200.00;
                try {
                    ttemp = buffer.getThisData();
                    if(ttemp != ""){
                        dtemp = Double.parseDouble(ttemp);
                    }
                    //System.out.println(dtemp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(dtemp != -200.00){
                    if (!isCelsius) {
                        double val = (1.8) * dtemp + 32.0;
                        val = val * 100;
                        val = Math.round(val);
                        dtemp = val / 100;
                    }
                    //int l = series.getData().size();
                    //series.getData().clear();
                    if ((ttemp.contains("85")) || (ttemp.contains("-127"))) {
                        info.setText("Temperature: NO DATA PROVIDED");
                        series.getData().add(new XYChart.Data<>(counter, -200.0));
                        xPoints.add(-200.0);
                    } else {
                        series.getData().add(new XYChart.Data<>(counter, dtemp));
                        xPoints.add(dtemp);
                        info.setText("Temperature: " + xPoints.get(xPoints.size() - 1).toString() + UNIT);
                    }
                    if(counter > 300.00){
                        xAxis.setLowerBound(0.0 + counter - 300.00);
                        xAxis.setUpperBound(counter);
                    }
                    counter++;
                    alertSMS();
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
        flow.setVgap(10.0);
        flow.getChildren().addAll(unitButton, minTemp, minUpdate, maxTemp, maxUpdate, phoneDisplay, phoneUpdate, updateButton);
        layout.setCenter(lineChart);
        layout.setLeft(flow);
        layout.setTop(info);
        Scene scene = new Scene(layout, 1000, 750);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdownNow();
    }

    public static void main(String[] args) {
        launch();
    }

    //unit conversion between F and C
    public void convert() {
        if (isCelsius) {
            UNIT = "F";
            isCelsius = false;
            yAxis.setLabel("Temperature (F)");
            yAxis.setLowerBound(50.0);
            yAxis.setUpperBound(122.0);

            for (int i = 0; i < counter; i++) {
                double val = (1.8) * xPoints.get(i) + 32;
                val = val * 100;
                val = Math.round(val);
                val = val / 100;
                xPoints.set(i, val);
            }
            double val = (1.8) * min + 32.0;
            val = val * 100;
            val = Math.round(val);
            min = val / 100; //min value
            val = (1.8) * max + 32.0;
            val = val * 100;
            val = Math.round(val);
            max = val / 100; //max value
            minUpdate.setText("Set new minimum value in F");
            maxUpdate.setText("Set new maximum value in F");
        } else { // is Fahrenheit to Celsius
            UNIT = "C";
            isCelsius = true;
            yAxis.setLowerBound(10);
            yAxis.setUpperBound(50);
            yAxis.setLabel("Temperatre (C)");

            for (int i = 0; i < counter; i++) {
                double val = (5.0/9.0) * (xPoints.get(i) - 32.0);
                val = val * 100;
                val = Math.round(val);
                val = val/100;
                xPoints.set(i, val);
            }
            double val = (5.0/9.0) * (max - 32.0);
            val = val * 100;
            val = Math.round(val);
            max = val/100; // max value
            val = (5.0/9.0) * (min - 32.0);
            val = val * 100;
            val = Math.round(val);
            min = val/100; // min value
            minUpdate.setText("Set new minimum value in C");
            maxUpdate.setText("Set new maximum value in C");
        }
    }

    //twilio send message service
    public void sendSMS(String args, String alert) {
        Twilio.init("AC8799b701835fb95790acd7adc35bb3da", "f915fa8cc761a899fc59db72f1fcaa2f");

        Message sms = Message.creator(new com.twilio.type.PhoneNumber("+12145663422"), new com.twilio.type.PhoneNumber("+1"+args), alert).create();
        System.out.println(sms.getSid());
    }

    public void alertSMS() {
        if (xPoints.get(xPoints.size() -1) > max && (xPoints.get(xPoints.size()-1)!=200.0) && (!smsSent)) {
            //max limit
            sendSMS(phoneNum, "TEMPERATURE ABOVE LIMIT");
            smsSent = true;
        }
        if (xPoints.get(xPoints.size() -1) < min && (xPoints.get(xPoints.size()-1)!=200.0) && (!smsSent)) {
            //min limit
            sendSMS(phoneNum, "TEMPERATURE BELOW LIMIT");
            smsSent = true;
        }
        if (smsSent) {
            //reset conditions
            if ((xPoints.get(xPoints.size() -1) > min) && (xPoints.get(xPoints.size() -1) < max)) {
                smsSent = false;
            }
        }
    }

    public void buttonAction() {
        smsSent = false;
        String temp;
        //minimum text field input
        if (!minUpdate.getText().isBlank()) { //if input for minimum is not blank
            temp = minUpdate.getText();
            if (temp.matches("[0-9]+")) {
                min = Double.parseDouble(minUpdate.getText());
                if (isCelsius) {
                    minTemp.setText("Minimum Temp. in C set to: " + min);
                } else {
                    minTemp.setText("Minumun Temp. in F set to: " + min);
                }
            }
        }
        //maximum text field input
        if (!maxUpdate.getText().isBlank()) { //if input for minimum is not blank
            temp = maxUpdate.getText();
            if (temp.matches("[0-9]+")) {
                max = Double.parseDouble(maxUpdate.getText());
                if (isCelsius) {
                    maxTemp.setText("Maximum Temp. in C set to: " + max);
                } else {
                    maxTemp.setText("Maximum Temp. in F set to: " + max);
                }
            }
        }
        //phone number text field input
        if (!phoneUpdate.getText().isBlank()) { //if input for minimum is not blank
            temp = phoneUpdate.getText();
            if (temp.matches("[0-9]+") && temp.length()==10) {
                phoneNum = temp;
                phoneDisplay.setText(phoneNum);
            }
        }
    }
}