package com.uwika.traveljournal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class LastJournalAdapter extends RecyclerView.Adapter<LastJournalAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LastJournalModel> modelLastJournal;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtV_title;
        TextView txtV_date;
        TextView txtV_month_year;
        ImageView imgV_cover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtV_title = itemView.findViewById(R.id.txtV_grid_journal_item_title);
            txtV_date = itemView.findViewById(R.id.txtV_grid_journal_item_date);
            txtV_month_year = itemView.findViewById(R.id.txtV_grid_journal_item_month_year);
            imgV_cover = itemView.findViewById(R.id.imgV_grid_journal_item_journal);
        }
    }

    LastJournalAdapter(Context context, ArrayList<LastJournalModel> data) {
        this.context = context;
        this.modelLastJournal = data;
    }

    @NonNull
    @Override
    public LastJournalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_journal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LastJournalAdapter.ViewHolder holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modelLastJournal.get(holder.getAdapterPosition()).getType().equals("click")){
                    Intent intent = new Intent(context, DetailJournalActivity.class);

                    Bundle mBundle = new Bundle();
                    mBundle.putString("uuid", modelLastJournal.get(holder.getAdapterPosition()).getUuid());
                    intent.putExtras(mBundle);
                    context.startActivity(intent);
                }else if(modelLastJournal.get(holder.getAdapterPosition()).getType().equals("show")){
                    Intent intent = new Intent(context, DetailPhotoActivity.class);

                    Bundle mBundle = new Bundle();
                    mBundle.putString("photo_url", modelLastJournal.get(holder.getAdapterPosition()).getReal_cover());
                    intent.putExtras(mBundle);
                    context.startActivity(intent);
                }
            }
        });

        TextView txtV_title = holder.txtV_title;
        TextView txtV_date = holder.txtV_date;
        TextView txtV_month_year = holder.txtV_month_year;
        ImageView imgV_cover = holder.imgV_cover;

        txtV_title.setText(modelLastJournal.get(position).getTitle());
        txtV_date.setText(modelLastJournal.get(position).getDate());
        txtV_month_year.setText(modelLastJournal.get(position).getMonth_year());
        if(modelLastJournal.get(position).getReal_cover() != ""){
            File imgFile = new File(modelLastJournal.get(position).getReal_cover());
            Glide.with(context).load(Uri.fromFile(imgFile)).into(imgV_cover);
        }else{
            imgV_cover.setImageResource(modelLastJournal.get(position).getCover());
        }
    }

    @Override
    public int getItemCount() {
        return modelLastJournal.size();
    }

}
