package sample;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class DictionaryCommandline extends DictionaryManagement{
    public void showAllWords() {
        System.out.printf("%-15s|%-35s|%s\n", "No", "English", "Vietnamese");
        for (int i = 0; i < words.size(); i++) {
            System.out.printf("%-15s|%-35s|%s\n", i + 1, words.get(i).getWord_target(), words.get(i).getWord_explain());
        }
    }
    public  void dictionaryBasic() throws IOException {
        insertFromCommandline();
        showAllWords();
    }

    public void dictionaryAdvanced() throws IOException {
        insertFromFile("dictionaries.txt");
        showAllWords();
        dictionaryLookup();
    }

    public Vector<String> dictionarySearcher() {
        String findWord;
        Scanner scanner = new Scanner(System.in);
        findWord = scanner.nextLine();
        Vector<String> result = new Vector<String>();
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getWord_target().startsWith(findWord)) {
                result.add(words.get(i).getWord_target());
            }
        }
        return result;
    }
}
