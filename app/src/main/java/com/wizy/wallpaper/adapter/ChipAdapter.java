package com.wizy.wallpaper.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wizy.wallpaper.R;

import java.util.List;

public abstract class ChipAdapter  extends RecyclerView.Adapter<ChipAdapter.MyChipAdapter>{
    private final List<String> chips;

    protected ChipAdapter(List<String> chips){
        this.chips=chips;
     }

    @NonNull
    @Override
    public MyChipAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chip_adapter, viewGroup, false);
        return new MyChipAdapter(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyChipAdapter myChipAdapter, int position) {
        myChipAdapter.button.setText(chips.get(position));
    }

    @Override
    public int getItemCount() {
        return chips.size();
    }

    class MyChipAdapter extends RecyclerView.ViewHolder{
        private final Button button;
        MyChipAdapter(@NonNull View itemView) {
            super(itemView);
            button =itemView.findViewById(R.id.button);
            button.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    clickListener(chips.get(pos));
                }
            });
        }
    }

    protected abstract void clickListener(String chip);

}
