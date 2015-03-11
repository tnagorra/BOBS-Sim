// TODO
// Microprocessor States
// Bus is derivation of Register16
//
// IN OUT
// EI DI
// RIM SIM

import java.io.*;

public class Microprocessor {
    private boolean active;

    private Register16 mar;
    private Register8 mbr;
    private Register8 ir;
    private Register16 pc, sp;

    private Flag flag;
    private Register8[] register;

    private Memory memory;

    // Constructor
    public Microprocessor(Memory mem) {
        // Initialize memory
        memory = mem;
        // Initialize instruction register
        ir = new Register8(0x00);
        // Intialize Program Counter
        pc = new Register16(0x0000);
        // Initialize Stack Pointer
        sp = new Register16(0xFFFF);
        // Initialize flags to zero
        flag = new Flag(0x00);
        // Initialize 8 registers where only 7 will be used
        register = new Register8[8];
        for(int i=0;i<register.length;i++)
            register[i] = new Register8(0);
    }

    // The fetch cycle
    private void fetch(){
        mar = pc.clone();
        Alu.inr(pc);
        mbr = memory.get(mar);
        ir = mbr.clone();
    }

    // The decode and execute cycle
    private void decode(){
        switch( ir.get(7,6) ){
            case 0b01:
                // HLT
                if ( ir.get(5,0) == 0b110110 ) {
                    active = false;
                }
                // MOV
                else {
                    int DDD = ir.get(5,3);
                    int SSS = ir.get(2,0);
                    // NOTE: used register[110] as temporary register
                    // Get from memory if needed
                    if(SSS == 0b110)
                        register[SSS] = memory.get(getHL());
                    // Transfer from source register to destination
                    register[DDD] = register[SSS].clone();
                    // Write to memory if needed
                    if (DDD == 0b110)
                        memory.set(getHL(), register[DDD]);
                }
                break;
            case 0b00:
                // MVI
                if (ir.get(2,0) == 0b110){
                    int DDD= ir.get(5,3);
                    // Get immediate data
                    register[DDD] = getData8FromMemory();
                    // Write to memory if needed
                    if (DDD == 0b110)
                        memory.set( getHL() , register[DDD] );
                }
                // LXI
                else if ( ir.get(3,0) == 0b0001 ){
                    int DDD = ir.get(5,4);
                    // for SP
                    if( DDD == 0b11){
                        sp = getData16FromMemory();
                    }
                    // for Rp
                    else {
                        setXL(DD, getData8FromMemory() );
                        setXH(DD, getData8FromMemory() );
                    }
                }
                // NOP
                else if( ir.get(5,0) == 0b00000 ) {
                    // nothing here
                }
                // LDA
                else if( ir.get(5,0) == 0b111010 ) {
                    register[7] = memory.get( getData16FromMemory() );
                }
                // STA
                else if( ir.get(5,0) == 0b110010 ) {
                    memory.set(getData16FromMemory(), register[7]);
                }
                // LHLD
                else if( ir.get(5,0) == 0b101010) {
                    Register16 addr = getData16FromMemory();
                    register[5] = memory.get(addr);
                    Alu.inr(addr);
                    register[4] = memory.get(addr);
                }
                // SHLD
                else if( ir.get(5,0) == 0b100010) {
                    Register16 addr = getData16FromMemory();
                    memory.set( addr, register[5]);
                    Alu.inr(addr);
                    memory.set(addr, register[4]);
                }
                // LDAX
                else if( ir.get(3,0) == 0b1010 ) {
                    // NOTE
                    // in LDAX and STAX it can't have Rp other than BC and DE
                    // but the previous cases of "if" should be sufficient for validation
                    int DDD = ir.get(5,4);
                    register[7] = memory.get( getXX(DDD) );
                }
                // STAX
                else if( ir.get(3,0) == 0b0010 ) {
                    int DDD = ir.get(5,4);
                    memory.set(getXX(DDD) , register[7]);
                }
                // INR
                else if( ir.get(2,0) == 0b100 ) {
                    int DDD = ir.get(5,3);
                    if(DDD == 0b110)
                        register[DDD] = memory.get( new Register16( register[4], register[5]));
                    Flag tflag = Alu.inr(register[DDD]);
                    if(DDD == 0b110)
                        memory.set( new Register16( register[4], register[5]) , register[DDD] );
                    // So that carry flag isn't updated
                    tflag.set("C",flag.get("C"));
                    flag = tflag;
                }
                // DCR
                else if( ir.get(2,0) == 0b101 ) {
                    int DDD = ir.get(5,3);
                    if(DDD == 0b110)
                        register[DDD] = memory.get( new Register16( register[4], register[5]));
                    Flag tflag = Alu.dcr(register[DDD]);
                    if(DDD == 0b110)
                        memory.set( new Register16( register[4], register[5]) , register[DDD] );
                    // So that carry flag isn't updated
                    tflag.set("C",flag.get("C"));
                    flag = tflag;
                }
                // INX
                else if( ir.get(3,0) == 0b0011 ) {
                    int DDD = ir.get(5,4);
                    if( DDD == 0b11){
                        Alu.inr(sp);
                    } else {
                        Flag tflag = Alu.inr( register[DDD*2+1] );
                        if( tflag.get("C") )
                            Alu.inr( register[DDD*2] );
                    }
                }
                // DCX
                else if( ir.get(3,0) == 0b1011 ) {
                    int DDD = ir.get(5,4);
                    if( DDD == 0b11){
                        Alu.dcr(sp);
                    } else {
                        Flag tflag = Alu.dcr( register[DDD*2+1] );
                        if( tflag.get("C") )
                            Alu.dcr( register[DDD*2] );
                    }
                }
                // DAD
                else if( ir.get(3,0) == 0b1001 ) {
                    int DDD = ir.get(5,4);
                    if( DDD == 0b11){
                        // HEre
                    } else {
                        Flag tflag = Alu.add( register[5], register[DDD*2+1],false );
                        tflag = Alu.add( register[4], register[DDD*2],tflag.get("C") );
                        flag.set("C",tflag.get("C"));
                    }
                }
                // DAA
                else if( ir.get(5,0) == 0b100111 ) {
                    if( register[7].get(3,0) > 0x09 || flag.get("AC") ){
                        Alu.add(register[7], new Register8(0x06),false);
                    }
                    if( register[7].get(7,4) > 0x09 || flag.get("C") ){
                        flag = Alu.add(register[7], new Register8(0x60),false);
                    }
                }
                // RLC
                else if( ir.get(5,0) == 0b000111 ) {
                    // get highest bit
                    boolean bit = register[7].get(7);
                    Alu.shl(register[7]);
                    register[7].set(0,bit);
                    flag.set("C",bit);
                }
                // RRC
                else if( ir.get(5,0) == 0b001111 ) {
                    // get highest bit
                    boolean bit = register[7].get(0);
                    Alu.shr(register[7]);
                    register[7].set(7,bit);
                    flag.set("C",bit);
                }
                // RAL
                else if( ir.get(5,0) == 0b010111 ) {
                    // get highest bit
                    boolean bit = register[7].get(7);
                    Alu.shl(register[7]);
                    register[7].set(0,flag.get("C"));
                    flag.set("C",bit);
                }
                // RAR
                else if( ir.get(5,0) == 0b011111 ) {
                    // get highest bit
                    boolean bit = register[7].get(0);
                    Alu.shr(register[7]);
                    register[7].set(7,flag.get("C"));
                    flag.set("C",bit);
                }
                // CMA
                else if( ir.get(5,0) == 0b101111) {
                    Alu.cmp(register[7]);
                }
                // CMC
                else if( ir.get(5,0) == 0b111111) {
                    flag.set("C", ! flag.get("C"));
                }
                // STC
                else if( ir.get(5,0) == 0b110111) {
                    flag.set("C",true);
                } else {
                    System.out.println("MISSED " +pc.hex()+ " : "+ ir.bin() );
                }


                break;

            case 0b10:

                // ADD
                if ( ir.get(5,3) == 0b000 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    flag = Alu.add( register[7], register[SSS], false );
                }
                // ADC
                else if ( ir.get(5,3) == 0b001 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    flag = Alu.add( register[7], register[SSS], flag.get("C") );
                }
                // SUB
                else if ( ir.get(5,3) == 0b010 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    flag = Alu.sub( register[7], register[SSS], false );
                }
                // SBB
                else if ( ir.get(5,3) == 0b011 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    flag = Alu.sub( register[7], register[SSS], flag.get("C") );
                }
                // ANA
                else if ( ir.get(5,3) == 0b100 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    flag = Alu.and( register[7], register[SSS]);
                }
                // XRA
                else if ( ir.get(5,3) == 0b101 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    flag = Alu.xor( register[7], register[SSS]);
                }
                // ORA
                else if ( ir.get(5,3) == 0b110 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    flag = Alu.or( register[7], register[SSS]);
                }
                // CMP
                else if ( ir.get(5,3) == 0b111 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( new Register16( register[4], register[5]));
                    // Register mustn't be changed
                    Register8 tregister = register[7].clone();
                    flag = Alu.sub( tregister , register[SSS], false );
                }
                else {
                    System.out.println(ir.get(5,3) == 0b000);
                }

                break;

            case 0b11:
                // ADI
                if ( ir.get(5,0) == 0b000110 ){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.add( register[7], register[6], false );
                }
                // ACI
                else if ( ir.get(5,0) == 0b001110 ){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.add( register[7], register[6], flag.get("C") );
                }
                // SUI
                else if ( ir.get(5,0) == 0b010110 ){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.sub( register[7], register[6], false );
                }
                // SBI
                else if ( ir.get(5,0) == 0b011110 ){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.sub( register[7], register[6], flag.get("C") );
                }
                // ANI
                else if ( ir.get(5,0) == 0b100110){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.and( register[7], register[6] );
                }
                // XRI
                else if ( ir.get(5,0) == 0b101110){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.xor( register[7], register[6] );
                }
                // ORI
                else if ( ir.get(5,0) == 0b110110){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.or( register[7], register[6] );
                }
                // CPI
                else if ( ir.get(5,0) == 0b111110 ){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    // Register mustn't be changed
                    Register8 tregister = register[7].clone();
                    flag = Alu.sub( tregister , register[6], false );
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
                    int SSS = ir.get(5,4);
                    if( SSS == 0b11){
                        // for M
                        Alu.dcr(sp);
                        memory.set(sp, register[7] );
                        Alu.dcr(sp);
                        memory.set(sp, flag );
                    } else {
                        // for Rp
                        Alu.dcr(sp);
                        memory.set(sp, register[ SSS*2 ] );
                        Alu.dcr(sp);
                        memory.set(sp, register[ SSS*2+1 ] );
                    }
                }
                // POP
                else if( ir.get(3,0) == 0b0001) {
                    int DDD = ir.get(5,4);
                    if( DDD == 0b11){
                        // for M
                        flag.set(memory.get(sp));
                        Alu.inr(sp);
                        register[7] = memory.get(sp);
                        Alu.inr(sp);
                    } else {
                        // for Rp
                        register[DDD*2+1] = memory.get(sp);
                        Alu.inr(sp);
                        register[DDD*2] = memory.get(sp);
                        Alu.inr(sp);
                    }
                }
                // JMP
                else if( ir.get(5,0) == 0b000011){
                    Register8 D2 = memory.get(pc);
                    Alu.inr(pc);
                    Register8 D1 = memory.get(pc);
                    Alu.inr(pc);
                    pc = new Register16(D1,D2);
                }
                // CALL
                else if( ir.get(5,0) == 0b001101){
                    Alu.dcr(sp);
                    memory.set(sp, new Register8(pc.upper()) );
                    Alu.dcr(sp);
                    memory.set(sp, new Register8(pc.lower()) );

                    Register8 D2 = memory.get(pc);
                    Alu.inr(pc);
                    Register8 D1 = memory.get(pc);
                    Alu.inr(pc);
                    pc = new Register16(D1,D2);
                }
                // RST
                else if( ir.get(2,0) == 0b111){
                    Alu.dcr(sp);
                    memory.set(sp, new Register8(pc.upper()) );
                    Alu.dcr(sp);
                    memory.set(sp, new Register8(pc.lower()) );

                    Register16 taddr = new Register16( ir.get(5,3) );
                    // ie times 8
                    Alu.shl(taddr);
                    Alu.shl(taddr);
                    Alu.shl(taddr);
                    pc = taddr;
                }
                // RET
                else if( ir.get(5,0) == 0b001001){
                    Register8 D2 = memory.get(sp);
                    Alu.inr(sp);
                    Register8 D1 = memory.get(sp);
                    Alu.inr(sp);
                    pc = new Register16(D1,D2);
                }
                // PCHL
                else if( ir.get(5,0) == 0b101001){
                    pc = new Register16( register[4], register[5]);
                }
                // SPHL
                else if( ir.get(5,0) == 0b111001){
                    sp = new Register16( register[4], register[5]);
                }
                // XTHL
                else if( ir.get(5,0) == 0b100011){
                    Register16 tadr = sp.clone();

                    Register8 treg = memory.get(tadr);
                    memory.set( tadr , register[5] );
                    register[5] = treg.clone();

                    Alu.inr(tadr);

                    treg = memory.get(tadr);
                    memory.set( tadr , register[4] );
                    register[4] = treg.clone();
                }
                // JX
                else if( ir.get(2,0) == 0b010) {
                    if( CCC( ir.get(5,3) ) ) {
                        Register8 D2 = memory.get(pc);
                        Alu.inr(pc);
                        Register8 D1 = memory.get(pc);
                        Alu.inr(pc);
                        pc = new Register16(D1,D2);
                    } else {
                        Alu.inr(pc);
                        Alu.inr(pc);
                    }
                }
                // CX
                else if( ir.get(2,0) == 0b100) {
                    if(CCC(ir.get(5,3))) {
                        Alu.dcr(sp);
                        memory.set(sp, new Register8(pc.upper()) );
                        Alu.dcr(sp);
                        memory.set(sp, new Register8(pc.lower()) );

                        Register8 D2 = memory.get(pc);
                        Alu.inr(pc);
                        Register8 D1 = memory.get(pc);
                        Alu.inr(pc);
                        pc = new Register16(D1,D2);
                    } else {
                        Alu.inr(pc);
                        Alu.inr(pc);
                    }
                }
                // RX
                else if( ir.get(2,0) == 0b000) {
                    if(CCC(ir.get(5,3))) {
                        Register8 D2 = memory.get(sp);
                        Alu.inr(sp);
                        Register8 D1 = memory.get(sp);
                        Alu.inr(sp);
                        pc = new Register16(D1,D2);
                    }
                }
                else {
                    System.out.println("MISSED " +pc.hex()+ " : "+ ir.bin() );
                }
                break;

            default:
                System.out.println("MISSED " +pc.hex()+ " : "+ ir.bin() );
                break;
        }
    }

    // Start the microprocessor operation
    public void start(Register16 mem, boolean verbose,boolean singlestep) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        active = true;
        pc = mem.clone();
        while(active){
            fetch();
            decode();

            if(singlestep){
                print(verbose);

                String y = br.readLine();
                if( y.equals("Q") || y.equals("q") )
                    active = false;
                if( y.equals("B") || y.equals("b") )
                    singlestep = false;
                if( y.equals("V") || y.equals("v") )
                    verbose = true;
                if( y.equals("NV") || y.equals("nv") )
                    verbose = false;
            }
        }
    }

    // Print the status of the program
    public void print(boolean verbose){
        if(verbose){
            flag.print();
            System.out.println("PSW\t: "+register[7].hex()+" "+flag.hex());
            System.out.println("BC\t: "+register[0].hex()+" "+register[1].hex());
            System.out.println("DE\t: "+register[2].hex()+" "+register[3].hex());
            System.out.println("HL\t: "+register[4].hex()+" "+register[5].hex());
        }
        System.out.println("IR\t: "+ir.hex());
        System.out.println("PC\t: "+pc.hex());
        if(verbose){
            System.out.println("SP\t: "+sp.hex());
        }
    }



    // val is a 3 bit number
    private boolean CCC(int val){
        if( (val==0b000 && !flag.get("Z")) ||
                (val==0b001 && flag.get("Z")) ||
                (val==0b010 && !flag.get("C")) ||
                (val==0b011 && flag.get("C")) ||
                (val==0b100 && !flag.get("P")) ||
                (val==0b101 && flag.get("P")) ||
                (val==0b110 && !flag.get("S")) ||
                (val==0b111 && flag.get("S")) )
            return true;
        return false;
    }

    private Register16 getData16FromMemory(){
        Register8 D2 = memory.get(pc);
        Alu.inr(pc);
        Register8 D1 = memory.get(pc);
        Alu.inr(pc);
        return new Register16(D1,D2);
    }

    private Register8 getData8FromMemory(){
        Register8 D = memory.get(pc);
        Alu.inr(pc);
        return D;
    }

    private Register16 getHL(){
        return getXX(0x02);
    }

    // val is a 3 bit number
    private Register16 getXX(int DD){
        if(DD==0b11)
            return sp.clone();
        int D1 = DD*2;
        int D2 = DD*2+1;
        return new Register16(register[D1], register[D2]);
    }

    private void setXH(int DD, Register8 val){
        // CHECK for SP
        register[DD*2] = val.clone();
    }
    private void setXL(int DD, Register8 val){
        // CHECK for SP
        register[DD*2+1] = val.clone();
    }

}
