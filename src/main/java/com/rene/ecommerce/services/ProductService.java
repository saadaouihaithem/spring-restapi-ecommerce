package com.rene.ecommerce.services;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rene.ecommerce.domain.Product;
import com.rene.ecommerce.domain.users.Client;
import com.rene.ecommerce.repositories.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private SellerService sellerService;

	@Autowired
	private ClientService clientService;
	

	@Autowired
	private CategoryService categoryService;

	public Product findById(Integer id) {
		Optional<Product> obj = productRepo.findById(id);

		try {
			return obj.get();
		} catch (NoSuchElementException e) {
			throw new NoSuchElementException();
		}

	}

	@Transactional
	public Product insert(Product obj, Integer sellerId, Integer categoryId) {
		obj.setId(null);
		obj.setProductOwner(sellerService.findById(sellerId));
		obj.setCategory(categoryService.findById(categoryId));
		return productRepo.save(obj);

	}
	
	@Transactional
	public Product update(Product obj, Integer sellerId, Integer categoryId) throws Exception {
		if(Product.isSold(obj)) {
			throw new Exception("The product is already sold");
		}
				
		obj.setProductOwner(sellerService.findById(sellerId));
		obj.setCategory(categoryService.findById(categoryId));
		return productRepo.save(obj);

	}


	public void delete(Integer id) throws Exception {

		if (Product.isSold(findById(id))) {
			throw new Exception("O produto já está vendido");
		}

		productRepo.deleteById(id);

	}

	public List<Product> findAll() {
		return productRepo.findAll();
	}


	@Transactional
	public Product buyProduct(Integer productId, Integer clientId) {

		Product boughtProduct = findById(productId);
		Client buyer = clientService.findById(clientId);

		buyer.setBoughtProducts(Arrays.asList(boughtProduct));
		boughtProduct.setBuyerOfTheProduct(buyer);

		return productRepo.save(boughtProduct);
	}
	
	

}
