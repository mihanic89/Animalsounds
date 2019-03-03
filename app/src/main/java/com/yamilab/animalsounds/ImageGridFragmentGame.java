package com.yamilab.animalsounds;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.yamilab.animalsounds.R.id.buttonNext;
import static com.yamilab.animalsounds.R.id.buttonSound;
import static com.yamilab.animalsounds.R.id.imageFull;
import static com.yamilab.animalsounds.R.id.imageGame0;
import static com.yamilab.animalsounds.R.id.imageGame1;
import static com.yamilab.animalsounds.R.id.imageGame2;
import static com.yamilab.animalsounds.R.id.imageGame3;
import static com.yamilab.animalsounds.R.id.recyclerView;

/**
 * Created by Misha on 28.03.2017.
 */
public class ImageGridFragmentGame extends Fragment {


    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 40;
    private TTSListener ttsListener;

    public ImageGridFragmentGame (){

    }

    public static  ImageGridFragmentGame newInstance(
            ArrayList array,
            int screenWidth) {

        ImageGridFragmentGame fragmentGame = new ImageGridFragmentGame();
        Bundle args = new Bundle();
        args.putSerializable("key", array);
        args.putInt("width", screenWidth);
        fragmentGame.setArguments(args);
        return fragmentGame;
    }




    private ArrayList<LinkItem> mDataset;
    private ArrayList<Animal> animals;

    private int size=0, correctAnswer=0;
    private int wrong1=0, wrong2=0, wrong3=0;
    private int correctCard=0;

    private int[] cardsNumbers;

    ImageButton image0;
    ImageButton image1;
    ImageButton image2;
    ImageButton image3;
    ImageButton full;

    Button buttonAnswer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.

        if (ttsListener==null){
            ttsListener = (TTSListener)getContext();}
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_game2, container, false);

        animals = new ArrayList<Animal>();

        animals = (ArrayList<Animal>) getArguments().getSerializable("key");

        size = animals.size()-1;
        if (size<0) size=0;

        correctAnswer = new Random().nextInt(size);



        image0= (ImageButton) rootView.findViewById(imageGame0);
        image1= (ImageButton) rootView.findViewById(imageGame1);
        image2= (ImageButton) rootView.findViewById(imageGame2);
        image3= (ImageButton) rootView.findViewById(imageGame3);
        full = (ImageButton) rootView.findViewById(imageFull);

        //textAnswer = (TextView) rootView.findViewById(R.id.textAnswer);
        buttonAnswer = (Button) rootView.findViewById(R.id.buttonAnswer);

        ImageButton sound = (ImageButton) rootView.findViewById(buttonSound);
        ImageButton next = (ImageButton) rootView.findViewById(buttonNext);

        try
        {
            generateWrong();
            setImages();
           // newRound();
        }
        catch (Exception e){

        }

        // BEGIN_INCLUDE(initializeRecyclerView)
        /*
         mRecyclerView = rootView.findViewById(recyclerView);

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


        mAdapter = new CustomLinkAdapter(mDataset, (int) screenWidth/3, GlideApp.with(rootView.getContext()));
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)
        */

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.playSP(rootView.getContext(), animals.get(correctAnswer).getSound());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRound();
            }
        });

        image0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(0);

            }
        });

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(1);
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(2);
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(3);
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRound();
               // generateWrong();
               // setImages();
            }
        });

        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsListener.speak(animals.get(correctAnswer).getName(),animals.get(correctAnswer).getSound());
            }
        });

        return rootView;
    }




    private void generateWrong(){

        correctAnswer = new Random().nextInt(size);

        wrong1=new Random().nextInt(size);
        while (wrong1==correctAnswer){
            wrong1=new Random().nextInt(size);
        }
        wrong2=new Random().nextInt(size);
        while (wrong2==correctAnswer || wrong2==wrong1){
            wrong2=new Random().nextInt(size);
        }
        wrong3=new Random().nextInt(size);
        while (wrong3==correctAnswer || wrong3==wrong2 || wrong3==wrong1){
            wrong3=new Random().nextInt(size);
        }
    }

    private void setImages(){


        image0.setVisibility(View.VISIBLE);
        image1.setVisibility(View.VISIBLE);
        image2.setVisibility(View.VISIBLE);
        image3.setVisibility(View.VISIBLE);
        buttonAnswer.setVisibility(View.INVISIBLE);


        full.setVisibility(View.INVISIBLE);
        correctCard = new Random().nextInt(3);

        if (correctCard==0){
            /*
            image0.setImageResource(animals.get(correctAnswer).getImageSmall());
            image1.setImageResource(animals.get(wrong1).getImageSmall());
            image2.setImageResource(animals.get(wrong2).getImageSmall());
            image3.setImageResource(animals.get(wrong3).getImageSmall());
            */
            setImageGlide(image0,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image1,animals.get(wrong1).getImageSmall());
            setImageGlide(image2,animals.get(wrong2).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());
        }

        else if (correctCard==1){
            /*
            image1.setImageResource(animals.get(correctAnswer).getImageSmall());
            image0.setImageResource(animals.get(wrong1).getImageSmall());
            image2.setImageResource(animals.get(wrong2).getImageSmall());
            image3.setImageResource(animals.get(wrong3).getImageSmall());
            */
            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image2,animals.get(wrong2).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());

        }

        else if (correctCard==2){
            /*
            image0.setImageResource(animals.get(wrong1).getImageSmall());
            image1.setImageResource(animals.get(wrong2).getImageSmall());
            image2.setImageResource(animals.get(correctAnswer).getImageSmall());
            image3.setImageResource(animals.get(wrong3).getImageSmall());
             */
            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(wrong2).getImageSmall());
            setImageGlide(image2,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());
        }
        else {
            /*
            image0.setImageResource(animals.get(wrong1).getImageSmall());
            image1.setImageResource(animals.get(wrong2).getImageSmall());
            image2.setImageResource(animals.get(wrong3).getImageSmall());
            image3.setImageResource(animals.get(correctAnswer).getImageSmall());
             */
            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(wrong2).getImageSmall());
            setImageGlide(image2,animals.get(wrong3).getImageSmall());
            setImageGlide(image3,animals.get(correctAnswer).getImageSmall());
        }

       // SoundPlay.playSP(getContext(), animals.get(correctAnswer).getSound());

    }

    private void setImageGlide (ImageView imageView, int image){
        GlideApp.with(imageView.getContext())
                .load(image)
                .fitCenter()
                .into(imageView);
    }

    private void checkAnswer (int answer){



        if (correctCard==answer){




            SoundPlay.playSP(getContext(), R.raw.correct);
            //delay(500);
            setAllInvisible();
            setFull(correctAnswer);
            buttonAnswer.setVisibility(View.VISIBLE);
            buttonAnswer.setText(animals.get(correctAnswer).getName());

            ttsListener.playSilence(750);
            ttsListener.speak(animals.get(correctAnswer).getName(),animals.get(correctAnswer).getSound());


            /*
            //звук, название животного, смена карт
            try {


                //SoundPlay.playSP(getContext(), R.raw.correct);
                Thread.sleep(500);     //1000-задержка  на 1000 миллисекунду = 1 секунда
                ttsListener.speak(animals.get(correctAnswer).getName(),animals.get(correctAnswer).getSound());
           } catch (InterruptedException e) {

            }

            try {

                Thread.sleep(3500);     //1000-задержка  на 1000 миллисекунду = 1 секунда
                newRound();
            } catch (InterruptedException e) {

            }
            */
        }
        else
        {
            //звук ошибки
            SoundPlay.playSP(getContext(), R.raw.error);
            if (answer==0){
                image0.setVisibility(View.INVISIBLE);
            }
            if (answer==1){
                image1.setVisibility(View.INVISIBLE);
            }
             if (answer==2){
                 image2.setVisibility(View.INVISIBLE);
            }
            if (answer==3){
                image3.setVisibility(View.INVISIBLE);
        }

        }
    }


    private void newRound (){
        generateWrong();
        setImages();
        SoundPlay.playSP(getContext(), animals.get(correctAnswer).getSound());
    }

    private void setAllInvisible(){
        image0.setVisibility(View.INVISIBLE);
        image1.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);
        image3.setVisibility(View.INVISIBLE);
    }

    private void setFull (int num){
        full.setVisibility(View.VISIBLE);
        full.setImageResource(animals.get(num).getImageSmall());
    }

    private void delay (int seconds){
        try {
            // Using Thread.sleep() we can add delay in our
            // application in a millisecond time. For the example
            // below the program will take a deep breath for one
            // second before continue to print the next value of
            // the loop.
            Thread.sleep(seconds);

            // The Thread.sleep() need to be executed inside a
            // try-catch block and we need to catch the
            // InterruptedException.
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */

}
