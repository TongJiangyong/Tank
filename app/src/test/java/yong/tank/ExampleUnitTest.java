package yong.tank;

import org.junit.Test;

import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .build();
    //Retrofit2Base repo = retrofit.create(Retrofit2Base.class);
    private static final String TAG = "ExampleUnitTest";


    @Test
    public void test_addition_isCorrect() throws Exception {
        System.out.print("sfdfdsfsd");
        assertEquals(4, 2 + 2);
    }
}