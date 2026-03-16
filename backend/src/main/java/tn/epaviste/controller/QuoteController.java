package tn.epaviste.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.QuoteRequest;
import tn.epaviste.dto.response.QuoteResponse;
import tn.epaviste.service.QuoteService;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> submitQuote(@Valid @RequestBody QuoteRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(quoteService.submitQuote(request, authentication.getName()));
    }

    @GetMapping("/rfq/{rfqId}")
    public ResponseEntity<List<QuoteResponse>> getQuotesForRFQ(@PathVariable Long rfqId,
                                                                 Authentication authentication) {
        return ResponseEntity.ok(quoteService.getQuotesForRFQ(rfqId, authentication.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<QuoteResponse>> getMyQuotes(Authentication authentication) {
        return ResponseEntity.ok(quoteService.getMyQuotes(authentication.getName()));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<QuoteResponse> acceptQuote(@PathVariable Long id,
                                                      Authentication authentication) {
        return ResponseEntity.ok(quoteService.acceptQuote(id, authentication.getName()));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<QuoteResponse> rejectQuote(@PathVariable Long id,
                                                      Authentication authentication) {
        return ResponseEntity.ok(quoteService.rejectQuote(id, authentication.getName()));
    }
}
