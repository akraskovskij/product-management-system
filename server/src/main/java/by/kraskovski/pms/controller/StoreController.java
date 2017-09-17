package by.kraskovski.pms.controller;

import by.kraskovski.pms.controller.dto.StockDto;
import by.kraskovski.pms.controller.dto.StoreDto;
import by.kraskovski.pms.domain.model.Store;
import by.kraskovski.pms.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Controller for the {@link StoreService}.
 */
@RestController
@RequestMapping("/store")
@Slf4j
public class StoreController {

    private final StoreService storeService;
    private final Mapper mapper;

    @Autowired
    public StoreController(final StoreService storeService, final Mapper mapper) {
        this.storeService = storeService;
        this.mapper = mapper;
    }

    /**
     * Find all stores in database
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity loadAllStores() {
        log.info("Start loadAllStores");
        return ResponseEntity.ok(storeService.findAll().stream()
                .map(store -> mapper.map(store, StoreDto.class))
                .collect(toList()));
    }

    /**
     * Find stores in database with setting id in browser
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity loadStoreById(@PathVariable("id") final String id) {
        log.info("Start loadStoreById: {}", id);
        try {
            final Store store = storeService.find(id);
            Assert.notNull(store, "Unable to find store with id: " + id);
            return ResponseEntity.ok(mapper.map(store, StoreDto.class));
        } catch (IllegalArgumentException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Find stocks related to store
     */
    @RequestMapping(value = "/{id}/stock-manage", method = RequestMethod.GET)
    public ResponseEntity loadStoreStocksById(@PathVariable("id") final String id) {
        log.info("Start loadStoreStocksById: {}", id);
        try {
            final Store store = storeService.find(id);
            Assert.notNull(store, "Unable to find store with id: " + id);
            final List<StockDto> stockDtos = store.getStockList().stream()
                    .map(stock -> mapper.map(stock, StockDto.class)).collect(toList());
            return ResponseEntity.ok(stockDtos);
        } catch (IllegalArgumentException e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creating {@link Store} from client form.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createStore(@RequestBody final StoreDto storeDto) {
        log.info("Start createStore: {}", storeDto.getName());
        final Store store = mapper.map(storeDto, Store.class);
        try {
            return new ResponseEntity<>(mapper.map(storeService.create(store), StoreDto.class), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            log.error("Exception in createStore. " + e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create relation between {@link by.kraskovski.pms.domain.model.Stock} and {@link Store}
     */
    @RequestMapping(value = "/stock-manage", method = RequestMethod.PUT)
    public ResponseEntity addStock(@RequestParam("store_id") final String storeId,
                                   @RequestParam("stock_id") final String stockId) {
        log.info("Start add stock: {} to store: {}", stockId, storeId);
        return storeService.addStock(storeId, stockId)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Delete relation between {@link by.kraskovski.pms.domain.model.Stock} and {@link Store}
     */
    @RequestMapping(value = "/stock-manage", method = RequestMethod.DELETE)
    public ResponseEntity deleteStock(@RequestParam("store_id") final String storeId,
                                      @RequestParam("stock_id") final String stockId) {
        log.info("Start delete stock: {} from store: {}", stockId, storeId);
        return storeService.deleteStock(storeId, stockId)
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Update {@link Store} entity in database.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateStore(@RequestBody final StoreDto storeDto) {
        log.info("Start updateStore: {}", storeDto.getName());
        final Store store = mapper.map(storeDto, Store.class);
        try {
            return ResponseEntity.ok(mapper.map(storeService.update(store), StoreDto.class));
        } catch (DataAccessException e) {
            log.error("Exception in updateStore. " + e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete {@link Store} from database by identifier.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteStore(@PathVariable("id") final String id) {
        log.info("Start deleteStore: {}", id);
        try {
            storeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            log.error("Exception in deleteStore. " + e.getLocalizedMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
