import java.io.IOException;
import java.util.Arrays;

public class Main {
    // Main block
    public static void main(String args[]) throws IOException,InterruptedException {
        try {
            String filename = (args.length > 0) ? args[0] : "asm/test.asm";
            String datafilename = (args.length > 1) ? args[1] : "asm/testdata.asm";
            Parser asmParser = new Parser(filename,true,"asm");
            Parser dataParser = new Parser(datafilename,true,"data");

            Microprocessor up = new Microprocessor();
            Memory memory = new Memory(65536);
            Connector.connect(up,memory);
            memory.load(new Register16(0x9000),dataParser.value());
            memory.load(new Register16(0x8000),asmParser.value());        
            memory.start();
            up.start(new Register16(0x8000),false,false);

        } catch (ParseException ex){
            System.err.println("Caught MyException: " + ex.getMessage());
        }
    }
}
