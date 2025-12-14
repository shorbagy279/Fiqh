package com.fiqhmaster.service;

import com.fiqhmaster.dto.LeaderboardEntryDTO;
import com.fiqhmaster.entity.User;
import com.fiqhmaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final UserRepository userRepository;
    
    public List<LeaderboardEntryDTO> getGlobalLeaderboard(int limit) {
        List<User> users = userRepository.findTopUsers();
        return users.stream()
            .limit(limit)
            .map(this::toLeaderboardEntry)
            .collect(Collectors.toList());
    }
    
    public List<LeaderboardEntryDTO> getCategoryLeaderboard(Long categoryId, int limit) {
        return getGlobalLeaderboard(limit);
    }
    
    public List<LeaderboardEntryDTO> getStreakLeaderboard(int limit) {
        List<User> users = userRepository.findUsersByStreak();
        return users.stream()
            .limit(limit)
            .map(this::toLeaderboardEntry)
            .collect(Collectors.toList());
    }
    
    private LeaderboardEntryDTO toLeaderboardEntry(User user) {
        return new LeaderboardEntryDTO(
            user.getId(),
            user.getFullName(),
            user.getTotalQuizzes(),
            user.getTotalCorrectAnswers(),
            user.getCurrentStreak(),
            user.getCurrentRank(),
            0
        );
    }
}