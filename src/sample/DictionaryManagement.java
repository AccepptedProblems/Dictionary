package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;


public class DictionaryManagement extends Dictionary {

    public void parseHTML(String HtmlString) {
        Document html = Jsoup.parse(HtmlString);
        String wordTarget = html.body().getElementsByTag("i").text();

        String wordMeaning = "";
        Elements words = html.body().getElementsByTag("ul");
        for (Element word: words) {
            wordMeaning += word.text() + "\n";
        }
        addWordToDictionary(wordTarget, wordMeaning);
    }

    public void insertFromFile() throws IOException {

        File readFile = new File("src\\models\\E_V.txt");
        Scanner reader = new Scanner(readFile);
        int count = 0;
        while (reader.hasNextLine() && count < 10) {
            String wordLine = reader.nextLine();
            parseHTML(wordLine);
            count += 1;
        }
        reader.close();

    }

    public void loadFromHistory()  {
        try {
            File file = new File("history.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                String meaning = "";
                String info = "";
                String[] result = line.split("\t");
                info+= result[0];
                meaning += result[1];
                Word temp = new Word(info, meaning);
                histories.add(temp);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void WriteToFile(String word)  {
        try {
            File file = new File("history.txt");
            FileWriter writer = new FileWriter(file);
            writer.write(word);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            //System.out.println("bug o day nay");
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


    public void changeExplain(Word newWord) {
        int wordIndex = indexOfWord(newWord);
        words.set(wordIndex, newWord);
    }

    public void deleteWordFromDictionary(Word word) {
        int wordIndex = indexOfWord(word);
        words.removeElementAt(wordIndex);
    }

    public void addToDictionaryFile(Word word) throws IOException {
        File file = new File("sample\\Dictionaries.txt");

        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

        outputStreamWriter.write(word.getWord_target()+ " " + word.getWord_explain() + "\n");

        outputStreamWriter.flush();
    }









}
