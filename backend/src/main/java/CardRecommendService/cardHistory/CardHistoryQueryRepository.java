
package CardRecommendService.cardHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Repository
public class CardHistoryQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QCardHistory qCardHistory = QCardHistory.cardHistory;

    public CardHistoryQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }



    public Page<CardHistory> findSelectedByMemberIdAndPeriod(String uuid, List<Long> memberCardIds, Integer monthOffset, Pageable pageable) {

        List<CardHistory> content = queryFactory
                .selectFrom(qCardHistory)
                .where(qCardHistory.uuid.eq(uuid), qCardHistory.memberCard.id.in(memberCardIds), queryConditions(monthOffset))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qCardHistory.paymentDatetime.asc())
                .fetch();

        Long pageCount = queryFactory
                .select(qCardHistory.count())
                .where(qCardHistory.uuid.eq(uuid), qCardHistory.memberCard.id.in(memberCardIds), queryConditions(monthOffset))
                .from(qCardHistory)
                .fetchOne();

        //pageCount null 방지
        long safePageCount = (pageCount != null) ? pageCount : 0L;

        return new PageImpl<>(content, pageable, safePageCount);
    }


    //기간 조건 설정하기
    private BooleanExpression queryConditions(Integer monthOffset) {

        if(monthOffset == null || monthOffset > 3){
            throw new IllegalArgumentException("조회는 최장 3개월 전까지 가능합니다");
        }

        //현재 날짜의 전월, 전전월, 전전전월. 최장 3개월
        YearMonth targetMonth = YearMonth.from(LocalDate.now()).minusMonths(monthOffset);

        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(23, 59, 59);

        return qCardHistory.paymentDatetime.between(startDate, endDate);
    }



    public int getMemberCardsTotalAmount(String uuid, List<Long> memberCardIds, Integer monthOffset) {
        QCardHistory qCardHistory = QCardHistory.cardHistory;

        Integer totalAmount = queryFactory
                .select(qCardHistory.amount.sum())
                .from(qCardHistory)
                .where(qCardHistory.uuid.eq(uuid), qCardHistory.memberCard.id.in(memberCardIds), queryConditions(monthOffset))
                .fetchOne();
        return (totalAmount != null) ? totalAmount : 0;
    }

}
