package sql;

import java.sql.*;
import java.util.ArrayList;

import movies.*;

public class DBConnect
{
	private Connection conn;
	private Statement state;
	private ResultSet result;

	public DBConnect()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");

			conn = DriverManager.getConnection(
					"Amazon AWS Databse", "user",
					"userpass");

			state = conn.createStatement();
		}
		catch (Exception ex)
		{
		}
	}

	public ArrayList<Movie> getData(String username, String password)
	{
		MovieCatalog movies = new MovieCatalog();
		try
		{
			String query = "SELECT * FROM `movies`";

			result = state.executeQuery(query);

			while(result.next())
			{
				String title, date, genreString, rating;
				String[] splitGenres;
				ArrayList<String> genres = new ArrayList<>();
				Movie movie = new Movie();

				title = result.getString("title").trim();
				date = result.getString("date").trim();
				genreString = result.getString("genre");
				rating = result.getString("imdbRating");

				splitGenres = genreString.split(",");

				for(int i = 0; i < splitGenres.length; i++)
				{
					if(isGenre(splitGenres[i].trim()))
					{
						genres.add(splitGenres[i].trim());
					}
				}

				movie.setTitle(title);
				movie.setDate(date);
				movie.setGenre(genres);
				movie.setImdbRating(rating);

				movies.addMovie(movie);
			}
		}
		catch (Exception ex)
		{

		}

		if(!username.isEmpty() && !password.isEmpty())
		{
			try
			{
				String query = "SELECT * FROM `userData` WHERE username = '" + username + "' AND password = '"
						+ password + "'";

				result = state.executeQuery(query);

				while(result.next())
				{
					String title, date, rating;

					title = result.getString("title");
					date = result.getString("date");
					rating = result.getString("rating");

					Movie movie = movies.findMovie(title, date);
					int index = movies.getCatalog().indexOf(movie);

					movie.setWatched(true);
					movie.setRating(rating);

					movies.setMovie(index, movie);
				}
			}
			catch (Exception ex)
			{

			}
		}
		return movies.getCatalog();
	}

	public String userCheck(String username, String password)
	{
		try
		{
			String query = "SELECT username, password FROM `userData` WHERE username = '" + username + "'";

			result = state.executeQuery(query);

			while(result.next())
			{
				String tableUser = result.getString("username");
				String tablePass = result.getString("password");
				if(tableUser.equals(username))
				{
					if(tablePass.equals(password))
					{
						result.close();
						return "foundUser";
					}
					result.close();
					return "invPassword";
				}
			}

			result.close();
			return "noUserFound";
		}
		catch (Exception ex)
		{
			return "connectionError";
		}
	}

	public String saveData(String username, String password, MovieCatalog seen)
	{
		ArrayList<Movie> movies = seen.getCatalog();

		try
		{
			String query = "SELECT * FROM `userData` WHERE username = '" + username + "' AND password = '"
					+ password + "'";

			result = state.executeQuery(query);

			if(result.isBeforeFirst())
			{
				while(result.next())
				{
					String title, date, rating;

					title = result.getString("title");
					date = result.getString("date");
					rating = result.getString("rating");

					Movie movie = seen.findMovie(title, date);

					if(movie.getTitle().equals(title) && movie.getDate().equals(date))
					{
						if(!movie.getRating().equals(rating))
						{
							title = title.replaceAll("'", "\\\\'");
							
							String update = "UPDATE `userData` " + "SET rating = " + movie.getRating() + " "
									+ "WHERE title = '" + title + "' AND date = '" + date + "'";
							
							PreparedStatement statement = conn.prepareStatement(update);
							statement.executeUpdate();
						}
					}
					else
					{
						title = title.replaceAll("'", "\\\\'");
						String delete = "DELETE FROM `userData` " + "WHERE title = '" + title + "' AND date = '" + date
								+ "'";
						
						PreparedStatement statement = conn.prepareStatement(delete);
						statement.executeUpdate();
					}
				}
				for(Movie movie : movies)
				{
					String title = movie.getTitle();
					title = title.replaceAll("'", "\\\\'");
					
					String search = "SELECT * FROM `userData` " + "WHERE username = '" + username + "' "
							+ "AND password = '" + password + "' " + "AND title = '" + title + "' "
							+ "AND date = '" + movie.getDate() + "'";


					PreparedStatement statement = conn.prepareStatement(search);
					
					if(!statement.executeQuery().next())
					{
						String insertQuery = "INSERT INTO `userData` (`username`, `password`, `title`, `date`, `rating`) VALUES";
						
						insertQuery += "('" + username + "','" + password + "','" + title + "','"
								+ movie.getDate() + "','" + movie.getRating() + "');";

						PreparedStatement statement2 = conn.prepareStatement(insertQuery);
						statement2.executeUpdate();
					}
				}
			}
			else
			{
				String insertQuery = "INSERT INTO `userData` (`username`, `password`, `title`, `date`, `rating`) VALUES";

				for(Movie movie : movies)
				{
					insertQuery += "('" + username + "','" + password + "','" + movie.getTitle() + "','"
							+ movie.getDate() + "','" + movie.getRating() + "')";
					if(movies.indexOf(movie) < movies.size() - 1)
					{
						insertQuery += ",";
					}
					else
					{
						insertQuery += ";";
					}
				}

				state.executeUpdate(insertQuery);
			}

			result.close();
			return "Success!";
		}
		catch (Exception ex)
		{
			return "Connection Error";
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
}
