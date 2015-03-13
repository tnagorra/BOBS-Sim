public class Register16 extends Register {


    public Register16(Register8 regl, Register8 regh){
        super((regh.get()<<regh.m_length) + regl.get(),16);
    }

    public Register16(int val) {
        super(val,16);
    }

    public Register16 clone() {
        return new Register16(m_value);
    }

}
