package sample;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.Vector;


public class DictionaryManagement extends Dictionary {

    //////////// Read and get data method

    public void parseHTML(String HtmlString) {
        Document html = Jsoup.parse(HtmlString);
        String wordTarget = html.body().getElementsByTag("i").text();

        String wordMeaning = HtmlString;
        addWordToDictionary(wordTarget, wordMeaning, false);
    }

    public void insertFromFile() throws IOException {
        File readFile = new File("src\\models\\E_V.txt");
        Scanner reader = new Scanner(readFile);
        int count = 0;
        while (reader.hasNextLine()) {
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
    }

    public void loadHistory() {
        loadDataFromSQL("history");
    }

    private void execute(String query, Connection connection) throws Exception {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.execute();
    }

    public void saveTOSQL(String tableName){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/acprobs_database","root","276183");

            execute("delete from " + tableName, con);

            if (tableName.equals("dictionary")) {
                for (Word word: words) {
                    String meaning = word.getWord_explain();
                    StringBuffer q = new StringBuffer(word.getWord_explain());
                    int plusIndex = 0;


                    for (int i = 0; i < meaning.length(); i++) {
                        if (meaning.charAt(i) == '"') {
                            q.insert(i+plusIndex, "\\");
                            plusIndex += 1;
                        }
                    }
                    String target = word.getWord_target();
                    meaning = q.toString();
                    int status = (word.getFavourite() == true)? 1 : 0;
                    String query = "insert into dictionary (wordTarget, Meaning, favourite) "
                            + "values (\"" + target + "\", \"" + meaning + "\", \"" + status + "\")";
                    execute(query, con);
                }
            } else {
                for (String his: histories) {
                    String query = "insert into history (word) "
                            + " values (\'" + his + "\')";
                    execute(query, con);
                }
            }
            con.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void loadDataFromSQL(String tableName) {
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
                    boolean isFavourite = (res.getInt(3) == 1);
                    addWordToDictionary(wordTarget, meaning, isFavourite);
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
    public void addWordToDictionary(String word, String meaning, boolean favourite) {

        Word newWord = new Word(word, meaning, favourite);
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
