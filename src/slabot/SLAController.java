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
import javafx.scene.control.CheckBox;
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
    private CheckBox keepTrackingBox;
    @FXML
    private Button importButton;
    @FXML
    private Button trackButton;
    @FXML
    private Label detailLabel;
    @FXML
    private TextField nameField;
    private static final StringProperty labelProperty = new SimpleStringProperty();
    private static boolean continueTracking;
/***
 * Class to do it's job hour after hour without getting tired of its slave work
 */
    static class OneHourJob extends TimerTask {

        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                public void run() {
                    if (continueTracking)
                    try {
                        if(Tracker.needsNewTracker()){
                            File newTracker = Tracker.CreateTrackerFile(tracker.getWorkbook().getParentFile().getAbsolutePath());
                            if(newTracker != null){
                            tracker.setWorkbook(newTracker);
                            System.out.println("Tracker updated to: " + tracker.getWorkbook().getAbsolutePath());
                            }
                        }
                } catch (Exception ex) {
                    Logger.getLogger(SLAController.class.getName()).log(Level.SEVERE, null, ex);
                }
                    tracker.setBotName(botName);
                    System.out.println("Set botName = " + botName);
                    System.out.println("Tracked " + getHourPST() + ":00\n");
                    labelProperty.set("Tracked " + getHourPST() + ":00\n");
                    tracker.trackSLA();
                }
            });
        }
    }

    
    /***
     *  Imports the file containing the cURL funny text file. 
     * Technical note: file must be in UTF-8
     */
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
    /***
     * Get the the Excel file 
     * we currently only support only one .XLS (the input example) for this. 
     * Therefore there is a filter to avoid over users being users
     * @param event user action
     */
    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Excel files (*.xls, *.xlsx)", "*.xls","*.xlsx");
//        FileChooser.ExtensionFilter xlsFilter = new FileChooser.ExtensionFilter("Excel 2003 files (*.xls)", "*.xls");
//        FileChooser.ExtensionFilter xlsxFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(excelFilter);
//        fileChooser.getExtensionFilters().add(xlsxFilter);
        
        //Show save file dialog
        tracker.setWorkbook(fileChooser.showOpenDialog(importButton.getScene().getWindow()));
        hasSLAFile = true;
    }

    /**
     * Basically prepare the controller to call the main method each hour
     * @param event 
     */
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
                continueTracking = keepTrackingBox.isSelected();
                timer.scheduleAtFixedRate(hourJob, firstRun * MIN, HOUR); //  0,2 *MIN);//
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
/***
 * Just bind the detailLabel & the labelProperty
 * @param url
 * @param rb 
 */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tracker = new Tracker();
        detailLabel.textProperty().bind(labelProperty);
    }

}
