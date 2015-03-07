import java.io.IOException;
import java.util.Arrays;

public class Main {
    // Main block
    public static void main(String args[]) throws IOException{
        try {
            Parser myParser = new Parser("asm/test.asm",true);
            Memory memory = new Memory(65536);
            Microprocessor up = new Microprocessor(memory);

            memory.load(new Register16(0x0000),myParser.value());
            up.start(new Register16(0x0000));
            up.print();
        } catch (ParseException ex){
            System.err.println("Caught MyException: " + ex.getMessage());
        }

    }

}
