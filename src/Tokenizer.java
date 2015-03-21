import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Tokenizer {

    protected String[] tokens;

    public Tokenizer(String name,boolean open,String[][] replace, String splitter) throws IOException {
        String content;
        if(open){
            //get InputStream of a file
            InputStream inputFile = new FileInputStream(name);
            content = new Scanner(inputFile).useDelimiter("\\A").next();
        } else {
            content = name;
        }

        // Transform every letter to uppercase
        content = content.toUpperCase();

        // Replace commands with spaces or a 16 bit number to single representation
        if(replace != null){
            for( String[] rep : replace)
                content = content.replaceAll(rep[0], rep[1]);
        }

        // No other whitespaces are allowed
        // Tokenize it - Seperators are spaces, commas, and new lines
        // Doesn't care much about commas placement
        tokens = content.split(splitter,-1);
        //StringSplitOptions.RemoveEmptyEntries
    }

    // For debugging purposes
    public void print(){
        for (String token : tokens)
            System.out.println( token );
    }

    // Return the tokens as string array
    public String[] string() {
        return tokens;
    }
}
