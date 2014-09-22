/*
 * Created on Aug 13, 2007 by pladd
 *
 */
package com.bottinifuel.ADD_Cust_Import;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import com.bottinifuel.ADD_Cust_Import.Tank.FuelType;

/**
 * @author pladd
 *
 */
public class Account
{
    public enum CType {
        RESIDENTIAL(1),
        COMMERCIAL(2),
        FARM(3),
        MUNI(4);
        
        public final int CodeNum;
        CType(int code) { CodeNum = code; }
    }

    public final String AcctNum;
    
    private String   SortCode;
    private String   Name;
    private String   Street1;
    private String   Street2;
    private String   TownState;
    private int      Zip;
    private int      AreaCode;
    private int      TelExch;
    private int      TelNum;
    private CType    CustType;
    private Calendar LastPaymentDate;
    private int      County;
    private String   GeneralText;

    private Vector<Tank>    Tanks       = new Vector<Tank>();
    private Vector<Service> ServiceLocs = new Vector<Service>();
    
    public void ValidateData() throws Exception
    {
        if (AcctNum.length() > 10)
            throw new Exception("AcctNum exceeds 10 characters");
        if (Name.length() > 30)
            throw new Exception("Name exceeds 30 characters");
        if (SortCode.length() > 6)
            throw new Exception("SortCode exceeds 6 characters");
        if (Street1.length() > 30)
            throw new Exception("Street1 exceeds 30 characters");
        if (Street2.length() > 28)
            throw new Exception("Street2 exceeds 28 characters");
        if (TownState.length() > 22)
        {
            System.err.println(AcctNum + ": TownState truncated to 22 characters");
            TownState = TownState.substring(0, 22);
        }
        if ((Name.trim().length() +
             Street1.trim().length() + Street2.trim().length() +
             TownState.trim().length())
            > 87)
            throw new Exception("Name/Street/TownState fields total more than 87 characters");

        if ( Zip < 0 ||
                Zip > 999999999 ||
               (Zip < 9999999 && Zip > 99999)) 
               throw new Exception("Zip must be non-negative and either 5 or 9 digits");
        if (AreaCode < 0 || AreaCode > 999)
            throw new Exception("Area Code must be non-negative and 3 digits");
        if (TelExch < 0 || TelExch > 999)
            throw new Exception("Telephone exchange must be non-negative and 3 digits");
        if (TelNum < 0 || TelNum > 9999)
            throw new Exception("Telephone number must be non-negative and 4 digits");
        
        if (LastPaymentDate == null)
            throw new Exception("Last payment date not set");
        
        if (CustType == null)
            throw new Exception("CustType not set");
    }
    
    public Account(String acctNum)
    {
        AcctNum = acctNum;
    }
    
    public void AddTank(Tank t)
    {
        Tanks.add(t);
    }
    public void AddServiceLoc(Service s)
    {
        ServiceLocs.add(s);
    }
    
    
    public String getSortCode()
    {
        return SortCode;
    }

    public void setSortCode(String sortCode)
    {
        SortCode = sortCode;
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

    public String getStreet2()
    {
        return Street2;
    }

    public void setStreet2(String street2)
    {
        Street2 = street2;
    }

    public String getTownState()
    {
        return TownState;
    }

    public void setTownState(String townState)
    {
        TownState = townState;
    }

    public void setTownState(String town, String state) throws Exception
    {
        if (state.length() > 2)
            throw new Exception("State exceeds 2 characters");
        TownState = "|" + town + "|" + state;
    }

    public int getZip()
    {
        return Zip;
    }

    public void setZip(int zip)
    {
        Zip = zip;
    }

    public int getAreaCode()
    {
        return AreaCode;
    }

    public int getTelExch()
    {
        return TelExch;
    }

    public int getTelNum()
    {
        return TelNum;
    }

    public void setTelNum(int areaCode, int exch, int telNum)
    {
        AreaCode = areaCode;
        TelExch = exch;
        TelNum = telNum;
    }
    public CType getCustType()
    {
        return CustType;
    }

    public void setCustType(CType type)
    {
        CustType = type;
    }

    public Calendar getLastPaymentDate()
    {
        return LastPaymentDate;
    }

    public void setLastPaymentDate(Calendar lastPaymentDate)
    {
        LastPaymentDate = lastPaymentDate;
    }

    public int getCounty()
    {
        return County;
    }

    public void setCounty(int county)
    {
        County = county;
    }

    public void setAreaCode(int areaCode)
    {
        AreaCode = areaCode;
    }

    public void setTelExch(int telExch)
    {
        TelExch = telExch;
    }

    public void setTelNum(int telNum)
    {
        TelNum = telNum;
    }

    public void WriteAccount(PrintStream out) throws Exception
    {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");

        ValidateData();
        
        Trailer.AddAcct();
        Trailer.AddBalanceCents(0);

        // RECORD ID 01
        if (Zip < 100000)
            out.printf("01%10s%-6s%-30s%-30s%-28s%-22s%05d0000\n",
                       AcctNum, SortCode, Name, Street1, Street2, TownState, Zip);
        else
            out.printf("01%10s%-6s%-30s%-30s%-28s%-22s%09d\n",
                       AcctNum, SortCode, Name, Street1, Street2, TownState, Zip);
        
        // RECORD ID 02
        out.printf("02%10s%3d%3d%4d%2s%02d%02d%05d%1d%s%s%s" +
                   "%02d%02d%08d%08d" +  // budget fields
                   "%08d%08d%08d%08d%08d%08d%08d%08d%08d" + // balance & payment fields
                   "%1s%05d%04d%08d%08d" + // codes, BDB, BAJ
                   "%05d%05d%05d%07d%09d\n", // groups, BSY, ext bal & credit
                   AcctNum, AreaCode, TelExch, TelNum,
                   "DF",       // Salesman
                   7,          // Division
                   CustType.CodeNum,
                   75,         // Credit Dollar Line 
                   2,          // Credit Time Code
                   "07/24/07", // Acquisition Date
                   df.format(LastPaymentDate.getTime()),
                   "01/01/60", // Last Statement Date
                   0,          // Budget Start Month
                   0,          // Num buget payments
                   0,          // BPA
                   0,          // Budget paid
                   0,          // Last Payment $
                   0, 0, 0, 0, 0, 0, // Balance / Current / 30+ / 60+ / 90+ / 120+
                   0, 0,       // Current Acq bal, Starting Acq Balance
                   "N",        // Govt compliance charge
                   0,          // Acq Company Code
                   0,          // Statement message code
                   0,          // BDB
                   0,          // BAJ
                   0, 0,       // Cust group 1 & 2
                   0,          // Budget start year
                   75,         // Extended credit line
                   0           // Extended balance
                   );
        
        // RECORD ID 03
        out.printf("03%10s%1s%1s %1s%1s%1s%1s%1s%1s%03d%1s%02d%08d%1d%08d%-250s\n",
                   AcctNum,
                   "N",  // Del Hold
                   "N",  // Svc Hold
                   "Y",  // SCB
                   "N",  // Instant Invoice
                   "N",  // Keyoff
                   "N",  // no dunning allowed
                   "N",  // terminated
                   "N",  // instant statement
                   0,    // OSD
                   "Y",  // Finance charge
                   0,    // "       "      days
                   0,    // "       "      amt pending
                   1,    // Premium billing code - cents per gal
                   5,    // Premium billing base - 5 cents
                   "OAC:" + AcctNum + "/" + GeneralText // General text
                   );
        
        // RECORD ID 10
        out.printf("10%10s" +
                   "%1d%1d%1d%1d%1d%1d%1d%1d%1d%1d%1d%1d" + // aging mask
                   "%05d%02d%8s%8s%1s%1s%1s%1s%1s%1d%02d%03d%1s " + // flags etc
                   "%02d%04d%08d%08d%8s%08d" + // info after filler 
                   "%02d%02d%02d%02d%02d%02d%02d" + // tax exempt reasons
                   "%04d%02d%8s%1d" + // stuff
                   "%1s%1s%1s%1s%1s%1s" + // AT2 - AT8
                   "%03d%02d%04d%02d%06d%8s%8s%02d%08d%04d%04d%1s\n", // rest of stuff
                   
                   AcctNum,
                   
                   0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // Aging mask

                   0,          // PBT Premium billing cents
                   0,          // NIN # invoices
                   "01/01/60", // CAD Credit action date
                   "01/01/60", // RED Reminder date
                   "N",        // ISH Invoice special handling
                   "Y",        // STM Statement flag
                   "N",        // ZBS
                   "Y",        // AGS
                   "N",        // IVO
                   0,          // CRL
                   0,          // CAC
                   0,          // FGR
                   "N",        // AT1
                   
                   1,          // CAT "1 = REGULAR"
                   0,          // PRA Promised $
                   0,          // YTS YTD Sales
                   0,          // YTG YTD Gals
                   "01/01/60", // DPD Last deposit
                   0,          // DPA Deposit amt
                   
                   0, 0, 0, 0, 0, 0, 0, // tax exempt reasons
                   
                   0,          // TTD Tax to date
                   0,          // ADP
                   "01/01/60", // ISD Installment start
                   0,          // NST # stat
                   
                   "N", "N", "N", "N", "N", "N", // AT2 - AT8
                   
                   County,     // ACY Account county
                   0,          // CMM Commission months
                   50,         // COL Collector #
                   0,          // NRC # ret checks
                   0,          // OAD Overage limit $
                   "01/01/60", // LDU Last dunning
                   "01/01/60", // LHO Last hold
                   0,          // CCF Company Code for Forms
                   0,          // OIN On installment $
                   0,          // ASY Sales type
                   0,          // ADE Dept code
                   "E"         // LNG Language "E" english
                   );
        int i;
        for (i = 0; i < Tanks.size() && i < ServiceLocs.size(); i++)
        {
            if (i < Tanks.size())
            {
                Tank t = Tanks.get(i);
                t.ValidateData();
                t.WriteTank(out);
            }
            if (i < ServiceLocs.size())
            {
                Service s = ServiceLocs.get(i);
                s.ValidateData();
                s.WriteService(out);
            }
        }
        
        for (; i < Tanks.size(); i++)
        {
            Tank t = Tanks.get(i);
            t.ValidateData();
            t.WriteTank(out);
        }
        for (; i < ServiceLocs.size(); i++)
        {
            Service s = ServiceLocs.get(i);
            s.ValidateData();
            s.WriteService(out);
        }
    }
    
    public static void main(String[] args)
    {
        try {
            FileOutputStream os = new FileOutputStream("C:\\Documents and Settings\\pladd\\Desktop\\addout.txt");
            PrintStream out = new PrintStream(os); 

            Account pml1 = new Account("D72125");
            pml1.setSortCode("LADD");
            pml1.setName("Patrick M. Ladd");
            pml1.setStreet1("21 Beatty Rd");
            pml1.setStreet2(""); 
            pml1.setTownState("Wappingers Falls, NY");
            pml1.setZip(12590);
            pml1.setTelNum(845, 297, 5580);
            pml1.setCustType(CType.RESIDENTIAL);
            pml1.setCounty(3);
            Calendar c1 = Calendar.getInstance();
            c1.set(2007, 2, 2);
            pml1.setLastPaymentDate(c1);

            Tank tpml11 = new Tank("D72125", "D72125", 1);
            tpml11.setName("Patrick M. Ladd");
            tpml11.setStreet1("21 Beatty Rd");
            tpml11.setDelRefInfo(""); 
            tpml11.setTownStateZip("Wappingers Falls, NY 12590");
            tpml11.setCounty(2);
            tpml11.setDegreeDayLast(2222);
            tpml11.setDegreeDayNext(3333);
            tpml11.setDelInstr("Delivery instruction");
            tpml11.setDelRefInfo("Del ref info");
            tpml11.setHotWaterGallons(30);
            tpml11.setK_Factor(2.5);
            tpml11.setLastDelDate(c1);
            tpml11.setNextDelDate(c1);
            tpml11.setProduct(FuelType.TWO_OIL);
            tpml11.setTankSize(275, false);
            tpml11.setUsable(256);
            tpml11.setReserve(72);
            tpml11.setWillCall(false);
            tpml11.ValidateData();

            Tank tpml12 = new Tank("D72125", "D72", 2);
            tpml12.setName("", "Patrick", "M", "Ladd", "");
            tpml12.setStreet1("21 Beatty Rd");
            tpml12.setDelRefInfo(""); 
            tpml12.setTown("Wappingers Falls");
            tpml12.setState("NY");
            tpml12.setZip(12590);
            tpml12.setCounty(2);
            tpml12.setDegreeDayLast(2222);
            tpml12.setDegreeDayNext(3333);
            tpml12.setDelInstr("Delivery instruction");
            tpml12.setDelRefInfo("Del ref info");
            tpml12.setHotWaterGallons(30);
            tpml12.setK_Factor(2.5);
            tpml12.setLastDelDate(c1);
            tpml12.setNextDelDate(c1);
            tpml12.setProduct(FuelType.TWO_OIL);
            tpml12.setTankSize(275, false);
            tpml12.setUsable(256);
            tpml12.setReserve(72);
            tpml12.setWillCall(false);
            tpml12.ValidateData();

            pml1.AddTank(tpml11);
            pml1.AddTank(tpml12);
            pml1.ValidateData();
            pml1.WriteAccount(out);

            Account pml2 = new Account("D72124");
            pml2.setSortCode("LADD");
            pml2.setName("", "Patrick", "M", "Ladd", "");
            pml2.setStreet1("21 Beatty Rd");
            pml2.setStreet2(""); 
            pml2.setTownState("Wappingers Falls", "NY");
            pml2.setZip(125903678);
            pml2.setTelNum(845, 297, 5580);
            pml2.setCustType(CType.COMMERCIAL);
            pml2.setCounty(3);
            Calendar c2 = Calendar.getInstance();
            c2.set(2007, 5, 25);
            pml2.setLastPaymentDate(c2);

            Tank tpml21 = new Tank("D72124", "D72124", 1);
            tpml21.setName("Patrick M. Ladd");
            tpml21.setStreet1("21 Beatty Rd");
            tpml21.setDelRefInfo(""); 
            tpml21.setTownStateZip("Wappingers Falls, NY 125903678");
            tpml21.setCounty(2);
            tpml21.setDegreeDayLast(2222);
            tpml21.setDegreeDayNext(3333);
            tpml21.setDelInstr("Delivery instruction");
            tpml21.setDelRefInfo("Del ref info");
            tpml21.setHotWaterGallons(30);
            tpml21.setK_Factor(2.5);
            tpml21.setLastDelDate(c1);
            tpml21.setNextDelDate(c1);
            tpml21.setProduct(FuelType.TWO_OIL);
            tpml21.setTankSize(275, false);
            tpml21.setUsable(256);
            tpml21.setReserve(72);
            tpml21.setWillCall(false);
            tpml21.ValidateData();

            Tank tpml22 = new Tank("D72124", "D999", 2);
            tpml22.setName("", "Patrick", "M", "Ladd", "");
            tpml22.setStreet1("21 Beatty Rd");
            tpml22.setDelRefInfo(""); 
            tpml22.setTown("Wappingers Falls");
            tpml22.setState("NY");
            tpml22.setZip(125903678);
            tpml22.setCounty(2);
            tpml22.setDegreeDayLast(2222);
            tpml22.setDegreeDayNext(3333);
            tpml22.setDelInstr("Delivery instruction");
            tpml22.setDelRefInfo("Del ref info");
            tpml22.setHotWaterGallons(30);
            tpml22.setK_Factor(2.5);
            tpml22.setLastDelDate(c1);
            tpml22.setNextDelDate(c1);
            tpml22.setProduct(FuelType.TWO_OIL);
            tpml22.setTankSize(275, false);
            tpml22.setUsable(256);
            tpml22.setReserve(72);
            tpml22.setWillCall(false);
            tpml22.ValidateData();
            
            pml2.AddTank(tpml21);
            pml2.AddTank(tpml22);
            pml2.ValidateData();
            pml2.WriteAccount(out);
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }
    
    public String toString()
    {
        return AcctNum;
    }

    public String getGeneralText()
    {
        return GeneralText;
    }

    public void setGeneralText(String generalText)
    {
        GeneralText = generalText;
    }
}
