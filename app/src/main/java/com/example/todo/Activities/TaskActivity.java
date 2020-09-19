package com.example.todo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SearchView;

import com.example.todo.Fragments.AddTaskFragment;
import com.example.todo.Model.Task;
import com.example.todo.R;
import com.example.todo.CallBacks.SwipeToArchiveCallBack;
import com.example.todo.CallBacks.SwipeToDeleteCallback;
import com.example.todo.Adapters.TaskAdapter;
import com.example.todo.DataBases.TaskSQLiteHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private static final String TAG = "TaskActivity";
    private static RecyclerView tasksRecyclerView;
    private static RecyclerView doneTasksRecyclerView;
    private static FloatingActionButton addTaskBtn;
    private static TaskSQLiteHelper taskSQLiteHelper;
    private static TaskAdapter taskAdapter_todo;
    private static TaskAdapter taskAdapter_done;
    public static final String my_preference = "mypref";
    public static final String filter = "filter";
    private static SharedPreferences sharedPreferences;
    private CoordinatorLayout coordinatorLayout;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //for toolbar and back if needed
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tasksRecyclerView = findViewById(R.id.task_recycler_view);
        doneTasksRecyclerView = findViewById(R.id.done_task_recycler_view);
        addTaskBtn = findViewById(R.id.add_btn);
        taskSQLiteHelper = new TaskSQLiteHelper(this);
        coordinatorLayout = findViewById(R.id.main_layout);
        sharedPreferences = getSharedPreferences(my_preference, MODE_PRIVATE);

        getToDoTaskFromDatabase(this);
        getDONETaskFromDatabase(this);
        System.out.println("task todo size: " + taskAdapter_todo.getTasks().size());
        System.out.println("task done size: " + taskAdapter_done.getTasks().size());
        enableSwipeToDeleteAndUndo_TODO();
        enableSwipeToDeleteAndUndo_DONE();
        enableSwipeToArchive_TODO();
        enableSwipeToArchive_DONE();
        setSearch();
        hideFABonScroll();

        findViewById(R.id.filter_tasks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddTaskFragment addTaskFragment = new AddTaskFragment();
                        addTaskFragment.show(getSupportFragmentManager(), addTaskFragment.getTag());
                        //showAddTaskDialog();
                    }
                });
            }
        });


    }

    private void hideFABonScroll() {
        tasksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !addTaskBtn.isShown())
                    addTaskBtn.show();
                else if (dy > 0 && addTaskBtn.isShown())
                    addTaskBtn.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        doneTasksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !addTaskBtn.isShown())
                    addTaskBtn.show();
                else if (dy > 0 && addTaskBtn.isShown())
                    addTaskBtn.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    public static void getToDoTaskFromDatabase(Context context) {

        int filter_index = sharedPreferences.getInt(filter, 0);
        List<Task> posts;
        if (filter_index == 0) {
            posts = taskSQLiteHelper.getPosts(0, 0);
        } else if (filter_index == 1) {
            posts = taskSQLiteHelper.getPostsByPro(0, 0);
        } else {
            posts = taskSQLiteHelper.getPosts(0, 0);
        }


        taskAdapter_todo = new TaskAdapter(context, posts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        tasksRecyclerView.setLayoutManager(linearLayoutManager);
        tasksRecyclerView.setAdapter(taskAdapter_todo);
    }

    public static void getDONETaskFromDatabase(Context context) {
        int filter_index = sharedPreferences.getInt(filter, 0);
        List<Task> posts;
        if (filter_index == 0) {
            posts = taskSQLiteHelper.getPosts(1, 0);
        } else if (filter_index == 1) {
            posts = taskSQLiteHelper.getPostsByPro(1, 0);
        } else {
            posts = taskSQLiteHelper.getPosts(1, 0);
        }
        /*for (int i = 0; i < posts.size(); i++) {
            System.out.println("title: " + posts.get(i).getDescription() + " is done: " + posts.get(i).getIs_done());
        }*/

        taskAdapter_done = new TaskAdapter(context, posts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        doneTasksRecyclerView.setLayoutManager(linearLayoutManager);
        doneTasksRecyclerView.setAdapter(taskAdapter_done);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                showFilterDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showFilterDialog() {
        int checkedItem;

        SharedPreferences.Editor editor = sharedPreferences.edit();

        final String[] items = {" Date", " Priority"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select filter : ");
        if (sharedPreferences.contains(filter)) {
            checkedItem = sharedPreferences.getInt(filter, 0);
        } else {
            checkedItem = 0;
        }
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putInt(filter, which);
                editor.apply();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sharedPreferences.contains(filter)) {
                    //Toast.makeText(TaskActivity.this, Integer.toString(sharedPreferences.getInt(filter, 0)), Toast.LENGTH_SHORT).show();
                    editor.clear();
                    getDONETaskFromDatabase(TaskActivity.this);
                    getToDoTaskFromDatabase(TaskActivity.this);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        editor.apply();
        editor.commit();

    }

    private void showAddTaskDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_task_dialog);

        Button saveButton = (Button) dialog.findViewById(R.id.save_task);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) dialog.findViewById(R.id.title_et)).getText().toString();
                String category = ((EditText) dialog.findViewById(R.id.category_et)).getText().toString();
                RadioButton low = dialog.findViewById(R.id.low);
                RadioButton medium = dialog.findViewById(R.id.medium);
                RadioButton high = dialog.findViewById(R.id.high);


                Task mainTask = new Task();
                SQLiteDatabase db = taskSQLiteHelper.getWritableDatabase();
                mainTask.setDescription(title);
                mainTask.setCategory(category);
                mainTask.setIs_done(0);

                if (low.isChecked()) {
                    mainTask.setPriority(1);
                } else if (medium.isChecked()) {
                    mainTask.setPriority(2);
                } else if (high.isChecked()) {
                    mainTask.setPriority(3);
                }


                taskSQLiteHelper.addPost(mainTask);
                getToDoTaskFromDatabase(TaskActivity.this);
                getDONETaskFromDatabase(TaskActivity.this);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void setSearch() {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskAdapter_todo.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskAdapter_todo.getFilter().filter(newText);
                return false;
            }

        });
    }

    private void enableSwipeToDeleteAndUndo_TODO() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                int id = taskAdapter_todo.getTasks().get(position).getId();

                taskSQLiteHelper.deletePost(id);

                getDONETaskFromDatabase(TaskActivity.this);
                getToDoTaskFromDatabase(TaskActivity.this);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        tasksRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(tasksRecyclerView);

    }

    private void enableSwipeToDeleteAndUndo_DONE() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                int id = taskAdapter_done.getTasks().get(position).getId();

                taskSQLiteHelper.deletePost(id);

                getDONETaskFromDatabase(TaskActivity.this);
                getToDoTaskFromDatabase(TaskActivity.this);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        doneTasksRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(doneTasksRecyclerView);

    }


    private void enableSwipeToArchive_TODO() {
        SwipeToArchiveCallBack swipeToDeleteCallback = new SwipeToArchiveCallBack(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                int id = taskAdapter_todo.getTasks().get(position).getId();

                taskSQLiteHelper.markAsArchive(id, 1);

                getDONETaskFromDatabase(TaskActivity.this);
                getToDoTaskFromDatabase(TaskActivity.this);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was archived.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        taskSQLiteHelper.markAsArchive(id, 0);
                        getDONETaskFromDatabase(TaskActivity.this);
                        getToDoTaskFromDatabase(TaskActivity.this);
                        tasksRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(tasksRecyclerView);

    }

    private void enableSwipeToArchive_DONE() {
        SwipeToArchiveCallBack swipeToDeleteCallback = new SwipeToArchiveCallBack(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                int id = taskAdapter_done.getTasks().get(position).getId();

                taskSQLiteHelper.markAsArchive(id, 1);

                getDONETaskFromDatabase(TaskActivity.this);
                getToDoTaskFromDatabase(TaskActivity.this);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item was archived.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        taskSQLiteHelper.markAsArchive(id, 0);
                        getDONETaskFromDatabase(TaskActivity.this);
                        getToDoTaskFromDatabase(TaskActivity.this);
                        doneTasksRecyclerView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(doneTasksRecyclerView);

    }


}
