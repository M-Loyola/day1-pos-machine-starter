package pos.machine;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PosMachine {
    public String printReceipt(List<String> barcodes) {
        List<ReceiptItem> receiptItems = decodeToItems(barcodes);
    }

    private List<ReceiptItem> decodeToItems(List<String> barcodes) {
        List<Item> items = ItemsLoader.loadAllItems();
        Map<String, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getBarcode, item -> item));

        Map<String, Long> barcodeCountMap = barcodes.stream()
                .collect(Collectors.groupingBy(barcode -> barcode, Collectors.counting()));

        return barcodeCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort entries by barcode
                .map(entry -> {
                    String barcode = entry.getKey();
                    long quantity = entry.getValue();
                    Item item = itemMap.get(barcode);
                    if (item != null) {
                        return new ReceiptItem(item.getName(), (int) quantity, item.getPrice(), item.getPrice() * (int) quantity);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
