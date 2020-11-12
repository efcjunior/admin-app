package coding4world.adminfaces.app.bean;

import com.github.adminfaces.persistence.bean.CrudMB;
import com.github.adminfaces.persistence.util.Messages;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import coding4world.adminfaces.app.model.City;
import coding4world.adminfaces.app.service.CityService;

import static com.github.adminfaces.persistence.util.Messages.addDetailMessage;
import static com.github.adminfaces.persistence.util.Messages.getMessage;
import static com.github.adminfaces.template.util.Assert.has;
import java.util.*;
import org.primefaces.PrimeFaces;
import coding4world.adminfaces.app.model.State;      
import coding4world.adminfaces.app.model.Customer;   

@Named
@ViewScoped
public class CityListMB extends CrudMB<City> implements Serializable {

    @Inject
    CityService cityService;

    List<Customer> cityCustomers;
 
    Map<Long,Boolean> showCustomersDetailMap = new HashMap<>();//used to show details in datatable rows    

    @Inject
    public void initService() {
        setCrudService(cityService);
    }

    public void delete() {
        int deletedEntities = 0;
        for (City selected : selectionList) {
        	deletedEntities++;
        	cityService.remove(selected);
        }
        selectionList.clear();
        addDetailMessage(deletedEntities + " city(s) deleted successfully!");
        clear();
    }

    /**
     * Used in datatable footer to display current search criteria
     */
    public String getSearchCriteria() {
        StringBuilder sb = new StringBuilder(21);
        City cityFilter = filter.getEntity();
 
        Long id = null;
        if (filter.hasParam("id")) {
            id = filter.getLongParam("id");
        } else if (has(cityFilter.getId())) {
            id = cityFilter.getId();
        }
        if (has(id)) {
  
	        sb.append("<b>id</b>: ").append(id).append(", ");
        }
 
        String name = null;
        if (filter.hasParam("name")) {
            name = (String)filter.getParam("name");
        } else if (has(cityFilter.getName())) {
            name = cityFilter.getName();
        }
        if (has(name)) {
  
	        sb.append("<b>name</b>: ").append(name).append(", ");
        }
 
        State state = null;
        if (filter.hasParam("state")) {
            state = (State)filter.getParam("state");
        } else if (has(cityFilter.getState())) {
            state = cityFilter.getState();
        }
        if (has(state)) {
            sb.append("<b>state</b>: ").append(state.getName()).append(", ");
                    }
        Set<Customer> customers = null;
        if (filter.hasParam("customers")) {
           customers = (Set<Customer>) filter.getParam("customers");
        } else if (has(cityFilter.getCustomers())) {
            customers = cityFilter.getCustomers();
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

    public List<State> completeState(String query) {
        return cityService.getStatesByName(query);
    }
    public void showCustomersDetail(Long id) {
        this.showCustomersDetailMap.clear();//show details of one row at a time
        this.showCustomersDetailMap.put(id,true);
        cityCustomers = cityService.getCustomersByCityId(id);  
    }
    
    public List<Customer> getCityCustomers() {
        return cityCustomers;
    }

    public void setcityCustomers(List<Customer> cityCustomers) {
        this.cityCustomers = cityCustomers;
    }

    public Map<Long,Boolean> getShowCustomersDetailMap() {
        return showCustomersDetailMap;
    }

}