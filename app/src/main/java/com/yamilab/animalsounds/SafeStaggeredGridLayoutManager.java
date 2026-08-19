package com.yamilab.animalsounds;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * A {@link StaggeredGridLayoutManager} that is safe against the well-known
 * {@link IndexOutOfBoundsException} "Index 0 out of bounds for length 0" crash.
 *
 * <p>The stock {@link StaggeredGridLayoutManager} can crash inside
 * {@code Span.calculateCachedStart()} / {@code Span.calculateCachedEnd()} when the internal gap
 * check runs (e.g. while a scroll gesture is being cancelled) at a moment when a span's
 * {@code mViews} list is empty (dataset swapped out mid-gesture, fragment recreated, etc.).
 *
 * <p>Since {@code androidx.recyclerview:recyclerview:1.4.0} the {@code checkForGaps()} method is
 * package-private, so it cannot be guarded directly from a subclass in a different package.
 * Instead, the public entry points through which the stock implementation performs the gap check
 * ({@link #onScrollStateChanged(int)} and {@link #onLayoutChildren(RecyclerView.Recycler,
 * RecyclerView.State)}) are guarded, preserving the crash-avoidance behaviour.
 */
public class SafeStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    public SafeStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SafeStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onScrollStateChanged(int state) {
        try {
            super.onScrollStateChanged(state);
        } catch (IndexOutOfBoundsException e) {
            // A span ended up with an empty mViews list while the gap check was running.
            // Nothing sensible can be done here, so just skip the gap check this frame.
        }
    }

    @Override
    public void onLayoutChildren(@NonNull RecyclerView.Recycler recycler,
                                 @NonNull RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            // Same guard for the layout path: skip this layout pass and let the next one retry.
        }
    }
}
