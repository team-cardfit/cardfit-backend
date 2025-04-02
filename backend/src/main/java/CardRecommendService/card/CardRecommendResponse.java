package CardRecommendService.card;

import CardRecommendService.cardHistory.Category;

import java.util.List;
import java.util.Set;

public record CardRecommendResponse(List<CardDetailResponse> recommendedCards,
                                    Set<Category> selectedCategories) {
}
