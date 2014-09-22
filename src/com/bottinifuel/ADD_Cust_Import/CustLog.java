/*
 * Created on Aug 27, 2007 by pladd
 *
 */
package com.bottinifuel.ADD_Cust_Import;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

/**
 * @author pladd
 *
 */
public class CustLog
{
    public final String   Acct;
    public final Calendar Date;

    Vector<String> Lines = new java.util.Vector<String>();
    
    public CustLog(String acct, Calendar date)
    {
        Acct = acct;
        Date = date;
    }
    
    public void WriteLog(PrintStream out, int noteSeqNum)
    {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        for (String line : Lines)
        {
            out.printf("%10s%04d%8s%-132s\n",
                       Acct,
                       noteSeqNum,
                       df.format(Date.getTime()),
                       line
                      );
        }
    }
    
    public void addLine(String l)
    {
        int remaining = l.length();
        int offset = 0;
        while (remaining > 132)
        {
            Lines.add(l.substring(offset, 132));
            offset += 132;
            remaining -= 132;
        }
        Lines.add(l.substring(offset, l.length()));
    }
}
