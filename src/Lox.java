import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException{
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
        } else if (args.length == 1){
            runFile(args[0]);
        } else{
            runPrompt();
        }
    }

    // runs input file
    private static void runFile(String path) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError){
            System.exit(65);
        }
        if(hadRuntimeError){
            System.exit(70);
        }
    }

    // accept input from prompt and execute user input
    private static void runPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.println("> ");
            String line = reader.readLine();
            if(line == null){ // CTRL-D triggers null from readline
                break;
            }
            run(line);
            hadError = false;
        }
    }

    // core function to run interpreter
    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // stop if there was an error
        if(hadError){
            return;
        }

        //System.out.println(new AstPrinter().print(expression));
        interpreter.interpret(statements);
    }

    // error handling functions
    // could be moved to a different class/interface
    static void error(int line, String message){
        report(line, "", message);
    }

    static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line, "  at end", message);
        }else{
            report(token.line, " at " + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error){
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }

}


