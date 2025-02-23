package com.ai.multi_agent.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USER_PREFERENCES")
public record UserPreferences(
        @Id
        Integer id,
        String category
) { }
