package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.entities.Book;


public class ViewBookActivity extends Activity {

	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);

		// TODO get book as parcelable intent extra and populate the UI with book details.
		Bundle data = getIntent().getExtras();
		Book book = (Book) data.getParcelable("book");

		TextView title = (TextView) findViewById(R.id.view_title);
		title.setText(book.title);

		TextView isbn = (TextView) findViewById(R.id.view_isbn);
		isbn.setText(book.isbn);

		TextView author = (TextView) findViewById(R.id.view_author);
		author.setText(book.authors[0].toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "Back");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case 1:
				finish();
		}
		return false;
	}

}