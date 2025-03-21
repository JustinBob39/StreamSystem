import ntsc.cas.cn.test.Add;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddTest {

    @Test
    public void add() {
        assertEquals(3, Add.add(1, 2));
        assertEquals(-3, Add.add(-1, -2));
        assertEquals(1, Add.add(0, 1));
        assertEquals(-1, Add.add(0, -1));
        assertEquals(0, Add.add(0, 0));

        assertEquals(0.1, Math.abs(1 - 9 / 10.0), 0.0000001);
    }
}
