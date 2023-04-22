package bg.fmi.services;

import bg.fmi.models.SecretMessage;
import bg.fmi.models.User;
import bg.fmi.payload.request.SecretMessageRequest;
import bg.fmi.payload.response.SecretMessageResponse;
import bg.fmi.payload.response.UnreadSecretMessageResponse;
import bg.fmi.payload.response.UnreadSecretMessagesCountResponse;
import bg.fmi.repositories.SecretMessageRepository;
import bg.fmi.repositories.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SecretMessageService {

    private static Logger log = Logger.getLogger(SecretMessageService.class.getName());

    @Autowired
    private SecretMessageRepository secretMessageRepository;

    @Autowired
    private UserRepository userRepository;

    public String create(SecretMessageRequest secretMessageRequest) {

        boolean isPublic = secretMessageRequest.getToUsers() == null || secretMessageRequest.getToUsers().isEmpty();

        List<User> dbUsers = isPublic ? null : userRepository.findByUsernameIn(secretMessageRequest.getToUsers());

        SecretMessage secretMessage = SecretMessage.builder()
                .uuid(UUID.randomUUID().toString())
                .initDate(LocalDateTime.now())
                .expireDays(secretMessageRequest.getExpireDays())
                .isOneTime(secretMessageRequest.getIsOneTime())
                .header(secretMessageRequest.getHeader())
                .content(secretMessageRequest.getContent())
                .toUsers(dbUsers)
                .isPublic(isPublic)
                .build();

        secretMessageRepository.save(secretMessage);

        log.info("A new secret message was created");

        return secretMessage.getUuid();
    }

    public SecretMessageResponse getSecret(String uuid, User user) {
        SecretMessageResponse secretMessageResponse = SecretMessageResponse.builder().build();

        SecretMessage secretMessage = secretMessageRepository.findByUuidAndUser(uuid, user).orElse(null);

        if (secretMessage != null
                && secretMessage.getInitDate().plusDays(secretMessage.getExpireDays()).isAfter(LocalDateTime.now())) {
            secretMessageResponse.setActive(Boolean.TRUE);
            secretMessageResponse.setHeader(secretMessage.getHeader());
            secretMessageResponse.setContent(secretMessage.getContent());

            List<User> hiddenFor = secretMessage.getHiddenFor();
            if (secretMessage.getIsOneTime() && !hiddenFor.contains(user)) {
                hiddenFor.add(user);
                secretMessage.setHiddenFor(hiddenFor);
                secretMessageRepository.save(secretMessage);
            }

            List<User> readBy = secretMessage.getReadBy();
            if (!readBy.contains(user)) {
                readBy.add(user);
                secretMessage.setReadBy(readBy);
                secretMessageRepository.save(secretMessage);
            }
        }

        log.info(String.format("Secret message was fetched by %s", user.getUsername()));

        return secretMessageResponse;
    }

    public List<UnreadSecretMessageResponse> getUnreadSecrets(User user) {
        List<UnreadSecretMessageResponse> unreadSecretMessageResponses = new ArrayList<>();

        List<SecretMessage> secretMessages = secretMessageRepository.findByUser(user);

        if (secretMessages != null) {
            secretMessages.forEach(secretMessage -> {
                if (secretMessage.getInitDate().plusDays(secretMessage.getExpireDays()).isAfter(LocalDateTime.now())
                && !secretMessage.getCreatedBy().equals(user.getUsername())) {
                    unreadSecretMessageResponses.add(UnreadSecretMessageResponse.builder()
                            .uuid(secretMessage.getUuid())
                            .header(secretMessage.getHeader())
                            .isOneTime(secretMessage.getIsOneTime())
                            .expireDate(secretMessage.getInitDate().plusDays(secretMessage.getExpireDays()))
                            .isNew(!secretMessage.getReadBy().contains(user))
                            .build());
                }
            });
        }

        log.info(String.format("User %s fetch unread messages", user.getUsername()));

        return unreadSecretMessageResponses;
    }

    public UnreadSecretMessagesCountResponse getUnreadSecretsCount(User user) {
        List<SecretMessage> secretMessages = secretMessageRepository.getByUserNotRead(user);

        int count = secretMessages.size();

        if (secretMessages != null) {
            for (SecretMessage secretMessage : secretMessages) {
                if (!(secretMessage.getInitDate().plusDays(secretMessage.getExpireDays()).isAfter(LocalDateTime.now())
                        && !secretMessage.getCreatedBy().equals(user.getUsername()))) {
                    count--;
                }
            }
        }

        log.info(String.format("User %s fetch unread messages", user.getUsername()));

        return UnreadSecretMessagesCountResponse.builder().count(count).build();
    }
}
