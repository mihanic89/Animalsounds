package com.yamilab.animalsounds;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Misha on 25.02.2018.
 */

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{

    private ArrayList<Animal> mDataSet;
    private final int screenWidth;
    private Context context;
    //private Fragment fr;
    private GlideRequests glideRequests=null;
    private final StorageReference
            mStorageRef= FirebaseStorage.getInstance().getReferenceFromUrl("gs://animalsounds-a4395.appspot.com/");

    private TTSListener ttsListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;
        private final ImageButton wikiButton;


        //public Context context;


        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            wikiButton = itemView.findViewById(R.id.buttonWiki);

            if (context instanceof MainActivity){
                try
                {
                    if (!((MainActivity)context).showWiki()) wikiButton.setVisibility(View.INVISIBLE);
                }
                catch (Exception e){

                }
            }


        }


        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public ImageButton getImageButton() {
            return wikiButton;
        }
        //public void setContext (Context context) {
        //    this.context=context;
        //}

    }

    public AnimalAdapter( ArrayList<Animal> dataSet, int screenWidth, GlideRequests glideRequests) {

        this.screenWidth = screenWidth;
        mDataSet = dataSet;

        //this.glideRequests= glideRequests;
        //glideRequests.

    }

    public AnimalAdapter( ArrayList<Animal> dataSet, int screenWidth) {

        this.screenWidth = screenWidth;
        mDataSet = dataSet;

       // glideRequests= null;

    }

    public AnimalAdapter( ArrayList<Animal> dataSet, int screenWidth, Context context) {

        this.screenWidth = screenWidth;
        mDataSet = dataSet;
        this.context = context;
        if (ttsListener==null){
            ttsListener = (TTSListener)this.context;}
        // glideRequests= null;

    }

    public AnimalAdapter(ArrayList<Animal> dataSet, int screenWidth, Context context, GlideRequests glide) {

        this.screenWidth = screenWidth;
        mDataSet = dataSet;
        this.context = context;
        if (ttsListener==null){
            ttsListener = (TTSListener)this.context;}

       // if (fr==null) {fr=fragment;};
        glideRequests= glide;
       // GlideApp.get(context).setMemoryCategory(MemoryCategory.LOW);

    }


    @Override
    public void onViewRecycled (ViewHolder holder){

        //holder.getImageView().setImageBitmap(null);

        holder.getTextView().setText(null);
        holder.getImageView().setImageDrawable(null);
        GlideApp.with(holder.getImageView().getContext()).clear(holder.getImageView());
        holder.getImageView().setOnClickListener(null);
        holder.getTextView().setOnClickListener(null);
        holder.getImageButton().setOnClickListener(null);
        //Toast toast = Toast.makeText(holder.getImageView().getContext(),
        //          "очищен" + holder.getImageView(), Toast.LENGTH_SHORT);
        //    toast.show();

        super.onViewRecycled(holder);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_item, parent, false);
       //context = parent.getContext();


        return new ViewHolder(v);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {



        final Animal animal = mDataSet.get(position);
        holder.getTextView().setText(mDataSet.get(position).getName());

        //GlideApp
        //        .with(context)

       // glideRequests.clear(holder.getImageView());
        //GlideApp.get(holder.itemView.getContext()).setMemoryCategory(MemoryCategory.LOW);
        //GlideApp.ц



            if (animal.isGIF() & Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

                try {
                    //GlideApp.with(context)
                    glideRequests

                            .load(mStorageRef.child(animal.getGifHref()))
                            .priority(Priority.LOW)

                            //.load(internetUrl)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            //.override((int) screenWidth)
                            .fitCenter()
                            .override((int)screenWidth/2, Target.SIZE_ORIGINAL)
                            .thumbnail( glideRequests.load(animal.getImageSmall()))
                            //.error(animal.getImageSmall())
                            //.placeholder(new ColorDrawable(context.getResources().getColor(R.color.colorBackground))
                            // .placeholder(animal.getImageSmall())
                            //.placeholder(new ColorDrawable(context.getResources().getColor(R.color.colorBackground)))
                            //.placeholder(R.mipmap.placeholder)

                            //.transition(withCrossFade(100))
                            .into(holder.getImageView())
                            //.clearOnDetach()
                             ;

                } catch (Exception e) {
                    holder.getImageView().setImageDrawable(holder.getImageView().
                            getContext().
                            getResources().
                            getDrawable(mDataSet.get(position).getImageSmall()));
                }
            } else {

                /*
              holder.getImageView().setImageDrawable(holder.getImageView().
                      getContext().
                      getResources().
                      getDrawable(mDataSet.get(position).getImageSmall()));
              */

                try {
                    // GlideApp.with(context)
                    glideRequests
                            .load(mDataSet.get(position).getImageSmall())
                            .priority(Priority.LOW)
                            //.load(internetUrl)

                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .override(screenWidth, Target.SIZE_ORIGINAL)
                            .fitCenter()
                            // .thumbnail()
                            //.error(R.mipmap.ic_launcher)
                            //.placeholder(new ColorDrawable(holder.itemView.getContext().getResources().getColor(R.color.colorBackground)))
                            //.placeholder(R.mipmap.placeholder)
                            //.transition(withCrossFade(1000))
                            .into(holder.getImageView());

                } catch (Exception e) {
                    holder.getImageView().setImageDrawable(holder.getImageView().
                            getContext().
                            getResources().
                            getDrawable(mDataSet.get(position).getImageSmall()));
                }

            }








        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              try {
                  SoundPlay.playSP(context, animal.getSound());
              }
              catch (Exception e){

              }
            }
        });



        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsListener.speak(animal.getName(),animal.getSound());
            }
        });

        holder.getImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStringWiki(animal.getWikiName());
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

    public void sendStringWiki (String url){
        ((MainActivity) context).startWiki(url);
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
