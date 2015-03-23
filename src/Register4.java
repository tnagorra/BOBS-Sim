public class Register4 extends Register {

    // Constructor for Register8 using integer value
    public Register4(int val) {
        super(val,4);
    }

    // Clone Register8
    public Register4 clone() {
        return new Register4(m_value);
    }
}
