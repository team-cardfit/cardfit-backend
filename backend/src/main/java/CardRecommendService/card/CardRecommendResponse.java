package CardRecommendService.card;

import CardRecommendService.cardHistory.Category;

import java.util.List;
import java.util.Set;

public record CardRecommendResponse(List<Long> memberCards,
                                    Set<Category> selectedCategories) {
}
