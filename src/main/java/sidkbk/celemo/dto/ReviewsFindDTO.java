package sidkbk.celemo.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewsFindDTO {
    @NotBlank
    public String reviewId;


    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
}
