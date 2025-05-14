package com.example.recipefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private Context context;
    private List<HomeActivity.Dish> dishes = new ArrayList<>();
    private OnItemClickListener listener;

    public DishAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dish_card, parent, false);
        return new DishViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        HomeActivity.Dish currentDish = dishes.get(position);
        
        // Set dish name
        holder.textViewDishName.setText(currentDish.getName());
        
        // Set dish description
        holder.textViewDishDescription.setText(currentDish.getDescription());
        
        // Set dish image
        holder.imageDish.setImageResource(currentDish.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public void setDishes(List<HomeActivity.Dish> dishes) {
        this.dishes = dishes;
        notifyDataSetChanged();
    }

    class DishViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageDish;
        private TextView textViewDishName;
        private TextView textViewDishDescription;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imageDish = itemView.findViewById(R.id.image_dish);
            textViewDishName = itemView.findViewById(R.id.text_dish_name);
            textViewDishDescription = itemView.findViewById(R.id.text_dish_description);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dishes.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(HomeActivity.Dish dish);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
} 