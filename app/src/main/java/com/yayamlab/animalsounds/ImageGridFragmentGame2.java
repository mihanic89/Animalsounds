package com.yamilab.animalsounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Random;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.yamilab.animalsounds.R.id.buttonNext;
import static com.yamilab.animalsounds.R.id.imageFull;
import static com.yamilab.animalsounds.R.id.imageGame0;
import static com.yamilab.animalsounds.R.id.imageGame1;
import static com.yamilab.animalsounds.R.id.imageGame2;
import static com.yamilab.animalsounds.R.id.imageGame3;

/**
 * Created by Misha on 28.03.2017.
 */
public class ImageGridFragmentGame2 extends Fragment {


    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 40;
    private static final String KEY_WRONG_COUNTER = "wrongCounter2";
    private static final String KEY_CORRECT_COUNTER = "correctCounter2";
    //private int adCounter=0;
    private TTSListener ttsListener;
    private FirebaseAnalytics mFirebaseAnalytics;

// ...
// Obtain the FirebaseAnalytics instance.

    public ImageGridFragmentGame2(){

    }

    public static ImageGridFragmentGame2 newInstance(
            ArrayList array,
            int screenWidth) {

        ImageGridFragmentGame2 fragmentGame = new ImageGridFragmentGame2();
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
    private int correctInt=0, wrongInt=0;
    private boolean wrongHasTry=false;

    private int[] cardsNumbers;
    private final ArrayList<Integer> numbers = new ArrayList<>();

    ImageButton image0;
    ImageButton image1;
    ImageButton image2;
    ImageButton image3;
    ImageButton full;

    Button buttonAnswer,correctCounter, wrongCounter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.

        Context context;
        if (getActivity()!=null){
            context=getActivity();
        }
        else
        {
            context = getContext();
        }
        if (ttsListener==null){
            ttsListener = (TTSListener)context;}
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext().getApplicationContext());
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_game2_rebuild_count, container, false);

        animals = new ArrayList<Animal>();

        animals = (ArrayList<Animal>) getArguments().getSerializable("key");

        size = animals.size()-1;
        if (size<0) size=0;

        correctAnswer = new Random().nextInt(size);



        image0= rootView.findViewById(imageGame0);
        image1= rootView.findViewById(imageGame1);
        image2= rootView.findViewById(imageGame2);
        image3= rootView.findViewById(imageGame3);
        full = rootView.findViewById(imageFull);

        //textAnswer = (TextView) rootView.findViewById(R.id.textAnswer);
        buttonAnswer = rootView.findViewById(R.id.buttonName);
        correctCounter = rootView.findViewById(R.id.correctCounter);
        wrongCounter = rootView.findViewById(R.id.wrongCounter);


       // correctCounter.setText(correctInt);
       // wrongCounter.setText(wrongInt);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());


        correctInt=getPrefs.getInt(KEY_CORRECT_COUNTER,0);
        wrongInt=getPrefs.getInt(KEY_WRONG_COUNTER,0);
        if (wrongInt>999||correctInt>999){
            wrongInt=0;
            correctInt=0;
        }

        wrongCounter.setText( String.valueOf(wrongInt));
        correctCounter.setText( String.valueOf(correctInt));
       // ImageButton sound = rootView.findViewById(buttonSound);
        ImageButton next = rootView.findViewById(buttonNext);

        try
        {
            generateWrong();
            setImages();
            buttonAnswer.setText(animals.get(correctAnswer).getName());
           // newRound();
           // adCounter=0;
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

        /*
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlay.playSP(rootView.getContext(), animals.get(correctAnswer).getSound());
            }
        });
        */

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

        while (numbers.contains(correctAnswer)){
            correctAnswer = new Random().nextInt(size);
        }

        numbers.add(correctAnswer);

        if (numbers.size()>(size-5)){
            numbers.clear();
        }

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
            setImageGlide(image0,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image1,animals.get(wrong1).getImageSmall());
            setImageGlide(image2,animals.get(wrong2).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());
        }

        else if (correctCard==1){
            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image2,animals.get(wrong2).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());

        }

        else if (correctCard==2){
            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(wrong2).getImageSmall());
            setImageGlide(image2,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());
        }
        else {
            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(wrong2).getImageSmall());
            setImageGlide(image2,animals.get(wrong3).getImageSmall());
            setImageGlide(image3,animals.get(correctAnswer).getImageSmall());
        }
    }

    private void setImageGlide (ImageView imageView, int image){
            GlideApp.with(imageView.getContext())
                    .load(image)
                   // .fitCenter()
                    .transition(withCrossFade(1000))
                    .priority(Priority.LOW)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(new RequestOptions().override((int)getArguments().getInt("width")/3))
                    .into(imageView);
        }

    private void checkAnswer (int answer){



        if (correctCard==answer){

            setCorrectInt();


            SoundPlay.playSP(getContext(), R.raw.correct);
            //delay(500);
            setAllInvisible();
            setFull(correctAnswer);
            //buttonAnswer.setVisibility(View.VISIBLE);
            //buttonAnswer.setText(animals.get(correctAnswer).getName());
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

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */

            if (adCount>adShowInt){
                ((MainActivity) getActivity()).incAdCounter();
                mFirebaseAnalytics.logEvent("game2_ad", null);
                adCount=0;
            }
        }
        else{
            setWrongInt();
            SoundPlay.playSP(getContext(), R.raw.wrong);
            setAllInvisible();
            generateWrong();
            setImages();
            buttonAnswer.setText(animals.get(correctAnswer).getName());
            wrongHasTry=true;
        }
    }

    private void setCorrectInt(){
        correctInt++;
        correctCounter.setText(String.valueOf(correctInt));
        saveInt(KEY_CORRECT_COUNTER, correctInt);
    }

    private void setWrongInt(){
        wrongInt++;
        wrongCounter.setText( String.valueOf(wrongInt));
        saveInt(KEY_WRONG_COUNTER, wrongInt);
    }

    private void setAllInvisible(){
        image0.setVisibility(View.INVISIBLE);
        image1.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);
        image3.setVisibility(View.INVISIBLE);
        buttonAnswer.setVisibility(View.INVISIBLE);
    }

    private void setFull(int image){
        full.setVisibility(View.VISIBLE);
        full.setImageResource(image);
    }

    private void newRound(){
        generateWrong();
        setImages();
        buttonAnswer.setText(animals.get(correctAnswer).getName());
        buttonAnswer.setVisibility(View.INVISIBLE);
    }

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        image0 = null;
        image1 = null;
        image2 = null;
        image3 = null;
        full = null;
        buttonAnswer = null;
        correctCounter = null;
        wrongCounter = null;
    }

}
