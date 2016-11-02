package yong.tank;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest extends InstrumentationTestCase {

    private static String TAG ="TEST";
    @Test
    public void test() throws Exception{
        Log.d(TAG,"TESTIS OK********123*****");
        assertEquals(2, 3);
        Log.d(TAG,"TEST");
        Log.d(TAG,"TESTIS OK*************");
    }
}
