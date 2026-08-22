package com.yamilab.animalsounds;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * A {@link StaggeredGridLayoutManager} that is safe against two well-known framework crashes:
 *
 * <ul>
 *   <li>{@link IndexOutOfBoundsException} "Index 0 out of bounds for length 0" thrown from
 *       {@code Span.calculateCachedStart()} / {@code Span.calculateCachedEnd()} when the internal
 *       gap check runs (e.g. while a scroll gesture is being cancelled) at a moment when a span's
 *       {@code mViews} list is empty.</li>
 *   <li>{@link NullPointerException} thrown from {@code recycleFromStart()} /
 *       {@code recycleFromEnd()} when a fling-driven {@code fill()} pass dereferences a null
 *       {@code lp.mSpan.mViews} (span state invalidated mid-scroll). Observed on
 *       {@code androidx.recyclerview:recyclerview:1.4.0} via the
 *       {@code ViewFlinger -> scrollVerticallyBy -> fill -> recycle} path.</li>
 * </ul>
 *
 * <p>Since {@code androidx.recyclerview:recyclerview:1.4.0} the affected internals are private or
 * package-private ({@code checkForGaps()}, {@code recycleFromStart()}, ...), so they cannot be
 * guarded directly from a subclass in a different package. Instead, every public entry point
 * through which the stock implementation reaches them is guarded:
 * {@link #onScrollStateChanged(int)},
 * {@link #onLayoutChildren(RecyclerView.Recycler, RecyclerView.State)},
 * {@link #scrollVerticallyBy(int, RecyclerView.Recycler, RecyclerView.State)} and
 * {@link #scrollHorizontallyBy(int, RecyclerView.Recycler, RecyclerView.State)}.
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

    @Override
    public int scrollVerticallyBy(int dy, @NonNull RecyclerView.Recycler recycler,
                                  @NonNull RecyclerView.State state) {
        try {
            return super.scrollVerticallyBy(dy, recycler, state);
        } catch (NullPointerException e) {
            // Fling-driven fill() pass hit recycleFromStart()/recycleFromEnd() while a child's
            // lp.mSpan was already null (known framework bug). Skip this scroll step; the next
            // frame will retry with fresh layout state.
            return 0;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, @NonNull RecyclerView.Recycler recycler,
                                    @NonNull RecyclerView.State state) {
        try {
            return super.scrollHorizontallyBy(dx, recycler, state);
        } catch (NullPointerException e) {
            // Same guard as scrollVerticallyBy(), for the horizontal orientation.
            return 0;
        }
    }
}
