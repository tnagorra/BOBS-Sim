public class Microprocessor {
    Register16 mar;
    Register8 mbr;

    Register8 ir;
    Register16 pc, sp;

    Flag flag;
    Register8[] register;

    Memory memory;

    public Microprocessor() {
        // Initialize instruction register
        ir = new Register8(0x00);
        // Intialize Program Counter
        pc = new Register16(0x0000);
        // Initialize Stack Pointer
        sp = new Register16(0xFFFF);

        // Initialize memory
        memory = new Memory(65536);

        // Initialize flags to zero
        flag = new Flag(0x00);

        // Initialize 8 registers where only 7 will be used
        register = new Register8[8];
        for(int i=0;i<register.length;i++)
            register[i] = new Register8(i);
    }

    public void start(){
        //register[1].set(0xFE);
        //flag = Alu.add(register[1],register[2]);
        //flag = Alu.inr(pc);
        memory.set( new Register16(0x000), new Register8(0x7E));
        print();
        fetch();
        decode();
        print();
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
            // For MOV R/M, R/M
                int DDD = ir.get(5,3);
                int SSS = ir.get(2,0);

                Register8 value;
                if( SSS == 0b110 )
                    value = memory.get( Register16.from( register[4], register[5]));
                else
                    value = register[SSS].clone();

                if ( DDD == 0b110 )
                    memory.set( Register16.from( register[4], register[5]) , value );
                else
                    register[DDD] = value.clone();

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
