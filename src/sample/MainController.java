package sample;

import javafx.application.Platform;
import com.sun.speech.freetts.VoiceManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;


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
    public Button setFavouriteButton;
    @FXML
    public Label targetLabel;
    @FXML
    public TextField searchTextField;
    @FXML
    public ListView searchListView;
    @FXML
    public WebView meaningWebView;
    @FXML
    public Label FavouriteLabel;
    @FXML
    public Button dictionaryListButton;
    @FXML
    public Button changeControllerButton;

    DictionaryManagement dictionaryManager = new DictionaryManagement();

    @FXML
    public void ChangeScene(ActionEvent event) throws IOException {
        dictionaryManager.saveTOSQL("dictionary");
        dictionaryManager.saveTOSQL("history");

        Parent tableViewParent = FXMLLoader.load(getClass().getResource("sample2.fxml"));
        Scene tableViewScene = new Scene(tableViewParent, 900, 600);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(tableViewScene);
        window.show();
    }

    public void exitApplication() {
        dictionaryManager.saveTOSQL("dictionary");
        dictionaryManager.saveTOSQL("history");
        Platform.exit();
    }

    public void showHTML(String content) {
        meaningWebView.getEngine().loadContent(content);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        targetLabel.setText("");

        FavouriteLabel.setVisible(false);

        try {
            this.initializeWordList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dictionaryListButton.setOnAction(actionEvent -> {
            searchListView.getItems().clear();
            searchListView.getItems().addAll(dictionaryManager.wordsStartWith(""));
        });

        searchTextField.setOnAction(event -> {
            if (searchTextField.getText().equals("")) {
                targetLabel.setText("");
                FavouriteLabel.setVisible(false);
                showHTML("");
                searchListView.getItems().clear();
                searchListView.getItems().addAll(dictionaryManager.wordsStartWith(""));
            }
            String searchedWord = searchTextField.getText();
            Vector<String> result = dictionaryManager.wordsStartWith(searchedWord);

            if (result.size() > 0) dictionaryManager.addWordToHistory(result.get(0));

            searchListView.getItems().clear();
            searchListView.getItems().addAll(result);
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
                showHTML(firstSearchedWord.getWord_explain());

                FavouriteLabel.setVisible(firstSearchedWord.getFavourite());
                searchListView.getSelectionModel().select(0);
                dictionaryManager.addWordToHistory(firstSearchedWord.getWord_target());
            } else {
                showHTML("");
                //TODO: Get a API call to google translate
            }
        });

        searchListView.setOnMouseClicked(event -> {
            String searchStr = (String) searchListView.getSelectionModel().getSelectedItem();
            Word searchedWord = dictionaryManager.findWord(searchStr);

            showHTML(searchedWord.getWord_explain());
            targetLabel.setText(searchStr);
            FavouriteLabel.setVisible(searchedWord.getFavourite());
            dictionaryManager.addWordToHistory(searchStr);
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

                FavouriteLabel.setVisible(false);
                targetLabel.setText("");
                showHTML("");
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
                    return new Pair<>(targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(word -> {
                dictionaryManager.addWordToDictionary(word.getKey(), word.getValue(), false);
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
                    return new Pair<>(targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(word -> {
                Word newWord = new Word(word.getKey(), word.getValue());
                dictionaryManager.changeExplain(newWord);
                showHTML(newWord.getWord_explain());
            });

        });

        favouriteButton.setOnAction(actionEvent -> {
            dictionaryManager.updateFavourite();
            searchListView.getItems().clear();
            searchListView.getItems().addAll(dictionaryManager.favourite);
        });

        setFavouriteButton.setOnAction(actionEvent -> {
            if (targetLabel.getText().equals("")) return;

            dictionaryManager.updateFavourite(targetLabel.getText());
            Word currentWord = dictionaryManager.findWord(targetLabel.getText());
            FavouriteLabel.setVisible(currentWord.getFavourite());
        });

        historyButton.setOnAction(actionEvent -> {
            //TODO: -Add some function here
            searchListView.getItems().clear();
            searchListView.getItems().addAll(dictionaryManager.histories);
        });

        spellButton.setOnMouseClicked(event -> {
            String searchedWord = targetLabel.getText();
            speech(searchedWord);
        });

    }

    
    public void initializeWordList() throws IOException {
    //        dictionaryManager.insertFromFile();
        dictionaryManager.loadHistory();
        dictionaryManager.loadDictionaries();
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
