package com.example.todo.DataBases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.todo.Model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "NoteDatabaseOpenHelper";
    private static final String DB_NAME = "Notes database";
    private static final String TABLE_NAME = "Note_table";
    private static final String COL_TASK_ID = "id";
    private static final String COL_TASK_TITLE = "title";
    private static final String COL_TASK_CONTENT = "content";
    private static final String COL_TASK_COLOR = "color";
    private static final String COL_TASK_IS_ARCHIVED = "is_archived";
    private static final String COL_TASK_CREATE_AT = "created_at";
    private Context context;


    private static final String createNoteListTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TASK_TITLE + " TEXT," +
            COL_TASK_CONTENT + " TEXT," +
            COL_TASK_COLOR + " TEXT," +
            COL_TASK_IS_ARCHIVED + " INTEGER," +
            COL_TASK_CREATE_AT + " DATETIME);";


    public NoteSQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createNoteListTable);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean addNote(Note note) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TASK_TITLE, note.getTitle());
        cv.put(COL_TASK_CONTENT, note.getContent());
        cv.put(COL_TASK_COLOR, note.getColor());
        cv.put(COL_TASK_IS_ARCHIVED, note.getIs_archived());
        cv.put(COL_TASK_CREATE_AT, note.getCreated_at());


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long isInserted = sqLiteDatabase.insert(TABLE_NAME, null, cv);

        Log.i(TAG, "addPost: " + isInserted);
        if (isInserted > 0) {
            return true;
        } else {
            return false;
        }
    }

   /* public void addPosts(List<Task> posts) {
        for (int i = 0; i < posts.size(); i++) {
            addPost(posts.get(i));
        }
    }*/


    public List<Note> getNotes(int archive,Context context) {
        List<Note> notes = new ArrayList<>();
        NoteSQLiteHelper n=new NoteSQLiteHelper(context);
        SQLiteDatabase sqLiteDatabase = n.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TASK_IS_ARCHIVED + " = " + archive + " ORDER BY " + COL_TASK_ID + " DESC ", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                note.setColor(cursor.getString(3));
                note.setIs_archived(cursor.getInt(4));
                note.setCreated_at(cursor.getString(5));
                notes.add(note);
                //برای اینکه لوپ بتونه بسته شه
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return notes;
    }


    public void deleteNote(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, COL_TASK_ID + " = ?", new String[]{String.valueOf(id)});
    }


    public void markNoteAsArchive(int id, int archive) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String strSQL = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_IS_ARCHIVED + " =" + archive + " WHERE " + COL_TASK_ID + " =" + id;

        sqLiteDatabase.execSQL(strSQL);
    }

    public void editNote(int id, String title, String content) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String strSQL = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_TITLE + " =" + "\'" + title + "\'" + " WHERE " + COL_TASK_ID + " =" + id;
        String strSQL2 = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_CONTENT + " =" + "\'" + content + "\'" + " WHERE " + COL_TASK_ID + " =" + id;

        sqLiteDatabase.execSQL(strSQL);
        sqLiteDatabase.execSQL(strSQL2);
    }


}
