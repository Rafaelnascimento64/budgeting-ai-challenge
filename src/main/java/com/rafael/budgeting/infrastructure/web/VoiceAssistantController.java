package com.rafael.budgeting.infrastructure.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
public class VoiceAssistantController {

    private final ChatClient chatClient;
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public VoiceAssistantController(ChatClient chatClient, OpenAiAudioTranscriptionModel transcriptionModel) {
        this.chatClient = chatClient;
        this.transcriptionModel = transcriptionModel;
    }

    @PostMapping(value = "/voice-command", consumes = "multipart/form-data")
    public Map<String, String> handleVoiceCommand(@RequestParam("audio") MultipartFile audio) throws IOException {
        String transcript = transcribe(audio);
        String response = chatClient.prompt()
                .user(transcript)
                .call()
                .content();

        return Map.of(
                "transcript", transcript,
                "response", response
        );
    }

    @PostMapping("/text-command")
    public Map<String, String> handleTextCommand(@RequestParam("message") String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return Map.of("response", response);
    }

    private String transcribe(MultipartFile audio) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(audio.getBytes()) {
            @Override
            public String getFilename() {
                return audio.getOriginalFilename() != null ? audio.getOriginalFilename() : "audio.mp3";
            }
        };

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
            .withLanguage("pt")
            .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(resource, options);
        return transcriptionModel.call(prompt).getResult().getOutput();
    }
}
