/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coding4world.adminfaces.app.bean;

import com.github.adminfaces.persistence.service.CrudService;
import com.github.adminfaces.persistence.service.Service;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import coding4world.adminfaces.app.model.City;
import javax.enterprise.inject.Produces;
import coding4world.adminfaces.app.model.Customer;
import coding4world.adminfaces.app.model.State;

/**
 *
 * Cache for lists used across multiple pages
 */
@Named
@ApplicationScoped
public class AppLists implements Serializable {

	@Inject
	@Service
	private CrudService<City, Long> cityService;
	private Set<City> citys;

	@Inject
	@Service
	private CrudService<Customer, Long> customerService;
	private Set<Customer> customers;

	@Inject
	@Service
	private CrudService<State, Long> stateService;
	private Set<State> states;

	@Produces
	@Named("allCitys")
	public Set<City> allCitys() {
		if (citys == null) {
			citys = new HashSet<>(cityService.criteria().getResultList());
		}
		return citys;
	}

	public void clearCitys() {
		citys = null;
	}

	@Produces
	@Named("allCustomers")
	public Set<Customer> allCustomers() {
		if (customers == null) {
			customers = new HashSet<>(customerService.criteria()
					.getResultList());
		}
		return customers;
	}

	public void clearCustomers() {
		customers = null;
	}

	@Produces
	@Named("allStates")
	public Set<State> allStates() {
		if (states == null) {
			states = new HashSet<>(stateService.criteria().getResultList());
		}
		return states;
	}

	public void clearStates() {
		states = null;
	}

}
