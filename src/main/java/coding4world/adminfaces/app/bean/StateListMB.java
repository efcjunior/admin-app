package coding4world.adminfaces.app.bean;

import com.github.adminfaces.persistence.bean.CrudMB;
import com.github.adminfaces.persistence.util.Messages;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import coding4world.adminfaces.app.model.State;
import coding4world.adminfaces.app.service.StateService;

import static com.github.adminfaces.persistence.util.Messages.addDetailMessage;
import static com.github.adminfaces.persistence.util.Messages.getMessage;
import static com.github.adminfaces.template.util.Assert.has;
import java.util.*;
import org.primefaces.PrimeFaces;
import coding4world.adminfaces.app.model.City;   
import coding4world.adminfaces.app.model.Customer;   

@Named
@ViewScoped
public class StateListMB extends CrudMB<State> implements Serializable {

    @Inject
    StateService stateService;

    List<City> stateCities;
 
    Map<Long,Boolean> showCitiesDetailMap = new HashMap<>();//used to show details in datatable rows    

    List<Customer> stateCustomers;
 
    Map<Long,Boolean> showCustomersDetailMap = new HashMap<>();//used to show details in datatable rows    

    @Inject
    public void initService() {
        setCrudService(stateService);
    }

    public void delete() {
        int deletedEntities = 0;
        for (State selected : selectionList) {
        	deletedEntities++;
        	stateService.remove(selected);
        }
        selectionList.clear();
        addDetailMessage(deletedEntities + " state(s) deleted successfully!");
        clear();
    }

    /**
     * Used in datatable footer to display current search criteria
     */
    public String getSearchCriteria() {
        StringBuilder sb = new StringBuilder(21);
        State stateFilter = filter.getEntity();
 
        Long id = null;
        if (filter.hasParam("id")) {
            id = filter.getLongParam("id");
        } else if (has(stateFilter.getId())) {
            id = stateFilter.getId();
        }
        if (has(id)) {
  
	        sb.append("<b>id</b>: ").append(id).append(", ");
        }
 
        String name = null;
        if (filter.hasParam("name")) {
            name = (String)filter.getParam("name");
        } else if (has(stateFilter.getName())) {
            name = stateFilter.getName();
        }
        if (has(name)) {
  
	        sb.append("<b>name</b>: ").append(name).append(", ");
        }
 
        String abbreviation = null;
        if (filter.hasParam("abbreviation")) {
            abbreviation = (String)filter.getParam("abbreviation");
        } else if (has(stateFilter.getAbbreviation())) {
            abbreviation = stateFilter.getAbbreviation();
        }
        if (has(abbreviation)) {
  
	        sb.append("<b>abbreviation</b>: ").append(abbreviation).append(", ");
        }
        Set<City> cities = null;
        if (filter.hasParam("cities")) {
           cities = (Set<City>) filter.getParam("cities");
        } else if (has(stateFilter.getCities())) {
            cities = stateFilter.getCities();
        }
        if (has(cities)) {
            sb.append("<b>cities</b>: ");
            for (City city : cities) {
                sb.append(city.getName()).append(", ");
            }
        }
	            Set<Customer> customers = null;
        if (filter.hasParam("customers")) {
           customers = (Set<Customer>) filter.getParam("customers");
        } else if (has(stateFilter.getCustomers())) {
            customers = stateFilter.getCustomers();
        }
        if (has(customers)) {
            sb.append("<b>customers</b>: ");
            for (Customer customer : customers) {
                sb.append(customer.getName()).append(", ");
            }
        }
	            int commaIndex = sb.lastIndexOf(",");
        if (commaIndex != -1) {
            sb.deleteCharAt(commaIndex);
        }
        if (sb.toString().trim().isEmpty()) {
            PrimeFaces.current().executeScript("jQuery('div[id=footer] .fa-filter').addClass('ui-state-disabled')");
            return getMessage("empty-search-criteria");
        } else {
            PrimeFaces.current().executeScript("jQuery('div[id=footer] .fa-filter').removeClass('ui-state-disabled')");
        }
        return sb.toString();
    }

    public void showCitiesDetail(Long id) {
        this.showCitiesDetailMap.clear();//show details of one row at a time
        this.showCitiesDetailMap.put(id,true);
        stateCities = stateService.getCitiesByStateId(id);  
    }
    
    public List<City> getStateCities() {
        return stateCities;
    }

    public void setstateCities(List<City> stateCities) {
        this.stateCities = stateCities;
    }

    public Map<Long,Boolean> getShowCitiesDetailMap() {
        return showCitiesDetailMap;
    }

    public void showCustomersDetail(Long id) {
        this.showCustomersDetailMap.clear();//show details of one row at a time
        this.showCustomersDetailMap.put(id,true);
        stateCustomers = stateService.getCustomersByStateId(id);  
    }
    
    public List<Customer> getStateCustomers() {
        return stateCustomers;
    }

    public void setstateCustomers(List<Customer> stateCustomers) {
        this.stateCustomers = stateCustomers;
    }

    public Map<Long,Boolean> getShowCustomersDetailMap() {
        return showCustomersDetailMap;
    }

}