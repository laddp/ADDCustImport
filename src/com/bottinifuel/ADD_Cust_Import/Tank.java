/*
 * Created on Aug 13, 2007 by pladd
 *
 */
package com.bottinifuel.ADD_Cust_Import;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author pladd
 *
 */
public class Tank
{
    
    public enum FuelType {
        TWO_OIL(2),
        DYED_KERO(9),
        WINTER_BLEND(11),
        DYED_DIESEL(19),
        CLEAR_DIESEL(20),
        NOTAX_DIESEL(21);
        
        public final int CodeNum;
        FuelType(int code) { CodeNum = code; }
    }

    public final String AcctNum;
    public final String OrigAcctNum;
    public final int    TankNum;

    private String   Name;
    private String   Street1;
    private String   DelRefInfo;
    private String   TownStateZip;
    private String   Town;
    private String   State;
    private int      Zip;
    private String   DelInstr;
    
    private int      TankSize;
    private int      Usable;
    private int      Reserve;

    private FuelType Product;
    
    private boolean  WillCall;
    private boolean  Period = false;

    private double   K_Factor;
    private int      DegreeDayNext;
    private int      DegreeDayLast;
    
    private int      DBD;
    private Calendar NextDelDate;

    private boolean  SeparateHotWater;
    private int      HotWaterGallons;

    private int      County;
    private boolean  Commercial = false;

    private Calendar LastDelDate;
    private double   LastDelGal;
    private boolean  LastDelPartial;

    public void ValidateData() throws Exception
    {
        if (AcctNum.length() > 10)
            throw new Exception("AcctNum exceeds 10 characters");
        if (OrigAcctNum.length() > 10)
            throw new Exception("OrigAcctNum exceeds 10 characters");
        if (Name.length() > 30)
            throw new Exception("Name exceeds 30 characters");
        if (Street1.length() > 30)
            throw new Exception("Street1 exceeds 30 characters");
        if (DelRefInfo.length() > 30)
            throw new Exception("Street2 exceeds 30 characters");
        if (TownStateZip != null && TownStateZip.length() > 30)
            throw new Exception("TownStateZip exceeds 30 characters");
        if (Town != null && (Town.length() + State.length() + 9 + 3) > 30)
            throw new Exception("TownStateZip exceeds 30 characters");
        if ( Zip < 0 ||
             Zip > 999999999 ||
            (Zip < 99999999 && Zip > 99999)) 
               throw new Exception("Zip must be non-negative and either 5 or 9 digits");
    }


    public Tank(String acctNum, String origAcctNum, int tankNum)
    {
        AcctNum = acctNum;
        OrigAcctNum = origAcctNum;
        TankNum = tankNum;
    }


    public void WriteTank(PrintStream out)
    {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        
        Trailer.AddTank();
        // RECORD ID 04
        out.printf("04%10s%03d%05d%05d%05d%-6s%-4s       " +
                   "%1s%02d%03d%08d%1s%1s",
                   AcctNum, TankNum,
                   TankSize,          // SIZ Tank size
                   Usable,            // USE Usable gal
                   Reserve,           // RES Reserve gal
                   "U555",            // ZON Delivery Zone
                   "????",            // LOC Fill Location

                   "N",               // MPR Pull related
                   Product.CodeNum,   // PRO Product code
                   4,                 // BPC Base price code
                   0,                 // DEV Deviation
                   "N",               // TIC Ticket out
                   (WillCall)?"Y":"N" // WCA Will call
                   );

        if (Period)
        {
            if (DBD > 99)
                out.printf("Y%04d", 0);
            else
                out.printf("Y%04d", DBD * 100);
        }
        else
            out.printf("N%04.0f",
                       K_Factor*100);
        
        out.printf("%04d%04d%05d%8s%1s%1s" +
                   "%1s%1s%1s%1s%1s%1s%1s%1s" + // tax flags 1-5, ENU, tax 7-8
                   "%1s%03d%8s%06d%10s%03d%03d" +
                   "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + // tax flags 9-40
                   "%04d%2s%1s%1s%05d%05d%-25s%1s" +
                   "%03d%05d%05d%05d%1s%1s%1s\n",
                   0,               // LK1 Prev K
                   HotWaterGallons, // HWG Hot water gallons
                   DegreeDayNext,   // DDN Degree Day Next
                   (NextDelDate==null)?"01/01/60":df.format(NextDelDate.getTime()), // NDD Next Deliv Date
                   SeparateHotWater?"Y":"N", // HWH Separate hot water
                   "N",             // KLK K Factor lock
                   
                   "N", "N", "N", "N", "N", "N", "N", "N",
                   
                   (Commercial)?"Y":"N", // COM Commercial acct
                   County,           // TCY Tank county
                   df.format(LastDelDate.getTime()), // LDD Last del date
                   (int)LastDelGal,  // GLD Last del gallons
                   OrigAcctNum,      // OAC Original account num
                   (DBD > 99)?DBD:0, // DBD Alternate DBD > 99
                   0,                // TCA Tank category of use
                   
                   0,    // SEQ Sequence #
                   "DF", // DSA Delivery salesperson
                   "N",  // VHW Var hot water 
                   "N",  // DOW Day of week
                   0,    // ITP ITS Prod #
                   0,    // ITQ ITS Qty
                   "",   // PON PO#
                   "N",  // DAP Don't auto price
                   
                   100,  // PCF %full last del
                   0,    // DGP Delivery group
                   0,    // TC1 Tank Cat 1
                   0,    // TC2 Tank Cat 2
                   "N",  // PUB Pub price
                   "N",  // PRQ Purch ord required
                   "N"   // SRQ Signature Required
                   );
        
        // RECORD ID 05
        if (Town != null)
            out.printf("05%10s%03d%-30s%-30s%-30s%-30s%09d %02d%-34s%-34s%-34s%-34s\n",
                       AcctNum,
                       TankNum,
                       Name,
                       "|" + Street1,
                       "|" + DelRefInfo, 
                       "|" + Town + "|" + State + "|" + Zip,
                       Zip,
                       0,
                       "", "", "", "");
        else
            out.printf("05%10s%03d%-30s%-30s%-30s%-30s%09d %02d%-34s%-34s%-34s%-34s\n",
                       AcctNum,
                       TankNum,
                       Name,
                       "|" + Street1,
                       "|" + DelRefInfo,
                       "|" + TownStateZip,
                       Zip,
                       0,
                       "", "", "", "");
        
        // RECORD ID 06
        out.printf("06%10s%03d%-240s\n", AcctNum, TankNum, DelInstr);
        
        // RECORD ID 11
        out.printf("11%10s%03d%1s%1s%1s%1s%1s%1s%04d%04d" +
                   "%03d%05d%06d%06d%2s%8s%08d%8s%8s" +
                   "%05d%03d%03d%05d%03d%03d%05d%03d%03d%05d%03d%03d%05d%03d%03d%05d%03d%03d" + // size/company/cust owned 1-6
                   "%1s%1s%06d%02d%03d%8s%1s%02d%8s%04d%04d%04d%05d%08d\n",
                   AcctNum, TankNum,
                   "N",  // MPP Must pull phone
                   "N",  // DPT Don't print tick
                   "N",  // RTB Route table
                   "N",  // PCN Price change notice
                   "N",  // SDB Snow drop
                   LastDelPartial?"Y":"N", // LDP Last deliv partial
                   0,    // LK2 Last K #2
                   0,    // LK3 Last K #3
                   
                   0,    // PCT Sales tax exempt %
                   0,    // PPR Plan price
                   0,    // PGR Plan gal remain
                   0,    // LTI Last Tick #
                   " 0", // BSL Base Sub-level
                   "01/01/60", // CDD Current dev date
                   0,    // PDV Prior deviation
                   "01/01/60", // PDD Prior dev date
                   "",   // PTC Property tax code
                   
                   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                   
                   "Y",  // PPU Print price per unit on invo/stat
                   "Y",  // PGP Print gallons/ppg on invo/stat
                   0,    // URD Usage rate/day
                   2,    // DDT Degree day table
                   0,    // DST Delivery stop code
                   "01/01/60", // DRD Delivery reinstate date
                   "N",  // TKS Ticket status
                   0,    // TRC Tax reason code
                   "01/01/60", // MBS Minimum billing start
                   0,    // MBT Miniumum billing table
                   0,    // TSY Sales type
                   0,    // TDE Dept code
                   0,    // TID Tank term id
                   0     // RSD Remote site dev
                   );
    }

    
    public String toString()
    {
        return AcctNum + ", " + OrigAcctNum + ", T#" + TankNum;
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


    public int getZip()
    {
        return Zip;
    }


    public void setZip(int zip)
    {
        Zip = zip;
    }


    public String getTownStateZip()
    {
        return TownStateZip;
    }


    public void setTownStateZip(String townStateZip)
    {
        TownStateZip = townStateZip;
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


    public String getDelRefInfo()
    {
        return DelRefInfo;
    }


    public void setDelRefInfo(String delRefInfo)
    {
        DelRefInfo = delRefInfo;
    }


    public String getDelInstr()
    {
        return DelInstr;
    }


    public void setDelInstr(String delInstr)
    {
        DelInstr = delInstr;
    }


    public int getTankSize()
    {
        return TankSize;
    }


    public void setTankSize(int tankSize, boolean defaultRest)
    {
        TankSize = tankSize;
        
        if (defaultRest)
        {
            switch (tankSize)
            {
            case  275: Usable =  256;  Reserve =    72; break;
            case  330: Usable =  307;  Reserve =    86; break;
            case  500: Usable =  465;  Reserve =   130; break;
            case  550: Usable =  512;  Reserve =   143; break;
            case  660: Usable =  614;  Reserve =   172; break;
            case 1000: Usable =  930;  Reserve =   260; break;
            case 2000: Usable = 1860;  Reserve =   521; break;
            case 5000: Usable = 4650;  Reserve =  1302; break;
            default:
                Usable  =(int)(tankSize * 0.93);
                Reserve =(int)(tankSize * 0.26);
                break;
            }
        }
    }


    public int getUsable()
    {
        return Usable;
    }


    public void setUsable(int usable)
    {
        Usable = usable;
    }


    public int getReserve()
    {
        return Reserve;
    }


    public void setReserve(int reserve)
    {
        Reserve = reserve;
    }


    public FuelType getProduct()
    {
        return Product;
    }


    public void setProduct(FuelType product)
    {
        Product = product;
    }


    public boolean isWillCall()
    {
        return WillCall;
    }


    public void setWillCall(boolean willCall)
    {
        WillCall = willCall;
    }


    public boolean isPeriod()
    {
        return Period;
    }


    public void setPeriod(boolean period)
    {
        Period = period;
    }


    public double getK_Factor()
    {
        return K_Factor;
    }


    public void setK_Factor(double factor)
    {
        K_Factor = factor;
    }


    public int getDBD()
    {
        return DBD;
    }


    public void setDBD(int dbd)
    {
        DBD = dbd;
    }


    public int getDegreeDayNext()
    {
        return DegreeDayNext;
    }


    public void setDegreeDayNext(int degreeDayNext)
    {
        DegreeDayNext = degreeDayNext;
    }


    public int getDegreeDayLast()
    {
        return DegreeDayLast;
    }


    public void setDegreeDayLast(int degreeDayLast)
    {
        DegreeDayLast = degreeDayLast;
    }


    public Calendar getNextDelDate()
    {
        return NextDelDate;
    }


    public void setNextDelDate(Calendar nextDelDate)
    {
        NextDelDate = nextDelDate;
    }


    public int getCounty()
    {
        return County;
    }


    public void setCounty(int county)
    {
        County = county;
    }


    public boolean isCommercial()
    {
        return Commercial;
    }


    public void setCommercial(boolean commercial)
    {
        Commercial = commercial;
    }


    public Calendar getLastDelDate()
    {
        return LastDelDate;
    }


    public void setLastDelDate(Calendar lastDelDate)
    {
        LastDelDate = lastDelDate;
    }


    public boolean isSeparateHotWater()
    {
        return SeparateHotWater;
    }


    public void setSeparateHotWater(boolean separateHotWater)
    {
        SeparateHotWater = separateHotWater;
    }


    public int getHotWaterGallons()
    {
        return HotWaterGallons;
    }


    public void setHotWaterGallons(int hotWaterGallons)
    {
        HotWaterGallons = hotWaterGallons;
    }


    public double getLastDelGal()
    {
        return LastDelGal;
    }


    public void setLastDelGal(double lastDelGal)
    {
        LastDelGal = lastDelGal;
    }


    public boolean isLastDelPartial()
    {
        return LastDelPartial;
    }


    public void setLastDelPartial(boolean lastDelPartial)
    {
        LastDelPartial = lastDelPartial;
    }
}
