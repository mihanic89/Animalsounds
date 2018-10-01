package com.yamilab.animalsounds;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
    //private Context context;
    private final GlideRequests glideRequests;
    private final StorageReference mStorageRef= FirebaseStorage.getInstance().getReferenceFromUrl("gs://animalsounds-a4395.appspot.com/");

    private  TTSListener ttsListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public final ImageView imageView;


        //public Context context;


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
        //public void setContext (Context context) {
        //    this.context=context;
        //}

    }

    public AnimalAdapter( ArrayList<Animal> dataSet, int screenWidth, GlideRequests glideRequests) {

        this.screenWidth = screenWidth;
        mDataSet = dataSet;

        this.glideRequests= glideRequests;

    }



    @Override
    public void onViewRecycled (ViewHolder holder){

        holder.getImageView().setImageBitmap(null);
        glideRequests.clear(holder.getImageView());
        holder.getImageView().setOnClickListener(null);
        holder.getTextView().setOnClickListener(null);
        super.onViewRecycled(holder);
       //  Toast toast = Toast.makeText(context,
        //          "очищен" + holder.getImageView(), Toast.LENGTH_SHORT);
       //   toast.show();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_item, parent, false);
       //context = parent.getContext();
        if (ttsListener==null){
          ttsListener = (TTSListener)v.getContext();}

        return new ViewHolder(v);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Animal animal = mDataSet.get(position);
        holder.getTextView().setText(mDataSet.get(position).getName());

        //GlideApp
        //        .with(context)

       // glideRequests.clear(holder.getImageView());
        //GlideApp.get(holder.itemView.getContext()).setMemoryCategory(MemoryCategory.LOW);


        if (animal.isGIF() & Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            glideRequests
                    .load(mStorageRef.child(animal.getGifHref()))
                    //.priority(Priority.LOW)

                    //.load(internetUrl)
                    // .skipMemoryCache(true)
                   // .diskCacheStrategy(DiskCacheStrategy.ALL)
                   // .override((int) screenWidth)
                    .fitCenter()
                    // .thumbnail()
                    //.error(animal.getImageSmall())
                    // .placeholder(new ColorDrawable(context.getResources().getColor(R.color.colorBackground)))
                    .placeholder(animal.getImageSmall())
                    //.placeholder(new ColorDrawable(context.getResources().getColor(R.color.colorBackground)))
                    //.placeholder(R.mipmap.placeholder)

                    //.transition(withCrossFade(1000))
                    .into(holder.getImageView());
        }


            else {
                glideRequests
                        .load(mDataSet.get(position).getImageSmall())
                       // .priority(Priority.LOW)
                        //.load(internetUrl)

                         //.skipMemoryCache(true)
                        //.diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.override((int) screenWidth)
                        .fitCenter()
                        // .thumbnail()
                        //.error(R.mipmap.ic_launcher)
                        .placeholder(new ColorDrawable(holder.itemView.getContext().getResources().getColor(R.color.colorBackground)))
                        //.placeholder(R.mipmap.placeholder)
                        .transition(withCrossFade(1000))
                        .into(holder.getImageView());
            }






        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SoundPlay.playSP(holder.itemView.getContext(), animal.getSound());
            }
        });



        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsListener.speak(animal.getName(),animal.getSound());
            }
        });


        /*
        holder.getImageView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               // startAnotherActivity(position);
                return true;
            }
        });
        */
    }

    public void startAnotherActivity (int counter){
     //   Intent intent = new Intent(context, TabbedActivity.class);
     //   Bundle args = new Bundle();
     //   args.putSerializable("key",mDataSet);
     //   intent.putExtra("BUNDLE",args);

       // context.startActivity(intent);

      //  ((MainActivity) context).startActivityForResult(intent,1);
    }
}
