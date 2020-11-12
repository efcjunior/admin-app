package coding4world.adminfaces.app.bean;

import com.github.adminfaces.persistence.bean.CrudMB;
import com.github.adminfaces.persistence.util.Messages;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

import coding4world.adminfaces.app.model.Customer;
import coding4world.adminfaces.app.service.CustomerService;

import static com.github.adminfaces.persistence.util.Messages.addDetailMessage;
import static com.github.adminfaces.persistence.util.Messages.getMessage;
import static com.github.adminfaces.template.util.Assert.has;
import java.util.*;
import org.primefaces.PrimeFaces;
import coding4world.adminfaces.app.model.City;      
import coding4world.adminfaces.app.model.State;      

@Named
@ViewScoped
public class CustomerListMB extends CrudMB<Customer> implements Serializable {

    @Inject
    CustomerService customerService;

    @Inject
    public void initService() {
        setCrudService(customerService);
    }

    public void delete() {
        int deletedEntities = 0;
        for (Customer selected : selectionList) {
        	deletedEntities++;
        	customerService.remove(selected);
        }
        selectionList.clear();
        addDetailMessage(deletedEntities + " customer(s) deleted successfully!");
        clear();
    }

    /**
     * Used in datatable footer to display current search criteria
     */
    public String getSearchCriteria() {
        StringBuilder sb = new StringBuilder(21);
        Customer customerFilter = filter.getEntity();
 
        Long id = null;
        if (filter.hasParam("id")) {
            id = filter.getLongParam("id");
        } else if (has(customerFilter.getId())) {
            id = customerFilter.getId();
        }
        if (has(id)) {
  
	        sb.append("<b>id</b>: ").append(id).append(", ");
        }
 
        String name = null;
        if (filter.hasParam("name")) {
            name = (String)filter.getParam("name");
        } else if (has(customerFilter.getName())) {
            name = customerFilter.getName();
        }
        if (has(name)) {
  
	        sb.append("<b>name</b>: ").append(name).append(", ");
        }
 
        String email = null;
        if (filter.hasParam("email")) {
            email = (String)filter.getParam("email");
        } else if (has(customerFilter.getEmail())) {
            email = customerFilter.getEmail();
        }
        if (has(email)) {
  
	        sb.append("<b>email</b>: ").append(email).append(", ");
        }
 
        Date dateOfBirth = null;
        if (filter.hasParam("dateOfBirth")) {
            dateOfBirth = (Date)filter.getParam("dateOfBirth");
        } else if (has(customerFilter.getDateOfBirth())) {
            dateOfBirth = customerFilter.getDateOfBirth();
        }
        if (has(dateOfBirth)) {
  
	        sb.append("<b>dateOfBirth</b>: ").append(dateOfBirth).append(", ");
        }
 
        City city = null;
        if (filter.hasParam("city")) {
            city = (City)filter.getParam("city");
        } else if (has(customerFilter.getCity())) {
            city = customerFilter.getCity();
        }
        if (has(city)) {
            sb.append("<b>city</b>: ").append(city.getName()).append(", ");
                    }
 
        State state = null;
        if (filter.hasParam("state")) {
            state = (State)filter.getParam("state");
        } else if (has(customerFilter.getState())) {
            state = customerFilter.getState();
        }
        if (has(state)) {
            sb.append("<b>state</b>: ").append(state.getName()).append(", ");
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

    public List<City> completeCity(String query) {
        return customerService.getCitysByName(query);
    }
    public List<State> completeState(String query) {
        return customerService.getStatesByName(query);
    }
}