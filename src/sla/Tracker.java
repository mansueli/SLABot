/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sla;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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

    /**
     * Defines the name for the BOT (if empty = SLAbot)
     *
     * @param botName
     */
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

    /**
     *
     * @param workbook Excel .XLS (old file format)
     */
    public void setWorkbook(File workbook) {
        Tracker.workbook = workbook;
    }
    private static String botName;
    private static File workbook;

    /**
     * get current time in PST
     *
     * @return Java Date in the PST timezone
     */
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

    /**
     * Date where you want to find the day in the month
     *
     * @param date
     * @return current day from the given time
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer day = cal.get(Calendar.DATE);
        //System.out.println("date>>" + day);
        return day;
    }

    /**
     * Calculate the days between dates properly
     *
     * @param day1 calendar with the first day
     * @param day2 calendar with the second day
     * @return the difference between them from
     */
    public static int daysBetweenFirstPST(Calendar day1) {
        Calendar dayOne = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        Calendar dayTwo = (Calendar) day1.clone();
//                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    /**
     *
     * @param date current time / another day
     * @return equivalent day in PST
     */
    public static int getDayPST(Date date) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        cal.setTime(date);
        Integer day = cal.get(Calendar.DATE);
        //System.out.println("date>>" + day);
        return day;
    }

    /**
     * @return hour of the day in PST
     */
    public static int getHourPST() {
        Calendar localTime = Calendar.getInstance();
        Calendar pstTime = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        pstTime.setTimeInMillis(localTime.getTimeInMillis());
        int hour = pstTime.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    /**
     * get the row to write contents based on the first cell if you are
     * following the right input it would be X3:X26 or X29:X52
     *
     * @param firstCell
     */
    public static int getRow(Calendar first) {
        int diff = daysBetweenFirstPST(first);
        int cell = first.get(Calendar.DAY_OF_MONTH);
        System.out.println("row=diff>>" + diff);
        System.out.println("cell = " + cell);
        boolean isFirst = (diff) < 7;
        System.out.println("diff=" + diff);
        if (isFirst) {
            System.out.println("Row: " + (getHourPST() + 2));
            return getHourPST() + 2;

        } else {
            System.out.println("Row: " + (getHourPST() + 28));
            return getHourPST() + 28;
        }
    }

    /**
     *
     * @param firstCell finds the first cell with some day (note the input
     * example, this is should get the Excel cell B2 if you are doing everything
     * right.
     * @return date from the first cell.
     */
    public static int getCell(Calendar first) {
        int diff = daysBetweenFirstPST(first);
        boolean isFirst = (diff) < 7;
        if (isFirst) {
            System.out.println("here");
            return (diff) * 2 + 1;
        } else {
            System.out.println("vaca: " + (diff - 7));
            System.out.println("boi: " + ((diff - 7) * 2 + 1));
            return ((diff - 7) * 2 + 1);
        }
    }

    /**
     * Uses everything else to do the Magic i.e the main code for tracking and
     * writing to the file.
     */
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
            Calendar firstDayCalendar = Calendar.getInstance();
            firstDayCalendar.setTime(firstCellContents);
            rowIndex = getRow(firstDayCalendar);
            cellIndex = getCell(firstDayCalendar);
            System.out.println("getRow --->" + rowIndex);
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

    /**
     * get the SLA waiting time based on the value shown on the queue**
     * IMPORTANT! If over an 1hour Helpshift will be updating it on a hourly
     * basis so: it is not possible to get something like 1h01min.
     *
     * @return the time in a readable form or no issue if there is no waiting
     * issue there.
     */
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

    public static boolean needsNewTracker() throws Exception {
        final String FILE = workbook.getAbsolutePath();
        System.out.println("open " + workbook.getAbsolutePath());
        InputStream inp = new FileInputStream(FILE);
        Workbook wb = WorkbookFactory.create(inp);
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(1);
        Cell cell = row.getCell(1);
        Date firstCellContents = cell.getDateCellValue();
        Calendar firstDayCalendar = Calendar.getInstance();
        firstDayCalendar.setTime(firstCellContents);
        int diff = daysBetweenFirstPST(firstDayCalendar);
        if (diff < 14) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Read a file from the system
     *
     * @param folderPath the Path where to store the file
     * @param encoding charset i.e UTF-8 or Klingon
     * @return string with file contents
     * @throws IOException
     */
    public static File CreateTrackerFile(String folderPath) throws IOException, InvalidFormatException {
        File nextTracker = null;
        try {
            final File template = new File("res/template.xlsx");
            Tracker newTracker = new Tracker();
            newTracker.setWorkbook(template);
            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
            cal.setTime(Tracker.getPSTTime());
            //cal.add(Calendar.DATE, 1);
            System.out.println("open " + template.getAbsolutePath());
            InputStream inp = new FileInputStream(template);
            int rowIndex = 1, cellIndex = 1;
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(1);
            Date firstCellContents = cell.getDateCellValue();
            cell.setCellValue(cal.getTime());
            wb.getCreationHelper().createFormulaEvaluator().evaluateAll();
            wb.setForceFormulaRecalculation(true);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String name = "SLA Tracker " + dateFormat.format(cal.getTime());
            cal.add(Calendar.DATE, 13);
            dateFormat = new SimpleDateFormat("MM-dd");
            name = name + " to " + dateFormat.format(cal.getTime());
            nextTracker = new File(folderPath + File.separator + name + ".xlsx");
            System.out.println(nextTracker.getAbsolutePath());
            try ( //write it on the file
                    FileOutputStream fileOut = new FileOutputStream(nextTracker)) {
                wb.write(fileOut);
            }
        } catch (Exception e) {
            try {
                Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
                cal.setTime(Tracker.getPSTTime());
                cal.add(Calendar.DATE, 1);
                int rowIndex = 1, cellIndex = 1;
                Workbook wb = WorkbookFactory.create(Tracker.class.getResourceAsStream("res/template.xlsx"));
                Sheet sheet = wb.getSheetAt(0);
                Row row = sheet.getRow(1);
                Cell cell = row.getCell(1);
                Date firstCellContents = cell.getDateCellValue();
                cell.setCellValue(cal.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String name = "SLA Tracker " + dateFormat.format(cal.getTime());
                cal.add(Calendar.DATE, 13);
                dateFormat = new SimpleDateFormat("MM-dd");
                name = name + " to " + dateFormat.format(cal.getTime());
                nextTracker = new File(folderPath + File.separator + name + ".xlsx");
                System.out.println(nextTracker.getAbsolutePath());
                try ( //write it on the file
                        FileOutputStream fileOut = new FileOutputStream(nextTracker)) {
                    wb.write(fileOut);
                }
            } catch (Exception ep) {
                  System.out.println("Could not use the resource or the template file;");
            }
        }
        return nextTracker;
    }
}
