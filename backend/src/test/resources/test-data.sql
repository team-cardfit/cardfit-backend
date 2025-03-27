-- Card 테이블에 카드 데이터 삽입
-- 카드 정보 삽입
INSERT INTO Card (cardIssuer, cardName, cardType, annualFee)
VALUES
('국민카드', '국민체크카드', 'CHECK', 3000),
('삼성카드', '삼성신용카드', 'CREDIT', 5000),
('롯데카드', '롯데체크카드', 'CHECK', 2500);

-- 카드 혜택 정보 삽입
INSERT INTO CardBenefits (card_id, benefit_description)
VALUES
(1, '적립 혜택 1%'),
(2, '해외 결제 3% 캐시백'),
(3, '쇼핑 할인 10%');

-- 멤버 카드 정보 삽입
INSERT INTO MemberCard (card_id, member_id)
VALUES
(1, 101),
(2, 102),
(3, 103);

-- Member 테이블에 회원 데이터 삽입
INSERT INTO Member (email, nickname, password, gender, age)
VALUES
    ('johndoe@example.com', 'JohnDoe', 'password123', 'MALE', 28),
    ('janesmith@example.com', 'JaneSmith', 'password456', 'FEMALE', 34),
    ('alicejones@example.com', 'AliceJones', 'password789', 'FEMALE', 40),
    ('bobbrown@example.com', 'BobBrown', 'password101', 'MALE', 25),
    ('charliedavis@example.com', 'CharlieDavis', 'password202', 'MALE', 29);

-- MemberCard 테이블에 회원과 카드 관계 데이터 삽입
INSERT INTO MemberCard (card_number, expiration_date, member_id, card_id)
VALUES
    ('1234-5678-1234-5678', '2025-08-01', 1, 1),  -- John Doe가 Samsung Cashback 카드에 연결
    ('2345-6789-2345-6789', '2026-03-15', 2, 2),  -- Jane Smith가 KB Global Card에 연결
    ('3456-7890-3456-7890', '2025-07-30', 3, 3),  -- Alice Jones가 Shinhan Green Card에 연결
    ('4567-8901-4567-8901', '2027-01-10', 4, 4),  -- Bob Brown이 Lotte World Card에 연결
    ('5678-9012-5678-9012', '2025-11-25', 5, 5);  -- Charlie Davis가 Hyundai Mileage Card에 연결
