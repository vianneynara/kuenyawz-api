package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequiredArgsConstructor
public class AprioriServiceImpl implements AprioriService{
    private final double MIN_SUPPORT;
    private final double MIN_CONFIDENCE;
    private final ProductRepository productRepository;

    @Override
    public Set<Map<Long, Set<Long>>> findAllFrequentSetOfItems(Set<Map<Long, Set<Long>>> orders) {
        int topN = 3;
        Map<Long, Set<Long>> frequentSetOfItems = getTopN(orders, topN);
        List<Long> productIds = productRepository.findAllIds();

//        for


        return Set.of();
    }

    @Override
    public Map<Long, Set<Long>> findFrequentSetItemWith(Set<Map<Long, Set<Long>>> orders, Long productId) {
        Map<Set<Long>, Integer> itemCountMap = new HashMap<>();
        int totalOrders = orders.size();
        int productIdCount = 0;

        for (Map<Long, Set<Long>> order : orders) {
            for (Map.Entry<Long, Set<Long>> entry : order.entrySet()) {
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

        Set<Long> topItems = confidenceMap.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<Long, Set<Long>> result = new HashMap<>();
        result.put(productId, topItems);

        return result;
    }

    @Override
    public Map<Long, Set<Long>> findFrequentSetItemWith(Set<Map<Long, Set<Long>>> orders, Long productId, int topN) {
        Map<Set<Long>, Integer> itemCountMap = new HashMap<>();
        int totalOrders = orders.size();
        int productIdCount = 0;

        for (Map<Long, Set<Long>> order : orders) {
            for (Map.Entry<Long, Set<Long>> entry : order.entrySet()) {
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

        Set<Long> topItems = confidenceMap.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .limit(topN)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<Long, Set<Long>> result = new HashMap<>();
        result.put(productId, topItems);

        return result;
    }



    @Override
    public Map<Long, Set<Long>> getTopN(Set<Map<Long, Set<Long>>> orders, int topN) {
        Map<Long, Integer> itemFrequencyMap = new HashMap<>();

        for (Map<Long, Set<Long>> order : orders) {
            for (Set<Long> products : order.values()) {
                for (Long product : products) {
                    itemFrequencyMap.merge(product, 1, Integer::sum);
                }
            }
        }

        Set<Long> topItems = itemFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<Long, Set<Long>> result = new HashMap<>();
        result.put(0L, topItems);

        return result;
    }
}
