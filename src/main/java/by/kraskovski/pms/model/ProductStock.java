package by.kraskovski.pms.model;

import by.kraskovski.pms.model.base.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class ProductStock extends BaseEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    private int productsCount;

    public Product getProduct() {
        return product;
    }

    public Stock getStock() {
        return stock;
    }

    public void setProduct(final Product product) {
        this.product = product;
    }

    public void setStock(final Stock stock) {
        this.stock = stock;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }
}
