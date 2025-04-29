package com.sivalabs.ft.features.domain.release;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.sivalabs.ft.features.domain.feature.FeatureRepository;
import com.sivalabs.ft.features.domain.product.Product;
import com.sivalabs.ft.features.domain.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReleaseService {
    private final ReleaseRepository releaseRepository;
    private final ProductRepository productRepository;
    private final FeatureRepository featureRepository;

    ReleaseService(
            ReleaseRepository releaseRepository,
            ProductRepository productRepository,
            FeatureRepository featureRepository) {
        this.releaseRepository = releaseRepository;
        this.productRepository = productRepository;
        this.featureRepository = featureRepository;
    }

    public List<Release> findReleasesByProductCode(String productCode) {
        return releaseRepository.findByProductCode(productCode);
    }

    public Optional<Release> findReleaseByCode(String code) {
        return releaseRepository.findByCode(code);
    }

    public boolean isReleaseExists(String code) {
        return releaseRepository.existsByCode(code);
    }

    @Transactional
    public Long createRelease(CreateReleaseCommand cmd) {
        Product product = productRepository.findByCode(cmd.productCode()).orElseThrow();
        Release release = new Release();
        release.setProduct(product);
        release.setCode(cmd.code());
        release.setDescription(cmd.description());
        release.setStatus(ReleaseStatus.DRAFT);
        release.setCreatedBy(cmd.createdBy());
        release.setCreatedAt(Instant.now());
        releaseRepository.save(release);
        return release.getId();
    }

    @Transactional
    public void updateRelease(UpdateReleaseCommand cmd) {
        Release release = releaseRepository.findByCode(cmd.code()).orElseThrow();
        release.setDescription(cmd.description());
        release.setStatus(cmd.status());
        release.setReleasedAt(cmd.releasedAt());
        release.setUpdatedBy(cmd.updatedBy());
        release.setUpdatedAt(Instant.now());
        releaseRepository.save(release);
    }

    @Transactional
    public void deleteRelease(String code) {
        if (!releaseRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Release with code " + code + " not found");
        }
        featureRepository.deleteByReleaseCode(code);
        releaseRepository.deleteByCode(code);
    }
}
