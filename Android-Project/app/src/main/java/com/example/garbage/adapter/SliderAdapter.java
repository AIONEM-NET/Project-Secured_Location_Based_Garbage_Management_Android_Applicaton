package com.example.garbage.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.garbage.R;
import com.example.garbage.model.SliderImage;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderImage> slideritem;
    ViewPager2 viewPager2;

    public SliderAdapter(List<SliderImage> slideritem, ViewPager2 viewPager2) {
        this.slideritem = slideritem;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_images,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(slideritem.get(position));
        if(position == slideritem.size()-2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return slideritem.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView imageview;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(SliderImage slide){
            imageview.setImageResource(slide.getImage());

        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            slideritem.addAll(slideritem);
            notifyDataSetChanged();
        }
    };
}
