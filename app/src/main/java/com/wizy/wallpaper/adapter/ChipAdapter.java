package com.wizy.wallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wizy.wallpaper.R;
import com.wizy.wallpaper.Search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public abstract class ChipAdapter  extends RecyclerView.Adapter<ChipAdapter.MyChipAdapter>{
    private final List<String> chips;
    LinkedHashMap<String, Integer> chipIcons = new LinkedHashMap<>();
    Context mContext;
    protected ChipAdapter(Context context, List<String> chips, LinkedHashMap<String, Integer> chipIcons){
        mContext=context;
        this.chips=chips;
        this.chipIcons=chipIcons;

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
        myChipAdapter.chipName.setText(chips.get(position));

        int h =  new ArrayList<Integer>(chipIcons.values()).get(position);
        String m =  new ArrayList<String>(chipIcons.keySet()).get(position);

      //  myChipAdapter.button.setBackgroundResource(m);

        myChipAdapter.chipIcon.setImageResource(h);

        Drawable sampleDrawable = mContext.getResources().getDrawable(R.drawable.my_layer_list);
        sampleDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(m), PorterDuff.Mode.SRC_ATOP));
        myChipAdapter.button.setImageDrawable(sampleDrawable);


    }

    @Override
    public int getItemCount() {
        return chips.size();
    }

    class MyChipAdapter extends RecyclerView.ViewHolder{
        ImageButton button;
        TextView chipName;
        ImageView chipIcon;
        MyChipAdapter(@NonNull View itemView) {
            super(itemView);

                button = itemView.findViewById(R.id.button);
                chipName= itemView.findViewById(R.id.chipName);
                 chipIcon = itemView.findViewById(R.id.chipIcon);
                button.setOnClickListener(v -> {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        if (chips.get(pos).contains("Search")) {
                            Toasty.info(itemView.getContext(),"OK", Toast.LENGTH_LONG,true).show();
                            Intent intent = new Intent(itemView.getContext(), Search.class);
                            itemView.getContext().startActivity(intent);
                        }
                        else {
                            clickListener(chips.get(pos));

                        }

                        }

                });

        }
    }

    protected abstract void clickListener(String chip);

}
