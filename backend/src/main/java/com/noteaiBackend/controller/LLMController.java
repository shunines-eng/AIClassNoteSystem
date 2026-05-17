package com.noteaiBackend.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noteaiBackend.dto.AskNoteRequest;
import com.noteaiBackend.dto.CustomPromptRequest;
import com.noteaiBackend.service.LLMService;
import com.noteaiBackend.service.NoteService;

import lombok.RequiredArgsConstructor;

/**
 * 大语言模型（LLM）交互控制器
 * 处理与 DeepSeek AI 的各类交互请求
 * 
 * 输入/输出说明：
 * - POST /api/llm/ask-note
 *   输入：{ "noteContent": "笔记内容", "userQuestion": "用户问题" }
 *   输出：AI基于笔记内容的回答文本
 *   功能：基于笔记内容进行问答（RAG模式）。如果笔记过长会先检索相关段落再提问。
 * 
 * - POST /api/llm/summarize-note
 *   输入：纯文本（笔记内容）
 *   输出：AI生成的总结文本
 *   功能：对长文本笔记进行分段总结。先分段→每段AI总结→合并→最终总结。
 * 
 * - POST /api/llm/custom-prompt
 *   输入：{ "promptType": "polish_note", "dataId": 1 }
 *   输出：AI处理结果文本
 *   功能：根据提示类型调用AI：
 *         - polish_note: 润色笔记（dataId为笔记ID）
 *         - analyze_student_performance: 分析学情（dataId为用户ID）
 * 
 * - GET /api/llm/test?prompt=你好
 *   输入：prompt (请求参数，直接给AI的提示词)
 *   输出：AI回答文本
 *   功能：直接测试AI接口连通性
 */
@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LLMController {

    private final LLMService llmService;
    private final NoteService noteService;

    @PostMapping("/ask-note")
    public ResponseEntity<String> askNote(@RequestBody AskNoteRequest request) {
        try {
            String response = llmService.getRAGCompletion(request.getNoteContent(), request.getUserQuestion());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("调用 LLM 服务失败: " + e.getMessage());
        }
    }

    @PostMapping("/summarize-note")
    public ResponseEntity<String> summarizeNote(@RequestBody String content) {
        try {
            String response = llmService.getSegmentedSummary(content);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("调用 LLM 服务失败: " + e.getMessage());
        }
    }

    @PostMapping("/custom-prompt")
    public ResponseEntity<String> customPrompt(@RequestBody CustomPromptRequest request) {
        try {
            String prompt = llmService.buildPrompt(request.getPromptType(), request.getDataId(), noteService);
            if (prompt == null) {
                return ResponseEntity.badRequest().body("无效的提示类型");
            }
            String response = llmService.getCompletion(prompt);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("调用 LLM 服务失败: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testLLM(@RequestParam String prompt) {
        try {
            String response = llmService.getCompletion(prompt);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("调用 LLM 服务失败: " + e.getMessage());
        }
    }
}
