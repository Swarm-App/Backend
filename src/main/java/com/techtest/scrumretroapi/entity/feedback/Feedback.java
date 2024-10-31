package com.techtest.scrumretroapi.entity.feedback;

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
public class Feedback implements Serializable {
    @JsonProperty
    @Column(name = "item_id")
    private int item;

    @JsonProperty
    @Embedded
    private FeedbackItem itemBody;
}
