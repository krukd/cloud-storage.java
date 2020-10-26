package ru.kruk.dmitry.cloud.storage.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.kruk.dmitry.cloud.storage.common.FileInfo;


import java.nio.file.Path;
import java.util.List;


public class GUIHelper {
    public static List<FileInfo> serverFilesList;
    public static Path currentClientPath;
    public static Path currentServerPath;
    public static String targetServerDirectory;


    public static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public static void setFileLable(TableView<FileInfo> filesTable, Label fileLable) {
        TableView.TableViewSelectionModel<FileInfo> selectionModel = filesTable.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<FileInfo>() {
            @Override
            public void changed(ObservableValue<? extends FileInfo> observable, FileInfo oldInfo, FileInfo newInfo) {
                if (newInfo != null) {
                    fileLable.setText(newInfo.getFileName());
                }
            }
        });
    }

    public static void setCellValue(TableView<FileInfo> filesTable) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(30);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("File name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("File size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> new TableCell<FileInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "[DIR]";
                    }
                    setText(text);
                }
            }
        });
        fileSizeColumn.setPrefWidth(120);
        updateUI(() -> {
            filesTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn);
            filesTable.getSortOrder().add(fileTypeColumn);
        });

    }

    public static void showError(Exception e) {
        GUIHelper.updateUI(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong...");
            alert.setHeaderText(e.getMessage());
            VBox dialogPaneContent = new VBox();
            Label label = new Label("Stack trace:");
            String stackTrace = ExceptionUtils.getStackTrace(e);
            TextArea textArea = new TextArea();
            textArea.setText(stackTrace);

            dialogPaneContent.getChildren().addAll(label, textArea);

            alert.getDialogPane().setContent(dialogPaneContent);
            alert.setResizable(true);
            alert.showAndWait();
            e.printStackTrace();
        });
    }
}
