package com.nowcoder.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;

import com.nowcoder.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhan on 2018/9/26.
 */
@Service
public class SearchService {
    //参考官方文档写
	private static final String SOLR_URL = "http://localhost:8983/solr/wenda";
    private HttpSolrClient client = new HttpSolrClient(SOLR_URL);
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";

    /**
     * 搜索问题
     * @param keyword 搜索关键词
     * @param offset
     * @param count
     * @param hlPre  高亮前缀
     * @param hlPos  高亮后缀
     * @return
     * @throws Exception
     */
    public List<Question> searchQuestion(String keyword, int offset, int count,
                                         String hlPre, String hlPos) throws Exception {
        List<Question> questionList = new ArrayList<>();
        //参考官方文档
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);//高亮
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD);
        //查询结果
        QueryResponse response = client.query(query);
        //解析查询的高亮文本
        for (Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()) {
            Question q = new Question();
            //每个map的 id
            q.setId(Integer.parseInt(entry.getKey()));
            //是否包含内容字段
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)) {
            	//根据字段获取内容
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (contentList.size() > 0) {
                    q.setContent(contentList.get(0));
                }
            }
            //是否包含标题字段
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)) {
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (titleList.size() > 0) {
                    q.setTitle(titleList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }

    /**
     * 发表一个新题目时，需要索引question
     * @param qid
     * @param title
     * @param content
     * @return
     * @throws Exception
     */
    public boolean indexQuestion(int qid, String title, String content) throws Exception {
        //加入文档
    	SolrInputDocument doc =  new SolrInputDocument();
        doc.setField("id", qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = client.add(doc, 1000);
        return response != null && response.getStatus() == 0;
    }

}
