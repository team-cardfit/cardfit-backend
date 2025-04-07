package CardRecommendService.cardHistory;

public record Paging(int currentPage,
                     int size,
                     int totalPages,
                     long totalCount) {

}
