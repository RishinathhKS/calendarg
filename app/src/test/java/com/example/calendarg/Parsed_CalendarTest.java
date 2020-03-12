package com.example.calendarg;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class Parsed_CalendarTest {
    static int i = 1;

    @Before
    public void setUp(){
        System.out.println("Testcase: "+i+" inititiated");
        i+=1;
    }

    @Test
    public void isnumpositive() {
        parsed_calendar obj = new parsed_calendar();
        Boolean temp = obj.isnum("23");
        assertEquals("Test Case Failed",Boolean.TRUE, temp);
        Boolean temp1 = obj.isnum("12");
        assertEquals("Test Case Failed",Boolean.TRUE, temp1);
        Boolean temp2 = obj.isnum("01");
        assertEquals("Test Case Failed",Boolean.TRUE, temp2);
        Boolean temp3 = obj.isnum("023");
        assertEquals("Test Case Failed",Boolean.TRUE, temp3);

    }
    @Test
    public void isnumnegative() {
        parsed_calendar obj = new parsed_calendar();
        Boolean temp = obj.isnum("23ab");
        assertEquals("Test Case Failed",Boolean.FALSE, temp);
        Boolean temp1 = obj.isnum("");
        assertEquals("Test Case Failed",Boolean.FALSE, temp1);
        Boolean temp2 = obj.isnum("");
        assertEquals("Test Case Failed",Boolean.FALSE, temp2);
        Boolean temp3 = obj.isnum("0230");
        assertEquals("Test Case Failed",Boolean.FALSE, temp3);
    }
    @Test
    public void ismonpositive() {
        parsed_calendar obj = new parsed_calendar();
        Boolean temp = obj.ismon("january");
        assertEquals("Test Case Failed",Boolean.TRUE, temp);
        Boolean temp1 = obj.ismon("MAR");
        assertEquals("Test Case Failed",Boolean.TRUE, temp1);
        Boolean temp2 = obj.ismon("decem");
        assertEquals("Test Case Failed",Boolean.TRUE, temp2);
        Boolean temp3 = obj.ismon("Feb");
        assertEquals("Test Case Failed",Boolean.TRUE, temp3);
        Boolean temp4 = obj.ismon("APRil");
        assertEquals("Test Case Failed",Boolean.TRUE, temp4);
        Boolean temp5 = obj.ismon("DeCeMb");
        assertEquals("Test Case Failed",Boolean.TRUE, temp4);


    }

    @Test
    public void ismonnegative() {
        parsed_calendar obj = new parsed_calendar();
        Boolean temp = obj.ismon("jack");
        assertEquals("Test Case Failed",Boolean.FALSE, temp);
        Boolean temp1 = obj.ismon("");
        assertEquals("Test Case Failed",Boolean.FALSE, temp1);
        Boolean temp2 = obj.ismon("ju");
        assertEquals("Test Case Failed",Boolean.FALSE, temp2);
        Boolean temp3 = obj.ismon("m");
        assertEquals("Test Case Failed",Boolean.FALSE, temp3);
        Boolean temp4 = obj.ismon("fsanajdsg");
        assertEquals("Test Case Failed",Boolean.FALSE, temp4);
    }

    @Test
    public void monthvalpositive() {
        parsed_calendar obj = new parsed_calendar();
        String temp = obj.monthval("march");
        assertEquals("Test Case Failed","-03", temp);
        String temp1 = obj.monthval("november");
        assertEquals("Test Case Failed","-11-", temp1);
    }

    @Test
    public void monvalnegative() {
        parsed_calendar obj = new parsed_calendar();
        String temp = obj.monthval("notamonth");
        assertEquals("Test Case Failed"," ", temp);
        String temp1 = obj.monthval("");
        assertEquals("Test Case Failed"," ", temp1);
    }


    @After
    public void tearDown(){
        System.out.println("Testcase: "+(i-1)+" compiled ");
    }
}
