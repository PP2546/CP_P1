import java.util.StringTokenizer;

public class AssemblerTokenizer implements Tokenizer {
    StringTokenizer start_token;
    StringTokenizer repostion_token;

    AssemblerTokenizer(String Assembly){
        start_token = new StringTokenizer(Assembly , " \t");
        repostion_token = new StringTokenizer(Assembly , " \t");
    }

    @Override
    public boolean hasNext() {
        return start_token.hasMoreTokens();
    }

    @Override
    public String next() {
        return start_token.nextToken();
    }

    @Override
    public void repositionToStart() {
        start_token = repostion_token;
    }
}
