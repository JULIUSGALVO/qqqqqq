package com.example.recipefinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipefinder.data.ScheduledMeal;

import java.util.ArrayList;
import java.util.List;

public class ScheduledMealAdapter extends RecyclerView.Adapter<ScheduledMealAdapter.MealViewHolder> {
    private Context context;
    private List<ScheduledMeal> meals = new ArrayList<>();
    private List<ScheduledMeal> selectedMeals = new ArrayList<>();
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;
    private OnDeleteClickListener deleteListener;

    public ScheduledMealAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scheduled_meal_item, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        try {
            ScheduledMeal currentMeal = meals.get(position);
            if (currentMeal == null) {
                // Handle null meal
                holder.textViewRecipeName.setText("Unknown Recipe");
                holder.textViewMealType.setText("Unknown Type");
                holder.textViewDate.setText("Unknown Date");
                return;
            }
            
            // Set recipe name with null check
            String recipeName = currentMeal.getRecipeName();
            holder.textViewRecipeName.setText(recipeName != null ? recipeName : "");
            
            // Set meal type with null check
            String mealType = currentMeal.getMealType();
            holder.textViewMealType.setText(mealType != null ? mealType : "");
            
            // Set date with null check
            String date = currentMeal.getDate();
            holder.textViewDate.setText(date != null ? date : "");
            
            // Set selected state
            boolean isSelected = selectedMeals.contains(currentMeal);
            holder.itemView.setBackgroundColor(isSelected ? 
                    ContextCompat.getColor(context, R.color.selected_item_background) : 
                    ContextCompat.getColor(context, android.R.color.transparent));
        } catch (Exception e) {
            // Log error and set default values
            Log.e("ScheduledMealAdapter", "Error binding view holder: " + e.getMessage(), e);
            holder.textViewRecipeName.setText("Error");
            holder.textViewMealType.setText("");
            holder.textViewDate.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void setMeals(List<ScheduledMeal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }
    
    public void setSelectedMeals(List<ScheduledMeal> selectedMeals) {
        this.selectedMeals = new ArrayList<>(selectedMeals);
        notifyDataSetChanged();
    }

    public ScheduledMeal getMealAt(int position) {
        return meals.get(position);
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewRecipeName;
        private TextView textViewMealType;
        private TextView textViewDate;
        private ImageButton buttonDelete;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRecipeName = itemView.findViewById(R.id.text_view_recipe_name);
            textViewMealType = itemView.findViewById(R.id.text_view_meal_type);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(meals.get(position));
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    return longClickListener.onItemLongClick(meals.get(position));
                }
                return false;
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (deleteListener != null && position != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(meals.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ScheduledMeal meal);
    }
    
    public interface OnItemLongClickListener {
        boolean onItemLongClick(ScheduledMeal meal);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ScheduledMeal meal);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }
} 