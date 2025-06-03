package com.capstone.favicon.dataset.application.service;

import java.util.List;
import java.util.Map;

public interface OpenAIService {
    String chat(List<Map<String, String>> message);
}
