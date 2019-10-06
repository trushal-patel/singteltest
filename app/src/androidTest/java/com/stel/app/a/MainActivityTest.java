package com.stel.app.a;


import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest()
    {
        ViewInteraction editText = onView(
                allOf(withId(R.id.edt_input),
                        isDisplayed()));
        editText.check(matches(withText("Test")));

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_submit),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
        button.perform(ViewActions.click());

        ViewInteraction buttonDialog = onView(
                allOf(withId(android.R.id.button1),
                        isDisplayed()));
        buttonDialog.check(matches(isDisplayed()));

        buttonDialog.perform(ViewActions.click());

        ViewInteraction radioButton = onView(
                allOf(withId(R.id.rdo_btn_maths_operation),
                        isDisplayed()));
        radioButton.check(matches(isDisplayed()));
        radioButton.perform(ViewActions.click());



        doMathOperation();



        ViewInteraction operation = onView(
                allOf(withId(android.R.id.text1), withText("+"),
                        isDisplayed()));
        operation.check(matches(withText("+")));
        operation.perform(ViewActions.click());

        operation = onView(
                allOf(withId(android.R.id.text1), withText("*"),
                        isDisplayed()));
        operation.check(matches(withText("*")));
        operation.perform(ViewActions.click());
        doMathOperation();
    }

    void doMathOperation()
    {
        ViewInteraction editText1 = onView(
                withId(R.id.edt_input1));
        editText1.check(matches(withText("10")));

        ViewInteraction editText2 = onView(
                withId(R.id.edt_input2));
        editText2.check(matches(withText("10")));


        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_calculate),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction buttonDialog2 = onView(
                allOf(withId(android.R.id.button1),
                        isDisplayed()));
        buttonDialog2.check(matches(isDisplayed()));

        buttonDialog2.perform(ViewActions.click());
    }

}
