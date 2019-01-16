package movies;

import java.util.*;

public class Movie
{
	private String title;
	private String date;
	private String rating;
	private ArrayList<String> genre;
	private String imdbRating;
	private boolean watched;

	public Movie()
	{
		this("N/A", "N/A", "N/A", "N/A", false);
	}

	public Movie(String title, String rating, String date, String imdbRating, boolean watched)
	{
		genre = new ArrayList<>();
		this.title = title;
		this.date = date;
		this.rating = rating;
		this.imdbRating = imdbRating;
		this.watched = watched;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getRating()
	{
		return rating;
	}

	public void setRating(String rating)
	{
		this.rating = rating;
	}

	public String getImdbRating()
	{
		return imdbRating;
	}
	
	public void setImdbRating(String imdbRating)
	{
		this.imdbRating = imdbRating;
	}

	public ArrayList<String> getGenre()
	{
		return genre;
	}

	public void setGenre(ArrayList<String> genre)
	{
		this.genre = genre;
	}

	public boolean getWatched()
	{
		return watched;
	}

	public void setWatched(boolean watched)
	{
		this.watched = watched;
	}

	public String toString()
	{
		String printThis = "";
		String genres = "", seen = "";

		for(String genreString : genre)
		{
			genres += genreString;
			if(genreString.equals(genre.get(genre.size())))
			{
				genres += ":";
			}
		}

		if(watched)
		{
			seen = "seen";
		}
		else
		{
			seen = "unseen";
		}

		printThis += title + "," + date + "," + genres + "," + rating + "," + seen;

		return printThis;
	}
}