package com.techtest.scrumretroapi.entity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Serializable {
    @JsonProperty
    @Column(name = "item_id")
    private int item;

    @JsonProperty
    @Embedded
    private TaskItem itemBody;
}
