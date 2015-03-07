public class Alu {

    // Retuns true if register contains even no. of one
    public static boolean parity(Register reg){
        boolean prty = true;
        for(int i=0;i<reg.m_length;i++){
            if(reg.get(i))
                prty = !prty;
        }
        return prty;
    }

    // Retuns true if the register value is zero
    public static boolean zero(Register reg){
        return reg.get()==0;
    }

    // Returns true if the sign bit is one
    public static boolean sign(Register reg){
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
    // Complements the register value
    public static void cmp(Register reg){
        reg.set(~reg.get());
    }

    // Adds reg1 and reg2 and stores value in reg1
    // Also returns the value of flags
    public static Flag add(Register reg1, Register reg2){
        Flag flag = new Flag(0x00);
        flag.set("C",reg1.get()+reg2.get() > reg1.m_maxvalue);
        flag.set("AC",reg1.lower()+reg2.lower() > reg1.m_halfvalue);

        reg1.set(reg1.get()+reg2.get());

        flag.set("P",parity(reg1));
        flag.set("Z",zero(reg1));
        flag.set("S",sign(reg1));
        return flag;
    }

    public static Flag inr(Register reg){
        return add(reg,new Register(1,reg.m_length));
    }

    public static Flag dcr(Register reg){
        return sub(reg,new Register(1,reg.m_length));
    }

    // Subtracts reg1 and reg2 and stores value in reg1
    // Also returns the value of flags
    public static Flag sub(Register reg1, Register reg2){
        Flag flag = new Flag(0x00);
        flag.set("C",reg1.get()+reg2.get() > reg1.m_maxvalue);
        flag.set("AC",reg1.lower()+reg2.lower() > reg1.m_halfvalue);

        reg1.set(reg1.get()+(~reg2.get())+1);

        flag.set("P",parity(reg1));
        flag.set("Z",zero(reg1));
        flag.set("S",sign(reg1));
        return flag;
    }

    // Or reg1 and reg2 and stores value in reg1
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

    // A reg1 and reg2 and stores value in reg1
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

    // xor reg1 and reg2 and stores value in reg1
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
