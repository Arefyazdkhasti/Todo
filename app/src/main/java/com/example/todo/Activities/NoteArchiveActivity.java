package com.example.todo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.todo.Fragments.AddNoteFragment;
import com.example.todo.Model.Note;
import com.example.todo.R;
import com.example.todo.Adapters.NoteAdapter;
import com.example.todo.DataBases.NoteSQLiteHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NoteArchiveActivity extends AppCompatActivity {

    private static RecyclerView noteRecyclerView;
    private static NoteAdapter noteAdapter;
    private static NoteSQLiteHelper noteSQLiteHelper;
    private static SearchView searchView;
    private static FloatingActionButton fab;
    private TextView noteArchive;
    private LinearLayout noNoteYet;
    Context this_context;


    NoteAdapter.onNoteItemClicked onNoteItemClicked = new NoteAdapter.onNoteItemClicked() {
        @Override
        public void onNoteClick(Note note) {
            NoteDetailActivity.start(this_context, note, "NoteArchive");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for toolbar and back if needed
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_note);
        init();
        this_context = getApplicationContext();


        setUpNotes(this);
        setSearch();

        noteRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !fab.isShown())
                    fab.show();
                else if (dy > 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNoteFragment addNoteFragment = new AddNoteFragment();
                addNoteFragment.show(getSupportFragmentManager(), addNoteFragment.getTag());
            }
        });
    }

    private void init() {
        noteRecyclerView = findViewById(R.id.notes_recycler_view);
        fab = findViewById(R.id.add_note_btn);
        noNoteYet = findViewById(R.id.no_note_yet_layout);
        noteArchive=findViewById(R.id.note_archive);
        noteArchive.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);
    }


    public void setUpNotes(Context context) {
        noteSQLiteHelper = new NoteSQLiteHelper(this);
        List<Note> posts = noteSQLiteHelper.getNotes(1, context);
        noteAdapter = new NoteAdapter(context, posts, onNoteItemClicked);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        noteRecyclerView.setLayoutManager(linearLayoutManager);
        noteRecyclerView.setItemAnimator(new DefaultItemAnimator());
        noteRecyclerView.setAdapter(noteAdapter);
        this_context = context;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.search_view_note);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noteAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteAdapter.getFilter().filter(newText);
                return false;
            }

        });
    }

}

