package com.yamilab.animalsounds;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

public class ImageGridFragment extends Fragment {

   // private ViewPreloadSizeProvider<Animal> preloadSizeProvider;
    //private static final int PRELOAD_AHEAD_ITEMS = 5;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    AnimalAdapter animalAdapter;
    GlideRequests glideRequests;

    public ImageGridFragment() {

    }

    public static  ImageGridFragment newInstance(ArrayList array, int screenWidth) {


        ImageGridFragment fragment = new ImageGridFragment();
        Bundle args = new Bundle();
        args.putSerializable("key", array);
        args.putInt("width", screenWidth);
        fragment.setArguments(args);

        return fragment;
    }


    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private Parcelable layoutManagerState;

    private int calculateSpanCount(boolean isLandscape) {
        // smallestScreenWidthDp — фиксированная характеристика устройства,
        // не меняется при повороте экрана
        int smallestWidthDp = getResources().getConfiguration().smallestScreenWidthDp;

        int baseSpan;
        if (smallestWidthDp >= 720) {
            baseSpan = 3;
        } else if (smallestWidthDp >= 600) {
            baseSpan = 2;
        } else {
            baseSpan = 1;
        }

        int spanCount = isLandscape ? baseSpan + 1 : baseSpan;

        int maxAllowed = isLandscape ? 4 : 3;
        return Math.min(spanCount, maxAllowed);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
            outState.putParcelable(KEY_SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        if (savedInstanceState != null) {
            layoutManagerState = savedInstanceState.getParcelable(KEY_SCROLL_POSITION);
        }

        glideRequests = GlideApp.with(rootView.getContext());

        boolean isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        int spanCount = calculateSpanCount(isLandscape);

        try {
            if (((MainActivity) getActivity()).getGrid()) {
                spanCount = 1;
            }
        }
        catch (Exception e){
            // Ignore
        }

        recyclerView = rootView.findViewById(R.id.recyclerView);

        staggeredGridLayoutManager = new SafeStaggeredGridLayoutManager(
                spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);


        if (getActivity()!=null){
        animalAdapter = new AnimalAdapter(BundleCompat.getSerializable(getArguments(), "key", ArrayList.class),
                getArguments().getInt("width") / (spanCount + 1)
                ,getActivity()
                //,GlideApp.with(this)
                ,  glideRequests
        );}
        else

            animalAdapter = new AnimalAdapter(BundleCompat.getSerializable(getArguments(), "key", ArrayList.class),
                    getArguments().getInt("width") / (spanCount + 1)
                    ,rootView.getContext()
                  //  ,GlideApp.with(this)
                  ,  glideRequests
            );

        recyclerView.setAdapter(animalAdapter);
        recyclerView.setItemViewCacheSize(spanCount * 5);
        recyclerView.setHasFixedSize(true);

        // Восстановление позиции скролла (после setAdapter)
        if (layoutManagerState != null && recyclerView.getLayoutManager() != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
            layoutManagerState = null;
        }

            /*
            preloadSizeProvider = new ViewPreloadSizeProvider<>();
            RecyclerViewPreloader<Animal> preloader =
                    new RecyclerViewPreloader<>(
                            GlideApp.with(this), animalAdapter, animalAdapter, PRELOAD_AHEAD_ITEMS);

            recyclerView.addOnScrollListener(preloader);
            */

        //recyclerView.getRecycledViewPool().setMaxRecycledViews(0, spanCount * 3);
        //recyclerView.setItemViewCacheSize(0);

        return rootView;
    }



    /*
    @Override
    public void onStart() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                "onStart", Toast.LENGTH_SHORT);
        toast.show();
        super.onStart();
    }

    @Override
    public void onPause() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                "onPause", Toast.LENGTH_SHORT);
        toast.show();
        super.onPause();
    }

    @Override
    public void onStop() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                "onStop", Toast.LENGTH_SHORT);
        toast.show();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                "onDestroy", Toast.LENGTH_SHORT);
        toast.show();
        animalAdapter=null;
        super.onDestroy();
    }
*/
}

