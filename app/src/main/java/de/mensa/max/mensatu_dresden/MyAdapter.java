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

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Meal> meals;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDescription;
        public TextView tvPrice;

        ViewHolder(View itemView) {
            super(itemView);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }

    MyAdapter(List<Meal> meals) {
        this.meals = meals;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_item_view, parent, false);

        return new ViewHolder(v);
    }

    /**
     * fill the view holder with the appropriate item data at that position
     * @param holder view holder
     * @param position item position in recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvDescription.setText(meals.get(position).getName());
        holder.tvPrice.setText(meals.get(position).getStudentPrice());

    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

}
