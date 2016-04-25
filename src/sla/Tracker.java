/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sla;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.util.TimeZone;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author Rodrigo
 */
public class Tracker {

    private static String curl;

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        Tracker.curl = curl;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        if (botName.isEmpty()) {
            Tracker.botName = "SLAbot";
        } else {
            Tracker.botName = botName;
        }
    }

    public File getWorkbook() {
        return workbook;
    }

    public void setWorkbook(File workbook) {
        Tracker.workbook = workbook;
    }
    private static String botName;
    private static File workbook;
//    final static long SEC = 1000;
//    final static long MIN = 60 * SEC;
//    final static long HOUR = 60 * MIN;

//    public static void main(String[] args) {
//        Calendar cal = Calendar.getInstance();
//        long firstRun = 60 - cal.get(Calendar.MINUTE);
//        OneHourJob hourJob = new OneHourJob();
//        Timer timer = new Timer();
//        System.out.println("Will start tracking in " + firstRun + " minutes.");
//        timer.scheduleAtFixedRate(hourJob, firstRun * MIN, HOUR); // this code
//
//    }
    public static Date getPSTTime() {
        Calendar localTime = Calendar.getInstance();
        Calendar pstTime = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
//        pstTime.setTimeInMillis(localTime.getTimeInMillis());
//        int hour = pstTime.get(Calendar.HOUR);
//        int minute = pstTime.get(Calendar.MINUTE);
//        int second = pstTime.get(Calendar.SECOND);
        Date pstDate = new Date();
        return pstTime.getTime();
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer day = cal.get(Calendar.DATE);
        //System.out.println("date>>" + day);
        return day;
    }

    public static int getDayPST(Date date) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(date);
        Integer day = cal.get(Calendar.DATE);
        //System.out.println("date>>" + day);
        return day;
    }

    public static int getHourPST() {
        Calendar localTime = Calendar.getInstance();
        Calendar pstTime = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        pstTime.setTimeInMillis(localTime.getTimeInMillis());
        int hour = pstTime.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getRow(Date firstCell) {
        int pst = getDayPST(getPSTTime());
        int cell = getDay(firstCell);
        boolean isFirst = (pst - cell) < 7;
        System.out.println("PST-cell" + pst + "<--->" + cell);
        System.out.println("(pst - cell)==" + (pst - cell));
        System.out.println("hourPST = getHourPST()");
        if (isFirst) {
            System.out.println("Row: "  + (getHourPST() + 2));
            return getHourPST() + 2;

        } else {
            System.out.println("Row: "  + (getHourPST() + 28));
            return getHourPST() + 28;
        }
    }

    public static int getCell(Date firstCell) {
        int pst = getDayPST(getPSTTime());
        int cell = getDay(firstCell);
        boolean isFirst = (pst - cell) < 7;
        if (isFirst) {
            System.out.println("here");
            return (pst - cell) * 2 + 1;
        } else {
            System.out.println("vaca: " + ((pst - 7) - cell));
            System.out.println("boi: " + ((pst - 7) - cell) * 2 + 1);
            return ((pst - 7) - cell) * 2 + 1;
        }
    }

    public void trackSLA() {
        try {
            final String FILE = workbook.getAbsolutePath();
            System.out.println("open " + workbook.getAbsolutePath());
            InputStream inp = new FileInputStream(FILE);
            int rowIndex = 1, cellIndex = 1;
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(rowIndex);
            Cell cell = row.getCell(cellIndex);
            Date firstCellContents = cell.getDateCellValue();
            rowIndex = getRow(firstCellContents);
            cellIndex = getCell(firstCellContents);
            System.out.println("getCell ==" + cellIndex);
            //get the cell where it must write
            row = sheet.getRow(rowIndex);
            cell = row.getCell(cellIndex);

            //get SLA time
            String sla = getSLATime();
            System.out.println("\nSLA:" + sla);
            cell.setCellValue(sla);

            cell = row.getCell(cellIndex + 1);
            cell.setCellValue(botName);
            try ( //write it on the file
                    FileOutputStream fileOut = new FileOutputStream(workbook)) {
                wb.write(fileOut);
            }
            System.out.println("Saved on " + workbook.getAbsolutePath());
        } catch (IOException | InvalidFormatException | EncryptedDocumentException e) {
            System.out.println("ERROR : " + e.getMessage());
        }

    }

    private static String getSLATime() {
        String json = main.GetJSON.getRawSLA(curl);
        String empty = main.Utils.find("empty", json);
        String result;
        if (empty == "false") {
            result = main.Utils.find("changed-at", json);
        } else {
            return "no issue";
        }
        System.out.println("\nempty>>" + empty + "\nfound::" + result);
        return result;
    }
}
