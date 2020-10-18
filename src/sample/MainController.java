package sample;

import com.sun.speech.freetts.VoiceManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

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
    public Label targetLabel;
    @FXML
    public TextField searchTextField;
    @FXML
    public ListView searchListView;
    @FXML
    public TextArea meaningTextArea;

    DictionaryManagement dictionaryManager = new DictionaryManagement();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        meaningTextArea.setEditable(false);
        targetLabel.setText("");

        try {
            this.initializeWordList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchTextField.setOnAction(event -> {
            if (searchTextField.getText().equals("")) {
                targetLabel.setText("");
                meaningTextArea.clear();
                searchListView.getItems().clear();
                searchListView.getItems().addAll(dictionaryManager.wordsStartWith(""));
            }
        });

        searchButton.setOnMouseClicked(event -> {
            String searchedWord = searchTextField.getText();
            Vector<String> result = dictionaryManager.wordsStartWith(searchedWord);
            searchListView.getItems().clear();
            searchListView.getItems().addAll(result);

            if (!searchedWord.equals("") && result.size() > 0) {
                //First word in result
                searchedWord = result.firstElement();

                Word firstSearchedWord = dictionaryManager.findWord(searchedWord);

                targetLabel.setText(firstSearchedWord.getWord_target());
                meaningTextArea.setText(firstSearchedWord.getWord_explain());

                searchListView.getSelectionModel().select(0);
            } else {
                meaningTextArea.clear();
                //TODO: Get a API call to google translate
            }
        });

        searchListView.setOnMouseClicked(event -> {
            String searchStr = (String) searchListView.getSelectionModel().getSelectedItem();
            Word searchedWord = dictionaryManager.findWord(searchStr);

            meaningTextArea.setText(searchedWord.getWord_explain());
            targetLabel.setText(searchStr);

        });

        deleteButton.setOnAction(event -> {
            String deleteTargetWord = targetLabel.getText();
            if (deleteTargetWord.equals("")) return;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Alert");
                alert.setContentText("Do you want to delete word \"" + deleteTargetWord + "\" ?");

                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    Word deleteWord = new Word(deleteTargetWord, "");
                    dictionaryManager.deleteWordFromDictionary(deleteWord);

                    meaningTextArea.clear();
                    searchListView.getItems().clear();
                    searchListView.getItems().addAll(dictionaryManager.wordsStartWith(""));
                }
        });

        addButton.setOnAction(event -> {

            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add Word");
            dialog.setHeaderText("Add word in Dictionary");

            ButtonType addButtonType = new ButtonType("add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
            Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
            addButton.setDisable(true);

            GridPane gridPane = new GridPane();
            TextField targetTextField = new TextField();
            targetTextField.setPromptText("target");
            TextField explainTextField = new TextField();
            explainTextField.setPromptText("explain");

            targetTextField.textProperty().addListener((observableValue, s, t1) -> {
                addButton.setDisable(t1.trim().isEmpty());
            });

            gridPane.add(new Label("Target"), 0, 0);
            gridPane.add(targetTextField, 0, 1);
            gridPane.add(new Label("Explain"), 1, 0);
            gridPane.add(explainTextField, 1, 1);


            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return new Pair<> (targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent( word -> {
                dictionaryManager.addWordToDictionary(word.getKey(), word.getValue());
                searchListView.getItems().clear();
                searchListView.getItems().addAll(dictionaryManager.wordsStartWith(""));
            });

        });

        changeButton.setOnAction(event -> {
            String choosenWord = targetLabel.getText();
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Change Word");
            dialog.setHeaderText("Change word meaning");

            ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);
            Node changeButton = dialog.getDialogPane().lookupButton(changeButtonType);

            GridPane gridPane = new GridPane();

            TextField targetTextField = new TextField();
            targetTextField.setEditable(false);
            targetTextField.setText(choosenWord);

            TextField explainTextField = new TextField();
            explainTextField.setPromptText("explain");

            targetTextField.textProperty().addListener((observableValue, s, t1) -> {
                changeButton.setDisable(t1.trim().isEmpty());
            });

            gridPane.add(new Label("Target"), 0, 0);
            gridPane.add(targetTextField, 0, 1);
            gridPane.add(new Label("Explain"), 1, 0);
            gridPane.add(explainTextField, 1, 1);


            dialog.getDialogPane().setContent(gridPane);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == changeButtonType) {
                    return new Pair<String, String> (targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent( word -> {
                Word newWord = new Word(word.getKey(), word.getValue());
                dictionaryManager.changeExplain(newWord);
                meaningTextArea.setText(word.getValue());
            });

        });

        favouriteButton.setOnAction(actionEvent -> {
            
        });

        historyButton.setOnAction(actionEvent -> {
            //TODO: -Add some function here
        });

        spellButton.setOnMouseClicked(event -> {
            String searchedWord = searchTextField.getText();
            speech(searchedWord);
        });
    }

    public void initializeWordList() throws IOException {
        dictionaryManager.loadDataFromSQL("dictionary");
        searchListView.getItems().addAll(dictionaryManager.wordsStartWith(""));
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
