package com.yamilab.animalsounds;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton5 = onView(
                allOf(withId(R.id.done), isDisplayed()));
        appCompatImageButton5.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3588239);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.container),
                        withParent(withId(R.id.main_content)),
                        isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction cardView = onView(
                allOf(withId(R.id.card_view),
                        withParent(withId(R.id.recyclerView)),
                        isDisplayed()));
        cardView.perform(longClick());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3585386);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton6 = onView(
                allOf(withId(R.id.action_back), isDisplayed()));
        appCompatImageButton6.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3594455);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView2 = onView(
                allOf(withId(R.id.card_view),
                        withParent(withId(R.id.recyclerView)),
                        isDisplayed()));
        cardView2.perform(longClick());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3591064);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton7 = onView(
                allOf(withId(R.id.action_back), isDisplayed()));
        appCompatImageButton7.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3597037);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction viewPager2 = onView(
                allOf(withId(R.id.container),
                        withParent(withId(R.id.main_content)),
                        isDisplayed()));
        viewPager2.perform(swipeLeft());

        ViewInteraction viewPager3 = onView(
                allOf(withId(R.id.container),
                        withParent(withId(R.id.main_content)),
                        isDisplayed()));
        viewPager3.perform(swipeLeft());

        ViewInteraction appCompatTextView = onView(
                allOf(withClassName(is("android.support.v7.widget.AppCompatTextView")), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction viewPager4 = onView(
                allOf(withId(R.id.container),
                        withParent(withId(R.id.main_content)),
                        isDisplayed()));
        viewPager4.perform(swipeRight());

        ViewInteraction cardView3 = onView(
                allOf(withId(R.id.card_view),
                        withParent(withId(R.id.recyclerView)),
                        isDisplayed()));
        cardView3.perform(longClick());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3563023);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3563043);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3597012);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView4 = onView(
                allOf(withId(R.id.card_view),
                        withParent(withId(R.id.recyclerView)),
                        isDisplayed()));
        cardView4.perform(longClick());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3597265);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3598016);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView5 = onView(
                allOf(withId(R.id.card_view),
                        withParent(withId(R.id.recyclerView)),
                        isDisplayed()));
        cardView5.perform(longClick());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3584565);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3594704);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView6 = onView(
                allOf(withId(R.id.card_view),
                        withParent(withId(R.id.recyclerView)),
                        isDisplayed()));
        cardView6.perform(longClick());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3597585);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3596894);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction cardView7 = onView(
                allOf(withId(R.id.card_view),
                        withParent(withId(R.id.recyclerView)),
                        isDisplayed()));
        cardView7.perform(longClick());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3596631);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        pressBack();

    }

}
