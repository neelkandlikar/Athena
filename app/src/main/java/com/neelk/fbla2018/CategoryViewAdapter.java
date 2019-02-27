package com.neelk.fbla2018;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.CategoryViewHolder> {

    ArrayList<Quiz.QuizCategory> categories;
    private Context context;

    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClick(int position);
    }


    public CategoryViewAdapter(Context context, ArrayList<Quiz.QuizCategory> categories) {
        this.categories = categories;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        CategoryViewHolder viewHolder = new CategoryViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, final int i) {
        categoryViewHolder.categoryTextView.setText(categories.get(i).message);
        categoryViewHolder.categoryIcon.setImageResource(categories.get(i).resId);
        categoryViewHolder.nameTextView.setText(categories.get(i).categoryName);
        categoryViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView categoryTextView;
        de.hdodenhof.circleimageview.CircleImageView categoryIcon;
        TextView nameTextView;
        View view;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.quiz_cardView);
            categoryIcon = itemView.findViewById(R.id.cardViewImage);
            categoryTextView = itemView.findViewById(R.id.cardViewText);
            nameTextView = itemView.findViewById(R.id.nameTextView);


        }


    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

}