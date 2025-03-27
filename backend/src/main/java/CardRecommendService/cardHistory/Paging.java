package CardRecommendService.cardHistory;

public record Paging(int pageNumber,
                     int size,
                     int totalPages,
                     long totalCount) {

}
