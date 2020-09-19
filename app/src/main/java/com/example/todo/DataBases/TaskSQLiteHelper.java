package com.example.todo.DataBases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.todo.Model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseOpenHelper";
    private static final String DB_NAME = "Todo DataBase";
    private static final String TABLE_NAME = "TASKS";
    private static final String COL_TASK_ID = "id";
    private static final String COL_TASK_DESC = "description";
    private static final String COL_TASK_IS_DONE = "is_done";
    private static final String COL_TASK_PRIORITY = "priority";
    private static final String COL_TASK_CATEGORY = "category";
    private static final String COL_TASK_IS_ARCHIVED = "archive";
    private Context context;

    private static final String createTodoListTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TASK_DESC + " TEXT," +
            COL_TASK_PRIORITY + " INTEGER," +
            COL_TASK_IS_ARCHIVED + " INTEGER," +
            COL_TASK_CATEGORY + " TEXT," +
            COL_TASK_IS_DONE + " INTEGER);";


    public TaskSQLiteHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createTodoListTable);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean addPost(Task task) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TASK_DESC, task.getDescription());
        cv.put(COL_TASK_PRIORITY, task.getPriority());
        cv.put(COL_TASK_IS_ARCHIVED, task.getIs_archive());
        cv.put(COL_TASK_CATEGORY, task.getCategory());
        cv.put(COL_TASK_IS_DONE, task.getIs_done());


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

    public List<Task> getPosts(int done, int archive) {
        List<Task> posts = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TASK_IS_DONE + " = " + done + " AND " + COL_TASK_IS_ARCHIVED + " = " + archive + " ORDER BY " + COL_TASK_ID + " DESC ", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Task post = new Task();
                post.setId(cursor.getInt(0));
                post.setDescription(cursor.getString(1));
                post.setPriority(cursor.getInt(2));
                post.setIs_archive(cursor.getInt(3));
                post.setCategory(cursor.getString(4));
                post.setIs_done(cursor.getInt(5));
                posts.add(post);
                //برای اینکه لوپ بتونه بسته شه
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return posts;
    }

    public List<Task> getPostsByPro(int done, int archive) {
        List<Task> posts = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT  * FROM " + TABLE_NAME + " WHERE " + COL_TASK_IS_DONE + " = " + done + " AND " + COL_TASK_IS_ARCHIVED + " = " + archive + " ORDER BY " + COL_TASK_PRIORITY + " DESC ", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Task post = new Task();
                post.setId(cursor.getInt(0));
                post.setDescription(cursor.getString(1));
                post.setPriority(cursor.getInt(2));
                post.setIs_archive(cursor.getInt(3));
                post.setCategory(cursor.getString(4));
                post.setIs_done(cursor.getInt(5));
                posts.add(post);
                cursor.moveToNext();
            }
        }
        cursor.close();
        sqLiteDatabase.close();

        return posts;
    }

    public void deletePost(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, COL_TASK_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void markAsDone(int id, int done) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String strSQL = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_IS_DONE + " =" + done + " WHERE " + COL_TASK_ID + " =" + id;

        sqLiteDatabase.execSQL(strSQL);
    }

    public void markAsArchive(int id, int archive) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String strSQL = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_IS_ARCHIVED + " =" + archive + " WHERE " + COL_TASK_ID + " =" + id;

        sqLiteDatabase.execSQL(strSQL);
    }

    public void Update(int id, String title, String cat, int pro) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String strSQL = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_DESC + " =" + "\'" + title + "\'" + " WHERE " + COL_TASK_ID + " =" + id;
        String strSQL2 = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_PRIORITY + " =" + "\'" + pro + "\'" + " WHERE " + COL_TASK_ID + " =" + id;
        String strSQL3 = "UPDATE " + TABLE_NAME + " SET " + COL_TASK_CATEGORY + " =" + "\'" + cat + "\'" + " WHERE " + COL_TASK_ID + " =" + id;

        sqLiteDatabase.execSQL(strSQL);
        sqLiteDatabase.execSQL(strSQL2);
        sqLiteDatabase.execSQL(strSQL3);
    }


}
