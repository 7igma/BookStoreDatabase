package edu.stevens.cs522.bookstoredatabase.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;
import edu.stevens.cs522.bookstoredatabase.databases.CartDbAdapter;
import edu.stevens.cs522.bookstoredatabase.entities.Book;

public class MainActivity extends Activity {

	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();

	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;

	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

	// The database adapter
	private CartDbAdapter dba;

	private static SimpleCursorAdapter adapter;

	private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO Set the layout (use cart.xml layout)
		setContentView(R.layout.cart);

		// TODO open the database using the database adapter
		dba = new CartDbAdapter(this);
		dba.open();


        // TODO query the database using the database adapter, and manage the cursor on the main thread
		cursor = dba.fetchAllBooks();
		startManagingCursor(cursor);

        // TODO use SimpleCursorAdapter to display the cart contents.

		String[] cols = new String[] {CartDbAdapter.COL_TITLE, CartDbAdapter.COL_AUTHORS};
		int[] ids = new int[] {R.id.cart_row_title, R.id.cart_row_author};

		adapter = new SimpleCursorAdapter(this, R.layout.cart_row, cursor, cols, ids, 0);

		final ListView listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(adapter);
		listView.setSelection(0);

		listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				//mode.getMenuInflater().inflate(R.menu.context_menu, menu);
				menu.add(0, 1, 0, "Delete");
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case 1:
						long[] itemIds = listView.getCheckedItemIds();
						for (int i = 0; i < itemIds.length; i++)
						{
							dba.delete(dba.fetchBook(itemIds[i]));
						}
						adapter.notifyDataSetChanged();
						cursor.requery();
						mode.finish();
						break;

					default:
						break;
				}
				return true;
			}

		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(MainActivity.this, ViewBookActivity.class);
				intent.putExtra("book", dba.fetchBook(l));
				startActivity(intent);
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
				listView.setItemChecked(i, true);
				return true;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO inflate a menu with ADD and CHECKOUT options
		getMenuInflater().inflate(R.menu.bookstore_menu, menu);

        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODO ADD provide the UI for adding a book
            case R.id.add:
				Intent addIntent = new Intent(this, AddBookActivity.class);
				startActivityForResult(addIntent, ADD_REQUEST);
                break;

            // TODO CHECKOUT provide the UI for checking out
            case R.id.checkout:
				Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
				startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                break;

            default:
        }
        return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.

		// Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
		switch(requestCode) {
			case ADD_REQUEST:
				// ADD: add the book that is returned to the shopping cart.
				if (resultCode == RESULT_OK){
					Bundle data = intent.getExtras();
					Book book = (Book) data.getParcelable("book");
					dba.persist(book);
					//Toast toast = Toast.makeText(getApplicationContext(), "author: "+book.authors[0], Toast.LENGTH_LONG);
					//toast.show();
					//shoppingCart.add(book);
				}
				break;
			case CHECKOUT_REQUEST:
				// CHECKOUT: empty the shopping cart.
				if (resultCode == RESULT_OK)
				{
					//shoppingCart.clear();
					dba.deleteAll();
				}
				break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).

	}

}