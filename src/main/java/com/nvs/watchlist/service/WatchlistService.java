package com.nvs.watchlist.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nvs.watchlist.dto.request.ReorderRequest;
import com.nvs.watchlist.dto.request.WatchlistRequest;
import com.nvs.watchlist.dto.response.WatchlistResponse;
import com.nvs.watchlist.entity.User;
import com.nvs.watchlist.entity.Watchlist;
import com.nvs.watchlist.repository.UserRepository;
import com.nvs.watchlist.repository.WatchlistRepository;

@Service
public class WatchlistService {

    private final WatchlistRepository repo;
    private final UserRepository userRepository;

    public WatchlistService(WatchlistRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public Watchlist addStock(WatchlistRequest request) {
        String email = getLoggedInUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long count = repo.countByUserId(user.getId());

        Watchlist watchlist = new Watchlist();
        watchlist.setStockSymbol(request.stockSymbol);
        watchlist.setUser(user);
        watchlist.setPosition((int) count);
        return repo.save(watchlist);
    }

    public Page<WatchlistResponse> getUserWatchlist(@ParameterObject Pageable pageable) {

        return repo.findByUserId(getUserId(), pageable).map(this::toResponse);
    }

    public void deleteStock(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void reorderWatchlist(ReorderRequest request) {
        String email = getLoggedInUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Watchlist> list = repo.findByUserIdOrderByPosition(user.getId());

        Watchlist movedItem = list.stream()
                .filter(w -> w.getId().equals(request.id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));

        int oldPosition = movedItem.getPosition();
        int newPosition = request.newPosition;

        if (oldPosition == newPosition) {
            return; // No change needed
        }

        for (Watchlist w : list) {
            if (oldPosition < newPosition) {
                // Moving down
                if (w.getPosition() > oldPosition && w.getPosition() <= newPosition) {
                    w.setPosition(w.getPosition() - 1);
                }
            } else {
                // Moving up
                if (w.getPosition() < oldPosition && w.getPosition() >= newPosition) {
                    w.setPosition(w.getPosition() + 1);
                }
            }
        }

        movedItem.setPosition(newPosition);
        repo.saveAll(list);
    }

    private String getLoggedInUserEmail() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Long getUserId() {
        String email = getLoggedInUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    public WatchlistResponse toResponse(Watchlist w) {
        WatchlistResponse res = new WatchlistResponse();
        res.id = w.getId();
        res.stockSymbol = w.getStockSymbol();
        res.position = w.getPosition();
        return res;
    }
}