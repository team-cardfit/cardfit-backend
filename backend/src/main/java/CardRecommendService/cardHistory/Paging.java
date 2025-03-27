package CardRecommendService.cardHistory;

public record Paging(int page,
                     int size,
                     int totalPages,
                     long totalCount) {

}
