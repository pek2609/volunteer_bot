package com.bots.volonteerbot.persistence.repository;

import com.bots.volonteerbot.persistence.entity.BotUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotUserRepository extends BaseRepository<BotUser> {

    Optional<BotUser> findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
