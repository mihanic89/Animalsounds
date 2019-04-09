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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        // textView.setText(getStrng(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));





        glideRequests = GlideApp.with(this);
        //glideRequests = GlideApp.with(rootView.getContext());
        //glideRequests = GlideApp.with((ImageGridFragment)this);
        //GlideApp.get(rootView.getContext()).setMemoryCategory(MemoryCategory.LOW);
        //glideRequests=GlideApp.with(getActivity().getApplicationContext());



        int spanCount = 1;

        try {
            if (((MainActivity) getActivity()).getGrid()) {
                spanCount = 1;
            }
        }
        catch (Exception e){

        }


        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE) spanCount ++;

        recyclerView = rootView.findViewById(R.id.recyclerView);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        if (getActivity()!=null){
        animalAdapter = new AnimalAdapter((ArrayList<Animal>) getArguments().getSerializable("key"),
                getArguments().getInt("width") / (spanCount + 1)
                ,getActivity()
                //,GlideApp.with(this)
                ,  glideRequests
        );}
        else

            animalAdapter = new AnimalAdapter((ArrayList<Animal>) getArguments().getSerializable("key"),
                    getArguments().getInt("width") / (spanCount + 1)
                    ,rootView.getContext()
                  //  ,GlideApp.with(this)
                  ,  glideRequests
            );

        recyclerView.setAdapter(animalAdapter);


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

