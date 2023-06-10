package com.example.onlineshop.service;

import com.example.onlineshop.entity.HistoryEntity;
import com.example.onlineshop.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    public void addHistory(HistoryEntity history) {
        historyRepository.save(history);
    }

    public List<HistoryEntity> getHistoryByUserId(Long id) {
        return historyRepository.findByUserId(id);
    }
}
