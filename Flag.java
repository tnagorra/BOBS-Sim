public class Flag extends Register8 {

    // Constructor
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

    // Get the flag
    public boolean get(String flg){
        return get(index(flg));
    }
    // Update the flag
    public void set(String flg,boolean value){
        set(index(flg),value);
    }

    public void set(Register8 reg){
        super.set(reg.m_value);
    }

    // DEBUGGING

    // Displays the flag
    public void print() {
        System.out.print("Flags\t: " );
        if( get("C") ) System.out.print("C ");
        if( get("P") ) System.out.print("P ");
        if( get("AC") ) System.out.print("AC ");
        if( get("Z") ) System.out.print("Z ");
        if( get("S") ) System.out.print("S ");
        System.out.print("\n");
    }
}
