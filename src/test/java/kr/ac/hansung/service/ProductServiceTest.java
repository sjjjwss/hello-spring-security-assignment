package kr.ac.hansung.service;

import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("ProductService 테스트")
class ProductServiceTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final ProductService productService = new ProductService(productRepository);

    @Test
    @DisplayName("상품 수정은 조회한 영속 엔티티 필드만 변경하고 save를 호출하지 않는다")
    void updateProduct_changesManagedEntityWithoutSave() {
        Product product = new Product("기존 상품", 10000, "기존 설명", 5);
        ProductDto dto = new ProductDto();
        dto.setName("수정 상품");
        dto.setPrice(18000);
        dto.setDescription("수정 설명");
        dto.setStock(12);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        Product updatedProduct = productService.updateProduct(1L, dto);

        assertThat(updatedProduct.getName()).isEqualTo("수정 상품");
        assertThat(updatedProduct.getPrice()).isEqualTo(18000);
        assertThat(updatedProduct.getDescription()).isEqualTo("수정 설명");
        assertThat(updatedProduct.getStock()).isEqualTo(12);
        verify(productRepository, never()).save(product);
    }
}
