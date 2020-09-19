package com.example.todo.Fragments;


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

import com.example.todo.R;
import com.example.todo.Model.Task;
import com.example.todo.Activities.TaskActivity;
import com.example.todo.Adapters.TaskAdapter;
import com.example.todo.DataBases.TaskSQLiteHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddTaskFragment extends BottomSheetDialogFragment {

    EditText taskDes;
    EditText taskCategory;
    Button taskSaveButton;
    RadioButton low, medium, high;
    android.content.Context Context;
    TaskAdapter taskAdapter;
    TaskSQLiteHelper taskSQLiteHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_task_dialog, container, false);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        taskSQLiteHelper = new TaskSQLiteHelper(getContext());
        setUpFragmentUi(view);
        return view;
    }

    private void setUpFragmentUi(View view) {

        taskDes = view.findViewById(R.id.title_et);
        taskCategory = view.findViewById(R.id.category_et);
        taskSaveButton = view.findViewById(R.id.save_task);
        low = view.findViewById(R.id.low);
        medium = view.findViewById(R.id.medium);
        high = view.findViewById(R.id.high);

        taskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!taskDes.getText().toString().isEmpty() && !taskCategory.getText().toString().isEmpty()) {
                    //Toast.makeText(getContext(), "Save", Toast.LENGTH_SHORT).show();
                    saveNoteToDB(view);
                }
            }
        });

    }

    private void saveNoteToDB(View view) {
        String desc = taskDes.getText().toString().trim();
        String category = taskCategory.getText().toString().trim();
        int priority;
        if (low.isChecked()){
            priority=1;
        }else if(medium.isChecked()){
            priority=2;
        }else if(high.isChecked()){
            priority=3;
        }else{
            priority=1;
        }

       Task mainTask = new Task();
        SQLiteDatabase db = taskSQLiteHelper.getWritableDatabase();
        mainTask.setDescription(desc);
        mainTask.setCategory(category);
        mainTask.setPriority(priority);
        mainTask.setIs_done(0);
        mainTask.setIs_archive(0);
        taskSQLiteHelper.addPost(mainTask);

        dismiss();
        TaskActivity.getDONETaskFromDatabase(getContext());
        TaskActivity.getToDoTaskFromDatabase(getContext());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (taskAdapter != null) {
            taskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
