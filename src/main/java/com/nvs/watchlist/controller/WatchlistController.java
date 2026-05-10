package com.nvs.watchlist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nvs.watchlist.dto.request.ReorderRequest;
import com.nvs.watchlist.dto.request.WatchlistRequest;
import com.nvs.watchlist.dto.response.WatchlistResponse;
import com.nvs.watchlist.entity.Watchlist;
import com.nvs.watchlist.service.WatchlistService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/watchlist")
@SecurityRequirement(name = "bearerAuth")
public class WatchlistController {
    private final WatchlistService service;

    public WatchlistController(WatchlistService service) {
        this.service = service;
    }

    @PostMapping
    public WatchlistResponse addStock(@RequestBody WatchlistRequest request) {
        Watchlist watchlist = service.addStock(request);
        return service.toResponse(watchlist);
    }

    @GetMapping
    public Page<WatchlistResponse> getWatchlist(Pageable pageable) {
        return service.getUserWatchlist(pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable Long id) {
        service.deleteStock(id);
    }

    @PostMapping("/reorder")
    public void reorderWatchlist(@RequestBody ReorderRequest request) {
        service.reorderWatchlist(request);
    }

}
