package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.request.RFQRequest;
import tn.epaviste.dto.response.RFQResponse;
import tn.epaviste.entity.RFQ;
import tn.epaviste.entity.RFQImage;
import tn.epaviste.entity.User;
import tn.epaviste.enums.RFQStatus;
import tn.epaviste.enums.UserRole;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.exception.UnauthorizedException;
import tn.epaviste.repository.QuoteRepository;
import tn.epaviste.repository.RFQRepository;
import tn.epaviste.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RFQService {

    private final RFQRepository rfqRepository;
    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;
    private final NotificationService notificationService;

    @Transactional
    public RFQResponse createRFQ(RFQRequest request, String email) {
        User buyer = getUserByEmail(email);
        if (buyer.getRole() != UserRole.BUYER) {
            throw new UnauthorizedException("Only buyers can create RFQs");
        }

        RFQ rfq = RFQ.builder()
                .buyer(buyer)
                .carBrand(request.getCarBrand())
                .carModel(request.getCarModel())
                .carYear(request.getCarYear())
                .vin(request.getVin())
                .partName(request.getPartName())
                .partCategory(request.getPartCategory())
                .preferredCondition(request.getPreferredCondition())
                .description(request.getDescription())
                .location(request.getLocation())
                .build();

        if (request.getImageUrls() != null) {
            List<RFQImage> images = request.getImageUrls().stream()
                    .map(url -> RFQImage.builder().rfq(rfq).imageUrl(url).build())
                    .collect(Collectors.toList());
            rfq.setImages(images);
        }

        RFQ saved = rfqRepository.save(rfq);

        List<User> sellers = userRepository.findByRole(UserRole.SELLER);
        sellers.forEach(seller -> notificationService.createNotification(
                seller, "NEW_RFQ",
                "New RFQ posted: " + saved.getPartName() + " for " + saved.getCarBrand() + " " + saved.getCarModel()
        ));

        return toResponse(saved);
    }

    public Page<RFQResponse> listOpenRFQsWithFilters(String carBrand, String carModel, String partCategory, String location, Pageable pageable) {
        return rfqRepository.findByStatusAndFilters(RFQStatus.OPEN, carBrand, carModel, partCategory, location, pageable)
                .map(this::toResponse);
    }

    public Page<RFQResponse> listOpenRFQs(Pageable pageable) {
        return rfqRepository.findByStatus(RFQStatus.OPEN, pageable).map(this::toResponse);
    }

    public RFQResponse getRFQById(Long id) {
        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));
        return toResponse(rfq);
    }

    public List<RFQResponse> getMyRFQs(String email) {
        User buyer = getUserByEmail(email);
        return rfqRepository.findByBuyerOrderByCreatedAtDesc(buyer).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelRFQ(Long id, String email) {
        User buyer = getUserByEmail(email);
        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));
        if (!rfq.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedException("You can only cancel your own RFQs");
        }
        rfq.setStatus(RFQStatus.CANCELLED);
        rfqRepository.save(rfq);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    RFQResponse toResponse(RFQ rfq) {
        List<String> imageUrls = rfq.getImages() == null ? new ArrayList<>() :
                rfq.getImages().stream().map(RFQImage::getImageUrl).collect(Collectors.toList());
        long quoteCount = quoteRepository.countByRfq(rfq);
        return RFQResponse.builder()
                .id(rfq.getId())
                .buyerId(rfq.getBuyer().getId())
                .buyerName(rfq.getBuyer().getName())
                .carBrand(rfq.getCarBrand())
                .carModel(rfq.getCarModel())
                .carYear(rfq.getCarYear())
                .vin(rfq.getVin())
                .partName(rfq.getPartName())
                .partCategory(rfq.getPartCategory())
                .preferredCondition(rfq.getPreferredCondition())
                .description(rfq.getDescription())
                .location(rfq.getLocation())
                .status(rfq.getStatus())
                .createdAt(rfq.getCreatedAt())
                .images(imageUrls)
                .quoteCount(quoteCount)
                .build();
    }
}
