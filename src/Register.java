public class Register {

    public int m_length;
    public int m_halflength;
    public int m_maxvalue;
    public int m_halfvalue;

    protected int m_value;

    // Constructor for register with value and register length
    public Register(int val,int length) {
        m_length = length;
        m_halflength = m_length/2;
        m_maxvalue= (int)Math.pow(2,m_length)-1;
        m_halfvalue = m_maxvalue>> m_halflength;

        this.set(val);
    }

    // Clone Register
    public Register clone() {
        return new Register(m_value,m_length);
    }

    // Returns Hex representation of value in string
    public String hex(){
        // get one more bit and then truncate later so that we get leading zeros
        return  Integer.toHexString( (m_maxvalue+1) | m_value).substring(1).toUpperCase();
    }
    // Returns Binary representation of value in string
    public String bin(){
        // get one more bit and then truncate later so that we get leading zeros
        return  Integer.toBinaryString( (m_maxvalue+1) | m_value).substring(1);
    }

    // ACCESS FUNCTIONS

    // Returns the upper part of register
    public int upper(){
        return get(m_length-1,m_halflength);
    }

    // Returns the lower part of register
    public int lower(){
        return get(m_halflength-1,0);
    }

    // Returns the value of the register
    public int get(){
        return m_value;
    }

    // Returns the value of a bit in register
    public boolean get(int position){
        if( position>=m_length || position<0 )
            throw new IndexOutOfBoundsException();
        return ((m_value>>position) & 0x01) == 0x01;
    }

    // Retuns the register value with range
    public int get(int a, int b){
        if( a>=m_length || b>a || b<0 )
            throw new IndexOutOfBoundsException();
        return (m_value >> b) & (m_maxvalue >> (m_length-a-1+b) );
    }

    // Sets the value of the register
    public void set(int val){
        m_value = val & m_maxvalue;
    }

    // Copies value of register to register 'reg'
    public void copy(Register reg){
        set(reg.get());
    }

    // Sets the value of a bit in register
    public void set(int position, boolean value){
        if( position>=m_length || position<0 )
            throw new IndexOutOfBoundsException();
        if(value)
            m_value |= (0x01<<position);
        else
            m_value &= ~(0x01<<position);
    }

}
