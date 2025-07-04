package com.example.sdnews;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecylerViewAdapter extends RecyclerView.Adapter<RecylerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Model> Model;

    public RecylerViewAdapter(Context context, ArrayList<Model> model) {
        this.context = context;
        Model = model;
    }

    @NonNull
    @Override
    public RecylerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.row_recylerview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecylerViewAdapter.MyViewHolder holder, int position) {
        holder.txtTitle.setText(Model.get(position).getTitle());
        holder.txtDate.setText(Model.get(position).getDate());
        holder.txtContent.setText(Model.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return Model.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle;
        TextView txtDate;
        TextView txtContent;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle =itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtContent = itemView.findViewById(R.id.txtContent);
        }
    }
}
