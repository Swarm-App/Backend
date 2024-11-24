package com.techtest.scrumretroapi.entity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItem {
    @JsonProperty
    private String name;

    @JsonProperty
    private String body;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    private TaskStatus taskstatus;
}
