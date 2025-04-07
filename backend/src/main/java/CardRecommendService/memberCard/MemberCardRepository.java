package CardRecommendService.memberCard;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberCardRepository extends JpaRepository<MemberCard, String> {

    // 사용자 UUID에 해당하는 카드 목록 조회
    List<MemberCard> findByUuid(String uuid);

    // UUID와 카드 id 리스트가 모두 일치하는 경우에만 조회
    List<MemberCard> findAllByIdInAndUuid(List<Long> memberCardId, String uuid);

    Optional<MemberCard> findFirstByCard_IdAndUuid(Long cardId, String uuid);

    List<MemberCard> findAllByCard_IdInAndUuid(List<Long> cardIds, String uuid);
}
