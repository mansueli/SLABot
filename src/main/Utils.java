/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sla.Tracker;

/**
 *
 * @author Rodrigo
 */
public class Utils {

    //no reason to construct objects of this class
    private Utils() {
    }

    /**
     * Read a file from the system
     *
     * @param path the Path for the file
     * @param encoding charset i.e UTF-8 or Klingon
     * @return string with file contents
     * @throws IOException
     */
    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }



    /**
     * Save a file into the system
     *
     * @param content the content to be written
     * @param file the file, eh?!
     */
    public static void SaveFile(String content, File file) {
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Find all occurrences of a list of elements in a JSON String
     *
     * @param items
     * @param json
     * @return string with everything found (if something was found)
     * @throws IOException
     * @throws InterruptedException
     */
    public static String findAll(List<String> items, String json) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        int i = items.size();
        for (String item : items) {
            i--;
            try {
                sb.append(find(item, json)).append(i == 0 ? "" : ",");
            } catch (Exception e) {
                sleep(500);
                try {
                    sb.append(find(item, json)).append(i == 0 ? "" : ",");
                } catch (Exception e2) {
                    sleep(1000);
                    sb.append(find(item, json)).append(i == 0 ? "" : ",");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Finds the value for a determined JSON key
     *
     * @param key where the value is stored
     * @param jsonText the place where you have to find that thing
     * @return
     */
    public static String find(String key, String jsonText) {
        JSONParser parser = new JSONParser();
        KeyFinder finder = new KeyFinder();
        finder.setMatchKey(key);
        try {

            while (!finder.isEnd()) {
                parser.parse(jsonText, finder, true);
                if (finder.isFound()) {
                    finder.setFound(false);
                    if (key.equalsIgnoreCase("items")) {
                        finder.startArray();
                        System.out.println("finder.getValue() -->" + finder.getValue() + finder.endArray());
                    }

//        System.out.println(key + ",");
                    //System.out.println(key + "===>>" + finder.getValue().toString());//.replaceAll("[^A-Za-z0-9,_]", "") + ",");
                    break;
                } else {
                    //System.out.println("vaca,"); 
                }
            }
        } catch (Exception pe) {
            pe.printStackTrace();
        }
        return finder.getValue().toString().replaceAll("[^A-Za-z0-9,_.]", "").replaceAll(",", ";");
    }

}
