package CardRecommendService;

import CardRecommendService.Classification.Classification;
import CardRecommendService.card.Category;
import CardRecommendService.cardHistory.CardHistory;
import CardRecommendService.memberCard.MemberCard;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UnitTest {

    private final CardHistory cardHistory1 = new CardHistory(4500, "스타벅스", LocalDateTime.now(), Category.커피제과, new MemberCard(""), "UUID", new Classification(5L) );
    private final CardHistory cardHistory2 = new CardHistory(12000, "메가박스", LocalDateTime.now(), Category.영화, new MemberCard(""), "UUID", new Classification(5L));
    private final Classification classification1 = new Classification(1L, null);

    @Test
    public void deleteTest(){

        Classification c = new Classification(5L, List.of(cardHistory1, cardHistory2));
        c.reassignCardHistories(classification1);

        assertThat(c.getCardHistories().stream().map(cardHistory -> cardHistory.getClassification().getId())).allMatch(id -> id == 1L);
    }


}
