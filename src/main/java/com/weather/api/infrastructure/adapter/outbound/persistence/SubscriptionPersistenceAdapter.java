package com.weather.api.infrastructure.adapter.outbound.persistence;

import com.weather.api.domain.model.Subscription;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionPersistenceAdapter {

    private final SubscriptionJpaRepository repository;

    public SubscriptionPersistenceAdapter(SubscriptionJpaRepository repository) {
        this.repository = repository;
    }

    public Subscription save(Subscription subscription) {
        return toDomain(repository.save(toEntity(subscription)));
    }

    public List<Subscription> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    public List<Subscription> findByChatId(String chatId) {
        return repository.findByChatId(chatId).stream().map(this::toDomain).toList();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private SubscriptionEntity toEntity(Subscription s) {
        SubscriptionEntity e = new SubscriptionEntity();
        e.setId(s.getId());
        e.setChatId(s.getChatId());
        e.setCity(s.getCity());
        e.setUf(s.getUf());
        return e;
    }

    private Subscription toDomain(SubscriptionEntity e) {
        Subscription s = new Subscription();
        s.setId(e.getId());
        s.setChatId(e.getChatId());
        s.setCity(e.getCity());
        s.setUf(e.getUf());
        return s;
    }
}
