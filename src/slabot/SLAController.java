/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slabot;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import static main.GetJSON.getRawSLA;
import sla.Tracker;
import static sla.Tracker.getHourPST;

/**
 *
 * @author Rodrigo
 */
public class SLAController implements Initializable {

    final static long SEC = 1000;
    final static long MIN = 60 * SEC;
    final static long HOUR = 60 * MIN;
    private boolean isTracking = false;
    private boolean hasSLAFile;
    private boolean hasCURL;
    private Timer timer;
    private OneHourJob hourJob;
    private static Tracker tracker;
    private static String curl;
    private static String botName;
    @FXML
    private Button importButton;
    @FXML
    private Button trackButton;
    @FXML
    private Label detailLabel;
    @FXML
    private TextField nameField;
    private static final StringProperty labelProperty = new SimpleStringProperty();

    static class OneHourJob extends TimerTask {

        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                public void run() {
                    
                    tracker.setBotName(botName);
                    System.out.println("Set botName = " + botName);
                    System.out.println("Tracked " + getHourPST() + ":00\n");
                    labelProperty.set("Tracked " + getHourPST() + ":00\n");
                    tracker.trackSLA();
                }
            });
        }
    }

    @FXML
    private void importCURL(ActionEvent event) {

        final FileChooser directoryChooser = new FileChooser();
        File file = directoryChooser.showOpenDialog(importButton.getScene().getWindow());
        try {
            curl = main.Utils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
            System.out.println(getRawSLA(curl));
            hasCURL = true;
            tracker.setCurl(curl);
        } catch (Exception ex) {
            Logger.getLogger(SLAController.class.getName()).log(Level.SEVERE, null, ex);
            hasCURL = false;
        }

    }

    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        tracker.setWorkbook(fileChooser.showOpenDialog(importButton.getScene().getWindow()));
        hasSLAFile = true;
    }

    @FXML
    private void trackButton(ActionEvent event) {
        if (hasCURL && hasSLAFile) {
            isTracking = !isTracking;
            if (isTracking) {
                trackButton.setText("Stop Tracking");
                trackButton.setDefaultButton(false);
                trackButton.setCancelButton(true);

                Calendar cal = Calendar.getInstance();
                long firstRun = 60 - cal.get(Calendar.MINUTE);
                hourJob = new OneHourJob();
                timer = new Timer();
                System.out.println("Will start tracking in " + firstRun + " minutes.");
                labelProperty.set("Will start tracking in " + firstRun + " minutes.");
                botName = nameField.getText();
                timer.scheduleAtFixedRate(hourJob, 0, HOUR); //firstRun * MIN, HOUR); // this code  0,2 *MIN);
            } else {
                trackButton.setText("Start Tracking");
                trackButton.setCancelButton(false);
                trackButton.setDefaultButton(true);
                labelProperty.set("Stopped");
                timer.cancel();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Problem to read input files");
            alert.setContentText("Either your Curl or SLA file could not be read properly");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tracker = new Tracker();
        detailLabel.textProperty().bind(labelProperty);

    }

}
