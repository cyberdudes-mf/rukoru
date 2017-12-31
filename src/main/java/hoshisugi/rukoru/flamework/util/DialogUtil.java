package hoshisugi.rukoru.flamework.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Window;

public class DialogUtil {

	public static void showErrorDialog(final Throwable t) {
		createExceptionDialog(t);
	}

	public static void showWarningDialog(final String title, final String message) {
		showWarningDialog(FXUtil.getStage(), title, message);

	}

	public static void showWarningDialog(final Window window, final String title, final String message) {
		showAlert(window, AlertType.WARNING, title, message);
	}

	public static Dialog<ButtonType> createExceptionDialog(final Throwable th) {
		final Dialog<ButtonType> dialog = new Dialog<>();

		dialog.setTitle("Program exception");

		final DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.setContentText("Details of the problem:");
		dialogPane.getButtonTypes().addAll(ButtonType.OK);
		dialogPane.setContentText(th.getMessage());
		dialog.initModality(Modality.APPLICATION_MODAL);

		final Label label = new Label("Exception stacktrace:");
		final StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw)) {
			th.printStackTrace(pw);
			pw.close();
		}

		final TextArea textArea = new TextArea(sw.toString());
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		final GridPane root = new GridPane();
		root.setVisible(false);
		root.setMaxWidth(Double.MAX_VALUE);
		root.add(label, 0, 0);
		root.add(textArea, 0, 1);
		dialogPane.setExpandableContent(root);
		dialog.showAndWait();
		return dialog;
	}

	private static Alert showAlert(final Window owner, final AlertType type, final String title, final String message) {
		final Alert alert = new Alert(type, "");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(owner);
		alert.setTitle(title);
		alert.getDialogPane().setContentText(message);
		alert.getDialogPane().setHeaderText(null);
		alert.showAndWait();
		return alert;
	}
}
