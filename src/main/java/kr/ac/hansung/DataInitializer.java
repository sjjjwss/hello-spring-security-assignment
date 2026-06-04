package kr.ac.hansung;

import kr.ac.hansung.entity.Product;
import kr.ac.hansung.entity.Role;
import kr.ac.hansung.entity.User;
import kr.ac.hansung.repository.ProductRepository;
import kr.ac.hansung.repository.RoleRepository;
import kr.ac.hansung.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        if (!userRepository.existsByEmail("admin@hansung.ac.kr")) {
            User admin = new User();
            admin.setEmail("admin@hansung.ac.kr");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.getRoles().add(userRole);
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            log.info("초기 관리자 계정 생성: admin@hansung.ac.kr / admin1234");
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product("Spring Boot 4 완벽 가이드", 35000, "Spring Boot 4 + JPA + Security 실습서", 50));
            productRepository.save(new Product("Spring Security 7 핵심 원리", 28000, "세션·JWT·OAuth2 기반 보안 구현", 30));
            productRepository.save(new Product("JPA 프로그래밍 실전", 32000, "Hibernate 7 기반 ORM 마스터", 25));
            productRepository.save(new Product("Thymeleaf 완전 정복", 22000, "서버사이드 템플릿 엔진 가이드", 40));
            productRepository.save(new Product("React 입문", 27000, "컴포넌트 기반 프론트엔드 개발", 0));
            productRepository.save(new Product("Docker & Kubernetes", 38000, "컨테이너 오케스트레이션 실전", 15));
            productRepository.save(new Product("Git & GitHub 협업", 18000, "브랜치 전략과 Pull Request 가이드", 60));
            productRepository.save(new Product("MySQL 성능 최적화", 33000, "인덱스·쿼리 튜닝 실전 가이드", 20));
            productRepository.save(new Product("Redis 캐싱 전략", 29000, "캐시 설계와 세션 관리 실습", 10));
            productRepository.save(new Product("AWS 클라우드 입문", 42000, "EC2·S3·RDS 실전 배포 가이드", 35));
            productRepository.save(new Product("삼성전자 갤럭시 S25", 1290000, "최신 플래그십 스마트폰", 100));
            productRepository.save(new Product("삼성전자 갤럭시 탭 S10", 890000, "고해상도 AMOLED 태블릿", 45));
            productRepository.save(new Product("LG 그램 17 노트북", 1890000, "초경량 17인치 업무용 노트북", 8));
            productRepository.save(new Product("애플 맥북 프로 M4", 2990000, "M4 칩 탑재 고성능 노트북", 0));
            productRepository.save(new Product("소니 WH-1000XM6", 420000, "업계 최고 노이즈 캔슬링 헤드폰", 22));
            productRepository.save(new Product("로지텍 MX Master 3S", 129000, "무선 고정밀 업무용 마우스", 75));
            productRepository.save(new Product("삼성전자 65인치 QLED TV", 1590000, "Neo QLED 4K 스마트 TV", 5));
            productRepository.save(new Product("다이슨 V15 청소기", 890000, "레이저 먼지 감지 무선 청소기", 18));
            productRepository.save(new Product("닌텐도 스위치 2", 459000, "휴대·거치 겸용 게임 콘솔", 0));
            productRepository.save(new Product("애플 에어팟 프로 3", 359000, "공간 음향 지원 무선 이어폰", 55));
            log.info("샘플 상품 20건 생성 완료");
        }
    }
}
