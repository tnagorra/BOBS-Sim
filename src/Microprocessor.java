// ale, Ready, hold, hlda, clk
import java.io.*;

public class Microprocessor extends Thread {
    public boolean active;
    public boolean trapped;

    public boolean iom, read, write;

    // Interrupt masks
    public boolean sod, sid, m7, m6, m5, ie;
    // Interrupts
    public boolean r7, r6, r5, trap, intr, inta;
    public boolean resetin, resetout;

    public Register8 busL, busH;

    private Register16 mar;
    private Register8 mbr;

    public Register8 ir;
    public Register16 pc, sp;

    public Register8[] register;
    public Flag flag;

    // public Memory memory;


    public Register16 restartLocation;

    // Constructor
    public Microprocessor() {
        restartLocation = new Register16(0x0000);

        // Initialize Memory Address Register
        mar = new Register16(0x0000);
        // Initialize Memory Buffer Register
        mbr = new Register8(0x00);

        // Initialize instruction register
        ir = new Register8(0x00);
        // Intialize Program Counter
        pc = new Register16(0x0000);
        // Initialize Stack Pointer
        sp = new Register16(0xFFFF);
        // Initialize flags to zero
        flag = new Flag(0x00);

        busH = new Register8(0x00);
        busL = new Register8(0x00);

        // Initialize 8 registers where only 7 will be used
        register = new Register8[8];
        for(int i=0; i<register.length; i++)
            register[i] = new Register8(0);

        // Some attributes
        resetin = false;
        resetout = false;
        sid = false;
        sod = false;
        r7 = r6 = r5 = trap = intr = false;
        iom = read = write = false;
        inta = m7 = m6 = m5 = false;
    }

    public void run() {
        System.out.println("Microprocessor started!");
        try {
            while( true  ) {
                // Wait for some signal
                Thread.sleep(50);

                if( resetin)
                    resetHandler();

                while (active) {

                    // Initiates the microprocessor / restarts it
                    resetHandler();

                    // For single stepping
                    // singlestep = interactHandler(verbose,singlestep);

                    // TODO debugging
                    // print(true);
                    // Thread.sleep(100);

                    // Execute an instruction
                    execute();

                    // Handler interrupts
                    interruptHandler();

                    // Thread.sleep(1);
                    if(!active && !trap)
                        System.out.println("Microprocessor Halted!");
                }
            }

        } catch (InterruptedException e) {
            System.out.println("Microprocessor Stopped!");
        }
    }


    /*

    // Start the microprocessor operation
    // Jumps if resetin is true
    public void execute(Register16 jmpto, boolean once) throws InterruptedException, IOException {
        do {
            // Initiates the microprocessor / restarts it
            resetHandler(jmpto);

            // For single stepping
            // singlestep = interactHandler(verbose,singlestep);

            // Execute an instruction
            execute();
            // Handler interrupts
            interruptHandler();

            // Thread.sleep(1);
        } while (active && !once);

        // So that microprocessor can run again
        if(!active && !resetin) {
            resetin = true;
            System.out.println("Microprocessor Stopped!");
        }
    }
    */

    private void resetHandler() throws InterruptedException {
        if (resetin) {

            Thread.sleep(10);
            System.out.println("Microprocessor Resetting!");
            resetin = false;
            resetout = true;
            ie = false;
            resetout = false;
            active = true;
            //hlda = false;
            Thread.sleep(10);

            pc.copy(restartLocation);
        }
    }

    private boolean interactHandler(boolean verbose, boolean singlestep) throws IOException, InterruptedException {
        if (singlestep) {
            print(verbose);
            String y = new BufferedReader(new InputStreamReader(System.in)).readLine();
            // quit
            if( y.equals("Q") || y.equals("q") )
                active = false;
            // burst
            else if( y.equals("B") || y.equals("b") )
                singlestep = false;
            // verbose
            else if( y.equals("V") || y.equals("v") )
                verbose = true;
            // silent
            else if( y.equals("S") || y.equals("s") )
                verbose = false;
        }
        return singlestep;
    }

    private void interruptHandler() throws InterruptedException {
        if( trap ) {
            // trap = false;
            active = false;
            // pushPC();
            // pc = new Register16( 0x0024 );
        } else if ( ie ) {
            if ( r7 && !m7 ) {
                r7 = false;
                inta = true;
                pushPC();
                pc = new Register16( 0x003C );
                inta = false;
            } else if( r6 && !m6 ) {
                r6 = false;
                inta = true;
                pushPC();
                pc = new Register16( 0x0034 );
                inta = false;
            } else if( r5 && !m5) {
                r5 = false;
                inta = true;
                pushPC();
                pc = new Register16( 0x002C );
                inta = false;
            } else if( intr )  {
                synchronized(this) {
                    intr = false;
                    inta = true;
                    Register8 h,l;
                    notify();
                    wait();
                    l = busL.clone();
                    notify();
                    wait();
                    h = busL.clone();
                    inta = false;
                    pushPC();
                    pc = new Register16(l,h);
                }
            }
        }
    }

    // The fetch, decode and execute cycle
    private void execute() {
        // Fetch
        ir = getData8FromMemoryPC();

        switch( ir.get(7,6) ) {
        case 0b01:
            // HLT
            if ( ir.get(5,0) == 0b110110 ) {
                active = false;
                trap = false;
            }
            // MOV
            else {
                int DDD = ir.get(5,3);
                int SSS = ir.get(2,0);
                setR(DDD, getR(SSS));
            }
            break;
        case 0b00:
            // MVI
            if (ir.get(2,0) == 0b110) {
                int DDD= ir.get(5,3);
                setR(DDD, getData8FromMemoryPC());
            }
            // LXI
            else if ( ir.get(3,0) == 0b0001 ) {
                int DD = ir.get(5,4);
                if( DD == 0b11) {
                    // for SP
                    sp = getData16FromMemoryPC();
                } else {
                    // for Rp
                    setXL(DD, getData8FromMemoryPC() );
                    setXH(DD, getData8FromMemoryPC() );
                }
            }
            // NOP
            else if( ir.get(5,0) == 0b00000 ) {
                // nothing here
            }
            // LDA
            else if( ir.get(5,0) == 0b111010 ) {
                setA(getData8FromMemory( getData16FromMemoryPC() ));
            }
            // STA
            else if( ir.get(5,0) == 0b110010 ) {
                setData8ToMemory(getData16FromMemoryPC(), getA());
            }
            // LHLD
            else if( ir.get(5,0) == 0b101010) {
                Register16 addr = getData16FromMemoryPC();
                setL(getData8FromMemory(addr));
                Alu.inr(addr);
                setH(getData8FromMemory(addr));
            }
            // SHLD
            else if( ir.get(5,0) == 0b100010) {
                Register16 addr = getData16FromMemoryPC();
                setData8ToMemory( addr, getL());
                Alu.inr(addr);
                setData8ToMemory(addr, getH());
            }
            // LDAX
            else if( ir.get(3,0) == 0b1010 ) {
                // NOTE
                // in LDAX and STAX it can't have Rp other than BC and DE
                // but the previous cases of "if" should be sufficient for validation
                int DDD = ir.get(5,4);
                setA( getData8FromMemory( getXX(DDD) ));
            }
            // STAX
            else if( ir.get(3,0) == 0b0010 ) {
                int DDD = ir.get(5,4);
                setData8ToMemory(getXX(DDD) , getA());
            }
            // INR
            else if( ir.get(2,0) == 0b100 ) {
                int DDD = ir.get(5,3);
                if(DDD == 0b110)
                    register[DDD] = getData8FromMemory(getHL());
                Flag tflag = Alu.inr(register[DDD]);
                if(DDD == 0b110)
                    setData8ToMemory(getHL(), register[DDD] );
                // So that carry flag isn't updated
                tflag.set("C",flag.get("C"));
                flag = tflag;
            }
            // DCR
            else if( ir.get(2,0) == 0b101 ) {
                int DDD = ir.get(5,3);
                if(DDD == 0b110)
                    register[DDD] = getData8FromMemory(getHL());
                Flag tflag = Alu.dcr(register[DDD]);
                if(DDD == 0b110)
                    setData8ToMemory(getHL(), register[DDD] );
                // So that carry flag isn't updated
                tflag.set("C",flag.get("C"));
                flag = tflag;
            }
            // INX
            else if( ir.get(3,0) == 0b0011 ) {
                int DD = ir.get(5,4);
                if( DD == 0b11) {
                    Alu.inr(sp);
                } else {
                    Flag tflag = Alu.inr( getXL(DD) );
                    if( tflag.get("C") )
                        Alu.inr( getXH(DD) );
                }
            }
            // DCX
            else if( ir.get(3,0) == 0b1011 ) {
                int DD = ir.get(5,4);
                if( DD == 0b11) {
                    Alu.dcr(sp);
                } else {
                    Flag tflag = Alu.dcr( getXL(DD) );
                    if( tflag.get("C") )
                        Alu.dcr( getXH(DD) );
                }
            }
            // DAD
            else if( ir.get(3,0) == 0b1001 ) {
                int DD = ir.get(5,4);
                if( DD == 0b11) {
                    // HEre
                } else {
                    Flag tflag = Alu.add(getL(), getXL(DD) ,false );
                    tflag = Alu.add(getH(), getXH(DD), tflag.get("C") );
                    flag.set("C",tflag.get("C"));
                }
            }
            // DAA
            else if( ir.get(5,0) == 0b100111 ) {
                if( getA().get(3,0) > 0x09 || flag.get("AC") ) {
                    Alu.add(getA(), new Register8(0x06),false);
                }
                if( getA().get(7,4) > 0x09 || flag.get("C") ) {
                    flag = Alu.add(getA(), new Register8(0x60),false);
                }
            }
            // RLC
            else if( ir.get(5,0) == 0b000111 ) {
                // get highest bit
                boolean bit = getA().get(7);
                Alu.shl(getA());
                getA().set(0,bit);
                flag.set("C",bit);
            }
            // RRC
            else if( ir.get(5,0) == 0b001111 ) {
                // get highest bit
                boolean bit = getA().get(0);
                Alu.shr(getA());
                getA().set(7,bit);
                flag.set("C",bit);
            }
            // RAL
            else if( ir.get(5,0) == 0b010111 ) {
                // get highest bit
                boolean bit = getA().get(7);
                Alu.shl(getA());
                getA().set(0,flag.get("C"));
                flag.set("C",bit);
            }
            // RAR
            else if( ir.get(5,0) == 0b011111 ) {
                // get highest bit
                boolean bit = getA().get(0);
                Alu.shr(getA());
                getA().set(7,flag.get("C"));
                flag.set("C",bit);
            }
            // CMA
            else if( ir.get(5,0) == 0b101111) {
                Alu.cmp(getA());
            }
            // CMC
            else if( ir.get(5,0) == 0b111111) {
                flag.set("C", ! flag.get("C"));
            }
            // STC
            else if( ir.get(5,0) == 0b110111) {
                flag.set("C",true);
            }
            // RIM
            else if( ir.get(5,0) == 0b100000) {
                getA().set(0,m5);
                getA().set(1,m6);
                getA().set(2,m7);
                getA().set(3,ie);

                getA().set(4,r5);
                getA().set(5,r6);
                getA().set(6,r7);
                getA().set(7,sid);
            }
            // SIM
            else if( ir.get(5,0) == 0b110000) {
                if( getA().get(6) )
                    sod = getA().get(7);
                if( getA().get(4) )
                    r7 = false;
                if(getA().get(3)) {
                    m7 = getA().get(2);
                    m6 = getA().get(1);
                    m5 = getA().get(0);
                }
            }
            // Error
            else {
                missed();
            }
            break;

        case 0b10:
            // ADD
            if ( ir.get(5,3) == 0b000 ) {
                int SSS = ir.get(2,0);
                flag = Alu.add( getA(), getR(SSS), false );
            }
            // ADC
            else if ( ir.get(5,3) == 0b001 ) {
                int SSS = ir.get(2,0);
                flag = Alu.add( getA(), getR(SSS), flag.get("C") );
            }
            // SUB
            else if ( ir.get(5,3) == 0b010 ) {
                int SSS = ir.get(2,0);
                if(SSS == 0b110)
                    flag = Alu.sub( getA(), getR(SSS), false );
            }
            // SBB
            else if ( ir.get(5,3) == 0b011 ) {
                int SSS = ir.get(2,0);
                flag = Alu.sub( getA(), getR(SSS), flag.get("C") );
            }
            // ANA
            else if ( ir.get(5,3) == 0b100 ) {
                int SSS = ir.get(2,0);
                flag = Alu.and( getA(), getR(SSS) );
            }
            // XRA
            else if ( ir.get(5,3) == 0b101 ) {
                int SSS = ir.get(2,0);
                flag = Alu.xor( getA(), getR(SSS));
            }
            // ORA
            else if ( ir.get(5,3) == 0b110 ) {
                int SSS = ir.get(2,0);
                flag = Alu.or( getA(), getR(SSS) );
            }
            // CMP
            else if ( ir.get(5,3) == 0b111 ) {
                int SSS = ir.get(2,0);
                flag = Alu.sub( getA().clone() , getR(SSS) , false );
            } else {
                missed();
            }
            break;

        case 0b11:
            // ADI
            if ( ir.get(5,0) == 0b000110 ) {
                setT( getData8FromMemoryPC());
                flag = Alu.add( getA(), getT() , false );
            }
            // ACI
            else if ( ir.get(5,0) == 0b001110 ) {
                setT( getData8FromMemoryPC());
                flag = Alu.add( getA(), getT(), flag.get("C") );
            }
            // SUI
            else if ( ir.get(5,0) == 0b010110 ) {
                setT( getData8FromMemoryPC());
                flag = Alu.sub( getA(), getT(), false );
            }
            // SBI
            else if ( ir.get(5,0) == 0b011110 ) {
                setT( getData8FromMemoryPC());
                flag = Alu.sub( getA(), getT(), flag.get("C") );
            }
            // ANI
            else if ( ir.get(5,0) == 0b100110) {
                setT( getData8FromMemoryPC());
                flag = Alu.and( getA(), getT() );
            }
            // XRI
            else if ( ir.get(5,0) == 0b101110) {
                setT( getData8FromMemoryPC());
                flag = Alu.xor( getA(), getT() );
            }
            // ORI
            else if ( ir.get(5,0) == 0b110110) {
                setT( getData8FromMemoryPC());
                flag = Alu.or( getA(), getT() );
            }
            // CPI
            else if ( ir.get(5,0) == 0b111110 ) {
                setT( getData8FromMemoryPC());
                // Register mustn't be changed
                flag = Alu.sub( getA().clone() , getT(), false );
            }
            // XCHG
            else if( ir.get(5,0) == 0b101011 ) {
                Register8 temp = register[2];
                register[2] = register[4];
                register[4] = temp;
                temp = register[3];
                register[3] = register[5];
                register[5] = temp;
            }
            // PUSH
            else if( ir.get(3,0) == 0b0101) {
                int SS = ir.get(5,4);
                if( SS == 0b11) {
                    // for M
                    Alu.dcr(sp);
                    setData8ToMemory(sp, getA() );
                    Alu.dcr(sp);
                    setData8ToMemory(sp, flag );
                } else {
                    // for Rp
                    Alu.dcr(sp);
                    setData8ToMemory(sp, getXH(SS) );
                    Alu.dcr(sp);
                    setData8ToMemory(sp, getXL(SS));
                }
            }
            // POP
            else if( ir.get(3,0) == 0b0001) {
                int DD = ir.get(5,4);
                if( DD == 0b11) {
                    // for M
                    flag.set(getData8FromMemory(sp));
                    Alu.inr(sp);
                    setA(getData8FromMemory(sp));
                    Alu.inr(sp);
                } else {
                    // for Rp
                    setXL( DD, getData8FromMemory(sp) );
                    Alu.inr(sp);
                    setXH( DD, getData8FromMemory(sp) );
                    Alu.inr(sp);
                }
            }
            // JMP
            else if( ir.get(5,0) == 0b000011) {
                pc = getData16FromMemoryPC();
            }
            // CALL
            else if( ir.get(5,0) == 0b001101) {
                pushPC();
                pc = getData16FromMemoryPC();
            }
            // RST
            else if( ir.get(2,0) == 0b111) {
                pushPC();
                pc = new Register16( ir.get(5,3)*8 );
            }
            // RET
            else if( ir.get(5,0) == 0b001001) {
                popPC();
            }
            // PCHL
            else if( ir.get(5,0) == 0b101001) {
                pc = getHL();
            }
            // SPHL
            else if( ir.get(5,0) == 0b111001) {
                sp = getHL();
            }
            // XTHL
            else if( ir.get(5,0) == 0b100011) {
                Register16 tadr = sp.clone();

                Register8 treg = getData8FromMemory(tadr);
                setData8ToMemory( tadr , register[5] );
                register[5].copy(treg);

                Alu.inr(tadr);

                treg = getData8FromMemory(tadr);
                setData8ToMemory( tadr , register[4] );
                register[4].copy(treg);
            }
            // JX
            else if( ir.get(2,0) == 0b010) {
                if( getCondition( ir.get(5,3) ) ) {
                    pc = getData16FromMemoryPC();
                } else {
                    Alu.inr(pc);
                    Alu.inr(pc);
                }
            }
            // CX
            else if( ir.get(2,0) == 0b100) {
                if(getCondition(ir.get(5,3))) {
                    pushPC();
                    pc = getData16FromMemoryPC();
                } else {
                    Alu.inr(pc);
                    Alu.inr(pc);
                }
            }
            // RX
            else if( ir.get(2,0) == 0b000) {
                if(getCondition(ir.get(5,3))) {
                    popPC();
                }
            }
            // EI
            else if( ir.get(5,0) == 0b111011) {
                ie = true;
            }
            // DI
            else if( ir.get(5,0) == 0b110011) {
                ie = false;
            }
            // IN
            else if( ir.get(5,0) == 0b011011) {
                //setA( getDataFromIO(readData8FromMemory()) );
            }
            // OUT
            else if( ir.get(5,0) == 0b010011) {
                //setDataToIO(readData8FromMemory(),getA());
            } else {
                missed();
            }
            break;

        default:
            missed();
            break;
        }
    }

    // IOM to Register Transfer

    private Register8 getData8(boolean IOM, Register16 address) {
        try {
            synchronized(this) {
                mar.copy(address);
                //System.out.print("S ");
                busH = new Register8(mar.upper());
                busL = new Register8(mar.lower());
                iom = IOM;
                read = true;
                notify();
                wait();
                mbr.copy(busL);
                read = false;
            }
        } catch (InterruptedException i) {
            // caught
        }
        return mbr.clone();
    }

    private void setData8(boolean IOM, Register16 address,Register8 value) {
        try {
            synchronized(this) {
                mar.copy(address);
                //System.out.print("S ");
                busH = new Register8(mar.upper());
                busL = new Register8(mar.lower());
                iom = IOM;
                write = true;
                notify();
                wait();
                busL.copy(value);
                notify();
                wait();
                write = false;
            }
        } catch (InterruptedException i) {
            // caught
        }
    }

    // Memory to Register Transfer

    private Register8 getData8FromMemory(Register16 address) {
        return getData8(false,address);
    }

    private void setData8ToMemory(Register16 address, Register8 value) {
        setData8(false,address,value);
    }

    // IO to Register Transfer

    private Register8 getData8FromIO(Register16 address) {
        return getData8(true,address);
    }

    private void setData8ToIO(Register16 address, Register8 value) {
        setData8(true,address,value);
    }

    // Memory to Register Transfer using PC

    private Register8 getData8FromMemoryPC() {
        Register8 data = getData8FromMemory(pc);
        Alu.inr(pc);
        return data;
    }

    private Register16 getData16FromMemoryPC() {
        Register8 l = getData8FromMemoryPC();
        Register8 h = getData8FromMemoryPC();
        return new Register16(l,h);
    }


    // Condition after decoding
    private boolean getCondition(int CCC) {
        if( (CCC==0b000 && !flag.get("Z")) ||
                (CCC==0b001 && flag.get("Z")) ||
                (CCC==0b010 && !flag.get("C")) ||
                (CCC==0b011 && flag.get("C")) ||
                (CCC==0b100 && !flag.get("P")) ||
                (CCC==0b101 && flag.get("P")) ||
                (CCC==0b110 && !flag.get("S")) ||
                (CCC==0b111 && flag.get("S")) )
            return true;
        return false;
    }

    // Register Pair access after decoding

    private void setXH(int DD, Register8 val) {
        // CHECK for SP
        register[DD*2].copy(val);
    }
    private void setXL(int DD, Register8 val) {
        // CHECK for SP
        register[DD*2+1].copy(val);
    }
    private Register8 getXH(int DD) {
        // CHECK for SP
        return register[DD*2];
    }
    private Register8 getXL(int DD) {
        // CHECK for SP
        return register[DD*2+1];
    }

    private Register16 getXX(int DD) {
        // CHECK for SP
        if(DD==0b11)
            return sp.clone();
        return new Register16(getXL(DD), getXH(DD));
    }

    // Register access after decoding

    private void setR(int DDD, Register8 value) {
        register[DDD].copy(value);
        if(DDD == 0b110)
            setData8ToMemory( getHL() , register[DDD] );
    }
    private Register8 getR(int DDD) {
        if(DDD == 0b110)
            register[DDD] = getData8FromMemory(getHL());
        return register[DDD];
    }

    // Push and Pop operations on PC

    private void pushPC() {
        Alu.dcr(sp);
        setData8ToMemory(sp, new Register8(pc.upper()));
        Alu.dcr(sp);
        setData8ToMemory(sp, new Register8(pc.lower()));
    }
    private void popPC() {
        Register8 D2 = getData8FromMemory(sp);
        Alu.inr(sp);
        Register8 D1 = getData8FromMemory(sp);
        Alu.inr(sp);
        pc = new Register16(D2,D1);
    }

    // Some generic register access functions

    private Register16 getHL() {
        return getXX(0x02);
    }

    private void setA(Register8 val) {
        register[7] = val;
    }
    private void setH(Register8 val) {
        register[4] = val;
    }
    private void setL(Register8 val) {
        register[5] = val;
    }
    private void setT(Register8 value) {
        register[6] = value;
    }

    private Register8 getH() {
        return register[4];
    }
    private Register8 getL() {
        return register[5];
    }
    private Register8 getA() {
        return register[7];
    }
    private Register8 getT() {
        return register[6];
    }

    // Print the status of the program
    public void print(boolean verbose) {
        if(verbose) {
            flag.print();
            System.out.println("PSW\t: "+register[7].hex()+" "+flag.hex());
            System.out.println("BC\t: "+register[0].hex()+" "+register[1].hex());
            System.out.println("DE\t: "+register[2].hex()+" "+register[3].hex());
            System.out.println("HL\t: "+register[4].hex()+" "+register[5].hex());
        }
        System.out.println("IR\t: "+ir.hex());
        System.out.println("PC\t: "+pc.hex());
        if(verbose) {
            System.out.println("SP\t: "+sp.hex());
        }
    }

    // Print if any statement is missed
    public void missed() {
        System.out.println("MISSED " +pc.hex()+ " : "+ ir.bin() );
    }


}





