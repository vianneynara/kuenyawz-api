package dev.kons.kuenyawz.services.logic;

import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AprioriServiceImpl implements AprioriService{
    private final double MIN_SUPPORT;
    private final double MIN_CONFIDENCE;

    @Override
    public Set<Map<Long, Set<Long>>> findFrequentSetItemWith(Set<Map<Long, Set<Long>>> orders, Long productId) {
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

                    for (Long otherProduct : filteredSet) {
                        Set<Long> itemSet = new HashSet<>();
                        itemSet.add(otherProduct);

                        itemCountMap.merge(itemSet, 1, Integer::sum);
                    }
                }
            }
        }

        Map<Set<Long>, Double> itemConfidenceMap = new HashMap<>();
        for (Map.Entry<Set<Long>, Integer> entry : itemCountMap.entrySet()) {
            double support = (double) entry.getValue() / totalOrders;

            double confidence = productIdCount > 0 ?
                    (double) entry.getValue() / productIdCount : 0;

            if (support >= MIN_SUPPORT && confidence >= MIN_CONFIDENCE) {
                itemConfidenceMap.put(entry.getKey(), confidence);
            }
        }

        Map<Set<Long>, Double> sortedItemConfidenceMap = itemConfidenceMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Convert to required return format
        Set<Map<Long, Set<Long>>> result = new LinkedHashSet<>();

        for (Map.Entry<Set<Long>, Double> entry : sortedItemConfidenceMap.entrySet()) {
            Map<Long, Set<Long>> frequentItemMap = new HashMap<>();
            frequentItemMap.put(productId, entry.getKey());
            result.add(frequentItemMap);
        }
        return result;
    }

    @Override
    public Set<Map<Long, Set<Long>>> findFrequentSetItemWith(Set<Map<Long, Set<Long>>> orders, Long productId, int topN) {
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

                    for (Long otherProduct : filteredSet) {
                        Set<Long> itemSet = new HashSet<>();
                        itemSet.add(otherProduct);

                        itemCountMap.merge(itemSet, 1, Integer::sum);
                    }
                }
            }
        }

        Map<Set<Long>, Double> itemConfidenceMap = new HashMap<>();
        for (Map.Entry<Set<Long>, Integer> entry : itemCountMap.entrySet()) {
            double support = (double) entry.getValue() / totalOrders;

            double confidence = productIdCount > 0 ?
                    (double) entry.getValue() / productIdCount : 0;

            if (support >= MIN_SUPPORT && confidence >= MIN_CONFIDENCE) {
                itemConfidenceMap.put(entry.getKey(), confidence);
            }
        }

        Map<Set<Long>, Double> sortedItemConfidenceMap = itemConfidenceMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        Set<Map<Long, Set<Long>>> result = new LinkedHashSet<>();

        for (Map.Entry<Set<Long>, Double> entry : sortedItemConfidenceMap.entrySet()) {
            Map<Long, Set<Long>> frequentItemMap = new HashMap<>();
            frequentItemMap.put(productId, entry.getKey());
            result.add(frequentItemMap);
        }

        return result;
    }

    @Override
    public Set<Map<Long, Set<Long>>> getTopN(Set<Map<Long, Set<Long>>> orders, int topN) {
        Map<Set<Long>, Integer> itemFrequencyMap = new HashMap<>();

        for (Map<Long, Set<Long>> order : orders) {
            for (Set<Long> products : order.values()) {
                itemFrequencyMap.merge(products, 1, Integer::sum);
            }
        }

        return itemFrequencyMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(topN)
                .map(e -> {
                    Map<Long, Set<Long>> result = new HashMap<>();
                    result.put(null, e.getKey());
                    return result;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
