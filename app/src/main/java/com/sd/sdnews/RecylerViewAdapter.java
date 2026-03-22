package com.sd.sdnews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_recylerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model currentItem = Model.get(position);

        holder.txtTitle.setText(currentItem.getTitle());
        holder.txtPublisher.setText(currentItem.getPublisher());
        holder.txtDate.setText(currentItem.getDate());
        holder.txtContent.setText(currentItem.getContent());

        // Tap card → open Bottom Sheet
        holder.itemView.setOnClickListener(v -> showBottomSheet(currentItem));
    }

    private void showBottomSheet(Model item) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View sheetView = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_article, null);

        // Wire up views
        TextView sheetSource      = sheetView.findViewById(R.id.sheetSource);
        TextView sheetDate        = sheetView.findViewById(R.id.sheetDate);
        TextView sheetTitle       = sheetView.findViewById(R.id.sheetTitle);
        TextView sheetDescription = sheetView.findViewById(R.id.sheetDescription);
        Button   btnReadFull      = sheetView.findViewById(R.id.btnReadFull);
        Button   btnShare         = sheetView.findViewById(R.id.btnShare);

        // Populate fields
        sheetSource.setText(item.getPublisher() != null
                ? item.getPublisher().toUpperCase() : "");
        sheetDate.setText(item.getDate() != null
                ? item.getDate() : "");
        sheetTitle.setText(item.getTitle() != null
                ? item.getTitle() : "");

        String desc = item.getContent();
        sheetDescription.setText((desc != null && !desc.isEmpty())
                ? desc
                : "No summary available. Tap below to read the full article.");

        // Read Full Article → WebViewActivity (Reader Mode)
        btnReadFull.setOnClickListener(v -> {
            String url = item.getLink();
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL,    url);
                intent.putExtra(WebViewActivity.EXTRA_TITLE,  item.getTitle());
                intent.putExtra(WebViewActivity.EXTRA_SOURCE, item.getPublisher());
                intent.putExtra(WebViewActivity.EXTRA_DATE,   item.getDate());
                context.startActivity(intent);
            }
            dialog.dismiss();
        });

        // Share → branded share sheet
        btnShare.setOnClickListener(v -> {
            String shareText = buildShareText(item);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            context.startActivity(Intent.createChooser(shareIntent, "Share via..."));
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private String buildShareText(Model item) {
        StringBuilder sb = new StringBuilder();

        if (item.getPublisher() != null && !item.getPublisher().isEmpty()) {
            sb.append("📰 ").append(item.getPublisher()).append("\n\n");
        }
        if (item.getTitle() != null && !item.getTitle().isEmpty()) {
            sb.append(item.getTitle()).append("\n\n");
        }
        if (item.getLink() != null && !item.getLink().isEmpty()) {
            sb.append("Read more: ").append(item.getLink()).append("\n\n");
        }
        sb.append("Shared via SD News\n");
        sb.append("Get it on Google Play: ");
        sb.append("https://play.google.com/store/apps/details?id=com.sd.sdnews");

        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return Model.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate, txtContent, txtPublisher;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle     = itemView.findViewById(R.id.txtTitle);
            txtPublisher = itemView.findViewById(R.id.txtPublisher);
            txtDate      = itemView.findViewById(R.id.txtDate);
            txtContent   = itemView.findViewById(R.id.txtContent);
        }
    }
}