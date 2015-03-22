public class Register16 extends Register {


    // Constructor for Register16 using two Register8 values
    public Register16(Register8 regl, Register8 regh){
        super((regh.get()<<regh.m_length) + regl.get(),16);
    }

    // Constructor for Register16 using integer value
    public Register16(int val) {
        super(val,16);
    }

    // Clone Register16
    public Register16 clone() {
        return new Register16(m_value);
    }

}
