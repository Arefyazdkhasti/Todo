package com.example.todo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.todo.Model.Note;
import com.example.todo.Adapters.NoteAdapter;
import com.example.todo.DataBases.NoteSQLiteHelper;
import com.example.todo.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static List<Note> notes;
    private View main_content;
    private BottomAppBar bottomAppBar;
    private LinearLayout fabLayout1, fabLayout2;
    private View fabBGLayout;
    private static RecyclerView latestNote, latestTodoList;
    private FloatingActionButton fab, fab_add_note, fab_add_todo;
    boolean isFABOpen = false;
    private static NoteSQLiteHelper noteSQLiteHelper;
    private static NoteAdapter noteAdapter;
    private SwitchCompat toggleDarkMode;
    private ImageView refresh_latest_note;
    private SearchView searchView;
    private int orientation;


    NoteAdapter.onNoteItemClicked onNoteItemClicked = new NoteAdapter.onNoteItemClicked() {
        @Override
        public void onNoteClick(Note note) {
            NoteDetailActivity.start(MainActivity.this, note, "MainActivity");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        orientation = this.getResources().getConfiguration().orientation;
        noteSQLiteHelper = new NoteSQLiteHelper(this);
        setUpBottomAppBar();
        setUpNoteRecyclerViews(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

    }

    public void setUpNoteRecyclerViews(Context context) {
        notes = noteSQLiteHelper.getNotes(0, context);
        noteAdapter = new NoteAdapter(context, notes, onNoteItemClicked);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
        StaggeredGridLayoutManager staggeredGridLayoutManager;
        if (orientation == 1) {
            //set 2 column for portrait mode
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            //set 3 column for landscape mode
            staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        }
        latestNote.setLayoutManager(staggeredGridLayoutManager);
        latestNote.setAdapter(noteAdapter);

    }

    private void init() {

        main_content = findViewById(R.id.main_content);
        fabLayout1 = findViewById(R.id.fabLayout1);
        fabLayout2 = findViewById(R.id.fabLayout2);
        fab = findViewById(R.id.fab);
        fab_add_note = findViewById(R.id.fab_add_note);
        fab_add_todo = findViewById(R.id.fab_add_todo);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        latestNote = findViewById(R.id.latest_note_recycler_view);
        latestTodoList = findViewById(R.id.latest_todo_recycler_view);
        refresh_latest_note = main_content.findViewById(R.id.refresh);
        searchView = main_content.findViewById(R.id.search_view);


    }

    private void setUpBottomAppBar() {
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        //add menu to bottom app bar
        setSupportActionBar(bottomAppBar);

        bottomAppBar.replaceMenu(R.menu.bottom_app_bar_menu);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(MainActivity.this, "search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_notification:
                        Toast.makeText(MainActivity.this, "list", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.notes_archive:
                        //Toast.makeText(MainActivity.this, "note archive", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, NoteArchiveActivity.class));
                        break;
                    case R.id.task_archive:
                        //Toast.makeText(MainActivity.this, "task archive", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, TaskArchiveActivity.class));
                        break;
                }
                return false;
            }
        });
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);

        fab_add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
                //closed when we return to main activity
                fabLayout1.setVisibility(View.GONE);
                fabLayout2.setVisibility(View.GONE);
                fabBGLayout.setVisibility(View.GONE);
            }
        });

        fab_add_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TaskActivity.class));
                fabLayout1.setVisibility(View.GONE);
                fabLayout2.setVisibility(View.GONE);
                fabBGLayout.setVisibility(View.GONE);
            }
        });
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab.animate().rotation(0);
        fabLayout1.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fabLayout2.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        fabBGLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_app_bar_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onRestart() {
        setUpNoteRecyclerViews(this);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}