import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // offsets to index into string
    private int start = 0;
    private int current = 0;
    // used to store location of lexeme/token
    private int line = 1;

    Scanner(String source){
        this.source = source;
    }

    List<Token> scanTokens(){
        while(!isAtEnd()){
            // beginning of next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));

        return tokens;
    }

    private boolean isAtEnd(){
        return current >= source.length();
    }
}
