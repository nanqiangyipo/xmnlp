package org.xm.xmnlp.test.seg;

import junit.framework.TestCase;
import org.xm.xmnlp.util.TextUtil;

/**
 * @author hankcs
 */
public class TestUtil extends TestCase {
    public void testNonZero() throws Exception {
        System.out.println(TextUtil.nonZero(0));
    }
}
