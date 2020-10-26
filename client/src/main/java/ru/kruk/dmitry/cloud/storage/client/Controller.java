package ru.kruk.dmitry.cloud.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.kruk.dmitry.cloud.storage.common.FileInfo;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextField clientPathField;
    public TextField serverPathField;
    public TableView<FileInfo> serverFilesTable;
    public TableView<FileInfo> clientFilesTable;
    public BorderPane storagePanel;
    public HBox authPanel;
    public TextField loginField;
    public PasswordField passField;

    public void shutdown() {
    }

    public void btnSendFile(ActionEvent actionEvent) {
    }

    public void btnClientFileDelete(ActionEvent actionEvent) {
    }

    public void btnDownloadFile(ActionEvent actionEvent) {
    }

    public void btnServerFileDelete(ActionEvent actionEvent) {
    }

    public void btnClientPathUpAction(ActionEvent actionEvent) {
    }

    public void btnServerPathUpAction(ActionEvent actionEvent) {
    }

    public void sendAuth(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
