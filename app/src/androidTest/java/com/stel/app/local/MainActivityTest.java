package com.stel.app.local;


import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void basicTest() {


        try {
            //Wait for Main Activity
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        mMainActivityTestRule.launchActivity()
        ViewInteraction textView = onView(
                allOf(withId(R.string.title_hello_world),
                        isDisplayed()));


        ViewInteraction button = onView(
                allOf(withText("HINDI"),
                        isDisplayed()));
        button.check(matches(isDisplayed()));




        button.perform(ViewActions.click());
        textView.check(matches(withText("Hello World--hindi")));

        button = onView(
                allOf(withText("GUJARATI"),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
        button.perform(ViewActions.click());
        textView.check(matches(withText("Hello World: Gujarati Res")));

    }
//
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
}
