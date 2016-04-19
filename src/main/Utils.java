/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Rodrigo
 */
public class Utils {

    //no reason to construct objects of this class
    private Utils() {
    }

    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static List<Long> readCSV2Long(String path) throws IOException {
        String raw = readFile(path, StandardCharsets.UTF_8);
        String[] rawValues = raw.split(",");
        List<Long> valueList = new ArrayList<>();
        for (String v : rawValues) {
            valueList.add(Long.parseLong(v));
        }
        return valueList;
    }

    public static List<String> readCSV(String path) throws IOException {
        String raw = readFile(path, StandardCharsets.UTF_8);
        return Arrays.asList(raw.split(","));
    }

    public static String getCookieFromFile(String path) throws IOException {
        String moi = Utils.readFile(path, StandardCharsets.UTF_8);
        String[] mois = moi.split("-H");
        for (String m : mois) {
            if (m.contains("Cookie")) {
                return " -H" + m;
            }
        }
        return "Error couldn't read cookie";
    }

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

    public static String find(String key, String jsonText) {
        JSONParser parser = new JSONParser();
        KeyFinder finder = new KeyFinder();
        finder.setMatchKey(key);
        try {

            while (!finder.isEnd()) {
                parser.parse(jsonText, finder, true);
                if (finder.isFound()) {
                    finder.setFound(false);
                    if (key.equalsIgnoreCase("items")){
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

    public static List<String> findArray(String key, String jsonText) throws ParseException {
        List<String> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        KeyFinder finder = new KeyFinder();
        finder.setMatchKey(key);
        Object obj = parser.parse(jsonText);
        JSONObject jsonObject = (JSONObject) obj;

        JSONObject msg = (JSONObject) jsonObject.get("data");
        System.out.println("bla");
//        Iterator<String> iterator = msg.iterator();
//        while (iterator.hasNext()) {
//            String s = iterator.next();
//            list.add(s);
//            System.out.println(s);
//        }
        return list;
    }

    public static List<String> getTags(String jsonText) throws ParseException {
        List<String> list = new ArrayList<>();
        JSONParser parser = new JSONParser();

        Object obj = parser.parse(jsonText);
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject jsonTags = (JSONObject) ((JSONObject) ((JSONObject) jsonObject.get("data")).get("issue")).get("tags");
        String tags = (String) jsonTags.get("items").toString().replaceAll("[^A-Za-z0-9,_.]","");
        String[] vaca = tags.split(",");
        for(String s : vaca) {
            list.add(s);
            System.out.println(s);
        }
            return list;
        }

    }
