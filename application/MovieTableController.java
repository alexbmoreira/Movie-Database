package application;

import movies.*;
import exceptions.*;
import sql.*;

import java.text.*;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * View-Controller for the movie table.
 * 
 * @author Marco Jakob
 */

public class MovieTableController
{
	String username, password;
	NumberFormat formatter = new DecimalFormat("#0.0");

	MovieCatalog movieList = new MovieCatalog();
	Movie topMovie = new Movie();

	@FXML
	AnchorPane anchorPane;

	@FXML
	CheckBox actionBox;
	@FXML
	CheckBox adventureBox;
	@FXML
	CheckBox animationBox;
	@FXML
	CheckBox biographyBox;
	@FXML
	CheckBox comedyBox;
	@FXML
	CheckBox crimeBox;
	@FXML
	CheckBox disneyBox;
	@FXML
	CheckBox documentaryBox;
	@FXML
	CheckBox dramaBox;
	@FXML
	CheckBox familyBox;
	@FXML
	CheckBox fantasyBox;
	@FXML
	CheckBox historyBox;
	@FXML
	CheckBox horrorBox;
	@FXML
	CheckBox musicBox;
	@FXML
	CheckBox musicalBox;
	@FXML
	CheckBox mysteryBox;
	@FXML
	CheckBox noirBox;
	@FXML
	CheckBox romanceBox;
	@FXML
	CheckBox scifiBox;
	@FXML
	CheckBox sportBox;
	@FXML
	CheckBox superheroBox;
	@FXML
	CheckBox thrillerBox;
	@FXML
	CheckBox warBox;
	@FXML
	CheckBox westernBox;

	ArrayList<CheckBox> addBoxes = new ArrayList<>();
	@FXML
	Button addButton;

	@FXML
	ComboBox<String> topGenresDropDown;
	@FXML
	Label topMovieLabel;

	@FXML
	TextField addTitle;
	@FXML
	ComboBox<String> addDate;
	@FXML
	ComboBox<String> addRating;

	@FXML
	Button genreRecommendButton;
	@FXML
	Button movieRecommendButton;
	@FXML
	Button toggleSeenButton;

	@FXML
	TextField filterField;
	@FXML
	TableView<Movie> movieTable;
	@FXML
	TableColumn<Movie, String> titleColumn;
	@FXML
	TableColumn<Movie, String> dateColumn;
	@FXML
	TableColumn<Movie, String> genreColumn;
	@FXML
	TableColumn<Movie, String> imdbRatingColumn;
	@FXML
	TableColumn<Movie, String> ratingColumn;
	@FXML
	TableColumn<Movie, String> watchedColumn;
	@FXML
	Label numMovies;
	@FXML
	Label saveLabel;

	@FXML
	TitledPane titledPane;
	@FXML
	SplitPane splitPane;

	private Callback<TableColumn<Movie, String>, TableCell<Movie, String>> defaultTextFieldCellFactory = TextFieldTableCell
			.<Movie>forTableColumn();
	private ObservableList<Movie> masterData = FXCollections.observableArrayList();

	private PseudoClass movieWatched = PseudoClass.getPseudoClass("movie-watched");

	public MovieTableController()
	{
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setMovieList(MovieCatalog movieList)
	{
		this.movieList = movieList;
		this.movieList.shuffle();
		for(Movie movie : this.movieList.getCatalog())
		{
			masterData.add(movie);
		}
		numMovies.setText(movieTable.getItems().size() + " movies");
		getTopMovie();
	}

	public void addCheckBoxes()
	{
		addBoxes.add(actionBox);
		addBoxes.add(adventureBox);
		addBoxes.add(animationBox);
		addBoxes.add(biographyBox);
		addBoxes.add(comedyBox);
		addBoxes.add(crimeBox);
		addBoxes.add(disneyBox);
		addBoxes.add(documentaryBox);
		addBoxes.add(dramaBox);
		addBoxes.add(familyBox);
		addBoxes.add(fantasyBox);
		addBoxes.add(historyBox);
		addBoxes.add(horrorBox);
		addBoxes.add(musicBox);
		addBoxes.add(musicalBox);
		addBoxes.add(mysteryBox);
		addBoxes.add(noirBox);
		addBoxes.add(romanceBox);
		addBoxes.add(scifiBox);
		addBoxes.add(sportBox);
		addBoxes.add(superheroBox);
		addBoxes.add(thrillerBox);
		addBoxes.add(warBox);
		addBoxes.add(westernBox);
	}

	public void getTopMovie()
	{
		try
		{
			topMovie = movieList.findTopRated();
			try
			{
				double round = Math.round(Double.parseDouble(topMovie.getRating()) * 10) / 10.0;
				String rating = formatter.format(round);
				topMovieLabel.setText(topMovie.getTitle() + " - " + rating);

				ObservableList<String> genresList = FXCollections.observableArrayList();
				genresList.add("");
				for(String genre : topMovie.getGenre())
				{
					genresList.add(genre);
				}

				topGenresDropDown.setItems(genresList);
				genreRecommendButton.setDisable(false);
				movieRecommendButton.setDisable(false);
			}
			catch (NumberFormatException ex)
			{
				topMovieLabel.setText("You haven't rated any movies!");
				genreRecommendButton.setDisable(true);
				movieRecommendButton.setDisable(true);
			}
		}
		catch (EmptyCatalogException ex)
		{
			topMovieLabel.setText("No movies in database.");
			genreRecommendButton.setDisable(true);
			movieRecommendButton.setDisable(true);
		}
	}

	public void setAddValues()
	{
		ObservableList<String> dates = FXCollections.observableArrayList();
		dates.add("");
		for(int i = 1940; i < 2019; i++)
		{
			dates.add(Integer.toString(i));
		}

		addDate.setItems(dates);

		ObservableList<String> ratings = FXCollections.observableArrayList();
		ratings.add("");
		for(double i = 0.0; i < 10.0; i += 0.1)
		{
			ratings.add(formatter.format(i));
		}
		addRating.setItems(ratings);
	}

	public void tooltips()
	{
		genreRecommendButton.setTooltip(
				new Tooltip("Sort movies by most recommended\n" + "for you based on your top rated genres.\n"
						+ "The more movies you rate, the more\n" + "precise it is."));
		movieRecommendButton
				.setTooltip(new Tooltip("Sort movies by most recommended\n" + "for you based on your top movie."));
		addButton.setTooltip(
				new Tooltip("Fill out all the fields and click\n" + "here to add the movie to your database.\n\n"
						+ "*Note: If you add your own movie\n" + "you must mark it as watched to save it\n"
						+ "to your account. It will be taken\n" + "into account for recommendations."));
		toggleSeenButton.setTooltip(new Tooltip("Mark a movie as seen so it is not\n"
				+ "recommended for you in the future.\n" + "This can also be done by pressing spacebar."));
	}

	/**
	 * Initializes the controller class. Automatically called after the fxml file
	 * has been loaded.
	 * 
	 * Initializes the table columns and sets up sorting and filtering.
	 */
	@FXML
	private void initialize()
	{
		Platform.runLater(() -> movieTable.refresh());
		tooltips();
		addCheckBoxes();
		setAddValues();

		titledPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
			if(!isNowExpanded)
			{
				splitPane.setDividerPositions(0);
			}
		});

		// 0. Initialize the columns.
		titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
		genreColumn.setCellValueFactory(cellData -> {
			String genreString = "";
			ArrayList<String> genres = cellData.getValue().getGenre();

			for(String genre : genres)
			{
				genreString += genre;
				if(!genre.equals(genres.get(genres.size() - 1)))
				{
					genreString += ", ";
				}
			}
			return new SimpleStringProperty(genreString);
		});
		imdbRatingColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImdbRating()));
		ratingColumn.setCellValueFactory(cellData -> {
			String rating = cellData.getValue().getRating();
			try
			{
				if(cellData.getValue().getWatched())
				{
					double ratingNum = Double.parseDouble(rating);

					if(ratingNum > 10)
					{
						rating = "10.0";
					}
					else if(ratingNum < 0)
					{
						rating = "0.0";
					}
					else
					{
						double round = Math.round(ratingNum * 10) / 10.0;
						rating = formatter.format(round);
					}
				}
				else
				{
					rating = "";
				}
			}
			catch (Exception ex)
			{
				rating = "";
			}

			return new SimpleStringProperty(rating);
		});
		watchedColumn.setCellValueFactory(cellData -> {
			if(cellData.getValue().getWatched())
			{
				return new SimpleStringProperty("Seen");
			}
			else
			{
				return new SimpleStringProperty("Unseen");
			}
		});

		// Make rating column editable
		ratingColumn.setCellFactory(column -> {
			TableCell<Movie, String> cell = defaultTextFieldCellFactory.call(column);
			return cell;
		});
		movieTable.setRowFactory(tv -> new TableRow<Movie>()
		{
			@Override
			protected void updateItem(Movie item, boolean empty)
			{
				super.updateItem(item, empty);
				pseudoClassStateChanged(movieWatched, item != null && item.getWatched());
			}
		});

		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<Movie> filteredData = new FilteredList<>(masterData, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(movie -> {
				// If filter text is empty, display all movies.
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();
				String[] filterFieldList = lowerCaseFilter.split(" ");
				int count = 0;

				String allGenres = "";
				for(String genre : movie.getGenre())
				{
					allGenres += genre;
				}

				// "convert" boolean to string
				String seen = "";
				if(movie.getWatched())
				{
					seen = "seen";
				}
				else
				{
					seen = "unseen";
				}

				if(movie.getTitle().toLowerCase().indexOf(lowerCaseFilter) != -1)
				{
					return true; // Filter matches title
				}
				else if(allGenres.toLowerCase().indexOf(lowerCaseFilter) != -1)
				{
					return true;
				}
				else if(seen.toLowerCase().equals(lowerCaseFilter))
				{
					return true;
				}
				else if(movie.getImdbRating().toLowerCase().indexOf(lowerCaseFilter) != -1)
				{
					return true; // Filter matches rating
				}
				// Check if all genres in filterField are part of the movie
				for(String genre : filterFieldList)
				{
					if(movie.getGenre().contains(sentenceCase(genre)))
					{
						count++;
					}
				}
				if(count == filterFieldList.length)
				{
					return true;
				}

				return false; // Does not match
			});
		});

		SortedList<Movie> sortedData = new SortedList<>(filteredData);

		sortedData.comparatorProperty().bind(movieTable.comparatorProperty());

		// 5. Add filtered data to the table.
		movieTable.setItems(sortedData);

	}

	public void recommendByGenre()
	{
		movieList.sortByTopGenres(movieList.findTopGenres());
		masterData.setAll(movieList.getCatalog());
	}

	public void recommendByMovie()
	{
		movieList.sortByTopMovie(topMovie);
		masterData.setAll(movieList.getCatalog());
	}

	public void searchTopMovie()
	{
		filterField.setText(topGenresDropDown.getSelectionModel().getSelectedItem());
	}

	public boolean checkFields()
	{
		if(addTitle.getText().length() > 0 && addDate.getSelectionModel().getSelectedIndex() > 0
				&& addRating.getSelectionModel().getSelectedIndex() > 0)
		{
			return true;
		}
		return false;
	}

	public void addMovie()
	{
		ArrayList<CheckBox> checkedBoxes = new ArrayList<>();
		if(checkFields())
		{
			for(CheckBox box : addBoxes)
			{
				if(box.isSelected())
				{
					checkedBoxes.add(box);
				}
			}
			if(!checkedBoxes.isEmpty())
			{
				String title = addTitle.getText();
				String date = addDate.getSelectionModel().getSelectedItem();
				String rating = addRating.getSelectionModel().getSelectedItem();
				ArrayList<String> genres = new ArrayList<>();

				for(CheckBox box : checkedBoxes)
				{
					genres.add(box.getText());
				}

				Movie toAdd = new Movie(title, "N/A", date, rating, false);
				toAdd.setGenre(genres);

				for(CheckBox box : checkedBoxes)
				{
					box.setSelected(false);
				}
				addDate.getSelectionModel().select(0);
				addRating.getSelectionModel().select(0);
				addTitle.setText("");

				if(!movieList.getCatalog().contains(toAdd))
				{
					movieList.addMovie(toAdd);
					masterData.add(toAdd);
					numMovies.setText(masterData.size() + " movies");
				}
			}
		}
	}

	public void deleteMovie()
	{
		if(!movieTable.getSelectionModel().isEmpty())
		{
			masterData.remove(movieTable.getSelectionModel().getSelectedIndex());

			Movie toDelete = movieTable.getSelectionModel().getSelectedItem();
			movieList.deleteMovie(toDelete);
		}
	}

	public void toggleWatched()
	{
		if(!movieTable.getSelectionModel().isEmpty())
		{
			Movie toToggle = movieTable.getSelectionModel().getSelectedItem();
			if(!toToggle.getWatched())
			{
				toToggle.setWatched(true);
				toToggle.setRating("N/A");
			}
			else
			{
				toToggle.setWatched(false);
				toToggle.setRating("N/A");
			}
			masterData.set(masterData.indexOf(toToggle), toToggle);
			getTopMovie();
		}

	}

	public void shuffleButton()
	{
		movieList.shuffle();
		masterData.setAll(movieList.getCatalog());
	}

	@FXML
	private void ratingEdit(TableColumn.CellEditEvent<Movie, String> ratingCell)
	{
		Movie movie = movieTable.getSelectionModel().getSelectedItem();
		String rating = ratingCell.getNewValue();

		if(movie.getWatched())
		{
			try
			{
				double ratingNum = Double.parseDouble(rating);

				if(ratingNum > 10)
				{
					rating = "10.0";
				}
				else if(ratingNum < 0)
				{
					rating = "0.0";
				}
			}
			catch (Exception ex)
			{
				rating = "";
			}

			movie.setRating(rating);
			movieList.setMovie(movieList.getCatalog().indexOf(movie), movie);
			masterData.set(masterData.indexOf(movie), movie);
			getTopMovie();
		}
		else
		{
			masterData.set(masterData.indexOf(movie), movie);
		}
	}

	@FXML
	private void handleKeyInput(KeyEvent event)
	{
		int index = movieTable.getSelectionModel().getSelectedIndex();

		numMovies.setText("Movie " + (index + 1) + "/" + movieTable.getItems().size());

		if(event.getCode() == KeyCode.BACK_SPACE)
		{
			deleteMovie();
		}
		else if(event.getCode() == KeyCode.SPACE)
		{
			toggleWatched();
		}
		movieTable.getSelectionModel().select(index);
	}

	@FXML
	private void handleRowSelect(MouseEvent event)
	{
		int index = movieTable.getSelectionModel().getSelectedIndex();
		if(index + 1 != 0)
		{
			numMovies.setText("Movie " + (index + 1) + "/" + movieTable.getItems().size());
		}
	}

	public void updateLabel()
	{
		numMovies.setText(movieTable.getItems().size() + " movies");
	}

	public void save()
	{
		saveLabel.setText("Saving...");
		saveLabel.setTextFill(Color.BLACK);

		DBConnect connection = new DBConnect();
		MovieCatalog seen = new MovieCatalog();

		for(Movie movie : movieList.getCatalog())
		{
			if(movie.getWatched())
			{
				seen.addMovie(movie);
			}
		}

		String setToLabel = connection.saveData(username, password, seen);
		saveLabel.setText(setToLabel);
		if(saveLabel.getText().equals("Connection Error"))
		{
			saveLabel.setTextFill(Color.RED);
		}
	}

	public boolean isGenre(String check)
	{
		try
		{
			GENRE.valueOf(check.toUpperCase());
			return true;
		}
		catch (Exception ex)
		{
			if(check.toUpperCase().equals("SCI-FI"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	public String sentenceCase(String string)
	{
		string = string.trim();
		string = Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase();

		return string;
	}
}
