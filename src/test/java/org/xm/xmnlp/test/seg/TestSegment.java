package org.xm.xmnlp.test.seg;


import junit.framework.TestCase;
import org.xm.xmnlp.Xmnlp;
import org.xm.xmnlp.collection.ahocorasick.AhoCorasickDoubleArrayTrie;
import org.xm.xmnlp.dictionary.CoreBiGramTableDictionary;
import org.xm.xmnlp.dictionary.CoreDictionary;
import org.xm.xmnlp.dictionary.CustomDictionary;
import org.xm.xmnlp.dictionary.other.CharTable;
import org.xm.xmnlp.dictionary.other.CharType;
import org.xm.xmnlp.seg.CRFSegment;
import org.xm.xmnlp.seg.DijkstraSegment;
import org.xm.xmnlp.seg.Segment;
import org.xm.xmnlp.seg.ViterbiSegment;
import org.xm.xmnlp.seg.domain.Term;
import org.xm.xmnlp.seg.other.DoubleArrayTrieSegment;
import org.xm.xmnlp.tokenizer.*;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * @author hankcs
 */
public class TestSegment extends TestCase {
    public void testSeg() throws Exception {
        Xmnlp.Config.enableDebug();
        Segment segment = new DijkstraSegment();
        System.out.println(segment.seg(
                "我遗忘我的密码了"
        ));
    }

    public void testViterbi() throws Exception {
        Xmnlp.Config.enableDebug();
        CustomDictionary.add("网剧");
        Segment seg = new DijkstraSegment();
        List<Term> termList = seg.seg("结婚的和尚未结婚的,优酷总裁魏明介绍了优酷2015年的内容战略，表示要以“大电影、大网剧、大综艺”为关键词.");
        System.out.println(termList);

        Segment seg1 = new ViterbiSegment();
        List<Term> termList1 = seg1.seg("结婚的和尚未结婚的1,优酷总裁魏明介绍了优酷2015年的内容战略，表示要以“大电影、大网剧、大综艺”为关键词.");
        System.out.println(termList1);
    }

    public void testNotional() throws Exception {
        System.out.println(NotionalTokenizer.segment("算法可以宽泛的分为三类"));
    }

    public void testNGram() throws Exception {
        System.out.println(CoreBiGramTableDictionary.getBiFrequency("牺", "牲"));
    }

    public void testShortest() throws Exception {
        Xmnlp.Config.enableDebug();
        Segment segment = new ViterbiSegment().enableAllNamedEntityRecognize(true);
        System.out.println(segment.seg("把市场经济奉行的等价交换原则引入党的生活和国家机关政务活动中"));
    }

    public void testIndexSeg() throws Exception {
        System.out.println(IndexTokenizer.segment("中科院预测科学研究中心学术委员会"));
    }


    public void testSpeechTagging() throws Exception {
        Xmnlp.Config.enableDebug();
        String text = "教授正在教授自然语言处理课程";
        DijkstraSegment segment = new DijkstraSegment();

        System.out.println("未标注：" + segment.seg(text));
        segment.enablePartOfSpeechTagging(true);
        System.out.println("标注后：" + segment.seg(text));
    }

    public void testFactory() throws Exception {
        Segment segment = Xmnlp.newSegment();
    }

    public void testCustomDictionary() throws Exception {
        CustomDictionary.insert("肯德基", "ns 1000");
        Segment segment = new ViterbiSegment();
        System.out.println(segment.seg("肯德基"));
    }

    public void testNT() throws Exception {
        Xmnlp.Config.enableDebug();
        Segment segment = new DijkstraSegment().enableOrganizationRecognize(true);
        System.out.println(segment.seg("张克智与潍坊地铁建设工程公司"));
        Segment seg= new DijkstraSegment();
        System.out.println(seg.seg("张克智与潍坊地铁建设工程公司1"));
    }

    public void testACSegment() throws Exception {
        Segment segment = new DoubleArrayTrieSegment();
        segment.enablePartOfSpeechTagging(true);
        System.out.println(segment.seg("江西鄱阳湖干枯，中国最大淡水湖变成大草原.张克智与潍坊地铁建设工程公司1结婚的和尚未结婚的"));
    }

    public void testIssue2() throws Exception {
        String text = "BENQphone";
        System.out.println(Xmnlp.segment(text));
        CustomDictionary.insert("BENQ");
        System.out.println(Xmnlp.segment(text));
    }

    public void testIssue3() throws Exception {
        assertEquals(CharType.CT_DELIMITER, CharType.get('*'));
        System.out.println(Xmnlp.segment("300g*2"));
        System.out.println(Xmnlp.segment("３００ｇ＊２"));
        System.out.println(Xmnlp.segment("鱼300克*2/组"));
    }

    public void testQuickAtomSegment() throws Exception {
        String text = "你好1234abc Good一二三四3.14";
        System.out.println(Segment.quickAtomSegment(text.toCharArray(), 0, text.length()));
    }

    public void testJP() throws Exception {
        String text = "明天8.9你好abc对了";
        Segment segment = new ViterbiSegment().enableCustomDictionary(false).enableAllNamedEntityRecognize(false);
        System.out.println(segment.seg(text));
    }

    public void testSpeedOfSecondViterbi() throws Exception {
        String text = "王总和小丽结婚了";
        Segment segment = new ViterbiSegment().enableAllNamedEntityRecognize(false)
                .enableNameRecognize(false) // 人名识别需要二次维特比，比较慢
                .enableCustomDictionary(false);
        System.out.println(segment.seg(text));
        long start = System.currentTimeMillis();
        int pressure = 1000000;
        for (int i = 0; i < pressure; ++i) {
            segment.seg(text);
        }
        double costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("分词速度：%.2f字每秒", text.length() * pressure / costTime);
    }

    public void testNumberAndQuantifier() throws Exception {
        StandardTokenizer.SEGMENT.enableNumberQuantifierRecognize(true);
        String[] testCase = new String[]
                {
                        "十九元套餐包括什么",
                        "九千九百九十九朵玫瑰",
                        "壹佰块钱都不给我",
                        "９０１２３４５６７８只蚂蚁",
                };
        for (String sentence : testCase) {
            System.out.println(StandardTokenizer.segment(sentence));
        }
    }

    public void testIssue10() throws Exception {
        StandardTokenizer.SEGMENT.enableNumberQuantifierRecognize(true);
        IndexTokenizer.SEGMENT.enableNumberQuantifierRecognize(true);
        List termList = StandardTokenizer.segment("此帐号有欠费业务是什么");
        System.out.println(termList);
        termList = IndexTokenizer.segment("此帐号有欠费业务是什么");
        System.out.println(termList);
        termList = StandardTokenizer.segment("15307971214话费还有多少");
        System.out.println(termList);
        termList = IndexTokenizer.segment("15307971214话费还有多少");
        System.out.println(termList);
    }

    public void testIssue199() throws Exception {
        Segment segment = new CRFSegment();
        segment.enableCustomDictionary(false);// 开启自定义词典
        segment.enablePartOfSpeechTagging(true);
        List<Term> termList = segment.seg("更多采购");
        System.out.println(termList);
        for (Term term : termList) {
            if (term.nature == null) {
                System.out.println("识别到新词：" + term.word);
            }
        }
    }

    public void testMultiThreading() throws Exception {
        Segment segment = StandardTokenizer.SEGMENT;
        // 测个速度
        String text = "江西鄱阳湖干枯，中国最大淡水湖变成大草原。";
        System.out.println(segment.seg(text));
        int pressure = 100000;
        StringBuilder sbBigText = new StringBuilder(text.length() * pressure);
        for (int i = 0; i < pressure; i++) {
            sbBigText.append(text);
        }
        text = sbBigText.toString();
        long start = System.currentTimeMillis();
        List<Term> termList1 = segment.seg(text);
        double costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("单线程分词速度：%.2f字每秒\n", text.length() / costTime);

        segment.enableMultithreading(4);
        start = System.currentTimeMillis();
        List<Term> termList2 = segment.seg(text);
        costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("四线程分词速度：%.2f字每秒\n", text.length() / costTime);

        assertEquals(termList1.size(), termList2.size());
        Iterator<Term> iterator1 = termList1.iterator();
        Iterator<Term> iterator2 = termList2.iterator();
        while (iterator1.hasNext()) {
            Term term1 = iterator1.next();
            Term term2 = iterator2.next();
            assertEquals(term1.word, term2.word);
            assertEquals(term1.nature, term2.nature);
            assertEquals(term1.offset, term2.offset);
        }
    }

    public void testTryToCrashSegment() throws Exception {
        String text = "尝试玩坏分词器";
        Segment segment = new ViterbiSegment().enableMultithreading(100);
        System.out.println(segment.seg(text));
    }

    public void testCRFSegment() throws Exception {
        Xmnlp.Config.enableDebug();
        Segment segment = new CRFSegment();
        System.out.println(segment.seg("有句谚语叫做一个萝卜一个坑儿"));
    }

    public void testIssue16() throws Exception {
        CustomDictionary.insert("爱听4g", "nz 1000");
        Segment segment = new ViterbiSegment();
        System.out.println(segment.seg("爱听4g"));
        System.out.println(segment.seg("爱听4G"));
        System.out.println(segment.seg("爱听４G"));
        System.out.println(segment.seg("爱听４Ｇ"));
        System.out.println(segment.seg("愛聽４Ｇ"));
    }

    public void testIssuse17() throws Exception {
        System.out.println(CharType.get('\u0000'));
        System.out.println(CharType.get(' '));
        assertEquals(CharTable.convert(' '), ' ');
        System.out.println(CharTable.convert('﹗'));
        Xmnlp.Config.Normalization = true;
        System.out.println(StandardTokenizer.segment("号 "));
    }

    public void testIssue22() throws Exception {
        CoreDictionary.Attribute attribute = CoreDictionary.get("年");
        System.out.println(attribute);
        List<Term> termList = StandardTokenizer.segment("三年");
        System.out.println(termList);
        assertEquals(attribute.nature[0], termList.get(1).nature);
        System.out.println(StandardTokenizer.segment("三元"));
        StandardTokenizer.SEGMENT.enableNumberQuantifierRecognize(true);
        System.out.println(StandardTokenizer.segment("三年"));
    }

    public void testIssue71() throws Exception {
        Segment segment = Xmnlp.newSegment();
        segment = segment.enableAllNamedEntityRecognize(true);
        segment = segment.enableNumberQuantifierRecognize(true);
        System.out.println(segment.seg("曾幻想过，若干年后的我就是这个样子的吗"));
    }

    public void testIssue193() throws Exception {
        String[] testCase = new String[]{
                "以每台约200元的价格送到苹果售后维修中心换新机（苹果的保修基本是免费换新机）",
                "可能以2500~2800元的价格回收",
                "3700个益农信息社打通服务“最后一公里”",
                "一位李先生给高政留言说上周五可以帮忙献血",
                "一位浩宁达高层透露",
                "五和万科长阳天地5个普宅项目",
                "以1974点低点和5178点高点作江恩角度线",
                "纳入统计的18家京系基金公司",
                "华夏基金与嘉实基金两家京系基金公司",
                "则应从排名第八的投标人开始依次递补三名投标人"
        };
        Segment segment = Xmnlp.newSegment().enableOrganizationRecognize(true).enableNumberQuantifierRecognize(true);
        for (String sentence : testCase) {
            List<Term> termList = segment.seg(sentence);
            System.out.println(termList);
        }
    }

    public void testTime() throws Exception {
        TraditionalChineseTokenizer.segment("认可程度");
    }

    public void testBuildASimpleSegment() throws Exception {
        TreeMap<String, String> dictionary = new TreeMap<String, String>();
        dictionary.put("Xmnlp", "名词");
        dictionary.put("特别", "副词");
        dictionary.put("方便", "形容词");
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<String>();
        acdat.build(dictionary);
    }

    public void testNLPSegment() throws Exception {
        String text = "2013年4月27日11时54分";
        NLPTokenizer.SEGMENT.enableNumberQuantifierRecognize(true);
        System.out.println(NLPTokenizer.segment(text));
    }

    public void testTraditionalSegment() throws Exception {
        String text = "吵架吵到快取消結婚了";
        System.out.println(TraditionalChineseTokenizer.segment(text));
    }
}
