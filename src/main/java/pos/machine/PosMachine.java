package pos.machine;

import java.util.Comparator;
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
                .distinct()
                .collect(Collectors.toMap(Item::getBarcode, item -> item));

        return itemMap.values().stream()
                .sorted(Comparator.comparing(Item::getBarcode))
                .map(item -> {
                    int quantity = (int) barcodes.stream().filter(code -> code.equals(item.getBarcode())).count();
                    return quantity > 0 ? new ReceiptItem(item.getName(), quantity, item.getPrice(), item.getPrice() * quantity) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
