package com.example.recipefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class IngredientAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> ingredientList;

    public IngredientAdapter(Context context, ArrayList<String> ingredientList) {
        super(context, 0, ingredientList);
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_list_item, parent, false);
        }

        TextView ingredientText = convertView.findViewById(R.id.ingredientText);
        Button removeButton = convertView.findViewById(R.id.removeButton);

        String ingredient = getItem(position);
        ingredientText.setText(ingredient);

        // Remove ingredient when button is clicked
        removeButton.setOnClickListener(v -> {
            ingredientList.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
