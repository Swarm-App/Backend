package com.techtest.scrumretroapi.entity.feedback;

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
public class FeedbackItem {
    @JsonProperty
    private String name;

    @JsonProperty
    private String body;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    private FeedbackType feedbackType;
}
