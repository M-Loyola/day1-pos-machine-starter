package pos.machine;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PosMachine {
    public String printReceipt(List<String> barcodes) {
        List<ReceiptItem> receiptItems = decodeToItems(barcodes);
        calculateItemsCost(receiptItems);
        int totalCost = calculateTotalCost(receiptItems);
        return renderReceipt(receiptItems, totalCost);
    }

    private String renderReceipt(List<ReceiptItem> receiptItems, int totalCost) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("***<store earning no money>Receipt***\n");
        receiptItems.forEach(item -> receipt.append(generateReceipt(item)).append("\n"));
        receipt.append("----------------------\n");
        receipt.append("Total: ").append(totalCost).append(" (yuan)\n");
        receipt.append("**********************");

        return receipt.toString();
    }

    private String generateReceipt(ReceiptItem item) {
        return String.format("Name: %s, Quantity: %d, Unit price: %d (yuan), Subtotal: %d (yuan)",
                item.getName(), item.getQuantity(), item.getUnitPrice(), item.getSubTotal());
    }

    private int calculateTotalCost(List<ReceiptItem> receiptItems) {
        return receiptItems.stream()
                .mapToInt(ReceiptItem::getSubTotal)
                .sum();
    }

    private void calculateItemsCost(List<ReceiptItem> receiptItems) {
        receiptItems.forEach(ReceiptItem::calculateSubTotal);
    }

    private List<ReceiptItem> decodeToItems(List<String> barcodes) {
        List<Item> items = ItemsLoader.loadAllItems();
        Map<String, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getBarcode, item -> item));

        Map<String, Long> barcodeCountMap = barcodes.stream()
                .collect(Collectors.groupingBy(barcode -> barcode, Collectors.counting()));

        return barcodeCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> itemMap.get(entry.getKey()))
                .filter(Objects::nonNull)
                .map(item -> new ReceiptItem(item.getName(), barcodeCountMap.get(item.getBarcode()).intValue(), item.getPrice(), item.getPrice() * barcodeCountMap.get(item.getBarcode()).intValue()))
                .collect(Collectors.toList());
    }
}
