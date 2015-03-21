import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

class Parser extends Tokenizer {

    protected int[] tokenValue;

    public Parser(String name, boolean open, String datatype) throws IOException, ParseException {
        super(name,open,Parser.tokenReplace,Parser.tokenSplit);
        //print();

        // datatype doesn't matter much, if a data file
        // is sent as asm, it will still be okay with
        // a little hoverhead only
        if(datatype=="asm") {
            translateOpcode();
        } else if (datatype=="data") {
            // something here
        } else {
            throw new ParseException("Invalid datatype");
        }
        checkByteCode();
        assignValue();
    }

    public int[] value() {
        return tokenValue;
    }

    public String content(){
        String output = new String();
        for ( int val:tokenValue)
            output += new Register8(val).hex()+" ";
        return output;
    }

    // Find the opcode values in Hashmap and change those values
    private void translateOpcode() {
        for(int i=0;i<tokens.length;i++){
            // If the code is in hash table then change them
            if(Parser.tokenOpcode.containsKey(tokens[i]))
                tokens[i] = Parser.tokenOpcode.get(tokens[i]);
        }
    }

    private void checkByteCode() throws ParseException {
        for(int i=0;i<tokens.length;i++){
            // If they 8bit numbers then throw an error
            if ( !tokens[i].matches("[0-9A-F][0-9A-F]") )
                throw new ParseException("Invalid token: " + tokens[i]);
        }
    }

    // Translate the hex string representation in number
    private void assignValue() {
        int i = 0;
        tokenValue = new int[tokens.length];
        for( String token : tokens )
            tokenValue[i++] = Integer.parseInt(token,16);
    }

    // Doesn't support labels
    // All numbers are in Hexadecimal

    // Doen't support other whitespaces except space and newline
    private static String tokenSplit = "( *, *)|(( *\n+ *)+)|( +)";

    private static String[][] tokenReplace = {
        // UnTokenize everything between ";" and "\n"
        {";.*\n", "\n"},
        // Tokenize a 16 bit number to two 8 bit pair
        {"[ +,]([0-9A-F][0-9A-F])([(0-9A-F][0-9A-F])[H]? *", " $2 $1\n"},
        // Tokenize a 8 bit number
        {"[ +,]([0-9A-F][0-9A-F])[H]? *\n", " $1\n"},

        // Tokenize other commands
        {"MOV *([ABCDEHLM]) *, *([ABCDEHLM])", "MOV-$1$2 "},
        {"MVI *([ABCDEHLM]) *", "MVI-$1 "},
        {"LXI *([BDH]|SP) *", "LXI-$1 "},
        {"LDAX *([BD]) *", "LDAX-$1 "},
        {"STAX *([BD]) *", "STAX-$1 "},
        {"ADD *([ABCDEHLM]) *", "ADD-$1 "},
        {"ADC *([ABCDEHLM]) *", "ADC-$1 "},
        {"SUB *([ABCDEHLM]) *", "SUB-$1 "},
        {"SBB *([ABCDEHLM]) *", "SBB-$1 "},
        {"INR *([ABCDEHLM]) *", "INR-$1 "},
        {"DCR *([ABCDEHLM]) *", "DCR-$1 "},
        {"ANA *([ABCDEHLM]) *", "ANA-$1 "},
        {"XRA *([ABCDEHLM]) *", "XRA-$1 "},
        {"ORA *([ABCDEHLM]) *", "ORA-$1 "},
        {"CMP *([ABCDEHLM]) *", "CMP-$1 "},
        {"DAD *([BDH]|SP) *", "DAD-$1  "},
        {"INX *([BDH]|SP) *", "INX-$1 "},
        {"DCX *([BDH]|SP) *", "DCX-$1 "},
        {"PUSH *([BDH]|PSW) *", "PUSH-$1 "},
        {"POP *([BDH]|PSW) *", "POP-$1 "},
        {"RST *([0-7]) *", "RST-$1 "},
    };

    private static final Map<String , String> tokenOpcode;
    static {
        Map<String , String> opcode = new Hashtable<String, String>();
        opcode.put("NOP", "00");
        opcode.put("LXI-B", "01");
        opcode.put("STAX-B", "02");
        opcode.put("INX-B", "03");
        opcode.put("INR-B", "04");
        opcode.put("DCR-B", "05");
        opcode.put("MVI-B", "06");
        opcode.put("RLC", "07");
        opcode.put("[DSUB]", "08");
        opcode.put("DAD-B", "09");
        opcode.put("LDAX-B", "0A");
        opcode.put("DCX-B", "0B");
        opcode.put("INR-C", "0C");
        opcode.put("DCR-C", "0D");
        opcode.put("MVI-C", "0E");
        opcode.put("RRC", "0F");
        opcode.put("[AHRL]", "10");
        opcode.put("LXI-D", "11");
        opcode.put("STAX-D", "12");
        opcode.put("INX-D", "13");
        opcode.put("INR-D", "14");
        opcode.put("DCR-D", "15");
        opcode.put("MVI-D", "16");
        opcode.put("RAL", "17");
        opcode.put("[RDEL]-CYV", "18");
        opcode.put("DAD-D", "19");
        opcode.put("LDAX-D", "1A");
        opcode.put("DCX-D", "1B");
        opcode.put("INR-E", "1C");
        opcode.put("DCR-E", "1D");
        opcode.put("MVI-E", "1E");
        opcode.put("RAR", "1F");
        opcode.put("RIM", "20");
        opcode.put("LXI-H", "21");
        opcode.put("SHLD", "22");
        opcode.put("INX-H", "23");
        opcode.put("INR-H", "24");
        opcode.put("DCR-H", "25");
        opcode.put("MVI-H", "26");
        opcode.put("DAA", "27");
        opcode.put("[LDHI]", "28");
        opcode.put("DAD-H", "29");
        opcode.put("LHLD", "2A");
        opcode.put("DCX-H", "2B");
        opcode.put("INR-L", "2C");
        opcode.put("DCR-L", "2D");
        opcode.put("MVI-L", "2E");
        opcode.put("CMA", "2F");
        opcode.put("SIM", "30");
        opcode.put("LXI-SP", "31");
        opcode.put("STA", "32");
        opcode.put("INX-SP", "33");
        opcode.put("INR-M", "34");
        opcode.put("DCR-M", "35");
        opcode.put("MVI-M", "36");
        opcode.put("STC", "37");
        opcode.put("[LDSI]", "38");
        opcode.put("DAD-SP", "39");
        opcode.put("LDA", "3A");
        opcode.put("DCX-SP", "3B");
        opcode.put("INR-A", "3C");
        opcode.put("DCR-A", "3D");
        opcode.put("MVI-A", "3E");
        opcode.put("CMC", "3F");
        opcode.put("MOV-BB", "40");
        opcode.put("MOV-BC", "41");
        opcode.put("MOV-BD", "42");
        opcode.put("MOV-BE", "43");
        opcode.put("MOV-BH", "44");
        opcode.put("MOV-BL", "45");
        opcode.put("MOV-BM", "46");
        opcode.put("MOV-BA", "47");
        opcode.put("MOV-CB", "48");
        opcode.put("MOV-CC", "49");
        opcode.put("MOV-CD", "4A");
        opcode.put("MOV-CE", "4B");
        opcode.put("MOV-CH", "4C");
        opcode.put("MOV-CL", "4D");
        opcode.put("MOV-CM", "4E");
        opcode.put("MOV-CA", "4F");
        opcode.put("MOV-DB", "50");
        opcode.put("MOV-DC", "51");
        opcode.put("MOV-DD", "52");
        opcode.put("MOV-DE", "53");
        opcode.put("MOV-DH", "54");
        opcode.put("MOV-DL", "55");
        opcode.put("MOV-DM", "56");
        opcode.put("MOV-DA", "57");
        opcode.put("MOV-EB", "58");
        opcode.put("MOV-EC", "59");
        opcode.put("MOV-ED", "5A");
        opcode.put("MOV-EE", "5B");
        opcode.put("MOV-EH", "5C");
        opcode.put("MOV-EL", "5D");
        opcode.put("MOV-EM", "5E");
        opcode.put("MOV-EA", "5F");
        opcode.put("MOV-HB", "60");
        opcode.put("MOV-HC", "61");
        opcode.put("MOV-HD", "62");
        opcode.put("MOV-HE", "63");
        opcode.put("MOV-HH", "64");
        opcode.put("MOV-HL", "65");
        opcode.put("MOV-HM", "66");
        opcode.put("MOV-HA", "67");
        opcode.put("MOV-LB", "68");
        opcode.put("MOV-LC", "69");
        opcode.put("MOV-LD", "6A");
        opcode.put("MOV-LE", "6B");
        opcode.put("MOV-LH", "6C");
        opcode.put("MOV-LL", "6D");
        opcode.put("MOV-LM", "6E");
        opcode.put("MOV-LA", "6F");
        opcode.put("MOV-MB", "70");
        opcode.put("MOV-MC", "71");
        opcode.put("MOV-MD", "72");
        opcode.put("MOV-ME", "73");
        opcode.put("MOV-MH", "74");
        opcode.put("MOV-ML", "75");
        opcode.put("HLT", "76");
        opcode.put("MOV-MA", "77");
        opcode.put("MOV-AB", "78");
        opcode.put("MOV-AC", "79");
        opcode.put("MOV-AD", "7A");
        opcode.put("MOV-AE", "7B");
        opcode.put("MOV-AH", "7C");
        opcode.put("MOV-AL", "7D");
        opcode.put("MOV-AM", "7E");
        opcode.put("MOV-AA", "7F");
        opcode.put("ADD-B", "80");
        opcode.put("ADD-C", "81");
        opcode.put("ADD-D", "82");
        opcode.put("ADD-E", "83");
        opcode.put("ADD-H", "84");
        opcode.put("ADD-L", "85");
        opcode.put("ADD-M", "86");
        opcode.put("ADD-A", "87");
        opcode.put("ADC-B", "88");
        opcode.put("ADC-C", "89");
        opcode.put("ADC-D", "8A");
        opcode.put("ADC-E", "8B");
        opcode.put("ADC-H", "8C");
        opcode.put("ADC-L", "8D");
        opcode.put("ADC-M", "8E");
        opcode.put("ADC-A", "8F");
        opcode.put("SUB-B", "90");
        opcode.put("SUB-C", "91");
        opcode.put("SUB-D", "92");
        opcode.put("SUB-E", "93");
        opcode.put("SUB-H", "94");
        opcode.put("SUB-L", "95");
        opcode.put("SUB-M", "96");
        opcode.put("SUB-A", "97");
        opcode.put("SBB-B", "98");
        opcode.put("SBB-C", "99");
        opcode.put("SBB-D", "9A");
        opcode.put("SBB-E", "9B");
        opcode.put("SBB-H", "9C");
        opcode.put("SBB-L", "9D");
        opcode.put("SBB-M", "9E");
        opcode.put("SBB-A", "9F");
        opcode.put("ANA-B", "A0");
        opcode.put("ANA-C", "A1");
        opcode.put("ANA-D", "A2");
        opcode.put("ANA-E", "A3");
        opcode.put("ANA-H", "A4");
        opcode.put("ANA-L", "A5");
        opcode.put("ANA-M", "A6");
        opcode.put("ANA-A", "A7");
        opcode.put("XRA-B", "A8");
        opcode.put("XRA-C", "A9");
        opcode.put("XRA-D", "AA");
        opcode.put("XRA-E", "AB");
        opcode.put("XRA-H", "AC");
        opcode.put("XRA-L", "AD");
        opcode.put("XRA-M", "AE");
        opcode.put("XRA-A", "AF");
        opcode.put("ORA-B", "B0");
        opcode.put("ORA-C", "B1");
        opcode.put("ORA-D", "B2");
        opcode.put("ORA-E", "B3");
        opcode.put("ORA-H", "B4");
        opcode.put("ORA-L", "B5");
        opcode.put("ORA-M", "B6");
        opcode.put("ORA-A", "B7");
        opcode.put("CMP-B", "B8");
        opcode.put("CMP-C", "B9");
        opcode.put("CMP-D", "BA");
        opcode.put("CMP-E", "BB");
        opcode.put("CMP-H", "BC");
        opcode.put("CMP-L", "BD");
        opcode.put("CMP-M", "BE");
        opcode.put("CMP-A", "BF");
        opcode.put("RNZ", "C0");
        opcode.put("POP-B", "C1");
        opcode.put("JNZ", "C2");
        opcode.put("JMP", "C3");
        opcode.put("CNZ", "C4");
        opcode.put("PUSH-B", "C5");
        opcode.put("ADI", "C6");
        opcode.put("RST-0", "C7");
        opcode.put("RZ", "C8");
        opcode.put("RET", "C9");
        opcode.put("JZ", "CA");
        opcode.put("[RSTV]", "CB");
        opcode.put("CZ", "CC");
        opcode.put("CALL", "CD");
        opcode.put("ACI", "CE");
        opcode.put("RST-1", "CF");
        opcode.put("RNC", "D0");
        opcode.put("POP-D", "D1");
        opcode.put("JNC", "D2");
        opcode.put("OUT", "D3");
        opcode.put("CNC", "D4");
        opcode.put("PUSH-D", "D5");
        opcode.put("SUI", "D6");
        opcode.put("RST-2", "D7");
        opcode.put("RC", "D8");
        opcode.put("[SHLX]", "D9");
        opcode.put("JC", "DA");
        opcode.put("IN", "DB");
        opcode.put("CC", "DC");
        opcode.put("[JNUI]", "DD");
        opcode.put("SBI", "DE");
        opcode.put("RST-3", "DF");
        opcode.put("RPO", "E0");
        opcode.put("POP-H", "E1");
        opcode.put("JPO", "E2");
        opcode.put("XTHL", "E3");
        opcode.put("CPO", "E4");
        opcode.put("PUSH-H", "E5");
        opcode.put("ANI", "E6");
        opcode.put("RST-4", "E7");
        opcode.put("RPE", "E8");
        opcode.put("PCHL", "E9");
        opcode.put("JPE", "EA");
        opcode.put("XCHG", "EB");
        opcode.put("CPE", "EC");
        opcode.put("[LHLX]", "ED");
        opcode.put("XRI", "EE");
        opcode.put("RST-5", "EF");
        opcode.put("RP", "F0");
        opcode.put("POP-PSW", "F1");
        opcode.put("JP", "F2");
        opcode.put("DI", "F3");
        opcode.put("CP", "F4");
        opcode.put("PUSH-PSW", "F5");
        opcode.put("ORI", "F6");
        opcode.put("RST-6", "F7");
        opcode.put("RM", "F8");
        opcode.put("SPHL", "F9");
        opcode.put("JM", "FA");
        opcode.put("EI", "FB");
        opcode.put("CM", "FC");
        opcode.put("[JUI]", "FD");
        opcode.put("CPI", "FE");
        opcode.put("RST-7", "FF");
        tokenOpcode = Collections.unmodifiableMap(opcode);
    }
}
