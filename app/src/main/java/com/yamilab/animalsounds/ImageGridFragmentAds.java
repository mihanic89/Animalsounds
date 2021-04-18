package com.yamilab.animalsounds;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

import static com.yamilab.animalsounds.R.id.recyclerView;

/**
 * Created by Misha on 28.03.2017.
 */
public class ImageGridFragmentAds extends Fragment {


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
    protected CustomLinkAdapter mAdapter;
    protected TextView txt;
    protected RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<LinkItem> mDataset;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
   // private Button disableAds;
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
        View rootView;

        /*
        if (((MainActivity) getActivity()).ads_disable_button && !((MainActivity) getActivity()).ads_disabled) {
            rootView = inflater.inflate(R.layout.fragment_ads_with_button, container, false);
        }
        else
        {

         */
            rootView = inflater.inflate(R.layout.fragment_ads, container, false);
       // }
        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = rootView.findViewById(recyclerView);
        txt = rootView.findViewById(R.id.textViewPolicy);
       // disableAds = rootView.findViewById(buttonDisableAds);
        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize>=Configuration.SCREENLAYOUT_SIZE_LARGE) gridCount=3;
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(gridCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
       int screenWidth = size.x;


        mAdapter = new CustomLinkAdapter(mDataset, screenWidth /3, GlideApp.with(rootView.getContext()));
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)


        txt.setMovementMethod(LinkMovementMethod.getInstance());
        txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("http://www.yapapa.xyz/private-policy-animalsounds/"));
                try {
                    startActivity(browserIntent);
                }
                catch (Exception e)
                {
                    
                }

            }
        });

        /*
        if (((MainActivity) getActivity()).ads_disable_button && !((MainActivity) getActivity()).ads_disabled) {

            //disableAds.setVisibility(View.VISIBLE);
            //disableAds.setHeight(60dp);
            disableAds.setText("$ " + getString(R.string.buy_ads_disable)+" $");

            disableAds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MainActivity) getActivity()).ads_disabled){
                        allReadyDisabled();
                    }
                    else
                    {
                        //запустить покупку
                        ((MainActivity) getActivity()).launchBilling();
                       // ((MainActivity) getActivity()).ads_disabled=true;
                    }
                }
            });
        }
        */
        /*
        else
        {
            disableAds.setText("$ Скоро $");
            disableAds.setEnabled(false);
           // disableAds.setVisibility(View.INVISIBLE);
           // disableAds.setHeight(wrap_content);
          //  disableAds.setHeight(0);
        }
        */
        return rootView;
    }

    private void allReadyDisabled() {
    }


    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {

        mDataset = new ArrayList<>();

        mDataset.add(new LinkItem(
                "ads/ads01.gif",

                "https://gf896.app.goo.gl/jdF1"
        ));

        mDataset.add(new LinkItem(
                "ads/ads02.gif",

                "https://gf896.app.goo.gl/GZGa"));

        mDataset.add(new LinkItem(
                "ads/ads03.gif",

                "https://gf896.app.goo.gl/TYCN"));

        mDataset.add(new LinkItem(
                "ads/ads04.gif",

                "https://gf896.app.goo.gl/EawQ"));

        mDataset.add(new LinkItem(
                "ads/ads05.gif",

                "https://gf896.app.goo.gl/gs5e"));

        mDataset.add(new LinkItem(
                "ads/ads06.gif",

                "https://gf896.app.goo.gl/wMv3"));



    }
}
