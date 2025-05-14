package com.example.recipefinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipefinder.data.GroceryItem;

import java.util.ArrayList;
import java.util.List;

public class GroceryItemAdapter extends RecyclerView.Adapter<GroceryItemAdapter.GroceryViewHolder> {
    private Context context;
    private List<GroceryItem> groceryItems = new ArrayList<>();
    private OnCheckChangedListener checkChangedListener;
    private OnDeleteClickListener deleteListener;

    public GroceryItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_item, parent, false);
        return new GroceryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        try {
            GroceryItem currentItem = groceryItems.get(position);
            if (currentItem == null) {
                // Handle null item
                holder.textViewItemName.setText("Unknown Item");
                holder.textViewQuantity.setText("");
                return;
            }
            
            // Set item name with null check
            String itemName = currentItem.getName();
            holder.textViewItemName.setText(itemName != null ? itemName : "");
            
            // Set quantity and unit with null check
            double quantity = currentItem.getQuantity();
            String unit = currentItem.getUnit();
            unit = unit != null ? unit : "";
            
            holder.textViewQuantity.setText(String.format("%.1f %s", quantity, unit).replaceAll("\\.0 ", " "));
            
            // Set checked state without triggering listener
            holder.checkBoxCompleted.setOnCheckedChangeListener(null);
            holder.checkBoxCompleted.setChecked(currentItem.isChecked());
            
            // Re-attach listener
            holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && checkChangedListener != null) {
                    checkChangedListener.onCheckChanged(groceryItems.get(adapterPosition), isChecked);
                }
            });
            
        } catch (Exception e) {
            // Log error and set default values
            Log.e("GroceryItemAdapter", "Error binding view holder: " + e.getMessage(), e);
            holder.textViewItemName.setText("Error");
            holder.textViewQuantity.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public void setGroceryItems(List<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
        notifyDataSetChanged();
    }

    public GroceryItem getItemAt(int position) {
        return groceryItems.get(position);
    }

    class GroceryViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewItemName;
        private TextView textViewQuantity;
        private CheckBox checkBoxCompleted;
        private ImageButton buttonDelete;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.text_view_item_name);
            textViewQuantity = itemView.findViewById(R.id.text_view_quantity);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (deleteListener != null && position != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(groceryItems.get(position));
                }
            });
        }
    }

    public interface OnCheckChangedListener {
        void onCheckChanged(GroceryItem item, boolean isChecked);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(GroceryItem item);
    }

    public void setOnCheckChangedListener(OnCheckChangedListener listener) {
        this.checkChangedListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }
} 