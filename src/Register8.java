public class Register8 extends Register {

    public Register8(int val) {
        super(val,8);
    }

    public Register8 clone() {
        return new Register8(m_value);
    }
}
