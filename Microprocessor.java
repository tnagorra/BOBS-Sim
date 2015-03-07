public class Microprocessor {
    Register16 mar;
    Register8 mbr;

    Register8 ir;
    Register16 pc, sp;

    Flag flag;
    Register8[] register;

    Memory memory;

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

    public void start(Register16 mem){
        pc = mem.clone();
        fetch();
        decode();
    }

    private void fetch(){
        mar = pc.clone();
        Alu.inr(pc);
        mbr = memory.get(mar);
        ir = mbr.clone();
    }

    private void decode(){
        switch( ir.get(7,6) ){
            case 0b01:
                // MOV
                int DDD = ir.get(5,3), SSS = ir.get(2,0);
                // Get from memory if needed
                if( SSS == 0b110 )
                    register[SSS] = memory.get( Register16.from( register[4], register[5]));
                // Transfer from source register to destination
                register[DDD] = register[SSS].clone();
                // Write to memory if needed
                if ( DDD == 0b110 )
                    memory.set( Register16.from( register[4], register[5]) , register[DDD] );
                break;
            case 0b00:
                break;
            case 0b10:
                break;
            case 0b11:
                break;
            default:
                break;
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
