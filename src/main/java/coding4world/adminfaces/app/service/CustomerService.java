package coding4world.adminfaces.app.service;

import coding4world.adminfaces.app.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.adminfaces.persistence.model.Filter;
import com.github.adminfaces.persistence.service.CrudService;
import com.github.adminfaces.template.exception.BusinessException;
import org.apache.deltaspike.data.api.criteria.Criteria;
import coding4world.adminfaces.app.bean.AppLists;
import coding4world.adminfaces.app.repository.CustomerRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.persistence.criteria.JoinType;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static com.github.adminfaces.template.util.Assert.has;


 
@Stateless
public class CustomerService extends CrudService<Customer, Long> implements Serializable {

    private Client client;

    private WebTarget target;

    @Inject
    protected CustomerRepository customerRepository;// you can use repositories to extract complex queries from your service

    @Inject
    protected AppLists appLists;

    @Override
    public void afterAll(Customer customer) {
        appLists.clearCustomers();//invalidate Customer list cache
    }

    /** 
     * This method is used for (real) pagination and is called by lazy 
     * PrimeFaces datatable on list page
     *
     * @param filter holds restrictions populated on the list page
     * @return a criteria populated with given restrictions 
     */ 
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    protected Criteria<Customer, Customer> configRestrictions(Filter<Customer> filter) {
        Criteria<Customer, Customer> criteria = criteria();
        
        if (filter.hasParam(Customer_.id.getName())) {
            criteria.eq(Customer_.id, filter.getLongParam(Customer_.id.getName()));   
        }  
        if (filter.hasParam(Customer_.name.getName())) {
            criteria.eq(Customer_.name, (String)filter.getParam(Customer_.name.getName()));   
        }  
        if (filter.hasParam(Customer_.email.getName())) {
            criteria.eq(Customer_.email, (String)filter.getParam(Customer_.email.getName()));   
        }  
        if (filter.hasParam(Customer_.dateOfBirth.getName())) {
            criteria.eq(Customer_.dateOfBirth, (Date)filter.getParam(Customer_.dateOfBirth.getName()));   
        }  
        if (filter.hasParam(Customer_.city.getName())) {
            criteria.join(Customer_.city,
                where(City.class, JoinType.LEFT)
               .eq(City_.id, ((City) filter.getParam("city")).getId()));
        }  
        if (filter.hasParam(Customer_.state.getName())) {
            criteria.join(Customer_.state,
                where(State.class, JoinType.LEFT)
               .eq(State_.id, ((State) filter.getParam("state")).getId()));
        }  

        //create restrictions based on filter entity
        if (has(filter.getEntity())) {
            Customer filterEntity = filter.getEntity();
            
	        if (has(filterEntity.getId())) {
                 criteria.eq(Customer_.id, filterEntity.getId());   
	        }  
	        if (has(filterEntity.getName())) {
                 criteria.eq(Customer_.name, filterEntity.getName());   
	        }  
	        if (has(filterEntity.getEmail())) {
                 criteria.eq(Customer_.email, filterEntity.getEmail());   
	        }  
	        if (has(filterEntity.getDateOfBirth())) {
                 criteria.eq(Customer_.dateOfBirth, filterEntity.getDateOfBirth());   
	        }  
	        if (has(filterEntity.getCity())) {
                 criteria.join(Customer_.city,
                    where(City.class, JoinType.LEFT)
                    .eq(City_.id, filterEntity.getCity().getId()));
	        }  
	        if (has(filterEntity.getState())) {
                 criteria.join(Customer_.state,
                    where(State.class, JoinType.LEFT)
                    .eq(State_.id, filterEntity.getState().getId()));
	        }  
        }
        return criteria;
    }
    
    public void beforeInsert(Customer customer) {
        validate(customer);
    }

    public void beforeUpdate(Customer customer) {
        validate(customer);
    }


    public void validate(Customer customer) {
        BusinessException be = new BusinessException();

        /** just an example of validation
        if (!car.hasModel()) {
            be.addException(new BusinessException("Car model cannot be empty"));
        }
        if (!car.hasName()) {
            be.addException(new BusinessException("Car name cannot be empty"));
        }

        if (!has(car.getPrice())) {
            be.addException(new BusinessException("Car price cannot be empty"));
        } 

        if (count(criteria()
                .eqIgnoreCase(Car_.name, car.getName())
                .notEq(Car_.id, car.getId())) > 0) {

            be.addException(new BusinessException("Car name must be unique"));
        }
        **/

        //if there is exceptions enqueued then throw them. Each business exception will be transformed into a JSF error message and displayed on the page
        if (has(be.getExceptionList())) {
            throw be;
        }
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<City> getCitysByName(String query) {
        List<City> citysByName =  criteria(City.class)
                .likeIgnoreCase(City_.name, "%" + query + "%")
                .getResultList();

        return citysByName;
    }
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<State> getStatesByName(String query) {
        List<State> statesByName = criteria(State.class)
               .likeIgnoreCase(State_.name, "%" + query + "%")
               .getResultList();

        if(statesByName.isEmpty()){
            ObjectMapper mapper = new ObjectMapper();
            client = ClientBuilder.newClient();
            target = client.target("https://servicodados.ibge.gov.br/api/v1/localidades/estados");
            JsonArray response = target.request(MediaType.APPLICATION_JSON).get(JsonArray.class);
            try {
                CityMapper[] pp1 = mapper.readValue(response.toString(),
                        CityMapper[].class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (JsonValue sticker : response) {
                Object obj = response;
            }
        }

        return statesByName;
    }

 
}
