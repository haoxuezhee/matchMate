package com.hxt.matchMate.test;

import com.hxt.matchMate.utils.AlgorithmUtil;
import springfox.bean.validators.plugins.schema.MinMaxAnnotationPlugin;
import sun.applet.Main;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName: AlgorithmUtilTest
 * Package: com.hxt.matchMate.test
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/23 14:16
 * @Version 1.0
 */
public class AlgorithmUtilTest {

    public static void main(String[] args) {
        List<String> tagList1 = Arrays.asList("java", "大一", "男");
        List<String> tagList2 =Arrays.asList("java","大一","女");
        List<String> tagList3 =Arrays.asList("python","大二","女");
        int i1 = AlgorithmUtil.minDistance(tagList1, tagList2);
        int i2 = AlgorithmUtil.minDistance(tagList1, tagList3);
        System.out.println(i1);
        System.out.println(i2);

    }
}
