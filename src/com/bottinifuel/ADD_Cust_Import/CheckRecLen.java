/*
 * Created on Aug 26, 2007 by Administrator
 *
 */
package com.bottinifuel.ADD_Cust_Import;

import java.io.FileReader;
import java.io.LineNumberReader;

/**
 * @author Administrator
 *
 */
public class CheckRecLen
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            LineNumberReader r = 
                new LineNumberReader(new FileReader("W:\\Patrick\\Dunn\\add.txt"));

            String line = r.readLine();
            while (line != null)
            {
                int rType = Integer.valueOf(line.substring(0,2));
                int length = line.length();
                int expected = 0;
                switch (rType)
                {
                case 1:  expected = 137; break;
                case 2:  expected = 207; break;
                case 3:  expected = 294; break;
                case 10: expected = 185; break;
                
                case 4:  expected = 230; break;
                case 5:  expected = 283; break;
                case 6:  expected = 255; break;
                case 11: expected = 206; break;
                
                case 7:  expected = 176; break;
                case 8:  expected = 175; break;

                case 99: expected =  36; break;
                default:
                }
                
                if (length != expected)
                {
                    System.out.println("Line: " + r.getLineNumber() + 
                                       " Type: " + rType +
                                       " Expected: " + expected +
                                       " Got: " + length);
                }
                
                line = r.readLine();
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

}
