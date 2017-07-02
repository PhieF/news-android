import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.luhmer.owncloudnewsreader.NewsReaderListActivity;
import okhttp3.HttpUrl;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class URLTests extends ActivityInstrumentationTestCase2<NewsReaderListActivity> {

    private NewsReaderListActivity mActivity;

    public URLTests() {
        super(NewsReaderListActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();


    }

    @Test
    public void testUrl() {
        // Key: Root Url, Value: Expected result
        Map<String, HttpUrl> testUrls = new HashMap<>();

        testUrls.put("https://test.com", HttpUrl.parse("https://test.com/test1/test2/test3"));
        testUrls.put("https://test.com/", HttpUrl.parse("https://test.com/test1/test2/test3"));

        testUrls.put("https://test.com/subfolder", HttpUrl.parse("https://test.com/subfolder/test1/test2/test3"));
        testUrls.put("https://test.com/subfolder/", HttpUrl.parse("https://test.com/subfolder/test1/test2/test3"));


    }
}