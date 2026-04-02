package com.sd.sdnews;

import android.content.Context;
import android.content.Intent;
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

    private final Context context;  // just store it directly, like your original
    private final ArrayList<Model> models;

    public RecylerViewAdapter(Context context, ArrayList<Model> models) {
        this.context = context;  // this is already the right themed context from the Fragment
        this.models = models;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use parent.getContext() — this always has the correct theme attached
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recylerview, parent, false);
        return new MyViewHolder(view);
    }

    // rest of the adapter stays exactly as your original

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model currentItem = models.get(position);
        holder.txtTitle.setText(currentItem.getTitle());
        holder.txtPublisher.setText(currentItem.getPublisher());
        holder.txtDate.setText(currentItem.getDate());
        holder.txtContent.setText(currentItem.getContent());

        // Pass the itemView's context here — this is the Activity context,
        // but we only use it transiently inside the click handler, not stored
        holder.itemView.setOnClickListener(v ->
                showBottomSheet(holder.itemView.getContext(), currentItem));
    }

    private void showBottomSheet(Context activityContext, Model item) {
        // BottomSheetDialog needs an Activity context to attach to a window —
        // this is fine because we're using it immediately and not storing it
        BottomSheetDialog dialog = new BottomSheetDialog(activityContext);
        View sheetView = LayoutInflater.from(activityContext)
                .inflate(R.layout.bottom_sheet_article, null);

        TextView sheetSource      = sheetView.findViewById(R.id.sheetSource);
        TextView sheetDate        = sheetView.findViewById(R.id.sheetDate);
        TextView sheetTitle       = sheetView.findViewById(R.id.sheetTitle);
        TextView sheetDescription = sheetView.findViewById(R.id.sheetDescription);
        Button   btnReadFull      = sheetView.findViewById(R.id.btnReadFull);
        Button   btnShare         = sheetView.findViewById(R.id.btnShare);

        sheetSource.setText(item.getPublisher() != null
                ? item.getPublisher().toUpperCase() : "");
        sheetDate.setText(item.getDate() != null ? item.getDate() : "");
        sheetTitle.setText(item.getTitle() != null ? item.getTitle() : "");

        String desc = item.getContent();
        sheetDescription.setText((desc != null && !desc.isEmpty())
                ? desc
                : "No summary available. Tap below to read the full article.");

        btnReadFull.setOnClickListener(v -> {
            String url = item.getLink();
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(activityContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.EXTRA_URL,    url);
                intent.putExtra(WebViewActivity.EXTRA_TITLE,  item.getTitle());
                intent.putExtra(WebViewActivity.EXTRA_SOURCE, item.getPublisher());
                intent.putExtra(WebViewActivity.EXTRA_DATE,   item.getDate());
                activityContext.startActivity(intent);
            }
            dialog.dismiss();
        });

        btnShare.setOnClickListener(v -> {
            String shareText = buildShareText(item);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            activityContext.startActivity(Intent.createChooser(shareIntent, "Share via..."));
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private String buildShareText(Model item) {
        StringBuilder sb = new StringBuilder();
        if (item.getPublisher() != null && !item.getPublisher().isEmpty())
            sb.append("📰 ").append(item.getPublisher()).append("\n\n");
        if (item.getTitle() != null && !item.getTitle().isEmpty())
            sb.append(item.getTitle()).append("\n\n");
        if (item.getLink() != null && !item.getLink().isEmpty())
            sb.append("Read more: ").append(item.getLink()).append("\n\n");
        sb.append("Shared via SD News\n");
        sb.append("https://play.google.com/store/apps/details?id=com.sd.sdnews");
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return models != null ? models.size() : 0;
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