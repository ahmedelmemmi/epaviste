package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.request.QuoteRequest;
import tn.epaviste.dto.request.UpdateQuoteRequest;
import tn.epaviste.dto.response.QuoteResponse;
import tn.epaviste.entity.*;
import tn.epaviste.enums.*;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.exception.UnauthorizedException;
import tn.epaviste.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuoteService {

    @Value("${app.commission.rate:0.10}")
    private double commissionRate;

    private final QuoteRepository quoteRepository;
    private final RFQRepository rfqRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final NotificationService notificationService;

    @Transactional
    public QuoteResponse submitQuote(QuoteRequest request, String email) {
        User seller = getUserByEmail(email);
        if (seller.getRole() != UserRole.SELLER) {
            throw new UnauthorizedException("Only sellers can submit quotes");
        }
        RFQ rfq = rfqRepository.findById(request.getRfqId())
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", request.getRfqId()));
        if (rfq.getStatus() != RFQStatus.OPEN) {
            throw new IllegalArgumentException("This RFQ is no longer open for quotes");
        }

        Quote quote = Quote.builder()
                .rfq(rfq)
                .seller(seller)
                .price(request.getPrice())
                .condition(request.getCondition())
                .deliveryTime(request.getDeliveryTime())
                .shippingMethod(request.getShippingMethod())
                .message(request.getMessage())
                .build();
        quote = quoteRepository.save(quote);

        notificationService.createNotification(
                rfq.getBuyer(), "NEW_QUOTE",
                "You received a new quote for your RFQ: " + rfq.getPartName()
        );

        return toResponse(quote);
    }

    public List<QuoteResponse> getQuotesForRFQ(Long rfqId, String email) {
        User buyer = getUserByEmail(email);
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", rfqId));
        if (!rfq.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedException("You can only view quotes for your own RFQs");
        }
        return quoteRepository.findByRfqOrderByCreatedAtAsc(rfq).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<QuoteResponse> getMyQuotes(String email) {
        User seller = getUserByEmail(email);
        return quoteRepository.findBySellerOrderByCreatedAtDesc(seller).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuoteResponse acceptQuote(Long quoteId, String email) {
        User buyer = getUserByEmail(email);
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", quoteId));

        RFQ rfq = quote.getRfq();
        if (!rfq.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedException("You can only accept quotes for your own RFQs");
        }

        // Reject all other quotes for this RFQ
        List<Quote> otherQuotes = quoteRepository.findByRfq(rfq).stream()
                .filter(q -> !q.getId().equals(quoteId))
                .collect(Collectors.toList());
        otherQuotes.forEach(q -> q.setStatus(QuoteStatus.REJECTED));
        quoteRepository.saveAll(otherQuotes);

        // Accept this quote
        quote.setStatus(QuoteStatus.ACCEPTED);
        quoteRepository.save(quote);

        // Close the RFQ
        rfq.setStatus(RFQStatus.CLOSED);
        rfqRepository.save(rfq);

        // Create order with configurable commission
        BigDecimal commission = quote.getPrice().multiply(BigDecimal.valueOf(commissionRate));
        Order order = Order.builder()
                .buyer(buyer)
                .seller(quote.getSeller())
                .quote(quote)
                .totalPrice(quote.getPrice())
                .commissionAmount(commission)
                .build();
        orderRepository.save(order);

        // Notify both parties
        notificationService.createNotification(
                buyer, "QUOTE_ACCEPTED",
                "You accepted a quote. Order created for: " + rfq.getPartName()
        );
        notificationService.createNotification(
                quote.getSeller(), "QUOTE_ACCEPTED",
                "Your quote was accepted! An order has been created for: " + rfq.getPartName()
        );

        return toResponse(quote);
    }

    @Transactional
    public QuoteResponse rejectQuote(Long quoteId, String email) {
        User buyer = getUserByEmail(email);
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", quoteId));

        if (!quote.getRfq().getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedException("You can only reject quotes for your own RFQs");
        }
        quote.setStatus(QuoteStatus.REJECTED);
        quoteRepository.save(quote);

        notificationService.createNotification(
                quote.getSeller(), "QUOTE_REJECTED",
                "Your quote was rejected for: " + quote.getRfq().getPartName()
        );

        return toResponse(quote);
    }

    @Transactional
    public QuoteResponse updateQuote(Long quoteId, UpdateQuoteRequest request, String email) {
        User seller = getUserByEmail(email);
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", quoteId));
        if (!quote.getSeller().getId().equals(seller.getId())) {
            throw new UnauthorizedException("You can only update your own quotes");
        }
        if (quote.getStatus() != QuoteStatus.PENDING) {
            throw new IllegalArgumentException("Only pending quotes can be updated");
        }
        if (request.getPrice() != null) quote.setPrice(request.getPrice());
        if (request.getCondition() != null) quote.setCondition(request.getCondition());
        if (request.getDeliveryTime() != null) quote.setDeliveryTime(request.getDeliveryTime());
        if (request.getShippingMethod() != null) quote.setShippingMethod(request.getShippingMethod());
        if (request.getMessage() != null) quote.setMessage(request.getMessage());
        return toResponse(quoteRepository.save(quote));
    }

    @Transactional
    public void withdrawQuote(Long quoteId, String email) {
        User seller = getUserByEmail(email);
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", quoteId));
        if (!quote.getSeller().getId().equals(seller.getId())) {
            throw new UnauthorizedException("You can only withdraw your own quotes");
        }
        if (quote.getStatus() != QuoteStatus.PENDING) {
            throw new IllegalArgumentException("Only pending quotes can be withdrawn");
        }
        quoteRepository.delete(quote);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    QuoteResponse toResponse(Quote quote) {
        String sellerCompany = sellerProfileRepository.findByUser(quote.getSeller())
                .map(SellerProfile::getCompanyName)
                .orElse(null);
        return QuoteResponse.builder()
                .id(quote.getId())
                .rfqId(quote.getRfq().getId())
                .sellerId(quote.getSeller().getId())
                .sellerName(quote.getSeller().getName())
                .sellerCompany(sellerCompany)
                .price(quote.getPrice())
                .condition(quote.getCondition())
                .deliveryTime(quote.getDeliveryTime())
                .shippingMethod(quote.getShippingMethod())
                .message(quote.getMessage())
                .status(quote.getStatus())
                .createdAt(quote.getCreatedAt())
                .build();
    }
}
