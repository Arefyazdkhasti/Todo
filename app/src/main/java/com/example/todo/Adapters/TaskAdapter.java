package com.example.todo.Adapters;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todo.Activities.TaskActivity;
import com.example.todo.Model.Task;
import com.example.todo.R;
import com.example.todo.DataBases.TaskSQLiteHelper;
import com.robertlevonyan.views.expandable.Expandable;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>implements Filterable {

    Context context;
    List<Task> tasks = new ArrayList<>();
    List<Task> filtered_tasks=new ArrayList<>();
    TaskSQLiteHelper taskSQLiteHelper;
    TaskActivity taskActivity;

    public TaskAdapter(Context context) {
        this.context = context;
    }

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
        this.filtered_tasks=tasks;
        taskSQLiteHelper = new TaskSQLiteHelper(context);
        taskActivity=new TaskActivity();
    }


    public void setTasks(List<Task> task) {
        this.tasks = task;
        notifyDataSetChanged();

    }

    public List<Task> getTasks(){
        return tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final Task task = filtered_tasks.get(position);

        holder.title.setText(task.getDescription());
        holder.category.setText(task.getCategory());

        switch (task.getPriority()){
            case 1:
                //Glide.with(context).load(R.drawable.one).into(holder.priority);
                holder.priority.setImageResource(R.drawable.one);
                break;
            case 2:
                //Glide.with(context).load(R.drawable.two).into(holder.priority);
                holder.priority.setImageResource(R.drawable.two);
                break;
            case 3:
                holder.priority.setImageResource(R.drawable.three);
                //Glide.with(context).load(R.drawable.three).into(holder.priority);
                break;
        }

        if (task.getIs_done() == 0) {
            holder.checkBox.setChecked(false);
        } else {
            holder.checkBox.setChecked(true);
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.task_done));
        }

        holder.title_expand.setText(task.getDescription());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    taskSQLiteHelper.markAsDone(task.getId(), 1);
                    TaskActivity.getToDoTaskFromDatabase(context);
                    TaskActivity.getDONETaskFromDatabase(context);
                } else {
                    taskSQLiteHelper.markAsDone(task.getId(), 0);
                    TaskActivity.getToDoTaskFromDatabase(context);
                    TaskActivity.getDONETaskFromDatabase(context);
                }
            }
        });
        switch (task.getPriority()){
            case 1:
                holder.low_rb.setChecked(true);
                break;
            case 2:
                holder.medium_rb.setChecked(true);
                break;
            case 3:
                holder.high_rb.setChecked(true);
                break;

        }


        holder.saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_title=holder.title_expand.getText().toString().trim();
                String new_category=holder.category.getText().toString().trim();
                int new_priority;
                if(holder.low_rb.isChecked()){
                    new_priority=1;
                }else if(holder.medium_rb.isChecked()){
                    new_priority=2;
                }else{
                    new_priority=3;
                }
                taskSQLiteHelper.Update(task.getId(), new_title,new_category,new_priority);
                TaskActivity.getToDoTaskFromDatabase(context);
                TaskActivity.getDONETaskFromDatabase(context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return filtered_tasks.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout header;
        RelativeLayout content;
        Expandable expandable;
        RelativeLayout layout;
        CheckBox checkBox;
        TextView title;
        EditText title_expand;
        ImageView priority;
        EditText category;
        RadioButton low_rb;
        RadioButton medium_rb;
        RadioButton high_rb;
        ImageView delete_task;

        Button saveChange;
        Button cancelChange;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.task_layout);
            checkBox = itemView.findViewById(R.id.task_checkbox);

            expandable=itemView.findViewById(R.id.expandable);
            header=itemView.findViewById(R.id.header);
            content=itemView.findViewById(R.id.content);

            title = header.findViewById(R.id.task_title);
            title_expand = content.findViewById(R.id.title_edit_text);
            priority=header.findViewById(R.id.priority_number);
            category=content.findViewById(R.id.category_edit_text);
            low_rb=content.findViewById(R.id.low);
            medium_rb=content.findViewById(R.id.medium);
            high_rb=content.findViewById(R.id.high);

            saveChange=content.findViewById(R.id.save_change_btn);
            cancelChange=content.findViewById(R.id.cancel_change_btn);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtered_tasks = tasks;
                } else {
                    List<Task> filteredList = new ArrayList<>();
                    for (Task row : tasks) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDescription().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtered_tasks = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered_tasks;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered_tasks = (ArrayList<Task>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void removeItem(int position) {
        tasks.remove(position);
        taskSQLiteHelper.deletePost(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Task task, int position) {
        tasks.add(position, task);
        notifyItemInserted(position);
    }


}
