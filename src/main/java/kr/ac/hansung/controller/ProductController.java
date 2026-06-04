package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int size,
                       Model model) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id"));
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;
        Page<Product> productPage = normalizedKeyword != null
            ? productService.searchProducts(normalizedKeyword, pageRequest)
            : productService.getProducts(pageRequest);

        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", normalizedKeyword);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "products/add";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setDescription(product.getDescription());

        model.addAttribute("productDto", dto);
        model.addAttribute("productId", id);
        return "products/edit";
    }

    @PostMapping("/{id}/edit")
    public String editProduct(@PathVariable Long id,
                              @Valid @ModelAttribute("productDto") ProductDto productDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "products/edit";
        }

        productService.updateProduct(id, productDto);
        ra.addFlashAttribute("successMessage", "상품이 수정되었습니다.");
        return "redirect:/products";
    }

    @PostMapping
    public String save(@ModelAttribute ProductDto dto) {
        productService.save(dto);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
