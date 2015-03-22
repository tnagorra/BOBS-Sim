public class Register8 extends Register {

    // Constructor for Register8 using integer value
    public Register8(int val) {
        super(val,8);
    }

    // Clone Register8
    public Register8 clone() {
        return new Register8(m_value);
    }
}
