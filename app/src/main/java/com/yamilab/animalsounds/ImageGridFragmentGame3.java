package com.yamilab.animalsounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
public class ImageGridFragmentGame3 extends Fragment {


    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 40;
    private static final String KEY_WRONG_COUNTER = "wrongCounter3";
    private static final String KEY_CORRECT_COUNTER = "correctCounter23";
    //private int adCounter=0;
    private TTSListener ttsListener;
    private FirebaseAnalytics mFirebaseAnalytics;

// ...
// Obtain the FirebaseAnalytics instance.

    public ImageGridFragmentGame3(){

    }

    public static ImageGridFragmentGame3 newInstance(
            ArrayList array,
            int screenWidth) {

        ImageGridFragmentGame3 fragmentGame = new ImageGridFragmentGame3();
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
    private int correctInt=0, wrongInt=0, checkedAnswer=0, correctAnswer1from4=0;
    private boolean wrongHasTry=false;
    private int sound1, sound2, sound3, sound4;

    private int[] cardsNumbers;
    private ArrayList<Integer> numbers = new ArrayList<>();

    ImageButton full,buttonCheck,buttonNext;

    Button buttonName,button1,button2, button3, button4,
            correctCounter, wrongCounter;


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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_game3, container, false);

        animals = new ArrayList<Animal>();

        animals = (ArrayList<Animal>) getArguments().getSerializable("key");

        size = animals.size()-1;
        if (size<0) size=0;

        correctAnswer = new Random().nextInt(size);

        buttonName= rootView.findViewById(R.id.buttonName);
        button1= rootView.findViewById(R.id.button1);
        button2= rootView.findViewById(R.id.button2);
        button3= rootView.findViewById(R.id.button3);
        button4= rootView.findViewById(R.id.button4);
        buttonCheck = rootView.findViewById(R.id.buttonCheck);
        buttonNext= rootView.findViewById(R.id.buttonNext);
        full = rootView.findViewById(imageFull);

        //textAnswer = (TextView) rootView.findViewById(R.id.textAnswer);

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


        try
        {
            generateWrong();
            setSounds();
            full.setImageResource(animals.get(correctAnswer).getImageSmall());
            buttonName.setText(animals.get(correctAnswer).getName());
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

        buttonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsListener.speak(animals.get(correctAnswer).getName(),animals.get(correctAnswer).getSound());
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRound();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswer(1);
                SoundPlay.playSP(getContext(), sound1);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswer(2);
                SoundPlay.playSP(getContext(), sound2);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswer(3);
                SoundPlay.playSP(getContext(), sound3);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswer(4);
                SoundPlay.playSP(getContext(), sound4);
            }
        });

        /*
        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRound();
               // generateWrong();
               // setImages();
            }
        });
        */


        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ttsListener.speak(animals.get(correctAnswer).getName(),animals.get(correctAnswer).getSound());
              checkAnswer();
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



    private void setSounds(){


        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        button3.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        buttonCheck.setVisibility(View.VISIBLE);
        //buttonAnswer.setVisibility(View.INVISIBLE);


        full.setVisibility(View.VISIBLE);
        correctCard = new Random().nextInt(3);

        if (correctCard==0){
            /*
            image0.setImageResource(animals.get(correctAnswer).getImageSmall());
            image1.setImageResource(animals.get(wrong1).getImageSmall());
            image2.setImageResource(animals.get(wrong2).getImageSmall());
            image3.setImageResource(animals.get(wrong3).getImageSmall());

            setImageGlide(image0,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image1,animals.get(wrong1).getImageSmall());
            setImageGlide(image2,animals.get(wrong2).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());
            */

            sound1 = animals.get(correctAnswer).getSound();
            sound2 = animals.get(wrong1).getSound();
            sound3 = animals.get(wrong2).getSound();
            sound4 = animals.get(wrong3).getSound();
        }

        else if (correctCard==1){
            /*
            image1.setImageResource(animals.get(correctAnswer).getImageSmall());
            image0.setImageResource(animals.get(wrong1).getImageSmall());
            image2.setImageResource(animals.get(wrong2).getImageSmall());
            image3.setImageResource(animals.get(wrong3).getImageSmall());

            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image2,animals.get(wrong2).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());

             */

            sound1 = animals.get(wrong1).getSound();
            sound2 = animals.get(correctAnswer).getSound();
            sound3 = animals.get(wrong2).getSound();
            sound4 = animals.get(wrong3).getSound();

        }

        else if (correctCard==2){
            /*
            image0.setImageResource(animals.get(wrong1).getImageSmall());
            image1.setImageResource(animals.get(wrong2).getImageSmall());
            image2.setImageResource(animals.get(correctAnswer).getImageSmall());
            image3.setImageResource(animals.get(wrong3).getImageSmall());

            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(wrong2).getImageSmall());
            setImageGlide(image2,animals.get(correctAnswer).getImageSmall());
            setImageGlide(image3,animals.get(wrong3).getImageSmall());

             */

            sound1 = animals.get(wrong1).getSound();
            sound2 = animals.get(wrong2).getSound();
            sound3 = animals.get(correctAnswer).getSound();
            sound4 = animals.get(wrong3).getSound();
        }
        else if (correctCard==3){
            /*
            image0.setImageResource(animals.get(wrong1).getImageSmall());
            image1.setImageResource(animals.get(wrong2).getImageSmall());
            image2.setImageResource(animals.get(wrong3).getImageSmall());
            image3.setImageResource(animals.get(correctAnswer).getImageSmall());

            setImageGlide(image0,animals.get(wrong1).getImageSmall());
            setImageGlide(image1,animals.get(wrong2).getImageSmall());
            setImageGlide(image2,animals.get(wrong3).getImageSmall());
            setImageGlide(image3,animals.get(correctAnswer).getImageSmall());
        */

            sound1 = animals.get(wrong1).getSound();
            sound2 = animals.get(wrong2).getSound();
            sound3 = animals.get(wrong3).getSound();
            sound4 = animals.get(correctAnswer).getSound();
        }
        correctAnswer1from4 = correctCard+1;

        /*
        Toast toast = Toast.makeText(getActivity(),
                "correctAnswer1from4 " + correctAnswer1from4 ,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

         */

       // SoundPlay.playSP(getContext(), animals.get(correctAnswer).getSound());

    }

    private void setImageGlide (ImageView imageView, int image){
        if (this!=null) {
            GlideApp.with(this)
                    .load(image)
                   // .fitCenter()
                    .transition(withCrossFade(1000))
                    .priority(Priority.LOW)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(new RequestOptions().override((int)getArguments().getInt("width")/3))
                    .into(imageView);
        }
        else
        {
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
    }

   private void setAnswer(int answer){
       if (answer==1){
           button1.setBackgroundResource(R.drawable.oval_shape_blue);
           button2.setBackgroundResource(R.drawable.oval_shape);
           button3.setBackgroundResource(R.drawable.oval_shape);
           button4.setBackgroundResource(R.drawable.oval_shape);
       }
       if (answer==2){
           button1.setBackgroundResource(R.drawable.oval_shape);
           button2.setBackgroundResource(R.drawable.oval_shape_blue);
           button3.setBackgroundResource(R.drawable.oval_shape);
           button4.setBackgroundResource(R.drawable.oval_shape);
       }
       if (answer==3){
           button1.setBackgroundResource(R.drawable.oval_shape);
           button2.setBackgroundResource(R.drawable.oval_shape);
           button3.setBackgroundResource(R.drawable.oval_shape_blue);
           button4.setBackgroundResource(R.drawable.oval_shape);
       }
       if (answer==4){
           button1.setBackgroundResource(R.drawable.oval_shape);
           button2.setBackgroundResource(R.drawable.oval_shape);
           button3.setBackgroundResource(R.drawable.oval_shape);
           button4.setBackgroundResource(R.drawable.oval_shape_blue);
       }
       checkedAnswer = answer;

        /*
       Toast toast = Toast.makeText(getActivity(),
               "checkedAnswer " + answer ,
               Toast.LENGTH_SHORT);
       toast.setGravity(Gravity.CENTER, 0, 0);
       toast.show();
       */

   }



    private void checkAnswer (){

        if (checkedAnswer!=0) {

            if (checkedAnswer == correctAnswer1from4) {

                if (!wrongHasTry) setCorrectInt();
                SoundPlay.playSP(getContext(), R.raw.correct);
                setButtonsInvisible(checkedAnswer);
            } else {
                setWrongInt();
                wrongHasTry = true;
                //звук ошибки
                SoundPlay.playSP(getContext(), R.raw.error);
                if (checkedAnswer == 1) {
                    button1.setVisibility(View.INVISIBLE);
                }
                if (checkedAnswer == 2) {
                    button2.setVisibility(View.INVISIBLE);
                }
                if (checkedAnswer == 3) {
                    button3.setVisibility(View.INVISIBLE);
                }
                if (checkedAnswer == 4) {
                    button4.setVisibility(View.INVISIBLE);
                }

            }
        }
        else {
            final Handler handler = new Handler();
            button1.setBackgroundResource(R.drawable.oval_shape_blue);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button1.setBackgroundResource(R.drawable.oval_shape);
                    button2.setBackgroundResource(R.drawable.oval_shape_blue);
                }
            }, 250);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button2.setBackgroundResource(R.drawable.oval_shape);
                    button3.setBackgroundResource(R.drawable.oval_shape_blue);
                }
            }, 500);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button3.setBackgroundResource(R.drawable.oval_shape);
                    button4.setBackgroundResource(R.drawable.oval_shape_blue);
                }
            }, 750);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button4.setBackgroundResource(R.drawable.oval_shape);
                    //button4.setBackgroundResource(R.drawable.oval_shape_blue);
                }
            }, 1000);
        }



    }

    private void setButtonsInvisible(int checkedAnswer) {
        if (checkedAnswer==1){
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
            button4.setVisibility(View.INVISIBLE);
            buttonCheck.setVisibility(View.INVISIBLE);
        }

        if (checkedAnswer==2){
            button1.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
            button4.setVisibility(View.INVISIBLE);
            buttonCheck.setVisibility(View.INVISIBLE);
        }

        if (checkedAnswer==3){
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
            button4.setVisibility(View.INVISIBLE);
            buttonCheck.setVisibility(View.INVISIBLE);
        }
        if (checkedAnswer==4){
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
            buttonCheck.setVisibility(View.INVISIBLE);
        }

    }


    private void newRound (){

        wrongHasTry=false;
        checkedAnswer=0;
        button1.setBackgroundResource(R.drawable.oval_shape);
        button2.setBackgroundResource(R.drawable.oval_shape);
        button3.setBackgroundResource(R.drawable.oval_shape);
        button4.setBackgroundResource(R.drawable.oval_shape);


        //adCounter++;
        ((MainActivity) getActivity()).incAdCounter();

        //SoundPlay.playSP(getContext(), animals.get(correctAnswer).getSound());


        //if (adCounter>13) {
        if (((MainActivity) getActivity()).getAdCounter()>((MainActivity) getActivity()).adShowInt) {
            ((MainActivity) getActivity()).showInterstitial();
            //adCounter=0;
           // ((MainActivity) getActivity()).zeroAdCounter();
            generateWrong();
            setSounds();
            buttonName.setText(animals.get(correctAnswer).getName());

            mFirebaseAnalytics.logEvent("game3_ad", null);
        }

        else{
            generateWrong();
            setSounds();
            buttonName.setText(animals.get(correctAnswer).getName());
            ttsListener.speak(animals.get(correctAnswer).getName(),animals.get(correctAnswer).getSound());
        }

        full.setImageResource(animals.get(correctAnswer).getImageSmall());

        Bundle params = new Bundle();
        params.putString("new_round3", "New round start 3");
        mFirebaseAnalytics.logEvent("new_round_3", params);
    }

    private void setAllInvisible(){
        button1.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);
        button3.setVisibility(View.INVISIBLE);
        button4.setVisibility(View.INVISIBLE);
    }

    private void setFull (int num){
        full.setVisibility(View.VISIBLE);
        full.setImageResource(animals.get(num).getImageSmall());
    }

    private void setCorrectInt(){
        if (!wrongHasTry) {
            correctInt++;

            correctCounter.setText(String.valueOf(correctInt));
            saveInt(KEY_CORRECT_COUNTER, correctInt);
            ((MainActivity) getActivity()).incrementUnlockCounter();
        }
    };


    private void setWrongInt(){
        if (!wrongHasTry){
        wrongInt++;
        wrongCounter.setText( String.valueOf(wrongInt));
        saveInt(KEY_WRONG_COUNTER,wrongInt);
        wrongHasTry=true;
        }
    };

    public void saveInt(String key, int value){
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

}
