package com.example.todo.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todo.Activities.NoteActivity;
import com.example.todo.Activities.MainActivity;
import com.example.todo.Model.Note;
import com.example.todo.R;
import com.example.todo.Adapters.NoteAdapter;
import com.example.todo.DataBases.NoteSQLiteHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteFragment extends BottomSheetDialogFragment {

    EditText noteTitle;
    EditText noteContent;
    Button noteSaveButton;
    RadioButton red, yellow, green, blue, purple;
    Context Context;
    NoteAdapter noteAdapter;
    NoteSQLiteHelper noteSQLiteHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_note_bottom_sheet_fragment, container, false);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        noteSQLiteHelper = new NoteSQLiteHelper(getContext());
        setUpFragmentUi(view);
        return view;
    }

    private void setUpFragmentUi(View view) {

        noteTitle = view.findViewById(R.id.note_title_et);
        noteContent = view.findViewById(R.id.note_content_et);
        noteSaveButton = view.findViewById(R.id.note_save_btn);
        red = view.findViewById(R.id.red_radio_button);
        yellow = view.findViewById(R.id.yellow_radio_button);
        green = view.findViewById(R.id.green_radio_button);
        blue = view.findViewById(R.id.blue_radio_button);
        purple = view.findViewById(R.id.purple_radio_button);

        noteSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noteTitle.getText().toString().isEmpty() && !noteContent.getText().toString().isEmpty()) {
                    //Toast.makeText(getContext(), "Save", Toast.LENGTH_SHORT).show();
                    saveNoteToDB(view);
                }
            }
        });

    }

    private void saveNoteToDB(View view) {
        String title = noteTitle.getText().toString().trim();
        String content = noteContent.getText().toString().trim();

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        String dateTime = currentDate + " " + currentTime;
        Note mainNote = new Note();

        if (red.isChecked()) {
            mainNote.setColor("red");
        } else if (yellow.isChecked()) {
            mainNote.setColor("yellow");
        } else if (green.isChecked()) {
            mainNote.setColor("green");
        } else if (blue.isChecked()) {
            mainNote.setColor("blue");
        } else if (purple.isChecked()) {
            mainNote.setColor("purple");
        } else {
            mainNote.setColor("white");
        }

        SQLiteDatabase db = noteSQLiteHelper.getWritableDatabase();
        mainNote.setTitle(title);
        mainNote.setContent(content);
        mainNote.setCreated_at(dateTime);
        mainNote.setIs_archived(0);
        noteSQLiteHelper.addNote(mainNote);

        dismiss();
        NoteActivity n = new NoteActivity();
        n.setUpNotes(getContext());

        MainActivity m=new MainActivity();
        m.setUpNoteRecyclerViews(getContext());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
