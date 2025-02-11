package gift.services;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.OptionDto;
import gift.dto.RequestOptionDto;
import gift.repositories.OptionRepository;
import gift.repositories.ProductRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public List<OptionDto> getOptionsByProductId(Long productId) {
        return optionRepository.findAllByProductId(productId)
            .stream()
            .map(Option::getOptionDto
            )
            .toList();
    }

    public OptionDto addOption(Long productId, RequestOptionDto requestOptionDto) {
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new NoSuchElementException("Product not found with id " + productId));

        List<Option> options = optionRepository.findAllByProductId(productId);

        boolean isDuplicate = options.stream()
            .anyMatch(option -> option.getName().equals(requestOptionDto.getName()));

        if (isDuplicate) {
            throw new IllegalArgumentException("동일한 상품 내에 옵션 이름이 중복될 수 없습니다.");
        }

        Option option = new Option(requestOptionDto.getName(), requestOptionDto.getQuantity(),
            product);

        Option savedOption = optionRepository.save(option);
        return savedOption.getOptionDto();
    }

    public OptionDto updateOption(Long optionId, RequestOptionDto requestOptionDto) {
        Option option = optionRepository.findById(optionId).orElseThrow(
            () -> new IllegalArgumentException("Option not found with id " + optionId));
        option.update(requestOptionDto.getName(), requestOptionDto.getQuantity());

        return option.getOptionDto();
    }

    public void deleteOption(Long optionId) {
        Option option = optionRepository.findById(optionId).orElseThrow(
            () -> new IllegalArgumentException("Option not found with id " + optionId));
        optionRepository.delete(option);
    }

    public void deductOption(Long optionId, int quantity) {
        Option option = optionRepository.findById(optionId).orElseThrow(
            () -> new IllegalArgumentException("Option not found with id " + optionId));
        option.decrementAmount(quantity);
    }

}

