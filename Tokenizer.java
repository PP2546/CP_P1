public interface Tokenizer {
    boolean hasNext();
    String next();
    void repositionToStart();
}
