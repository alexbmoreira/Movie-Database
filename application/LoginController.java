package application;

import movies.*;
import sql.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginController
{
	String username = "";
	String password = "";
	MovieCatalog movieList = new MovieCatalog();

	@FXML
	AnchorPane anchorPane;
	@FXML
	TextField usernameField;
	@FXML
	PasswordField passwordField;
	
	@FXML
	Label errorLabel;
	
	@FXML
	Button importButton;
	@FXML
	Button newDbButton;
	
	public void loadData()
	{
		if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty())
		{
			errorLabel.setText("Empty username/password");
			errorLabel.setTextFill(Color.RED);
			return;
		}
		
		DBConnect connection = new DBConnect();
		
		String userCheck = connection.userCheck(usernameField.getText(), passwordField.getText());
		
		if(userCheck.equals("foundUser"))
		{
			username = usernameField.getText();
			password = passwordField.getText();
			errorLabel.setText("Found user " + username + ".\nLoading Data...");
			createDatabase();
		}
		else if(userCheck.equals("invPassword"))
		{
			errorLabel.setText("Incorrect password");
			errorLabel.setTextFill(Color.RED);
		}
		else if(userCheck.equals("noUserFound"))
		{
			username = usernameField.getText();
			password = passwordField.getText();
			errorLabel.setText("Could not find user.\nClick \"Create New Database\" to create account");
			newDbButton.setDisable(false);			
		}
		else
		{
			errorLabel.setText("Connection error. Please try again later");
			errorLabel.setTextFill(Color.RED);
		}
	}
	
	public void newData()
	{
		if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty())
		{
			errorLabel.setText("Empty username/password");
			errorLabel.setTextFill(Color.RED);
			return;
		}
		
		DBConnect connection = new DBConnect();
		
		String userCheck = connection.userCheck(usernameField.getText(), passwordField.getText());
		
		if(userCheck.equals("foundUser"))
		{
			errorLabel.setText("User " + username + " already exists.\nPlease select \"Load Database\"");
			errorLabel.setTextFill(Color.RED);
		}
		else if(userCheck.equals("invPassword"))
		{
			errorLabel.setText("User " + username + " already exists");
			errorLabel.setTextFill(Color.RED);
		}
		else if(userCheck.equals("noUserFound"))
		{
			username = usernameField.getText();
			password = passwordField.getText();
			createDatabase();
		}
		else
		{
			errorLabel.setText("Connection error. Please try again later");
			errorLabel.setTextFill(Color.RED);
		}
	}

	public void createDatabase()
	{		
		try
		{
			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MovieTable.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			MovieTableController tableController = loader.getController();

			tableController.setUsername(username);
			tableController.setPassword(password);
			movieList.initializeMovieList(username, password);
			
			tableController.setMovieList(movieList);

			Scene scene = new Scene(page);
			scene.getStylesheets().add(getClass().getResource("MovieTable.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Movie Database");
			stage.getIcons().add(new Image(LoginController.class.getResourceAsStream("movie_database.png")));
			stage.show();

			importButton.getScene().getWindow().hide();
		}
		catch (Exception ex)
		{
		}
	}

	@FXML
	private void initialize()
	{
		importButton.setTooltip(new Tooltip("Import your own saved data from a\n" + "text file."));
		newDbButton.setTooltip(new Tooltip("Create a fresh database with a new\n" + "username and password."));
	}
}
