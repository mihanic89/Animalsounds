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
import android.media.SoundPool;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public  class CustomLinkAdapter extends RecyclerView.Adapter<CustomLinkAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<LinkItem> mDataset;
    Context context;
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;
        private Context context;


        public ViewHolder(View v)
        {
            super(v);


            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    String link = mDataset.get(getAdapterPosition()).getLink();

                    startLink(link);

                }
            });



            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.imageView);
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
    public CustomLinkAdapter(ArrayList<LinkItem> dataSet) {
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
                .inflate(R.layout.link_item, viewGroup, false);
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
        LinkItem data= new LinkItem();
        data=mDataset.get(position);
        viewHolder.setContext(context);
        viewHolder.getTextView().setText(data.getName());
        viewHolder.getImageView().setImageResource(data.getImage());

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void logLink (String link){

    }

    public void startLink (String link){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));

        context.startActivity(i);
    };

}
