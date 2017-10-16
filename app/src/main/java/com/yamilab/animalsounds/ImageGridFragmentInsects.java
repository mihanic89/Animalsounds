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
public class ImageGridFragmentInsects extends Fragment {


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
        mRecyclerView = (RecyclerView) rootView.findViewById(recyclerView);

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
        Animal data = new Animal();

        mDataset.add(new Animal(getString(R.string.bees),R.mipmap.i0hd,R.raw.i0));
        mDataset.add(new Animal(getString(R.string.flies),R.mipmap.i1hd,R.raw.i1));
        mDataset.add(new Animal(getString(R.string.mosquito),R.mipmap.i2hd,R.raw.i2));
        mDataset.add(new Animal(getString(R.string.grasshopper),R.mipmap.i3hd,R.raw.i3));
        mDataset.add(new Animal(getString(R.string.bumblebee),R.mipmap.i4hd,R.raw.i4));
        mDataset.add(new Animal(getString(R.string.cricket),R.mipmap.i5hd,R.raw.i5));
        mDataset.add(new Animal(getString(R.string.butterfly),R.mipmap.i6hd,R.raw.i6));
        mDataset.add(new Animal(getString(R.string.dragonfly),R.mipmap.i7hd,R.raw.i7));
        mDataset.add(new Animal(getString(R.string.ants),R.mipmap.i8hd,R.raw.i8));
        mDataset.add(new Animal(getString(R.string.mantis),R.mipmap.i9hd,R.raw.i9));
        mDataset.add(new Animal(getString(R.string.cicada), R.mipmap.i10hd,R.raw.i10));

    }
}
