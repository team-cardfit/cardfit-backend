package CardRecommendService.card;

import CardRecommendService.cardHistory.Category;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class QCardRepository {

    private final JPAQueryFactory queryFactory;
    private final QCard qCard = QCard.card;


    public QCardRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<Card> findCardsMatchingTopCategoriesAndAnnualFee(Set<Category> topCategories,
                                                                 int minAnnualFee,
                                                                 int maxAnnualFee) {
        if (topCategories == null || topCategories.isEmpty()) {

            return queryFactory
                    .select(qCard)
                    .from(qCard)
                    .where(getCardsAnnualFee(minAnnualFee, maxAnnualFee))
                    .fetch();
        } else {

            return queryFactory
                    .select(qCard)
                    .from(qCard)
                    .where(
                            getCardsAnnualFee(minAnnualFee, maxAnnualFee)
                                    .and(
                                            qCard.store1.in(topCategories)
                                                    .or(qCard.store2.in(topCategories))
                                                    .or(qCard.store3.in(topCategories))
                                    )
                    )
                    .fetch();
        }
    }

    private BooleanExpression getCardsAnnualFee (Integer minAnnualFee,
                                                 Integer maxAnnualFee){
        if (minAnnualFee == null || maxAnnualFee == null) return null;
        return qCard.annualFee.between(minAnnualFee, maxAnnualFee);
    }
}
