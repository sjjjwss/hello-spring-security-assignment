package kr.ac.hansung.controller;

import kr.ac.hansung.entity.Product;
import kr.ac.hansung.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest : 전체 Application Context 로드 (Controller, Service, Security, JPA 등 모든 Bean 등록)
//                   Controller는 Spring Bean이므로 Spring Context 없이는 테스트 불가
//                   webEnvironment 기본값 = MOCK (실제 Tomcat 없이 가짜 웹 환경 구성)
// @MockitoBean    : Spring Context에 등록된 특정 Bean을 Mock으로 교체
// @WithMockUser   : 실제 로그인 없이 인증된 사용자 흉내 (roles 지정 가능)
// @WithAnonymousUser : 비인증 사용자 흉내
@SpringBootTest
@DisplayName("ProductController 테스트")
class ProductControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @MockitoBean
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(wac)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("인증된 사용자 - 상품 목록 조회 성공 (200)")
    void listProducts_authenticated_returns200() throws Exception {
        given(productService.getProducts(any())).willReturn(
            new PageImpl<>(
                List.of(new Product("Spring Boot 4 교재", 35000, "실습서", 50)),
                PageRequest.of(0, 5),
                1
            )
        );

        mockMvc.perform(get("/products").param("page", "0").param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(view().name("products/list"))
            .andExpect(model().attributeExists("productPage"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("비인증 사용자 - 상품 목록 접근 시 로그인 페이지로 리다이렉트")
    void listProducts_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - 상품 등록 폼 조회 성공 (200)")
    void addForm_admin_returns200() throws Exception {
        mockMvc.perform(get("/products/add"))
            .andExpect(status().isOk())
            .andExpect(view().name("products/add"))
            .andExpect(model().attributeExists("product"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("일반 USER - 상품 등록 폼 접근 시 403 (권한 없음)")
    void addForm_user_returns403() throws Exception {
        mockMvc.perform(get("/products/add"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - 상품 등록 POST 후 목록으로 리다이렉트")
    void saveProduct_admin_redirectsToList() throws Exception {
        given(productService.save(any())).willReturn(
            new Product("테스트 상품", 15000, "설명", 10)
        );

        mockMvc.perform(post("/products")
                .with(csrf())
                .param("name", "테스트 상품")
                .param("price", "15000")
                .param("description", "테스트 설명")
                .param("stock", "10"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/products"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("일반 USER - 상품 등록 POST 시 403 (권한 없음)")
    void saveProduct_user_returns403() throws Exception {
        mockMvc.perform(post("/products")
                .with(csrf())
                .param("name", "테스트 상품")
                .param("price", "15000")
                .param("stock", "10"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN - 상품 삭제 후 목록으로 리다이렉트")
    void deleteProduct_admin_redirectsToList() throws Exception {
        willDoNothing().given(productService).deleteById(1L);

        mockMvc.perform(post("/products/1/delete")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/products"));
    }
}
