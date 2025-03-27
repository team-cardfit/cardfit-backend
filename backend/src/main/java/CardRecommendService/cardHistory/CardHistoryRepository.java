package CardRecommendService.cardHistory;

import CardRecommendService.Classification.Classification;
import CardRecommendService.memberCard.MemberCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface CardHistoryRepository extends JpaRepository<CardHistory, Long> {

    Page<CardHistory> findByMemberCardInAndPaymentDatetimeBetween(
            List<MemberCard> memberCards,
            LocalDateTime startOfMonthTime,
            LocalDateTime endOfMonthTime,
            Pageable pageable
    );

    Page<CardHistory> findByClassificationIdIn(List<Long> classificationIds, Pageable pageable);
}
