package com.yamilab.animalsounds;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Misha on 25.02.2018.
 */

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{

    private ArrayList<Animal> mDataSet;
    private final int screenWidth;
    private Context context;
    private GlideRequests glideRequests;

    private  TTSListener ttsListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public final ImageView imageView;


        public Context context;


        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);


        }


        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }
        public void setContext (Context context) {
            this.context=context;
        }

    }

    public AnimalAdapter( ArrayList<Animal> dataSet, int screenWidth, GlideRequests glideRequests) {

        this.screenWidth = screenWidth;
        mDataSet = dataSet;
        this.glideRequests= glideRequests;

    }

    @Override
    public void onViewRecycled (ViewHolder holder){


        glideRequests.clear(holder.getImageView());
        holder.getImageView().setImageBitmap(null);

        super.onViewRecycled(holder);
        // Toast toast = Toast.makeText(context,
        //          "очищен" + holder.getImageView(), Toast.LENGTH_SHORT);
        //  toast.show();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_item, parent, false);
        context = parent.getContext();
        if (ttsListener==null){
            ttsListener = (TTSListener)context;}

        return new ViewHolder(v);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        final Animal animal = mDataSet.get(position);

        holder.getTextView().setText(mDataSet.get(position).getName());

        //GlideApp
        //        .with(context)
        glideRequests
                .load(mDataSet.get(position).getImageSmall())
                .priority(Priority.LOW)
                //.load(internetUrl)
                //.skipMemoryCache(true)
                .override((int)screenWidth)
                .fitCenter()
                // .thumbnail()
                .error(R.mipmap.ic_launcher)
                .placeholder(new ColorDrawable(context.getResources().getColor( R.color.colorBackground)))
                //.placeholder(R.mipmap.placeholder)
                .transition(withCrossFade(1000))
                .into(holder.getImageView());

        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SoundPlay.playSP(context, animal.getSound());
            }
        });



        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsListener.speak(animal.getName(),animal.getSound());
            }
        });
    }
}
