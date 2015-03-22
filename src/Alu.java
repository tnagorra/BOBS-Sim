public class Alu {

    // Retuns true if register contains even number of bit'1'
    private static boolean parity(Register reg){
        boolean prty = true;
        for(int i=0;i<reg.m_length;i++){
            if(reg.get(i))
                prty = !prty;
        }
        return prty;
    }

    // Retuns true if the register value is zero
    private static boolean zero(Register reg){
        return reg.get()==0;
    }

    // Returns true if the sign bit is one
    private static boolean sign(Register reg){
        return reg.get(reg.m_length-1);
    }

    // Right shifst the register value
    public static void shr(Register reg){
        reg.set(reg.get() >> 1);
    }

    // Left shifs the register value
    public static void shl(Register reg){
        reg.set(reg.get() << 1);
    }

    // OnesComplement the register value
    public static void cmp(Register reg){
        reg.set(~reg.get());
    }

    // TwosComplement the register value
    public static void cmp2(Register reg){
        reg.set(~reg.get()+1);
    }

    // Add reg1 with reg2 and stores value in reg1
    // Returns the changed flag
    public static Flag add(Register reg1, Register reg2, boolean carry){
        Flag flag = new Flag(0x00);
        if( carry){
            flag.set("C",reg1.get()+reg2.get()+1 > reg1.m_maxvalue);
            flag.set("AC",reg1.lower()+reg2.lower()+1 > reg1.m_halfvalue);
            reg1.set(reg1.get()+reg2.get()+1);
        } else {
            flag.set("C",reg1.get()+reg2.get() > reg1.m_maxvalue);
            flag.set("AC",reg1.lower()+reg2.lower() > reg1.m_halfvalue);
            reg1.set(reg1.get()+reg2.get());
        }

        flag.set("P",parity(reg1));
        flag.set("Z",zero(reg1));
        flag.set("S",sign(reg1));
        return flag;
    }

    // Subtracts reg1 by reg2 and stores value in reg1
    // Returns the changed flags
    public static Flag sub(Register reg1, Register reg2, boolean borrow){
        Flag flag = new Flag(0x00);
        Register reg3 = reg2.clone();
        Alu.cmp(reg3);
        if( borrow ){
            flag.set("C",reg1.get()+reg3.get() <= reg1.m_maxvalue);
            flag.set("AC",reg1.lower()+reg3.lower() <= reg1.m_halfvalue);
            reg1.set(reg1.get()+reg3.get());
        } else {
            flag.set("C",reg1.get()+reg3.get()+1 <= reg1.m_maxvalue);
            flag.set("AC",reg1.lower()+reg3.lower()+1 <= reg1.m_halfvalue);
            reg1.set(reg1.get()+reg3.get()+1);
        }
        flag.set("P",parity(reg1));
        flag.set("Z",zero(reg1));
        flag.set("S",sign(reg1));
        return flag;
    }

    // Increases the value of register reg
    // Returns the changed flags
    public static Flag inr(Register reg){
        return add(reg,new Register(1,reg.m_length),false);
    }

    // Decreases the value of register reg
    // Returns the changed flags
    public static Flag dcr(Register reg){
        return sub(reg,new Register(1,reg.m_length),false);
    }



    // Or operation between reg1 and reg2 and stores value in reg1
    // Also returns the value of flags
    public static Flag or(Register reg1, Register reg2){
        Flag flag = new Flag(0x00);
        flag.set("C",false);
        flag.set("AC",false);

        reg1.set(reg1.get() | reg2.get());

        flag.set("P",parity(reg1));
        flag.set("Z",zero(reg1));
        flag.set("S",sign(reg1));
        return flag;
    }

    // And operation between  reg1 and reg2 and stores value in reg1
    // Also returns the value of flags
    public static Flag and(Register reg1, Register reg2){
        Flag flag = new Flag(0x00);
        flag.set("C",false);
        flag.set("AC",true);

        reg1.set(reg1.get() & reg2.get());

        flag.set("P",parity(reg1));
        flag.set("Z",zero(reg1));
        flag.set("S",sign(reg1));
        return flag;
    }

    // Xor operation between reg1 and reg2 and stores value in reg1
    // Also returns the value of flags
    public static Flag xor(Register reg1, Register reg2){
        Flag flag = new Flag(0x00);
        flag.set("C",false);
        flag.set("AC",false);

        reg1.set(reg1.get() ^ reg2.get());

        flag.set("P",parity(reg1));
        flag.set("Z",zero(reg1));
        flag.set("S",sign(reg1));
        return flag;
    }
}
