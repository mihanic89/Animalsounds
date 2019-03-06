package com.yamilab.animalsounds;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

/**
 * Created by Misha on 26.10.2017.
 */
@GlideModule
public class SoundsAppGlideModule extends AppGlideModule{
        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
                // Register FirebaseImageLoader to handle StorageReference
                registry.append(StorageReference.class, InputStream.class,
                        new FirebaseImageLoader.Factory());
        }


}
