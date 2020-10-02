package sample;

import com.sun.speech.freetts.VoiceManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    public Button addButton;
    @FXML
    public Button historyButton;
    @FXML
    public Button favouriteButton;
    @FXML
    public Button spellButton;
    @FXML
    public Button changeButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button searchButton;
    @FXML
    public TextField searchTextField;
    @FXML
    public ListView searchListView;
    @FXML
    public TextArea meaningTextArea;

    Map<String, String> dictionary = new HashMap<String, String>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            this.initializeWordList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchButton.setOnMouseClicked(event -> {
            String searchedWord = searchTextField.getText();
            if (searchedWord != null && searchedWord.equals("") == false) {
                String wordMeaning = dictionary.get(searchedWord);
                meaningTextArea.setText(wordMeaning);
                Vector<String> result = new Vector<String>();
                for (String i : dictionary.keySet()) {
                    if (i.startsWith(searchedWord)) {
                        result.add(i);
                    }
                }
                searchListView.getItems().clear();
                searchListView.getItems().addAll(result);
            } else {
                searchListView.getItems().clear();
                searchListView.getItems().addAll(dictionary.keySet());
            }
        });

        deleteButton.setOnAction(event -> {

            String deleteWord = searchTextField.getText();
            if (deleteWord.equals("") != true && deleteWord != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Word");
                alert.setContentText("Do you want to delete word \"" + deleteWord + "\" ?");
                //alert.show();
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {

                    dictionary.remove(deleteWord);
                    searchTextField.clear();
                    searchListView.getItems().clear();
                    meaningTextArea.clear();
                    searchListView.getItems().addAll(dictionary.keySet());
                }
            }
        });
        searchListView.setOnMouseClicked(event -> {
            String searchedWord = (String) searchListView.getSelectionModel().getSelectedItem();
            if (searchedWord != null && !searchedWord.equals("")) {
                String wordMeaning = dictionary.get(searchedWord);
                meaningTextArea.setText(wordMeaning);
                searchTextField.setText(searchedWord);
            }
        });

        addButton.setOnAction(event -> {
            String addTarget;
            String addExplain;
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add Word");
            dialog.setHeaderText("Add word in Dictionary");

            ButtonType addButtonType = new ButtonType("add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            GridPane gridPane = new GridPane();
            TextField targetTextField = new TextField();
            targetTextField.setPromptText("target");
            TextField explainTextField = new TextField();
            explainTextField.setPromptText("explain");

            gridPane.add(new Label("Target"), 0, 0);
            gridPane.add(targetTextField, 0, 1);
            gridPane.add(new Label("Explain"), 1, 0);
            gridPane.add(explainTextField, 1, 1);


            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return new Pair<String, String>(targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(word -> {
                dictionary.put(word.getKey(), word.getValue());
                //searchListView.getItems().clear();
                searchListView.getItems().add(word.getKey());
            });
        });

        changeButton.setOnAction(event -> {
            String addTarget;
            String addExplain;
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("change Word");

            ButtonType changeButtonType = new ButtonType("change", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

            GridPane gridPane = new GridPane();
            TextField targetTextField = new TextField();
            TextField explainTextField = new TextField();

            gridPane.add(new Label("Target"), 0, 0);
            gridPane.add(targetTextField, 0, 1);
            gridPane.add(new Label("New explanation"), 1, 0);
            gridPane.add(explainTextField, 1, 1);


            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == changeButtonType) {
                    return new Pair<String, String>(targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(word -> {
                dictionary.remove(targetTextField.getText());
                searchTextField.clear();
                searchListView.getItems().clear();
                meaningTextArea.clear();
                searchListView.getItems().addAll(dictionary.keySet());
                dictionary.put(word.getKey(), word.getValue());
                searchListView.getItems().add(word.getKey());
            });
        });

        spellButton.setOnMouseClicked(event -> {
            String searchedWord = searchTextField.getText();
            speech(searchedWord);
        });
    }

    public void initializeWordList() throws IOException {
        DictionaryManagement dictionaryManagement = new DictionaryManagement();
        Vector<Word> words = dictionaryManagement.insertFromFile("dictionaries.txt");
        for (int i = 0; i < words.size(); i++) {
            dictionary.put(words.get(i).getWord_target(), words.get(i).getWord_explain());
        }
        searchListView.getItems().addAll(dictionary.keySet());
    }


    private void speech(String searchedWord) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        com.sun.speech.freetts.Voice syntheticVoice = voiceManager.getVoice("kevin16");
        syntheticVoice.allocate();
        syntheticVoice.speak(searchedWord);
        syntheticVoice.deallocate();


    }
}
