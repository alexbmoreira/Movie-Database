package movies;

import sql.*;
import java.util.*;
import exceptions.*;
import java.io.*;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

public class MovieCatalog
{
	private ArrayList<Movie> catalog;
	private BufferedReader myReader;

	public MovieCatalog()
	{
		catalog = new ArrayList<>();
	}

	public ArrayList<Movie> getCatalog()
	{
		return catalog;
	}

	public void initializeMovieList()
	{
		DBConnect connection = new DBConnect();
		catalog = connection.getNewData();
	}

	public void importFromFile(String filename)
	{
		try
		{
			myReader = new BufferedReader(new FileReader(filename));
			String[] splitString, genres;

			while(myReader != null)
			{
				ArrayList<String> genreList = new ArrayList<>();
				Movie readMovie = new Movie();
				String oneLine = myReader.readLine();
				String title, date, imdbRating, rating = "";
				boolean hasSeen = false;

				splitString = oneLine.split("(?x)   " + ",          " + // Split on comma
						"(?=        " + // Followed by
						"  (?:      " + // Start a non-capture group
						"    [^\"]* " + // 0 or more non-quote characters
						"    \"     " + // 1 quote
						"    [^\"]* " + // 0 or more non-quote characters
						"    \"     " + // 1 quote
						"  )*       " + // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
						"  [^\"]*   " + // Finally 0 or more non-quotes
						"  $        " + // Till the end (This is necessary, else every comma will satisfy the condition)
						")          " // End look-ahead
				);

				title = removeQuotes(splitString[0]);
				date = removeQuotes(splitString[1]);
				imdbRating = removeQuotes(splitString[3]);

				genres = removeQuotes(splitString[2]).split(":");
				for(String genre : genres)
				{
					genreList.add(genre);
				}

				if(removeQuotes(splitString[5]).equals("seen"))
				{
					hasSeen = true;
					try
					{
						Double.parseDouble(removeQuotes(splitString[4]));
						rating = removeQuotes(splitString[4]);
					}
					catch (Exception ex)
					{
						rating = "N/A";
					}
				}

				readMovie.setTitle(title);
				readMovie.setDate(date);
				readMovie.setGenre(genreList);
				readMovie.setImdbRating(imdbRating);
				readMovie.setRating(rating);
				readMovie.setWatched(hasSeen);

				addMovie(readMovie);
			}
			myReader.close();
		}
		catch (Exception ex)
		{
		}
	}

	public void saveToFile(File file)
	{
		String saveMe = this.toString();

		try
		{
			PrintWriter myWriter = new PrintWriter(file);
			myWriter.println(saveMe);
			myWriter.close();
		}
		catch (Exception ex)
		{
		}
	}

	public void addMovie(Movie toAdd)
	{
		if(!catalog.contains(toAdd))
		{
			catalog.add(toAdd);
		}
		else
		{
			if(!catalog.get(catalog.indexOf(toAdd)).getWatched() && toAdd.getWatched())
			{
				catalog.set(catalog.indexOf(toAdd), toAdd);
			}
		}
	}

	public void deleteMovie(Movie toDelete)
	{
		if(catalog.contains(toDelete))
		{
			catalog.remove(toDelete);
		}
	}

	public void setMovie(int index, Movie movie)
	{
		catalog.set(index, movie);
	}

	public void sortByTopMovie(Movie topMovie)
	{
		ArrayList<Movie> sortedMovies = new ArrayList<>();
		ArrayList<Movie> watched = new ArrayList<>();
		ArrayList<String> genres = topMovie.getGenre();

		HashMap<Movie, Double> moviesWithMatches = new HashMap<>();
		for(Movie currentMovie : catalog)
		{
			if(!currentMovie.getWatched())
			{
				ArrayList<String> currentGenres = currentMovie.getGenre();
				Double currentMatches = 0.0;
				for(String genre : genres)
				{
					if(currentGenres.contains(genre))
					{
						currentMatches++;
					}
				}
				Double average = currentMatches / currentGenres.size();
				moviesWithMatches.put(currentMovie, average * currentMatches);
			}
			else
			{
				watched.add(currentMovie);
			}
		}

		HashMap<Movie, Double> sorted = moviesWithMatches.entrySet().stream()
				.sorted(Collections.reverseOrder(comparingByValue()))
				.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

		for(Movie movie : sorted.keySet())
		{
			sortedMovies.add(movie);
		}

		sortedMovies.addAll(watched);

		catalog = sortedMovies;
	}
	
	public void sortByTopGenres(ArrayList<String> genres)
	{
		ArrayList<Movie> sortedMovies = new ArrayList<>();
		ArrayList<Movie> watched = new ArrayList<>();

		HashMap<Movie, Double> moviesWithMatches = new HashMap<>();
		for(Movie currentMovie : catalog)
		{
			if(!currentMovie.getWatched())
			{
				ArrayList<String> currentGenres = currentMovie.getGenre();
				Double currentMatches = 0.0;
				for(String genre : genres)
				{
					if(currentGenres.contains(genre))
					{
						currentMatches++;
					}
				}
				Double average = currentMatches / currentGenres.size();
				moviesWithMatches.put(currentMovie, average * currentMatches);
			}
			else
			{
				watched.add(currentMovie);
			}
		}

		HashMap<Movie, Double> sorted = moviesWithMatches.entrySet().stream()
				.sorted(Collections.reverseOrder(comparingByValue()))
				.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

		for(Movie movie : sorted.keySet())
		{
			sortedMovies.add(movie);
		}

		sortedMovies.addAll(watched);

		catalog = sortedMovies;
	}

	public ArrayList<String> findTopGenres()
	{
		class GenreValue
		{
			int total;
			double average;
			
			public GenreValue(int total, double average)
			{
				this.total = total;
				this.average = average;
			}
		}
		HashMap<String, GenreValue> genres = new HashMap<>();
		HashMap<String, Double> topGenres = new HashMap<>();

		for(Movie movie : sortByRating())
		{
			if(movie.getWatched() && !movie.getRating().equals("N/A"))
			{
				for(String genreString : movie.getGenre())
				{
					if(!genres.containsKey(genreString))
					{
						genres.put(genreString, new GenreValue(1, Double.parseDouble(movie.getRating())));
					}
					else
					{
						double movieRating = Double.parseDouble(movie.getRating());
						GenreValue values = genres.get(genreString);
						int total = values.total + 1;
						double average = ((values.average * (total - 1)) + movieRating) / total;
						
						genres.put(genreString, new GenreValue(total, average));
					}
				}
			}
			else
			{
				break;
			}
		}
		
		for(String genre : genres.keySet())
		{
			GenreValue value = genres.get(genre);
			topGenres.put(genre, value.average);
		}
		
		HashMap<String, Double> sorted = topGenres.entrySet().stream()
				.sorted(Collections.reverseOrder(comparingByValue()))
				.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
		
		ArrayList<String> temp = new ArrayList<>();
		ArrayList<String> returnGenres = new ArrayList<>();
		
		for(String genre : sorted.keySet())
		{
			temp.add(genre);
		}
		
		returnGenres.add(temp.get(0));
		returnGenres.add(temp.get(1));
		returnGenres.add(temp.get(2));

		return returnGenres;
	}

	public boolean allWatched()
	{
		for(Movie movie : catalog)
		{
			if(!movie.getWatched())
			{
				return false;
			}
		}
		return true;
	}

	public void shuffle()
	{
		Collections.shuffle(catalog);
		if(!allWatched())
		{
			while(catalog.get(0).getWatched())
			{
				Collections.shuffle(catalog);
			}
		}
	}

	public ArrayList<Movie> sortByRating()
	{
		ArrayList<Movie> list = new ArrayList<>();
		ArrayList<Movie> unseen = new ArrayList<>();

		for(Movie movie : catalog)
		{
			if(movie.getWatched() && !movie.getRating().equals("N/A"))
			{
				list.add(movie);
			}
			else
			{
				unseen.add(movie);
			}
		}

		for(int i = 0; i < list.size() - 1; i++)
		{
			for(int j = 0; j < list.size() - 1 - i; j++)
			{
				double ratingJ = 0.0;
				double ratingJ1 = 0.0;
				try
				{
					ratingJ = Double.parseDouble(list.get(j).getRating());
					ratingJ1 = Double.parseDouble(list.get(j + 1).getRating());
				}
				catch (Exception ex)
				{
				}

				if(ratingJ < ratingJ1)
				{
					Movie temp = list.get(j + 1);
					list.set(j + 1, list.get(j));
					list.set(j, temp);
				}
			}
		}

		list.addAll(unseen);

		return list;
	}

	public Movie findTopRated() throws EmptyCatalogException
	{
		if(catalog.size() > 0)
		{
			return sortByRating().get(0);
		}
		else
		{
			throw new EmptyCatalogException();
		}
	}

	public String toString()
	{
		String printThis = "";
		for(Movie movie : catalog)
		{
			String title, date, genresString = "", imdbRating, rating = "", hasSeen = "";

			title = movie.getTitle();
			date = movie.getDate();

			for(String genre : movie.getGenre())
			{
				genresString += genre;
				if(!genre.equals(movie.getGenre().get(movie.getGenre().size() - 1)))
				{
					genresString += ":";
				}
			}

			imdbRating = movie.getImdbRating();

			try
			{
				Double.parseDouble(movie.getRating());
				rating = movie.getRating();
			}
			catch (Exception ex)
			{
				rating = "N/A";
			}

			if(movie.getWatched())
			{
				hasSeen = "seen";
			}
			else
			{
				hasSeen += "unseen";
			}

			printThis += "\"" + title + "\"," + "\"" + date + "\"," + "\"" + genresString + "\"," + "\"" + imdbRating
					+ "\"," + "\"" + rating + "\"," + "\"" + hasSeen + "\"\n";
		}

		return printThis;
	}

	public String removeQuotes(String string)
	{
		if(string.charAt(0) == '"')
		{
			string = string.substring(1);
		}
		if(string.charAt(string.length() - 1) == '"')
		{
			string = string.substring(0, string.length() - 1);
		}

		return string;
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
}