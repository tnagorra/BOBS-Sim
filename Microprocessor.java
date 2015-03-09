public class Microprocessor {
    // TODO
    // Microprocessor States
    // Bus is derivation of Register16
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
            register[i] = new Register8(i);
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
                if( SSS == 0b110 )
                    register[SSS] = memory.get( Register16.from( register[4], register[5]));
                // Transfer from source register to destination
                register[DDD] = register[SSS].clone();
                // Write to memory if needed
                if ( DDD == 0b110 )
                    memory.set( Register16.from( register[4], register[5]) , register[DDD] );
                }
                break;
            case 0b00:
                // MVI
                if( ir.get(2,0) == 0b110 ){
                    int DDD= ir.get(5,3);
                    // Get immediate data
                    register[DDD] = memory.get( pc );
                    Alu.inr(pc);
                    // Write to memory if needed
                    if ( DDD == 0b110 )
                        memory.set( Register16.from( register[4], register[5]) , register[DDD] );
                }
                // LXI
                else if ( ir.get(3,0) == 0b0001 ){
                    int DDD = ir.get(5,4);
                    if( DDD == 0b11){
                        // for M
                        Register8 D2 = memory.get(pc);
                        Alu.inr(pc);
                        Register8 D1 = memory.get(pc);
                        Alu.inr(pc);
                        sp = Register16.from(D1,D2);
                    } else {
                        // for Rp
                        register[DDD*2+1] = memory.get(pc);
                        Alu.inr(pc);
                        register[DDD*2] = memory.get(pc);
                        Alu.inr(pc);
                    }
                }
                // LDA
                else if( ir.get(5,0) == 0b111010 ) {
                    Register8 D2 = memory.get(pc);
                    Alu.inr(pc);
                    Register8 D1 = memory.get(pc);
                    Alu.inr(pc);
                    register[7] = memory.get(Register16.from(D1,D2));
                }
               // STA
                else if( ir.get(5,0) == 0b110010 ) {
                    Register8 D2 = memory.get(pc);
                    Alu.inr(pc);
                    Register8 D1 = memory.get(pc);
                    Alu.inr(pc);
                    memory.set( Register16.from(D1,D2), register[7]);
                }
                // LHLD
                else if( ir.get(5,0) == 0b101010) {
                    Register8 D2 = memory.get(pc);
                    Alu.inr(pc);
                    Register8 D1 = memory.get(pc);
                    Alu.inr(pc);
                    Register16 source = Register16.from(D1,D2);

                    register[5] = memory.get(source);
                    Alu.inr(source);
                    register[4] = memory.get(source);
                }
                // SHLD
                else if( ir.get(5,0) == 0b100010) {
                    Register8 D2 = memory.get(pc);
                    Alu.inr(pc);
                    Register8 D1 = memory.get(pc);
                    Alu.inr(pc);
                    Register16 source = Register16.from(D1,D2);

                    memory.set( source, register[5]);
                    Alu.inr(source);
                    memory.set( source, register[4]);
                }
                // NOTE
                // in LDAX and STAX it can't have Rp other than BC and DE
                // but the previous cases of "if" should be sufficient for validation
                // LDAX
                else if( ir.get(3,0) == 0b1010 ) {
                    int DDD = ir.get(5,4);
                    int D1 = DDD*2;
                    int D2 = D1+1;
                    register[7] = memory.get( Register16.from(register[D1],register[D2]));
                }
                // STAX
                else if( ir.get(3,0) == 0b0010 ) {
                    int DDD = ir.get(5,4);
                    int D1 = DDD*2;
                    int D2 = D1+1;
                    memory.set(Register16.from(register[D1], register[D2]), register[7]);
                }
                // INR
                else if( ir.get(2,0) == 0b100 ) {
                    int DDD = ir.get(5,3);
                    if(DDD == 0b110)
                        register[DDD] = memory.get( Register16.from( register[4], register[5]));
                    Flag tflag = Alu.inr(register[DDD]);
                    if(DDD == 0b110)
                        memory.set( Register16.from( register[4], register[5]) , register[DDD] );
                    // So that carry flag isn't updated
                    tflag.set("C",flag.get("C"));
                    flag = tflag;
                }
                // DCR
                else if( ir.get(2,0) == 0b101 ) {
                    int DDD = ir.get(5,3);
                    if(DDD == 0b110)
                        register[DDD] = memory.get( Register16.from( register[4], register[5]));
                    Flag tflag = Alu.dcr(register[DDD]);
                    if(DDD == 0b110)
                        memory.set( Register16.from( register[4], register[5]) , register[DDD] );
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

                break;

            case 0b10:
                // ADD
                if ( ir.get(5,3) == 0b000 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( Register16.from( register[4], register[5]));
                    flag = Alu.add( register[7], register[SSS], false );
                }
                // ADC
                else if ( ir.get(5,3) == 0b001 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( Register16.from( register[4], register[5]));
                    flag = Alu.add( register[7], register[SSS], flag.get("C") );
                }
                // SUB
                if ( ir.get(5,3) == 0b010 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( Register16.from( register[4], register[5]));
                    flag = Alu.sub( register[7], register[SSS], false );
                }
                // SBB
                else if ( ir.get(5,3) == 0b011 ){
                    int SSS = ir.get(2,0);
                    if(SSS == 0b110)
                        register[SSS] = memory.get( Register16.from( register[4], register[5]));
                    flag = Alu.sub( register[7], register[SSS], flag.get("C") );
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
                else if ( ir.get(5,0) == 0b000110 ){
                    register[6] = memory.get(pc);
                    Alu.inr(pc);
                    flag = Alu.add( register[7], register[6], flag.get("C") );
                }
                // SUI
                if ( ir.get(5,0) == 0b010110 ){
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
               // XCHG
                else if( ir.get(5,0) == 0b101011 ) {
                    Register8 temp = register[2];
                    register[2] = register[4];
                    register[4] = temp;
                    temp = register[3];
                    register[3] = register[5];
                    register[5] = temp;
                }
                break;

            default:
                break;
        }
    }

    // Start the microprocessor operation
    public void start(Register16 mem){
        active = true;
        pc = mem.clone();
        while(active){
            fetch();
            decode();
        }
    }

    // Print the status of the program
    public void print(){
        flag.print();
        System.out.println("AF\t: "+register[7].hex()+" "+flag.hex());
        System.out.println("BC\t: "+register[0].hex()+" "+register[1].hex());
        System.out.println("DE\t: "+register[2].hex()+" "+register[3].hex());
        System.out.println("HL\t: "+register[4].hex()+" "+register[5].hex());
        System.out.println("IR\t: "+ir.hex());
        System.out.println("PC\t: "+pc.hex());
        System.out.println("SP\t: "+sp.hex());
    }
}
