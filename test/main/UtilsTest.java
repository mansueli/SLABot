/*
 * Copyright 2016 Rodrigo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package main;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sla.Tracker;
import static sla.Tracker.daysBetweenFirstPST;

/**
 *
 * @author Rodrigo
 */
public class UtilsTest {
    
    public UtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
    }

    /**
     * Test of CreateTrackerFile method, of class Utils.
     */
    @Test
    public void testCreateTrackerFile() throws Exception {
        System.out.println("CreateTrackerFile");
        String folderPath = "C:\\Users\\Rodrigo\\Desktop";
        Tracker.CreateTrackerFile(folderPath);
        
    }
    @Test
    public void testGetCell() throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 22, 06);
        System.out.println("resultado = " + Tracker.getCell(cal));
        int diff = daysBetweenFirstPST(cal);
        System.out.println(diff);
        
        System.out.println("diff dates =>> " + daysBetweenFirstPSTdates(cal.getTime(), Tracker.getPSTTime()));
        System.out.println(cal.getTime());
    }
    
        public static int daysBetweenFirstPSTdates(Date d1, Date d2) {
        Calendar dayOne = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        dayOne.setTime(d1);
        Calendar dayTwo = Calendar.getInstance();
        dayTwo.setTime(d2);
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
}
