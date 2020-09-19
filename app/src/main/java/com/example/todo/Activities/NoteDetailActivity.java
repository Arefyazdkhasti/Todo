package com.example.todo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todo.Model.Note;
import com.example.todo.R;
import com.example.todo.DataBases.NoteSQLiteHelper;

public class NoteDetailActivity extends AppCompatActivity {

    private NoteDetailActivity activity;
    private static final String TAG = "note detail";
    public static final String EXTRA_KEY_NOTE = "note";
    public static final String FROM_ACTIVITY = "from_activity";
    private Note note;
    private String fromActivity;
    private NoteSQLiteHelper noteSQLiteHelper;

    private LinearLayout layout;
    private EditText title;
    private EditText content;
    private TextView date;

    private AlertDialog mAlertDialog;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        init();
        note = getIntent().getParcelableExtra(EXTRA_KEY_NOTE);
        fromActivity = getIntent().getStringExtra(FROM_ACTIVITY);
        if (note == null) {
            throw new IllegalStateException("food is null");
        }

        switch (note.getColor()) {
            case "red":
                layout.setBackgroundColor(ContextCompat.getColor(NoteDetailActivity.this, R.color.light_red));
                break;
            case "yellow":
                layout.setBackgroundColor(ContextCompat.getColor(NoteDetailActivity.this, R.color.light_yellow));
                break;
            case "green":
                layout.setBackgroundColor(ContextCompat.getColor(NoteDetailActivity.this, R.color.light_green));
                break;
            case "blue":
                layout.setBackgroundColor(ContextCompat.getColor(NoteDetailActivity.this, R.color.lighter_blue));
                break;
            case "purple":
                layout.setBackgroundColor(ContextCompat.getColor(NoteDetailActivity.this, R.color.light_purple));
                break;
            default:
                layout.setBackgroundColor(ContextCompat.getColor(NoteDetailActivity.this, R.color.white));
                break;
        }
        title.setText(note.getTitle());
        content.setText(note.getContent());
        date.setText(note.getCreated_at());
    }

    private void init() {
        layout = findViewById(R.id.note_detail_layout);
        title = findViewById(R.id.note_detail_title);
        content = findViewById(R.id.note_detail_content);
        date = findViewById(R.id.note_detail_date);
        layoutInflater = LayoutInflater.from(this);
        noteSQLiteHelper = new NoteSQLiteHelper(this);
    }

    public static void start(Context context, Note note, String activity) {
        Intent intent = new Intent(context, NoteDetailActivity.class);
        intent.putExtra(EXTRA_KEY_NOTE, note);
        intent.putExtra(FROM_ACTIVITY, activity);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        String newTitle = title.getText().toString().trim();
        String newContent = content.getText().toString().trim();

        if (newTitle.equals(note.getTitle()) && newContent.equals(note.getContent())) {
            Log.d(TAG, "no change in note");
            finish();
        } else {
            showSaveDialog(this);
        }
    }

    private void showSaveDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.confirm_update_note, null);

        Button noButton = view.findViewById(R.id.noButton);
        Button yesButton = view.findViewById(R.id.yesButton);

        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.show();

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                finish();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewData();
                mAlertDialog.dismiss();
                finish();
            }
        });

    }

    private void saveNewData() {
        String newTitle = title.getText().toString().trim();
        String newContent = content.getText().toString().trim();

        noteSQLiteHelper.editNote(note.getId(), newTitle, newContent);


        //Toast.makeText(this, fromActivity, Toast.LENGTH_SHORT).show();
        if (fromActivity.equals("NoteActivity")) {
            NoteActivity n = new NoteActivity();
            n.setUpNotes(this);
        }else if(fromActivity.equals("NoteArchive")){
            NoteArchiveActivity n = new NoteArchiveActivity();
            n.setUpNotes(this);
        }
    }

}

