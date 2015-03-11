public class Register16 extends Register {


    public Register16(Register8 reg1, Register8 reg2){
        super((reg1.get()<<reg1.m_length) + reg2.get(),16);
    }

    public Register16(int val) {
        super(val,16);
    }

    public Register16 clone() {
        return new Register16(m_value);
    }


}
