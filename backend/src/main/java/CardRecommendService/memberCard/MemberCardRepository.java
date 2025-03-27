package CardRecommendService.memberCard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCardRepository extends JpaRepository<MemberCard, String> {

    List<MemberCard> findByUuid(String uuid); // 사용자 UUID에 해당하는 카드 목록 조회

    List<MemberCard> findAllByIdIn(List<Long> memberCardId);
}

