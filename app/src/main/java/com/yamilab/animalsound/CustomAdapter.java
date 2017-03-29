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

package com.yamilab.animalsound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<Animal> mDataset;
    Context context;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public  class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;
        private Context context;


        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    playSp(getAdapterPosition());
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }

        private void playSp(int adapterPosition) {
            SoundPool sp;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                AudioAttributes audioAttrib = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                sp = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(1).build();
            }
            else {

                sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            }

            int soundId = sp.load(context, mDataset.get(adapterPosition).getSound(), 1);
            sp.play(soundId, 1, 1, 0, 0, 1);
            Log.d(TAG, "Element " + getAdapterPosition() + soundId+" played.");

            sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
            {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                    soundPool.play(sampleId,1,1,0,0,1);
                }
            });

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
        viewHolder.getImageView().setImageResource(data.getImageSmall());
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
