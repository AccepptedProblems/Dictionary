package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.Vector;


public class DictionaryManagement extends Dictionary {

    //////////// Read and get data method

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
        while (reader.hasNextLine() && count < 100) {
            String wordLine = reader.nextLine();
            parseHTML(wordLine);
            count += 1;
        }
        reader.close();

    }

    public void updateFavourite() {
        if(favourite.size() != 0) favourite.clear();
        for (Word word: words) {
            if (word.getFavourite()) {
                favourite.add(word.getWord_target());
            }
        }
    }

    public void updateFavourite(String word) {
        Word currentWord = new Word(word, "");
        int wordIndex = indexOfWord(currentWord);

        words.get(wordIndex).setFavourite(!words.get(wordIndex).getFavourite());
    }

    public void loadDictionaries () {
        loadDataFromSQL("dictionary");


    public void loadHistory () {
        loadDataFromSQL("history");
    }

    public void loadDataFromSQL(String tableName) {
        Vector <Word> results = new Vector<>();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/acprobs_database","root","276183");

            Statement stmt=con.createStatement();
            ResultSet res=stmt.executeQuery("select * from " + tableName);
            while(res.next()) {
                String wordTarget = res.getString(1);
                if (tableName.equals("dictionary")) {
                    String meaning = res.getString(2);
                    addWordToDictionary(wordTarget, meaning);
                } else {
                    addWordToHistory(wordTarget);
                }
            }
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Controller Method

    // Find list of word that begin with String(word)
    public Vector <String> wordsStartWith(String word) {
        Vector <String> result = new Vector<>();

        for (Word get_word: words) {
            if (get_word.getWord_target().startsWith(word)) {
                result.add(get_word.getWord_target());
            }
        }
        return result;
    }

    public Word findWord (String searchWord) {
        Word word = new Word(searchWord, "");
        int wordIndex = indexOfWord(word);
        return words.get(wordIndex);
    }

    //Add to dictionary
    public void addWordToDictionary(String word, String meaning) {

        Word newWord = new Word(word, meaning);
        // Get index of word in vector. If it doesn't exist, index = -1
        int wordIndex = indexOfWord(newWord);
        if (wordIndex != -1) {
            //TODO: Add an alert that word has appeared
            return;
        }
        wordIndex = indexToInsert(newWord);

        if (words.size() == 0 || wordIndex == words.size()) {
            words.add(newWord);
        } else {
            words.add(wordIndex, newWord);
        }
    }

    public void addWordToHistory(String word) {
        if (histories.contains(word) == true) {
            histories.remove(word);
        }
        if (histories.size() == 0) histories.add(word);
        else histories.add(0, word);
    }

    public void changeExplain(Word newWord) {
        int wordIndex = indexOfWord(newWord);
        words.set(wordIndex, newWord);
    }

    public void deleteWordFromDictionary(Word word) {
        int wordIndex = indexOfWord(word);
        words.removeElementAt(wordIndex);

        wordIndex = indexOfWord(word);
        if (wordIndex != -1) {
            histories.removeElementAt(wordIndex);
        }
    }











}
