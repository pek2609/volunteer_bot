package com.bots.volonteerbot.persistence.repository;

import com.bots.volonteerbot.persistence.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends BaseRepository<Order> {

    @Query("select o from Order o where o.botUser.chatId=:id order by o.created desc")
    List<Order> findByBotUserChatId(@Param("id") Long chatId);

    @Query(value = "select o from Order o where o.created = (select min (ord.created) from Order ord)")
    Optional<Order> getMinOrderByCreated();

    boolean existsByBotUserChatId(Long chatId);
}
