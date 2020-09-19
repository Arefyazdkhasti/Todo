package com.example.todo.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.Activities.NoteActivity;
import com.example.todo.Activities.NoteArchiveActivity;
import com.example.todo.Activities.NoteDetailActivity;
import com.example.todo.Activities.MainActivity;
import com.example.todo.Model.Note;
import com.example.todo.R;
import com.example.todo.DataBases.NoteSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable {

    private Context context;
    private List<Note> notes = new ArrayList<>();
    private List<Note> filtered_notes = new ArrayList<>();
    private NoteSQLiteHelper noteSQLiteHelper;
    private NoteActivity noteActivity;
    private LayoutInflater layoutInflater;
    private AlertDialog.Builder builder;
    private AlertDialog mAlertDialog;
    private onNoteItemClicked onNoteItemClicked;

    public NoteAdapter(Context context, List<Note> notes, onNoteItemClicked onNoteItemClicked) {
        this.context = context;
        this.notes = notes;
        this.filtered_notes = notes;
        layoutInflater = LayoutInflater.from(context);
        noteSQLiteHelper = new NoteSQLiteHelper(context);
        noteActivity = new NoteActivity();
        this.onNoteItemClicked = onNoteItemClicked;
    }

    public List<Note> getNotes() {
        return filtered_notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (context instanceof NoteActivity) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_view, parent, false);
        } else if (context instanceof MainActivity) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_view_small, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_view, parent, false);
        }
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        final Note note = filtered_notes.get(position);
        switch (note.getColor()) {
            case "red":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red));
                break;
            case "yellow":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_yellow));
                break;
            case "green":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_green));
                break;
            case "blue":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lighter_blue));
                break;
            case "purple":
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_purple));
                break;
            default:
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                break;
        }

        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.date_time.setText(note.getCreated_at());

        holder.delete_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDeleteDialog(note);

            }
        });

        holder.archive_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof NoteActivity) {
                    noteSQLiteHelper.markNoteAsArchive(note.getId(), 1);
                    NoteActivity n = new NoteActivity();
                    n.setUpNotes(context);
                    MainActivity m = new MainActivity();
                    m.setUpNoteRecyclerViews(context);
                    Toast.makeText(context, "Note Archived", Toast.LENGTH_SHORT).show();
                } else if (context instanceof NoteArchiveActivity) {
                    noteSQLiteHelper.markNoteAsArchive(note.getId(), 0);
                    NoteArchiveActivity n = new NoteArchiveActivity();
                    n.setUpNotes(context);
                    MainActivity m = new MainActivity();
                    m.setUpNoteRecyclerViews(context);
                    Toast.makeText(context, "Note UnArchived", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, NoteDetailActivity.class);
                String item;
                if (context instanceof MainActivity) {
                    item = "MainActivity";
                } else if (context instanceof NoteActivity) {
                    item = "NoteActivity";
                } else {
                    item = "nothing";
                }
                mIntent.putExtra("FROM_ACTIVITY", item);
                onNoteItemClicked.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (context instanceof MainActivity) {
            //show last 10 note in MainActivity
            if (filtered_notes.size()<10){
                return filtered_notes.size();
            }else{
                return 10;
            }
        } else {
            //show all notes in NoteActivity and NoteArchive
            return filtered_notes.size();
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView content;
        TextView date_time;
        ImageView delete_note;
        ImageView archive_note;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.note_card_view);
            title = itemView.findViewById(R.id.note_title);
            content = itemView.findViewById(R.id.note_content);
            date_time = itemView.findViewById(R.id.note_date);
            delete_note = itemView.findViewById(R.id.delete_note);
            archive_note = itemView.findViewById(R.id.archive_note);

            //set archive and unarchived icon
            if (context instanceof NoteArchiveActivity) {
                archive_note.setImageResource(R.drawable.unarchive);
            } else if (context instanceof NoteActivity) {
                archive_note.setImageResource(R.drawable.archive);
            }
        }
    }

    public interface onNoteItemClicked {
        void onNoteClick(Note note);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtered_notes = notes;
                } else {
                    List<Note> filteredList = new ArrayList<>();
                    for (Note row : notes) {
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) && row.getContent().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filtered_notes = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered_notes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered_notes = (ArrayList<Note>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void showConfirmDeleteDialog(Note note) {
        builder = new AlertDialog.Builder(context);
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.confirm_delete_item, null);

        Button noButton = view.findViewById(R.id.noButton);
        Button yesButton = view.findViewById(R.id.yesButton);

        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.show();

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteSQLiteHelper.deleteNote(note.getId());
                if (context instanceof NoteActivity) {
                    NoteActivity n = new NoteActivity();
                    n.setUpNotes(context);
                    MainActivity m = new MainActivity();
                    m.setUpNoteRecyclerViews(context);
                } else if (context instanceof NoteArchiveActivity) {
                    NoteArchiveActivity n = new NoteArchiveActivity();
                    n.setUpNotes(context);
                    MainActivity m = new MainActivity();
                    m.setUpNoteRecyclerViews(context);
                }
                //Toast.makeText(context, "delete " + note.getTitle(), Toast.LENGTH_SHORT).show();
                mAlertDialog.dismiss();
            }
        });

    }
}
