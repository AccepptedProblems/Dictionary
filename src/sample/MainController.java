package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
                searchListView.getItems().addAll(dictionaryManager.wordStartWith(""));
            }
        });

        searchButton.setOnMouseClicked(event -> {
            String searchedWord = searchTextField.getText();
            Vector<String> result = dictionaryManager.wordStartWith(searchedWord);
            System.out.println(result.stream().count());
            if (!searchedWord.equals("") && result.size() > 0) {

                //First word in result
                searchedWord = result.firstElement();
                Word firstSearchedWord = new Word(searchedWord, "");
                int wordIndex = dictionaryManager.indexOfWord(firstSearchedWord);

                //get meaning
                firstSearchedWord = dictionaryManager.words.get(wordIndex);

                targetLabel.setText(firstSearchedWord.getWord_target());
                meaningTextArea.setText(firstSearchedWord.getWord_explain());

            } else {
                meaningTextArea.clear();
            }
            searchListView.getItems().clear();
            searchListView.getItems().addAll(result);
            if (!searchListView.getItems().isEmpty()) {
                searchListView.getSelectionModel().select(0);
            }
        });

        deleteButton.setOnAction(event -> {
            String deleteTargetWord = targetLabel.getText();

            if (deleteTargetWord.equals("")) return;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Alert");
                alert.setContentText("Do you want to delete word \"" + deleteTargetWord + "\" ?");
                alert.show();
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() == ButtonType.OK) {
                    Word deleteWord = new Word(deleteTargetWord, "");
                    dictionaryManager.deleteWordFromDictionary(deleteWord);

                    meaningTextArea.clear();
                    searchListView.getItems().clear();
                    searchListView.getItems().addAll(dictionaryManager.wordStartWith(""));
                }
        });

        searchListView.setOnMouseClicked(event -> {
            String searchStr = (String) searchListView.getSelectionModel().getSelectedItem();
            Word searchedWord = new Word(searchStr, "");

            int wordIndex = dictionaryManager.indexOfWord(searchedWord);

            String wordMeaning = dictionaryManager.words.get(wordIndex).getWord_explain();

            meaningTextArea.setText(wordMeaning);
            targetLabel.setText(searchStr);

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
                searchListView.getItems().addAll(dictionaryManager.wordStartWith(""));
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

        historyButton.setOnAction(actionEvent -> {
            //TODO: -Add some function here
        });

    }

    public void initializeWordList() throws IOException {
        dictionaryManager.insertFromFile();
        searchListView.getItems().addAll(dictionaryManager.wordStartWith(""));
    }

}
