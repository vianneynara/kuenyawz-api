package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AprioriServiceImpl implements AprioriService {

    private final double MIN_SUPPORT = 0.05;
    private final double MIN_CONFIDENCE = 0.6;
    private final ProductRepository productRepository;

    @Override
    public Map<Long, Set<Long>> findAllFrequentSetOfItems(Map<Long, Set<Long>> orders) {
        int targetSetSize = 3;
        Map<Long, Set<Long>> result = new HashMap<>();
        List<Long> productIds = productRepository.findAllIds();

        for (Long productId : productIds) {
            Set<Long> currentFrequentSets = findFrequentSetItemWith(orders, productId, targetSetSize);
            result.put(productId, currentFrequentSets);
        }

        return result;
    }

    @Override
    public Set<Long> findFrequentSetItemWith(Map<Long, Set<Long>> orders, Long productId) {
        return findFrequentSetItemWith(orders, productId, Integer.MAX_VALUE);
    }

    @Override
    public Set<Long> findFrequentSetItemWith(Map<Long, Set<Long>> orders, Long productId, int topN) {
        Map<Set<Long>, Integer> itemCountMap = new HashMap<>();
        int totalOrders = orders.size();
        int productIdCount = 0;

        for (Map.Entry<Long, Set<Long>> entry : orders.entrySet()) {
            Set<Long> productSet = entry.getValue();

            if (productSet.contains(productId)) {
                productIdCount++;

                Set<Long> filteredSet = new HashSet<>(productSet);
                filteredSet.remove(productId);

                if (!filteredSet.isEmpty()) {
                    itemCountMap.merge(filteredSet, 1, Integer::sum);
                }
            }
        }

        Map<Long, Double> confidenceMap = new HashMap<>();
        for (Map.Entry<Set<Long>, Integer> entry : itemCountMap.entrySet()) {
            double support = (double) entry.getValue() / totalOrders;

            if (support >= MIN_SUPPORT) {
                for (Long item : entry.getKey()) {
                    double confidence = productIdCount > 0 ?
                            (double) entry.getValue() / productIdCount : 0;

                    if (confidence >= MIN_CONFIDENCE) {
                        confidenceMap.merge(item, confidence, Math::max);
                    }
                }
            }
        }

        return confidenceMap.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .limit(topN)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<Long> getTopN(Map<Long, Set<Long>> orders, int topN) {
        Map<Long, Integer> itemFrequencyMap = new HashMap<>();

        for (Set<Long> products : orders.values()) {
            for (Long product : products) {
                itemFrequencyMap.merge(product, 1, Integer::sum);
            }
        }

        return itemFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
