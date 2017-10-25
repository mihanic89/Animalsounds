/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.yamilab.animalsounds;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.SoundPool;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

import java.util.ArrayList;

import static android.R.attr.fragment;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public  class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<Animal> mDataset;
    Context context;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;
        private Context context;
        private SoundPool sp;

        public ViewHolder(View v)
        {
            super(v);


            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    playSp(getAdapterPosition());
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {

                public boolean onLongClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " longclicked.");
                    playSp(getAdapterPosition());
                    Intent intent = new Intent(context, FullImageActivity.class);
                    int image= mDataset.get(getAdapterPosition()).getImageSmall();
                    int sound= mDataset.get(getAdapterPosition()).getSound();
                    intent.putExtra("image", image);
                    intent.putExtra("sound", sound);
                    intent.putExtra("name", mDataset.get(getAdapterPosition()).getName() );
                    context.startActivity(intent);
                    return false;
                }
            });

            textView = v.findViewById(R.id.textView);
            imageView = v.findViewById(R.id.imageView);
        }


        private void playSp(int adapterPosition) {

            SoundPlay.playSP(context, mDataset.get(adapterPosition).getSound());

        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }
        public void setContext (Context context) {this.context=context;}
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(ArrayList<Animal> dataSet) {
        mDataset = new ArrayList<>();
        dataSet.size();


        for (int i = 0; i < dataSet.size(); i++) {

            mDataset.add(dataSet.get(i));
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.animal_item, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        Animal data= new Animal();
        data=mDataset.get(position);
        viewHolder.setContext(context);
        viewHolder.getTextView().setText(data.getName());

       // viewHolder.getImageView().setImageResource(data.getImageSmall());
      //  viewHolder.getImageView().setImageBitmap(
       //         decodeSampledBitmapFromResource(context.getResources(), data.getImageSmall()));

        RequestOptions myOptions = new RequestOptions()
                .fitCenter();


            Glide.with(context)

                    .load(data.getImageSmall())

                   // .placeholder(new ColorDrawable(Color.BLACK))
                    .apply( myOptions)
                    .into(viewHolder.getImageView());

        }

    public  Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);


        // Calculate inSampleSize
        options.inSampleSize = 3;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth) {
        // Raw height and width of image
       // final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 3;


        return inSampleSize;
    }


    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
