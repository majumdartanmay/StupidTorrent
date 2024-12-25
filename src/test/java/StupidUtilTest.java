import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.stupid.utils.StupidUtils;

import java.util.Arrays;

public class StupidUtilTest {
    @Test
    public void testStringToHash() {
        final byte[] output = StupidUtils.hexStringToByteArray("041727101980");
        final byte[] ans = new byte[]{0x04, 0x17, 0x27, 0x10, 0x19, (byte) 0x80};
        Assertions.assertArrayEquals(output, ans);
    }

    @Test
    public void testRandomHexString() {
        final byte[] output = StupidUtils.getRandomHexString(8);
        System.out.println(Arrays.toString(output));
    }
}
