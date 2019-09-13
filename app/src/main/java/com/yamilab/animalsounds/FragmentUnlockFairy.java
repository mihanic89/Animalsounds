package com.yamilab.animalsounds;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class FragmentUnlockFairy extends Fragment {


    public FragmentUnlockFairy(){

    }

    public static FragmentUnlockFairy newInstance (
            int unlockCounter){
        FragmentUnlockFairy fragment = new FragmentUnlockFairy();
        Bundle args = new Bundle();
        args.putInt("counter", unlockCounter);
        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.unlock_fairy, container, false);


        ImageButton startGame;
        TextView unlockTextUp,unlockTextCenter, unlockTextDown;
        ImageView unlockImage;

        startGame = rootView.findViewById(R.id.buttonStartGame);
        unlockTextUp = rootView.findViewById(R.id.textUnlockCounterUp);
        unlockTextCenter = rootView.findViewById(R.id.textUnlockCounterCenter);
        unlockTextDown = rootView.findViewById(R.id.textUnlockCounterDown);
        unlockImage = rootView.findViewById(R.id.unlockBackgroud);

        int need = 29 - getArguments().getInt("counter",50);
        if (need<0) need =0;
        unlockTextUp.setText(getString(R.string.unlock_text1));
        unlockTextCenter.setText(" " + need + " ");
        unlockTextDown.setText(getString(R.string.unlock_text2));


        startGame.setOnClickListener(v -> ((MainActivity) getActivity()).setGameTab());

        unlockTextUp.setOnClickListener(v -> ((MainActivity) getActivity()).setGameTab());

        unlockTextCenter.setOnClickListener(v -> ((MainActivity) getActivity()).setGameTab());

        unlockTextDown.setOnClickListener(v -> ((MainActivity) getActivity()).setGameTab());




        if (this!=null) {
            GlideApp.with(this)
                    .load( R.drawable.lock)
                    // .fitCenter()
                    .transition(withCrossFade(1000))
                    .priority(Priority.LOW)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(unlockImage);
        }
        else
        {
            GlideApp.with(unlockImage.getContext())
                    .load(R.drawable.lock)
                    // .fitCenter()
                    .transition(withCrossFade(1000))
                    .priority(Priority.LOW)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(unlockImage);
        }

        unlockImage.setOnClickListener(v -> ((MainActivity) getActivity()).setGameTab());



        return rootView;
    }




}
