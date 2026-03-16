package tn.epaviste.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.RFQRequest;
import tn.epaviste.dto.response.RFQResponse;
import tn.epaviste.service.RFQService;

import java.util.List;

@RestController
@RequestMapping("/api/rfqs")
@RequiredArgsConstructor
public class RFQController {

    private final RFQService rfqService;

    @PostMapping
    public ResponseEntity<RFQResponse> createRFQ(@Valid @RequestBody RFQRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(rfqService.createRFQ(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<Page<RFQResponse>> listOpenRFQs(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(rfqService.listOpenRFQs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RFQResponse> getRFQ(@PathVariable Long id) {
        return ResponseEntity.ok(rfqService.getRFQById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<RFQResponse>> getMyRFQs(Authentication authentication) {
        return ResponseEntity.ok(rfqService.getMyRFQs(authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRFQ(@PathVariable Long id, Authentication authentication) {
        rfqService.cancelRFQ(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
