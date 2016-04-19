/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Rodrigo
 */
public class GetJSON {

    String cookie;

    public GetJSON(String cookie) {
        this.cookie = cookie;
    }

    public String getRaw(String issueID) {
        issueID = issueID.replaceAll("\\D+", "");
        final String curlParams = "curl \"https://acompli.helpshift.com/xhr/view/issue-details/?publish_id=" + issueID + "&viewing=1\" -H \"Host: acompli.helpshift.com\" -H \"User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0\" -H \"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\" -H \"Accept-Language: en-US,en;q=0.5\" --compressed -H \"X-Requested-With: XMLHttpRequest\" -H \"Referer: https://acompli.helpshift.com/admin/issue/" + issueID + "/\";";
        try {
            System.out.println("issueID");
            System.out.println("curlParams = " + curlParams);
            Runtime rt = Runtime.getRuntime();
            String cmdString = "";

            if (OSValidator.isWindows()) {
                cmdString = curlParams + cookie;
            } else if (OSValidator.isUnix()) {
                cmdString = curlParams + cookie;
            } else {
                System.out.println("OS not supported.");
                return null;
            }
            System.out.println(cmdString);
            Process pr = rt.exec(cmdString);

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = input.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            pr.waitFor();
            System.out.println("Exited with error code " + pr.exitValue());
            return builder.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return null;
    }
  public static String getRawSLA(String curl) {

        final String curlParams = curl;//"curl \"https://www.osupportweb.com/xhr/search/?type=issue&page=1&view_id=acompli_group_view_20150123230705308-a6044ad4d68fc84&view_type=group_view&is_mentions_view=false\" -H \"Cookie: __hs=MSZsWKRnlX5VUPGIZVpUuhY7cGJBaAEugQ6P4F4zTJIdlvNfhIgNCUv0qqCGMlpIyiXxbCHiDQsTeY5k6jsh2IyITbZekVYTL6KJ\"%\"2FIbIV8PBYGJORthX3saEuCYDHeGtgoAnn02oHf\"%\"2F3pSoITKx30vjEAhwnzmL4sg\"%\"2BzEDzp4HaWJRqAdirZ\"%\"2Bzf\"%\"2FPJZi2nRhd2CLvwMMKH4ZAwGZQExTfsypMxSss3LyaYeNPsf\"%\"2BU\"%\"2Fs\"%\"3D--\"%\"2BKT5XWK7MdDfHH\"%\"2FH7bLq3cSYw7Fo4OPKs5fRtdi0Cuo\"%\"3D; _dc_gtm_UA-33692972-1=1; _gat_UA-33692972-1=1; _ga=GA1.2.1195473413.1458559949; _csrf_token=QIURlQ3_YMoOzIOj3hhRRHMy-PytgAM1np0Sjm1aICo\" -H \"Accept-Encoding: gzip, deflate, sdch\\\" -H \\\"Accept-Language: en-US,en;q=0.8,pt-BR;q=0.6,pt;q=0.4,da;q=0.2\\\" -H \\\"User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36\\\" -H \\\"Accept: */*\\\" -H \\\"Referer: https://www.osupportweb.com/admin/issues/\" -H \"X-Requested-With: XMLHttpRequest\" -H \"Connection: keep-alive\" --compressed";
        try {
            System.out.println(curlParams);
            Runtime rt = Runtime.getRuntime();
            String cmdString;
            if(!(curl.charAt(0)=='c'))
                cmdString = curl.substring(1);
            else
                cmdString = curl;
            System.out.println(cmdString);
            Process pr = rt.exec(cmdString);

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = input.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            pr.waitFor();
            System.out.println("Exited with error code " + pr.exitValue());
            return builder.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return null;
    }
    
    
    
}
