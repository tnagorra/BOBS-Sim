import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

class Seperator extends Tokenizer {

    protected String[][] tokenValue;

    public Seperator(String name, boolean open) throws IOException, ParseException {
        super(name,open,Seperator.tokenReplace,Seperator.tokenSplit);
        assignValue();
    }

    private void assignValue(){
        tokenValue = new String[tokens.length][2];

        String patternString = "^[0-9A-F][0-9A-F][0-9A-F][0-9A-F].*";
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        for(int i=0;i<tokens.length;i++){
            if(pattern.matcher(tokens[i]).matches()){
                tokenValue[i][0]= tokens[i].substring(0,4);
                tokenValue[i][1] = tokens[i].replaceFirst("[0-9A-F][0-9A-F][0-9A-F][0-9A-F] ","");
            } else {
                tokenValue[i][0]= "8000";
                tokenValue[i][1] = tokens[i];
            }
            // System.out.println( tokenValue[i][0]+":"+tokenValue[i][1]+"#");
        }

    }

    public String[][] value() {
        return tokenValue;
    }

    // @&@& is just some splitting value
    private static String tokenSplit = " *@&@& *";

    private static String[][] tokenReplace = {
        {
            "^[ *\n*]*@([0-9A-F][0-9A-F][0-9A-F][0-9A-F])[ *\n*]*",
            "$1 "
        },
        {
            "[ *\n*]*@([0-9A-F][0-9A-F][0-9A-F][0-9A-F])[ *\n*]*",
            "@&@& $1 "
        }
    };
}
