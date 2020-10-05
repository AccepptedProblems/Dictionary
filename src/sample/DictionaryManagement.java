package sample;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

public class DictionaryManagement extends Dictionary {

    public void insertFromFile() throws IOException {
        File myObj = new File("src\\sample\\Dictionaries.txt");

        Scanner reader = new Scanner(myObj);

        while (reader.hasNextLine()) {
            String target;
            String explain;

            target = reader.next();
            explain = reader.nextLine();

            Word temp = new Word(target, explain);
            words.add(temp);
        }
        reader.close();
    }

    public Vector <String> wordStartWith(String word) {
        Vector <String> result = new Vector<String>();

        for (Word get_word: words) {
            if (get_word.getWord_target().startsWith(word)) {
                result.add(get_word.getWord_target());
            }
        }

        return result;
    }

    public void addWordToDictionary(String word, String meaning) {
        System.out.println("add word");

        Word newWord = new Word(word, meaning);
        int wordIndex = indexOfWord(newWord);

        if (wordIndex != -1) {
            System.out.println("bug o day nay");
            //TODO: Add an alert that word has appeared
            return;
        }
        wordIndex = indexToInsert(newWord);

        if (words.size() == 0 || wordIndex == words.size()) {
            words.add(newWord);
        }
        else {
            words.add(wordIndex, newWord);
        }
    }

    /** Thay đổi nghĩa bằng cách nhập từ tiếng anh và nghĩa mới */
    public void changeExplain(Word newWord) {
        int wordIndex = indexOfWord(newWord);
        words.set(wordIndex, newWord);
    }

    public void deleteWordFromDictionary(Word word) {
        int wordIndex = indexOfWord(word);
        Word deleteWord = words.get(wordIndex);

        words.remove(word);
    }

    public void dictionaryExportToFile() throws IOException {
        File file = new File("sample\\dictionaries.txt");

        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        for (int i = 0; i < words.size(); i++) {
            outputStreamWriter.write(words.get(i).getWord_target()+ " " + words.get(i).getWord_explain() + "\n");
        }
        outputStreamWriter.flush();
    }



    

}
