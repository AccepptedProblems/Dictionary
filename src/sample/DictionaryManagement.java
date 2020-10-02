package sample;

import com.sun.speech.freetts.VoiceManager;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

public class DictionaryManagement extends Dictionary {

    public void insertFromCommandline() {
        int n;
        String s;
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        s = scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String target;
            String explain;
            Word temp;

            target = scanner.nextLine();
            explain = scanner.nextLine();
            temp = new Word(target, explain);

            words.add(temp);
        }
        scanner.close();
    }

    public Vector<Word> insertFromFile(String s) throws IOException {
        File myObj = new File("Dictionaries.txt");
        Scanner reader = new Scanner(myObj);
        while (reader.hasNextLine()) {
            String target;
            String explain;
            Word temp;
            target = reader.next();
            explain = reader.nextLine();

            temp = new Word(target, explain);
            words.add(temp);
        }
        reader.close();
        return words;
    }

    public void dictionaryLookup() throws IOException {
        String wordToLookup;
        Scanner scanner = new Scanner(System.in);
        wordToLookup = scanner.next();
        for (int i = 0; i < words.size(); i++) {
            if (wordToLookup.equals(words.get(i).getWord_target())) {
                System.out.println(words.get(i).getWord_explain());
            }
        }
    }

    public void eraseWord(String s) {
        for (int i = 0; i < words.size(); i++) {
            if (s.equals(words.get(i).getWord_target())) {
                words.remove(i);
                break;
            }
        }
    }

    public void addWordToDictionary() throws IOException {
        String s1, s2;
        Word temp;
        Scanner scanner = new Scanner(System.in);
        s1 = scanner.next();
        s2 = scanner.nextLine();
        temp = new Word(s1, s2);
        words.add(temp);
    }

    /** Thay đổi nghĩa bằng cách nhập từ tiếng anh và nghĩa mới */
    public void changeExplain() {
        String targetToChangeExplain, newExplain;
        Scanner scanner = new Scanner(System.in);
        targetToChangeExplain = scanner.next();
        newExplain = scanner.nextLine();
        for (int i = 0; i < words.size(); i++) {
            if (targetToChangeExplain.equals(words.get(i).getWord_target())) {
                words.get(i).setWord_explain(newExplain);
            }
            break;
        }
    }

    public void dictionaryExportToFile() throws IOException {
        File file = new File("dictionaries.txt");
        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        for (int i = 0; i < words.size(); i++) {
            outputStreamWriter.write(words.get(i).getWord_target()+ " " + words.get(i).getWord_explain() + "\n");
        }
        outputStreamWriter.flush();
    }



    

}
