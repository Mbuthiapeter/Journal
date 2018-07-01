
/**
 * Created by Mbuthia Peter on 25/06/2018.
 * As part of completion of 7 days of code challenge
 * In pursuit of the Google ALC nano degree program.
 * ALC With Google 3.0
 */
package com.friki.mbuthia.journalapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friki.mbuthia.journalapp.authPack.JournalAuth;
import com.friki.mbuthia.journalapp.database.JournalDatabase;
import com.friki.mbuthia.journalapp.database.JournalEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    // Constant for date format
    private static final String DATE_FORMAT = "EEE, d MMM yyyy";

    // Constant for logging
    private static final String TAG = JournalAdapter.class.getSimpleName();

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<JournalEntry> mJournalEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    private boolean multiSelect = false;

    //onLongClicked item identifier
    private int mItemLongClicked;
    private JournalEntry mClickedItem;

    //Menu Item identifiers
    private static final String DELETE = "Delete";
    private static final String EDIT = "Edit";


    private ArrayList<Integer> selectedItems = new ArrayList<Integer>();

    /**
     * Constructor for the JournalAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public JournalAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

        //Database instance
        JournalDatabase mDb = JournalDatabase.getInstance(JournalAuth.getContext());

    //Handling the onLongClick by displaying Contextual Action Bar to remove items and edit
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = false;
            menu.add(DELETE);
            menu.add(EDIT);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
         switch (item.toString()){

                case EDIT:
                    // Launch AddTaskActivity adding the itemId as an extra in the intent
                    Intent intent = new Intent(mContext, AddItemActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AddItemActivity.EXTRA_ENTRY_ID, mItemLongClicked);
                    mContext.startActivity(intent);
                    mode.finish();
                    return true;

                case DELETE:
                    deleteItem(mClickedItem);
                    mode.finish();
                    return true;
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    //This method deletes the item onLongClick
    private void deleteItem(final JournalEntry item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.journalDao().deleteEntry(item);
                    }
                });
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new JournalViewHolder that holds the view for each entry
     */
    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item_list to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_list, parent, false);

        return new JournalViewHolder(view);
    }

    //A method to display data at a specified position in the Cursor in the RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull JournalAdapter.JournalViewHolder holder, int position) {
        // Determine the values of the wanted data
        JournalEntry journalEntry = mJournalEntries.get(position);
        String journalString = journalEntry.getEntry();
        String truncatedString = journalString.substring(0,200);
        String entryDate = dateFormat.format(journalEntry.getCreatedAt());

        //Set values
        holder.createdAtView.setText(entryDate);
        holder.journalEntryView.setText(truncatedString);
        holder.update(mJournalEntries.get(position));
    }

    @Override
    public int getItemCount() {
        if (mJournalEntries == null) {
            return 0;
        }
        return mJournalEntries.size();
    }

    public List<JournalEntry> getJournalEntries() {
        return mJournalEntries;
    }

    /**
     * The method updates the list of journalEntries
     * and notifies the adapter to use the new values on it when data changes
     */
    public void setJournalEntries(List<JournalEntry> journalEntries) {
        mJournalEntries = journalEntries;
        notifyDataSetChanged();
    }

    // Inner class for creating ViewHolders
    class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ConstraintLayout layout;
        //TextView textView;

        // Class variables for the TextViews
        TextView journalEntryView,createdAtView;
        // OnLongClickListener
        ItemLongClickListener itemLongClickListener;

        public JournalViewHolder(View itemView) {
            super(itemView);
            layout = (ConstraintLayout)itemView.findViewById(R.id.constLayout);
            createdAtView = itemView.findViewById(R.id.tvJournalDate);
            journalEntryView = itemView.findViewById(R.id.tvJournalEntry);
            journalEntryView.setEllipsize(TextUtils.TruncateAt.END);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //This method changes the background of an item onLongClick
        void selectItem(Integer item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    layout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    layout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        @Override
        public void onClick(View v) {
            int elementId = mJournalEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }


        @Override
        public boolean onLongClick(View v) {
            this.itemLongClickListener.onItemLongClick(v,getLayoutPosition());
            return false;
        }

        // This method starts the ActionMenu and takes the item which was longClicked
        public void update(final JournalEntry entry){
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                    mClickedItem = entry;
                    mItemLongClicked = entry.getId();

                    return true;
                }
            });
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);

    }

    public interface ItemLongClickListener {
        void onItemLongClick(View v, int itemId);

    }
}
