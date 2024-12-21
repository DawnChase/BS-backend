package dawnchase.backend.model;

public class Product {
    private Long id;
    private String detail_href;
    private String img;
    private double price;
    private String title;
    private String timestamp;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetail_href() {
        return detail_href;
    }

    public void setDetail_href(String detail_href) {
        this.detail_href = detail_href;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

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

}
