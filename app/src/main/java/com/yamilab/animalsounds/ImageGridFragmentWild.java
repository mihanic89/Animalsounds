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
public class ImageGridFragmentWild extends Fragment {


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
        //nimal data = new Animal();

        mDataset.add(new Animal(getString(R.string.bear),R.mipmap.w0hd,R.raw.w0));
        mDataset.add(new Animal(getString(R.string.wolf),R.mipmap.w1hd,R.raw.w1));
        mDataset.add(new Animal(getString(R.string.leo),R.mipmap.w2hd,R.raw.w2));
        mDataset.add(new Animal(getString(R.string.tiger),R.mipmap.w3hd,R.raw.w3));
        mDataset.add(new Animal(getString(R.string.monkey),R.mipmap.w4hd,R.raw.w4));
        mDataset.add(new Animal(getString(R.string.elephant),R.mipmap.w5hd,R.raw.w5));
        mDataset.add(new Animal(getString(R.string.camel),R.mipmap.w6hd,R.raw.w6));
        mDataset.add(new Animal(getString(R.string.zebra),R.mipmap.w7hd,R.raw.w7));
        mDataset.add(new Animal(getString(R.string.jackal),R.mipmap.w8hd,R.raw.w8));
        mDataset.add(new Animal(getString(R.string.snake),R.mipmap.w9hd,R.raw.w9));
        mDataset.add(new Animal(getString(R.string.fox),R.mipmap.w10hd,R.raw.w10));
        mDataset.add(new Animal(getString(R.string.hare),R.mipmap.w11hd,R.raw.w11));
        mDataset.add(new Animal(getString(R.string.rhino),R.mipmap.w12hd,R.raw.w12));
        mDataset.add(new Animal(getString(R.string.crocodile),R.mipmap.w13hd,R.raw.w13));
        mDataset.add(new Animal(getString(R.string.koala),R.mipmap.w14hd,R.raw.w14));
        mDataset.add(new Animal(getString(R.string.panda),R.mipmap.w15hd,R.raw.w15));
        mDataset.add(new Animal(getString(R.string.kangoroo),R.mipmap.w16hd,R.raw.w16));
        mDataset.add(new Animal(getString(R.string.lemur),R.mipmap.w17hd,R.raw.w17));
        mDataset.add(new Animal(getString(R.string.lynx),R.mipmap.w18hd,R.raw.w18));
        mDataset.add(new Animal(getString(R.string.elk),R.mipmap.w19hd,R.raw.w19));
        mDataset.add(new Animal(getString(R.string.racoon),R.mipmap.w20hd,R.raw.w20));
        mDataset.add(new Animal(getString(R.string.squirrel),R.mipmap.w21hd,R.raw.w21));
        mDataset.add(new Animal(getString(R.string.rat),R.mipmap.w22hd,R.raw.w22));
        mDataset.add(new Animal(getString(R.string.mouse),R.mipmap.w23hd,R.raw.w23));
        mDataset.add(new Animal(getString(R.string.jaguar),R.mipmap.w24hd,R.raw.w24));
        mDataset.add(new Animal(getString(R.string.hippopotamus),R.mipmap.w25hd,R.raw.w25));
        mDataset.add(new Animal(getString(R.string.badger),R.mipmap.w26barsuk, R.raw.w26));
        mDataset.add(new Animal(getString(R.string.beaver),R.mipmap.w27beaver, R.raw.w27));
        mDataset.add(new Animal(getString(R.string.deer),R.mipmap.w28deer, R.raw.w28));
        mDataset.add(new Animal(getString(R.string.hedgehog),R.mipmap.w29hedgehog, R.raw.w29));
        mDataset.add(new Animal(getString(R.string.giraffe),R.mipmap.w30giraffe, R.raw.w30));
        mDataset.add(new Animal(getString(R.string.mole),R.mipmap.w31mole, R.raw.w31));
        mDataset.add(new Animal(getString(R.string.skunk),R.mipmap.w32skunk, R.raw.w32));
        mDataset.add(new Animal(getString(R.string.boar),R.mipmap.w33boar, R.raw.w33));
        mDataset.add(new Animal(getString(R.string.bison),R.mipmap.w34bison, R.raw.w34));
        mDataset.add(new Animal(getString(R.string.chipmunk),R.mipmap.w35chipmunk,R.raw.w35));
        mDataset.add(new Animal(getString(R.string.alpaca),R.mipmap.w36alpaca,R.raw.w36));
        mDataset.add(new Animal(getString(R.string.hyena),R.mipmap.w37hyena,R.raw.w37));

    }
}
