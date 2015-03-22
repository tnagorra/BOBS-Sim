public class Flag extends Register8 {

    // Constructor of Flag
    public Flag(int val) {
        super(val);
    }

    // Get proper index for flag selection
    private int index(String a){
        switch(a){
            case "S": return 7;
            case "Z": return 6;
            case "AC": return 4;
            case "P": return 2;
            case "C": return 0;
            default: return -1;
        }
    }

    // Get the flag from string
    public boolean get(String flg){
        return get(index(flg));
    }

    // Update the flag bit
    public void set(String flg,boolean value){
        set(index(flg),value);
    }

    // Update the flag using register value
    public void set(Register8 reg){
        super.set(reg.m_value);
    }

    // Return flags as string
    public String value() {
        String flags = "";
        if( get("C") ) flags += "C ";
        if( get("P") ) flags += "P ";
        if( get("AC") ) flags += "AC ";
        if( get("Z") ) flags += "Z ";
        if( get("S") ) flags += "S ";
        return flags;
    }

}
