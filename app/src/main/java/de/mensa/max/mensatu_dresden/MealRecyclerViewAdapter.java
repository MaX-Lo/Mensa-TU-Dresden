package de.mensa.max.mensatu_dresden;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * created by MaX-Lo on 17.06.2018
 */

public class MealRecyclerViewAdapter extends RecyclerView.Adapter<MealRecyclerViewAdapter.ViewHolder> {
    private List<Meal> meals;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDescription;
        public TextView tvPrice;
        public TextView tvCategory;

        ViewHolder(View itemView) {
            super(itemView);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvCategory = (TextView) itemView.findViewById(R.id.tvCategory);
        }
    }

    MealRecyclerViewAdapter(List<Meal> meals) {
        this.meals = meals;
    }

    /**
     * Create a new view holder containing later a meal item
     */
    @NonNull
    @Override
    public MealRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        LinearLayout mealView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_item_view, parent, false);
        return new ViewHolder(mealView);
    }

    /**
     * fill the view holder with the appropriate item data at that position
     * @param holder view holder
     * @param position item position in recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvDescription.setText(meals.get(position).getDescription());
        holder.tvPrice.setText(meals.get(position).getStudentPrice());
        holder.tvCategory.setText(meals.get(position).getCategory());
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

}
