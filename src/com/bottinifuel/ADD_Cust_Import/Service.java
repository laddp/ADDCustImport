/*
 * Created on Aug 13, 2007 by pladd
 *
 */
package com.bottinifuel.ADD_Cust_Import;

import java.io.PrintStream;

/**
 * @author pladd
 *
 */
public class Service
{
    public final String AcctNum;
    public final String OrigAcctNum;
    public final int    SvcNum;

    private String Name;
    private String Street1;
    private String Town;
    private String State;
    private int    Zip;
    private String Zone;
    private int    County;
    
    private String SvcInstr1;
    private String SvcInstr2;
    
    public Service(String acctNum, String origAcctNum, int svcNum)
    {
        AcctNum = acctNum;
        OrigAcctNum = origAcctNum;
        SvcNum = svcNum;
    }

    public boolean ValidateData()
    {
        return true;
    }
    
    public void WriteService(PrintStream out)
    {
        Trailer.AddSvc();
        // RECORD ID 07
        out.printf("07%10s%03d%2s%8s%08d%8s%1s%1s%03d%-6s" +
                   "%-30s%-30s%-30s%-30s%1s%03d\n",
                   AcctNum, SvcNum,
                   "  ",         // SCT Service contract type
                   "01/01/60",   // SRD Service renewal date
                   0,            // SDV Service deviation
                   "01/01/60",   // LCD Last cleaning date
                   "Y",          // LTX Labor taxable
                   "Y",          // PTX Parts taxable
                   County,       // SCY Service county
                   Zone,         // SZO Service zone

                   "|||||",      // SAD 1
                   "|" + Street1,// SAD 2
                   "|",          // SAD 3
                   "|" + Town + "|" + State + "|" + Zip, // SAD 4
                   "Y",          // CTZ Contract tax
                   1             // SCA Service category
                   );
        
        // RECORD ID 08
        out.printf("08%10s%03d%-40s%-40s%-40s%-40s\n",
                   AcctNum, SvcNum,
                   SvcInstr1,
                   SvcInstr2,
                   "",
                   ""
                   );
        
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }
    public void setName(String title, String firstName, String midInitial, String lastName, String suffix)
    throws Exception
    {
        if (title.length() > 4)
            throw new Exception("Title exceeds 4 characters");
        if (midInitial.length() > 1)
            throw new Exception("midInitial exceeds 1 character");
        if (suffix.length() > 3)
            throw new Exception("Suffix exceeds 3 characters");
        Name = "|" + title + "|" + firstName + "|" + midInitial + "|" + lastName + "|" + suffix;
    }


    public String getStreet1()
    {
        return Street1;
    }

    public void setStreet1(String street1)
    {
        Street1 = street1;
    }

    public int getCounty()
    {
        return County;
    }

    public void setCounty(int county)
    {
        County = county;
    }

    public String getZone()
    {
        return Zone;
    }

    public void setZone(String zone)
    {
        Zone = zone;
    }
    
    public void setZone(int zip)
    {
        switch (zip) {
        case 12401: Zone = "U1KGN"; break;
        case 12404: Zone = "U1ACC"; break;
        case 12419: Zone = "U1CTT"; break;
        case 12420: Zone = "W2CGM"; break;
        case 12428: Zone = "U3ELN"; break;
        case 12435: Zone = "U3GRF"; break;
        case 12440: Zone = "U1HIF"; break;
        case 12443: Zone = "U1HUR"; break;
        case 12446: Zone = "U3KHN"; break;
        case 12458: Zone = "U3NAP"; break;
        case 12461: Zone = "U1OLB"; break;
        case 12466: Zone = "U1PTE"; break;
        case 12472: Zone = "U1RSD"; break;
        case 12483: Zone = "J1SPG"; break;
        case 12484: Zone = "U1STR"; break;
        case 12489: Zone = "U3WAR"; break;
        case 12491: Zone = "U2WSH"; break;
        case 12494: Zone = "U2WSK"; break;
        case 12528: Zone = "U3HIL"; break;
        case 12561: Zone = "U3NPZ"; break;
        case 12740: Zone = "J1GRM"; break;
        case 12789: Zone = "J1WDR"; break;
        default:
            System.err.println("Acct #" + AcctNum +  " no svc zone for zip " + zip);
            Zone = "Z";
            break;
        }
    }

    public String getSvcInstr()
    {
        return SvcInstr1 + SvcInstr2;
    }

    public void setSvcInstr(String svcInstr)
    {
        if (svcInstr.length() > 40)
        {
            SvcInstr1 = svcInstr.substring(0, 40);
            SvcInstr2 = svcInstr.substring(40);
        }
        else
        {
            SvcInstr1 = svcInstr;
            SvcInstr2 = "";
        }
    }

    public String getTown()
    {
        return Town;
    }

    public void setTown(String town)
    {
        Town = town;
    }

    public String getState()
    {
        return State;
    }

    public void setState(String state)
    {
        State = state;
    }

    public int getZip()
    {
        return Zip;
    }

    public void setZip(int zip)
    {
        Zip = zip;
    }
}
