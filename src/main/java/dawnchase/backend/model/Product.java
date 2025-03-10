package dawnchase.backend.model;

public class Product {
    private Long id;
    private String category;
    private String href;
    private String imgSrc;
    private double price;
    private String title;
    private String timestamp;
    private String store;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getHref() { return href; }

    public void setHref(String href) { this.href = href; }

    public String getImgSrc() { return imgSrc; }

    public void setImgSrc(String imgSrc) { this.imgSrc = imgSrc; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

}
