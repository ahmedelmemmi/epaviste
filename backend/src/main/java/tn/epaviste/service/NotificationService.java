package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.response.NotificationResponse;
import tn.epaviste.entity.Notification;
import tn.epaviste.entity.User;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.exception.UnauthorizedException;
import tn.epaviste.repository.NotificationRepository;
import tn.epaviste.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(User user, String type, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .build();
        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> getUserNotifications(String email, Pageable pageable) {
        User user = getUserByEmail(email);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public NotificationResponse markAsRead(Long id, String email) {
        User user = getUserByEmail(email);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot mark another user's notification as read");
        }
        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = getUserByEmail(email);
        List<Notification> unread = notificationRepository.findByUserAndReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
