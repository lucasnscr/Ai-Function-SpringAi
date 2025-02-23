package com.ai.multi_agent.repository;

import com.ai.multi_agent.entity.UserPreferences;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends ListCrudRepository<UserPreferences, Integer> {
}
