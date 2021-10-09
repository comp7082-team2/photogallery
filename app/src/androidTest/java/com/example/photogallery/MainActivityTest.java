package com.example.photogallery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RunWith(JUnit4.class)
public class MainActivityTest {

    public static final String TAG = MainActivityTest.class.getName();
    public static final String TEST_CAPTION = "test caption";

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityActivityScenarioRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @BeforeClass
    /**
     * Static method that runs before all the tests in this class. Copies a file from resources
     * that can be used for test data.
     */
    public static void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        copyResources(context, R.drawable.test_image);
    }

    @AfterClass
    /**
     * Static method that runs after all the tests in this class. Deletes any files that match the
     * TEST_CAPTION used by the set up.
     */
    public static void tearDown() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/Android/data/com.example.photogallery/files/Pictures");
        File[] fList = file.listFiles();
        if(fList != null) {
            List<File> filesToDelete = Arrays.stream(fList)
                    .filter(f -> f.getName().contains(TEST_CAPTION))
                    .collect(Collectors.toList());
            filesToDelete.forEach(f -> {
                if(f.exists()) {
                    f.delete();
                }
            });
        }
    }

    @Test
    /**
     * Searches for photos taken sometime today, expects to find the photo created during setUp
     */
    public void performSearchUsingDate_success() {
        Instant now = Instant.now();
        String yesterday = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss").format(Date.from(now.minus(1, ChronoUnit.DAYS)));
        String tomorrow = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss").format(Date.from(now.plus(1, ChronoUnit.DAYS)));
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText(), replaceText(yesterday), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(clearText(), replaceText(tomorrow), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        // Validates that the timestamp field is not empty, indicating a photo has been returned
        onView(withId(R.id.tvTimestamp)).check(matches(not(withText(""))));
    }

    @Test
    /**
     * Searches for photos taken in the future and verifies that no pictures are displayed in the gallery
     */
    public void performSearchUsingDate_failure() {
        Instant now = Instant.now();
        String tomorrow = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss").format(Date.from(now.plus(1, ChronoUnit.DAYS)));
        String nextWeek = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss").format(Date.from(now.plus(7, ChronoUnit.DAYS)));
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText(), replaceText(tomorrow), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(clearText(), replaceText(nextWeek), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        // Validates that the timestamp field is empty, indicating no picture was returned
        onView(withId(R.id.tvTimestamp)).check(matches(withText("")));
    }


    @Test
    /**
     * Searches for photos with a caption matching
     */
    public void performSearchUsingKeyword() {
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(typeText(TEST_CAPTION), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText(TEST_CAPTION)));
    }

    /**
     * Copies a png from resources to the file system with the expected naming system to be used for
     * test data.
     * @param context The context the tests will be executing in
     * @param resId The resource that will be copied to the file system.
     */
    public static void copyResources(Context context, int resId) {
        Log.i(TAG, "Copying File");

        Drawable drawable = context.getDrawable(resId);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_" + TEST_CAPTION + "_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, new FileOutputStream(new File(storageDir, imageFileName)));
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
