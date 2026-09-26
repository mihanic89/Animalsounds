package com.yamilab.animalsounds;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by Misha on 25.02.2018.
 */

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{

    private final ArrayList<Animal> mDataSet;
    private final int screenWidth;
    private Context context;
    private GlideRequests glideRequests=null;

    private TTSListener ttsListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        // Текущие аниматоры «дыхания» карточки; отменяются при recycle,
        // ставятся на паузу при уходе view с экрана.
        private ObjectAnimator scaleXAnimator;
        private ObjectAnimator scaleYAnimator;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }


        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        void cancelAnimations() {
            if (scaleXAnimator != null) {
                scaleXAnimator.cancel();
                scaleXAnimator = null;
            }
            if (scaleYAnimator != null) {
                scaleYAnimator.cancel();
                scaleYAnimator = null;
            }
        }

        void pauseAnimations() {
            if (scaleXAnimator != null && !scaleXAnimator.isPaused()) {
                scaleXAnimator.pause();
            }
            if (scaleYAnimator != null && !scaleYAnimator.isPaused()) {
                scaleYAnimator.pause();
            }
        }

        void resumeAnimations() {
            if (scaleXAnimator != null && scaleXAnimator.isPaused()) {
                scaleXAnimator.resume();
            }
            if (scaleYAnimator != null && scaleYAnimator.isPaused()) {
                scaleYAnimator.resume();
            }
        }
    }

    public AnimalAdapter(ArrayList<Animal> dataSet, int screenWidth, Context context, GlideRequests glide) {

        this.screenWidth = screenWidth;
        mDataSet = dataSet;
        this.context = context;
        if (ttsListener==null){
            ttsListener = (TTSListener)this.context;}

        glideRequests= glide;
    }


    @Override
    public void onViewRecycled (ViewHolder holder){
        holder.cancelAnimations();

        holder.getTextView().setText(null);
        holder.getImageView().setImageDrawable(null);
        GlideApp.with(holder.getImageView().getContext()).clear(holder.getImageView());
        holder.getImageView().setOnClickListener(null);
        holder.getTextView().setOnClickListener(null);

        super.onViewRecycled(holder);

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        // Карточка ушла с экрана — пауза вместо бесконечной работы в фоне.
        holder.pauseAnimations();
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.resumeAnimations();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_item, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Массив возможных периодов анимации (в миллисекундах)
        long[] durations = {3500, 4000, 4500};
        // Выбираем случайный период для этой карточки
        long duration = durations[position % durations.length];

        // Анимация масштабирования (95% → 100%, случайный период, бесконечно)
        // Аниматоры пересоздаются на каждый бинд и хранятся в ViewHolder:
        // так их можно поставить на паузу (detach) и отменить (recycle).
        // Бесконечные аниматоры на отвязанных view расходуют батарею и держат ссылки.
        holder.cancelAnimations();

        holder.scaleXAnimator = ObjectAnimator.ofFloat(
            holder.itemView, "scaleX", 0.95f, 1.0f
        );
        holder.scaleXAnimator.setDuration(duration);
        holder.scaleXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        holder.scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        holder.scaleXAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        holder.scaleXAnimator.start();

        holder.scaleYAnimator = ObjectAnimator.ofFloat(
            holder.itemView, "scaleY", 0.95f, 1.0f
        );
        holder.scaleYAnimator.setDuration(duration);
        holder.scaleYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        holder.scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        holder.scaleYAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        holder.scaleYAnimator.start();



        final Animal animal = mDataSet.get(position);
        holder.getTextView().setText(mDataSet.get(position).getName());

        if (animal.isGIF() && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            try {
                glideRequests
                        .load("https://apps.mayak.net.ru/gifs/" + animal.getGifHref())
                        .priority(Priority.LOW)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .override(screenWidth /2, Target.SIZE_ORIGINAL)
                        .thumbnail(glideRequests.load(animal.getImageSmall()))
                        .into(holder.getImageView());

            } catch (Exception e) {
                holder.getImageView().setImageDrawable(
                        ContextCompat.getDrawable(
                                holder.getImageView().getContext(),
                                mDataSet.get(position).getImageSmall()));
            }
        } else {

            try {
                glideRequests
                        .load(mDataSet.get(position).getImageSmall())
                        .priority(Priority.LOW)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(screenWidth, Target.SIZE_ORIGINAL)
                        .fitCenter()
                        .into(holder.getImageView());

            } catch (Exception e) {
                holder.getImageView().setImageDrawable(
                        ContextCompat.getDrawable(
                                holder.getImageView().getContext(),
                                mDataSet.get(position).getImageSmall()));
            }

        }





        holder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              try {
                  SoundPlay.playSP(context, animal.getSound());
              }
              catch (Exception e){

              }
            }
        });



        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ttsListener.speak(animal.getName(),animal.getSound());
            }
        });
    }
}
