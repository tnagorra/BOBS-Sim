import java.io.IOException;
import java.util.Arrays;

public class Main {
    // Main block
    public static void main(String args[]) throws IOException{
        try {
            String filename = (args.length > 0) ? args[0] : "asm/test.asm";
            String datafilename = (args.length > 1) ? args[1] : "asm/testdata.asm";
            Parser asmParser = new Parser(filename,true,"asm");
            Parser dataParser = new Parser(datafilename,true,"data");

            Memory memory = new Memory(65536);
            memory.load(new Register16(0x9000),dataParser.value());
            memory.load(new Register16(0x8000),asmParser.value());

            Microprocessor up = new Microprocessor(memory);
            up.start(new Register16(0x8000),true,true);
            // memory.print(new Register16(0x9000),11);

        } catch (ParseException ex){
            System.err.println("Caught MyException: " + ex.getMessage());
        }

    }

}
