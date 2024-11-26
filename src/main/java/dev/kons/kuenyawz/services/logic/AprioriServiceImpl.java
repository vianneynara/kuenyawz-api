package dev.kons.kuenyawz.services.logic;

import dev.kons.kuenyawz.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AprioriServiceImpl implements AprioriService {

    private final double MIN_SUPPORT = 0.05;
    private final double MIN_CONFIDENCE = 0.6;
    private final ProductRepository productRepository;

    @Override
    public Set<Map<Long, Set<Long>>> findAllFrequentSetOfItems(Set<Map<Long, Set<Long>>> orders) {
        int targetSetSize = 3;
        Set<Map<Long, Set<Long>>> result = new HashSet<>();
        Map<Long, Set<Long>> frequentSetOfItems = getTopN(orders, targetSetSize);
        List<Long> productIds = productRepository.findAllIds();

        result.add(frequentSetOfItems);

        for (Long id : productIds) {
            Map<Long, Set<Long>> returnValue = findFrequentSetItemWith(orders, id, targetSetSize);

            int currentSetSize = returnValue.values().stream()
                    .mapToInt(Set::size)
                    .sum();

            if (currentSetSize < targetSetSize) {
                for (Map.Entry<Long, Set<Long>> entry : frequentSetOfItems.entrySet()) {
                    Long key = entry.getKey();
                    Set<Long> values = entry.getValue();

                    for (Long value : values) {
                        boolean valueExists = returnValue.values().stream()
                                .anyMatch(existingSet -> existingSet.contains(value));

                        if (!valueExists) {
                            returnValue.computeIfAbsent(key, k -> new HashSet<>())
                                    .add(value);

                            currentSetSize++;

                            if (currentSetSize >= targetSetSize) {
                                break;
                            }
                        }
                    }
                }
                if (currentSetSize >= targetSetSize) {
                    break;
                }
            }
            result.add(returnValue);
        }
        return result;
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
