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
					"jdbc:mysql://movies-mysql.cazmfn9lqzct.us-east-2.rds.amazonaws.com:3306/moviesDB",
					"user", "userpass");

			state = conn.createStatement();
		}
		catch (Exception ex)
		{
			System.out.println(ex);
		}
	}

	public ArrayList<Movie> getNewData()
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
		return movies.getCatalog();
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
