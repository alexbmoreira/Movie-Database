package application;

import movies.*;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class LoginController
{
	String fileName;
	MovieCatalog movieList = new MovieCatalog();

	@FXML
	AnchorPane anchorPane;
	@FXML
	Label errorLabel;

	public void chooseFile(ActionEvent event)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import Save Data");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Plain Text Files", "*.txt", "*.csv"),
				new ExtensionFilter("Text Files", "*.txt"),
				new ExtensionFilter("Comma-Seperated Value Files", "*.csv"));

		Stage stage = (Stage) (anchorPane.getScene().getWindow());
		File file = fileChooser.showOpenDialog(stage);

		if(file != null)
		{
			fileName = file.getName();
			movieList.importFromFile(file.getAbsolutePath());
			if(!movieList.getCatalog().isEmpty())
			{
				errorLabel.setText("Imported \"" + fileName + "\"");
				createDatabase(event);
			}
			else
			{
				errorLabel.setText("\"" + fileName + "\" is invalid");
			}
		}
		else
		{
			errorLabel.setText("Import Failed");
		}
	}

	public void createDatabase(ActionEvent event)
	{
		try
		{
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MovieTable.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			MovieTableController tableController = loader.getController();

			tableController.setMovieList(movieList);

			Scene scene = new Scene(page);
			scene.getStylesheets().add(getClass().getResource("MovieTable.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Movie Database");
			stage.getIcons().add(new Image(LoginController.class.getResourceAsStream("movie_database.png")));
			stage.show();

			((Node) (event.getSource())).getScene().getWindow().hide();
		}
		catch (Exception ex)
		{
		}
	}
}
