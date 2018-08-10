package com.witcher.refreshlayout;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestAdapter extends RecyclerView.Adapter{

    private Context context;

    public TestAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new TestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView tv = holder.itemView.findViewById(R.id.tv_content);
        final int click = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.i("onClick  "+click);
            }
        });
        tv.setText(""+click);
        if(position<2){
            holder.itemView.setBackgroundColor(Color.parseColor("#88000000"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#88000000"));
        }
    }

    @Override
    public int getItemCount() {
        return 40;
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder{

        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }
}
