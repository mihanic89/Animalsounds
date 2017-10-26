package com.yamilab.animalsounds;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static com.yamilab.animalsounds.R.id.recyclerView;

/**
 * Created by Misha on 28.03.2017.
 */
public class ImageGridFragmentHome extends Fragment {


    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 40;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    protected LayoutManagerType mCurrentLayoutManagerType;
    private int gridCount = 2;
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Animal> mDataset;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = rootView.findViewById(recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize>=Configuration.SCREENLAYOUT_SIZE_LARGE) gridCount=3;
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(gridCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);


        mAdapter = new CustomAdapter(mDataset);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)



        return rootView;
    }


    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mDataset = new ArrayList<>();
       // Animal data = new Animal();

        mDataset.add(new Animal(getString(R.string.dog),R.mipmap.h0hd,R.raw.h0));
        mDataset.add(new Animal(getString(R.string.cat),R.mipmap.h1hd,R.raw.h1));
        mDataset.add(new Animal(getString(R.string.pig),R.mipmap.h2hd,R.raw.h2));
        mDataset.add(new Animal(getString(R.string.cock),R.mipmap.h3hd,R.raw.h3));
        mDataset.add(new Animal(getString(R.string.chiken),R.mipmap.h4hd,R.raw.h4));
        mDataset.add(new Animal(getString(R.string.cow),R.mipmap.h5hd,R.raw.h5));
        mDataset.add(new Animal(getString(R.string.horse),R.mipmap.h6hd,R.raw.h6));
        mDataset.add(new Animal(getString(R.string.sheep),R.mipmap.h7hd,R.raw.h7));
        mDataset.add(new Animal(getString(R.string.goat),R.mipmap.h8hd,R.raw.h8));
        mDataset.add(new Animal(getString(R.string.donkey),R.mipmap.h9hd,R.raw.h9));
        mDataset.add(new Animal(getString(R.string.turkey),R.mipmap.h10hd,R.raw.h10));
        mDataset.add(new Animal(getString(R.string.cavy),R.mipmap.h11hd,R.raw.h11));
        mDataset.add(new Animal(getString(R.string.rabbit),R.mipmap.h12rabbit,R.raw.h12));
    }
}
