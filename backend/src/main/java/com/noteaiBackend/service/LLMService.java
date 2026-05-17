package com.noteaiBackend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteaiBackend.dto.LLMRequest;
import com.noteaiBackend.dto.LLMResponse;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class LLMService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private static final int MAX_CHUNK_LENGTH = 4000; // 假设大模型单次处理最大长度为4000字符

    private OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LLMService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
                .readTimeout(120, TimeUnit.SECONDS)    // 读取超时，AI生成需要时间
                .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时
                .callTimeout(180, TimeUnit.SECONDS)    // 整个调用超时
                .build();
    }

    /**
     * 分段总结逻辑
     * 如果字段过长超过大语言模型能处理的长度，则分段，
     * 然后让AI对这几个段落进行总结，最后把所有的总结合并成一整段，让AI进行最后的总结。
     */
    public String getSegmentedSummary(String content) throws IOException {
        if (content == null || content.isEmpty()) {
            return "";
        }

        if (content.length() <= MAX_CHUNK_LENGTH) {
            return getCompletion("请对以下内容进行简明扼要的总结：\n\n" + content);
        }

        // 1. 分段
        List<String> chunks = splitContent(content, MAX_CHUNK_LENGTH);
        List<String> chunkSummaries = new ArrayList<>();

        // 2. 对每个段落进行总结
        for (String chunk : chunks) {
            String summary = getCompletion("请对以下内容段落进行总结：\n\n" + chunk);
            chunkSummaries.add(summary);
        }

        // 3. 合并总结并进行最终总结
        String combinedSummary = String.join("\n\n", chunkSummaries);
        return getCompletion("以下是长文本各段落的总结，请将它们整合成一段连贯、全面的最终总结：\n\n" + combinedSummary);
    }

    /**
     * 简单的RAG框架逻辑
     * 找到笔记中和问答有关联的字段进行提问。
     */
    public String getRAGCompletion(String content, String question) throws IOException {
        if (content == null || content.isEmpty()) {
            return getCompletion(question);
        }

        if (content.length() <= MAX_CHUNK_LENGTH) {
            return getCompletion("基于以下内容回答问题：\n\n内容：" + content + "\n\n问题：" + question);
        }

        // 简单的关键词匹配或语义检索（这里实现简单的关键词匹配分片）
        List<String> relevantChunks = findRelevantChunks(content, question);
        String context = String.join("\n\n", relevantChunks);

        return getCompletion("基于以下相关的笔记片段回答问题：\n\n内容片段：\n" + context + "\n\n问题：" + question);
    }

    /**
     * 根据提示类型构建完整的prompt字符串
     * 用于自定义提示词调用（如润色笔记、分析学情等）
     *
     * @param promptType 提示类型：polish_note（润色笔记）/ analyze_student_performance（学情分析）
     * @param dataId     相关数据ID（如笔记ID或用户ID）
     * @param noteService 用于获取笔记数据（可通过构造器注入或参数传入）
     * @return 构建好的prompt字符串，如果类型无效返回null
     */
    public String buildPrompt(String promptType, Long dataId, NoteService noteService) {
        switch (promptType) {
            case "polish_note":
                return noteService.findById(dataId.intValue())
                        .map(note -> "请帮我润色以下笔记内容，使其更清晰、更专业:\n\n" + note.getContent())
                        .orElse(null);
            case "analyze_student_performance":
                // TODO: 实现学情分析逻辑，例如从数据库获取某个学生的所有笔记并进行分析
                return "请分析一下 ID 为 " + dataId + " 的学生的学习情况和知识掌握程度。";
            default:
                return null;
        }
    }

    private List<String> splitContent(String content, int maxLength) {
        List<String> chunks = new ArrayList<>();
        int length = content.length();
        for (int i = 0; i < length; i += maxLength) {
            chunks.add(content.substring(i, Math.min(length, i + maxLength)));
        }
        return chunks;
    }

    private List<String> findRelevantChunks(String content, String question) {
        // 这里实现一个极其简单的RAG：按句或按段分割，然后计算关键词重合度
        String[] paragraphs = content.split("\n\n");
        List<String> chunks = new ArrayList<>();
        
        // 提取问题中的关键词（简单处理：去重后的词）
        String[] keywords = question.split("\\s+|[，。？！、]");
        
        // 计算每个段落的相关性分
        List<ParagraphScore> scores = new ArrayList<>();
        for (String p : paragraphs) {
            int score = 0;
            for (String kw : keywords) {
                if (kw.length() > 1 && p.contains(kw)) {
                    score++;
                }
            }
            if (score > 0) {
                scores.add(new ParagraphScore(p, score));
            }
        }
        
        // 按分数排序，取前N个
        scores.sort((a, b) -> b.score - a.score);
        for (int i = 0; i < Math.min(scores.size(), 3); i++) {
            chunks.add(scores.get(i).text);
        }
        
        // 如果没找到相关的，就取前几个段落
        if (chunks.isEmpty()) {
            for (int i = 0; i < Math.min(paragraphs.length, 3); i++) {
                chunks.add(paragraphs[i]);
            }
        }
        
        return chunks;
    }

    private static class ParagraphScore {
        String text;
        int score;
        ParagraphScore(String text, int score) {
            this.text = text;
            this.score = score;
        }
    }

    public String getCompletion(String userContent) throws IOException {
        LLMRequest.Message userMessage = new LLMRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent(userContent);

        LLMRequest llmRequest = new LLMRequest();
        llmRequest.setModel("deepseek-chat");
        llmRequest.setMessages(Collections.singletonList(userMessage));

        String jsonBody = objectMapper.writeValueAsString(llmRequest);

        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            LLMResponse llmResponse = objectMapper.readValue(response.body().string(), LLMResponse.class);
            return llmResponse.getChoices().get(0).getMessage().getContent();
        }
    }
}
