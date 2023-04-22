package bg.fmi.repositories;

import bg.fmi.models.SecretMessage;
import bg.fmi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SecretMessageRepository extends JpaRepository<SecretMessage, Long> {

    @Query("SELECT sm FROM SecretMessage sm WHERE " +
            "(sm.uuid = :uuid " +
            " and (sm.isPublic = true or :user member sm.toUsers) " +
            "and (sm.isOneTime = false or (sm.isOneTime = true and :user not member sm.hiddenFor )))")
    Optional<SecretMessage> findByUuidAndUser(
            @Param("uuid") String uuid,
            @Param("user") User user);

    @Query("SELECT sm FROM SecretMessage sm WHERE " +
            "(sm.isPublic = true or :user member sm.toUsers) " +
            "and :user not member sm.hiddenFor")
    List<SecretMessage> findByUser(
            @Param("user") User user);

    @Query("SELECT sm FROM SecretMessage sm WHERE " +
            "(sm.isPublic = true or :user member sm.toUsers) " +
            "and :user not member sm.readBy")
    List<SecretMessage> getByUserNotRead(
            @Param("user") User user);
}