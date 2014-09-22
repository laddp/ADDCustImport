/*
 * Created on Aug 26, 2007 by Administrator
 *
 */
package com.bottinifuel.ADD_Cust_Import;

import java.io.PrintStream;

/**
 * @author Administrator
 *
 */
public class Trailer
{
    static private int AcctCount = 0;
    static private int TankCount = 0;
    static private int SvcCount  = 0;
    static private int WDMSCount = 0;
    static private int BalanceCents;

    static public void WriteTrailer(PrintStream out)
    {
        out.printf("99%06d%10d%06d%06d%06d\n",
                   AcctCount,
                   BalanceCents,
                   TankCount,
                   SvcCount,
                   WDMSCount
                   );
    }
    
    static public void AddAcct() { AcctCount++; }
    static public void AddTank() { TankCount++; }
    static public void AddSvc () { SvcCount ++; }
    static public void AddWDMS() { WDMSCount++; }

    static public void AddBalanceCents(int cents) { BalanceCents += cents; }
}
