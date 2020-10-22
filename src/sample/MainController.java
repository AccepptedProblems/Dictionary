package sample;

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
    public TextArea meaningTextArea;
    @FXML
    public Label FLabel;

    @FXML
    public void ChangeScene(ActionEvent event) throws IOException {
        Parent tableViewParent = FXMLLoader.load(getClass().getResource("sample2.fxml"));
        Scene tableViewScene = new Scene(tableViewParent);

        //This line gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();
    }

    @FXML
    public Button changeSceneButton1;


    DictionaryManagement dictionaryManager = new DictionaryManagement();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        meaningTextArea.setEditable(false);
        targetLabel.setText("");
        FLabel.setVisible(false);

        try {
            this.initializeWordList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchTextField.setOnAction(event -> {
            if (searchTextField.getText().equals("")) {
                targetLabel.setText("");
                FLabel.setVisible(false);
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

                FLabel.setVisible(firstSearchedWord.getFavourite());
                searchListView.getSelectionModel().select(0);
                dictionaryManager.addWordToHistory(firstSearchedWord.getWord_target());
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
            FLabel.setVisible(searchedWord.getFavourite());
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

                FLabel.setVisible(false);
                targetLabel.setText("");
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
                    return new Pair<>(targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });
            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(word -> {
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
                    return new Pair<>(targetTextField.getText(), explainTextField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(word -> {
                Word newWord = new Word(word.getKey(), word.getValue());
                dictionaryManager.changeExplain(newWord);
                meaningTextArea.setText(word.getValue());
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
            FLabel.setVisible(currentWord.getFavourite());

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
        dictionaryManager.insertFromFile();
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
