import {Component} from "@angular/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Stock} from "../../model/stock";
import {Product} from "../../model/product";
import {CommonService} from "../../common/common.service";
import {ActivatedRoute, Router} from "@angular/router";
import {environment} from "../../constants/environment";
@Component({
    selector: 'stock-update-component',
    templateUrl: './stock-update.component.html'
})
export class StockUpdateComponent {
    stockForm: FormGroup;
    loading: boolean = false;
    stock: Stock;
    availableProducts: Product[] = [];
    selectedProducts: Product[] = [];

    constructor(private stockService: CommonService, private router: Router, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.stockService.loadAll(environment.PRODUCT_URL)
            .subscribe(productList => this.availableProducts = productList);
        this.createEmptyForm();
        this.fillForm();
    }

    private createEmptyForm(): void {
        this.stockForm = new FormGroup({
            specialize: new FormControl('', Validators.required),
        });
    }

    private fillForm(): void {
        this.stockService.loadById(environment.STOCK_URL, this.route.snapshot.params['id'])
            .subscribe(stock => {
                this.stock = stock;
                this.selectedProducts = this.stock.productList;
                this.cleanAvailableProducts();
                this.stockForm.setValue({
                    specialize: this.stock.specialize
                });
            });
    }

    private cleanAvailableProducts(): void {
        for (let availableProduct = 0; availableProduct < this.availableProducts.length; availableProduct++)
            for (let selectedProduct = 0; selectedProduct < this.selectedProducts.length; selectedProduct++)
                if (this.availableProducts[availableProduct].id === this.selectedProducts[selectedProduct].id)
                    this.availableProducts.splice(this.availableProducts.indexOf(this.availableProducts[availableProduct]), 1);
    }

    onSubmit(): void {
        this.loading = true;
        this.stock.specialize = this.stockForm.value.specialize;
        this.stock.productList = this.selectedProducts;
        this.stockService.update(environment.STOCK_URL, this.stock)
            .subscribe(result => result ? this.router.navigate(['user/user-content']) : this.errorMsg);
    }

    addProductToSelected(product: Product): void {
        this.availableProducts.splice(this.availableProducts.indexOf(product), 1);
        this.selectedProducts.push(product);
    }

    deleteProductFromSelected(product: Product): void {
        this.selectedProducts.splice(this.selectedProducts.indexOf(product), 1);
        this.availableProducts.push(product);
    }

    private errorMsg(): void {
        this.loading = false;
        alert("Error!");
    }
}