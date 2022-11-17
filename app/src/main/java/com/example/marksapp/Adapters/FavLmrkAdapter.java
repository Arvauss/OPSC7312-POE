package com.example.marksapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marksapp.LandmarksModel;
import com.example.marksapp.R;

import java.util.ArrayList;

// RecyclerView Adapter configuration (GeeksForGeeks, 2022) https://www.geeksforgeeks.org/cardview-using-recyclerview-in-android-with-example/
public class FavLmrkAdapter extends RecyclerView.Adapter<FavLmrkAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<LandmarksModel> lmrkList;

    public FavLmrkAdapter(Context context, ArrayList<LandmarksModel> lmrkList) {
        this.context = context;
        this.lmrkList = lmrkList;
    }

    @NonNull
    @Override
    public FavLmrkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favlistitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavLmrkAdapter.ViewHolder holder, int position) {
        LandmarksModel lm = lmrkList.get(position);

        if (lm.isHome())holder.lmrkName.setText("Home - " + lm.getLmName()); else
        if (lm.isWork())holder.lmrkName.setText("Work - " + lm.getLmName()); else
            holder.lmrkName.setText(lm.getLmName());
        holder.lmrkAddress.setText(lm.getLmAddress());
    }

    @Override
    public int getItemCount() {
        return lmrkList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView lmrkName;
        private final TextView lmrkAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lmrkName = itemView.findViewById(R.id.fav_Name);
            lmrkAddress = itemView.findViewById(R.id.fav_Address);
        }
    }
}
