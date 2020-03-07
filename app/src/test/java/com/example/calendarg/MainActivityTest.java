package com.example.calendarg;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MainActivityTest {
    static int i = 1;

    @Before
    public void setUp(){
        System.out.println("Testcase: "+i+" inititiated");
        i+=1;
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void isdatepositive() {
        MainActivity obj = new MainActivity();
        Boolean temp = obj.isdate("2019-29-30");
        assertEquals("Test Case Failed",Boolean.TRUE, temp);
    }
    @Test
    public void isdatenegative() {
        MainActivity obj = new MainActivity();
        Boolean temp = obj.isdate("");
        assertEquals("Test Case Failed",Boolean.FALSE, temp);
        Boolean temp1 = obj.isdate("abcd");
        assertEquals("Test Case Failed",Boolean.FALSE, temp);
    }


    @After
    public void tearDown(){
        System.out.println("Testcase: "+(i-1)+" compiled ");
    }
}