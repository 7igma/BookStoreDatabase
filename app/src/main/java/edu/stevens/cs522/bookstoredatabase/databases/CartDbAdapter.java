package edu.stevens.cs522.bookstoredatabase.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;
import edu.stevens.cs522.bookstoredatabase.entities.Book;

/**
 * Created by dduggan.
 */

public class CartDbAdapter {

    private static final String DATABASE_NAME = "books.db";

    private static final String BOOK_TABLE = "books";

    private static final String AUTHOR_TABLE = "authors";

    private static final int DATABASE_VERSION = 1;

    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_AUTHORS = "authors";
    public static final String COL_ISBN = "isbn";
    public static final String COL_PRICE = "price";

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE =
                "create table books (_id integer primary key autoincrement, title text, authors text, isbn text, price text);";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS books");
            db.execSQL("DROP TABLE IF EXISTS authors");
            onCreate(db);
        }
    }


    public CartDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public Cursor fetchAllBooks() {
        return db.query(BOOK_TABLE, new String[] {COL_ID, COL_TITLE, COL_AUTHORS,
                COL_ISBN, COL_PRICE}, null, null, null, null, null);
    }

    public Book fetchNthBook(long n)
    {
        String query = "select * from "+ BOOK_TABLE +" WHERE rownum = 2";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }
        return new Book(cursor);
    }

    public Book fetchBook(long rowId) {
        Cursor cursor =
                db.query(true, BOOK_TABLE, new String[] {COL_ID, COL_TITLE, COL_AUTHORS, COL_ISBN, COL_PRICE}, COL_ID + "=" + rowId, null,
                        null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new Book(cursor);
    }

    public void persist(Book book) throws SQLException
    {

        ContentValues vals = new ContentValues();
        if (book.title != null)
        {
            BookContract.putTitle(vals, book.title);
        }

        if (book.authors != null)
        {
            String[] authornames = new String[book.authors.length];
            for (int i = 0; i < book.authors.length; i++)
            {

                authornames[i] = book.authors[i].toString();
            }
            BookContract.putAuthors(vals, authornames);
        }
        if (book.isbn != null)
        {
            BookContract.putISBN(vals, book.isbn);
        }
        if (book.price != null)
        {
            BookContract.putPrice(vals, book.price);
        }

        db.insert(BOOK_TABLE, null, vals);
    }

    public boolean delete(Book book) {
        String title = book.getTitle();
        String[] args = {title};
        return db.delete(BOOK_TABLE, COL_TITLE+"=?", args) > 0;
    }

    public boolean deleteAll() {
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + BOOK_TABLE + "'");
        db.delete(BOOK_TABLE, null, null);
        return true;
    }

    public void close() {
        db.close();
    }

}
