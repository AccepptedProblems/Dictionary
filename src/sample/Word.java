package sample;

public class Word {
    private String word_target;
    private String word_explain;

    public Word() {
        word_target = null;
        word_explain = null;
    }

    public Word(String word_target, String word_explain) {
        this.word_target = word_target;
        this.word_explain = word_explain;
    }

    public String getWord_explain() {
        return word_explain;
    }

    public String getWord_target() {
        return word_target;
    }

    public void setWord_explain(String word_explain) {
        this.word_explain = word_explain;
    }

    public void setWord_target(String word_target) {
        this.word_target = word_target;
    }

    // Compare if word's dictionary order is greater than an other word
    public boolean isGreaterThan(Word b) {
        String word = this.getWord_target();
        String target = b.getWord_target();

        int len = Math.min(word.length(), target.length());

        for (int i = 0; i < len; i++) {

            int val1 = word.charAt(i);
            int val2 = target.charAt(i);

            if (val1 > val2) {
                return true;
            }
            if (val1 < val2) {
                return false;
            }
        }

        if (word.length() > target.length()) return true;
        return false;

    }

    public boolean sameTargetWord(Word b) {
        return this.getWord_target().equals(b.getWord_target());
    }

}
