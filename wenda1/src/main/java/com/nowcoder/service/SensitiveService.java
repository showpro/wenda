package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * InitializingBean 初始化读取文件
 * @author Administrator
 *
 */
@Service
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    /**
     * 默认敏感词替换符
     */
    private static final String DEFAULT_REPLACEMENT = "敏感词";

    /**
     * 创建字典树
     * @author Administrator
     *
     */
    private class TrieNode {

        /**
         * 是否是敏感词结尾
         * true 关键词的终结 ； false 继续
         */
        private boolean end = false;

        /**
         * 当前节点下所有的子节点 ab ac ad。  a节点下有 b c d三个子节点，b c d为key
         * key下一个字符，value是对应的节点
         */
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        /**
         * 将敏感词加入树的子节点
         */
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        /**
         * 获取当前下个节点
         */
        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        //关键词结尾
        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }
    }


    /**
     * 根节点，为空
     */
    private TrieNode rootNode = new TrieNode();


    /**
     * 判断是否是一个符号
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }


    /**
     * 过滤敏感词
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0; // 回滚数
        int position = 0; // 当前比较的位置

        while (position < text.length()) {
            char c = text.charAt(position);
            // 空格直接跳过
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            // 当前位置的匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词， 从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }

        result.append(text.substring(begin));

        return result.toString();
    }

    /**
     * 增加关键字  abc
     * 
     * @param lineTxt
     */
    private void addWord(String lineTxt) {
        //当前指针指向根节点
    	TrieNode tempNode = rootNode;
        // 循环遍历每个字符
        for (int i = 0; i < lineTxt.length(); ++i) {
            //当前字符
        	Character c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            //当前节点下面是否有当前字符a的节点
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) { // 当前结点下面没有a子节点
                node = new TrieNode();//新建一个节点
                tempNode.addSubNode(c, node);//放到当前节点的下面
            }
            //有，当前指针指向下一个节点
            tempNode = node;
            //如果是最后一个字符，
            if (i == lineTxt.length() - 1) {
                // 关键词结束， 设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }


    /**
     * 读取敏感词文件
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

/*    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("好色");
        System.out.print(s.filter("你好X色**情XX"));
    }*/
}
