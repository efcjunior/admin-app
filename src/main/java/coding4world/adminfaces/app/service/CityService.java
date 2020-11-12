package coding4world.adminfaces.app.service;

import com.github.adminfaces.persistence.model.Filter;
import com.github.adminfaces.persistence.service.CrudService;
import com.github.adminfaces.template.exception.BusinessException;
import org.apache.deltaspike.data.api.criteria.Criteria;
import coding4world.adminfaces.app.model.City;
import coding4world.adminfaces.app.model.City_;
import coding4world.adminfaces.app.bean.AppLists;
import coding4world.adminfaces.app.repository.CityRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;
import coding4world.adminfaces.app.model.State;      
import coding4world.adminfaces.app.model.State_;   
import coding4world.adminfaces.app.model.Customer;   
import coding4world.adminfaces.app.model.Customer_;   
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.JoinType;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import static com.github.adminfaces.template.util.Assert.has;


 
@Stateless
public class CityService extends CrudService<City, Long> implements Serializable {

    @Inject
    protected CityRepository cityRepository;// you can use repositories to extract complex queries from your service

    @Inject
    protected AppLists appLists;

    @Override
    public void afterAll(City city) {
        appLists.clearCitys();//invalidate City list cache
    }

    /** 
     * This method is used for (real) pagination and is called by lazy 
     * PrimeFaces datatable on list page
     *
     * @param filter holds restrictions populated on the list page
     * @return a criteria populated with given restrictions 
     */ 
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    protected Criteria<City, City> configRestrictions(Filter<City> filter) {
        Criteria<City, City> criteria = criteria();
        
        if (filter.hasParam(City_.id.getName())) {
            criteria.eq(City_.id, filter.getLongParam(City_.id.getName()));   
        }  
        if (filter.hasParam(City_.name.getName())) {
            criteria.eq(City_.name, (String)filter.getParam(City_.name.getName()));   
        }  
        if (filter.hasParam(City_.state.getName())) {
            criteria.join(City_.state,
                where(State.class, JoinType.LEFT)
               .eq(State_.id, ((State) filter.getParam("state")).getId()));
        }  
        if (filter.hasParam(City_.customers.getName())) {
            criteria.distinct().join(City_.customers, where(Customer.class, JoinType.LEFT)
                .in(Customer_.id, toListOfIds((Set<Customer>) filter.getParam(City_.customers.getName()), new Long[0])));   
        }  

        //create restrictions based on filter entity
        if (has(filter.getEntity())) {
            City filterEntity = filter.getEntity();
            
	        if (has(filterEntity.getId())) {
                 criteria.eq(City_.id, filterEntity.getId());   
	        }  
	        if (has(filterEntity.getName())) {
                 criteria.eq(City_.name, filterEntity.getName());   
	        }  
	        if (has(filterEntity.getState())) {
                 criteria.join(City_.state,
                    where(State.class, JoinType.LEFT)
                    .eq(State_.id, filterEntity.getState().getId()));
	        }  
	        if (has(filterEntity.getCustomers())) {
                 criteria.distinct().join(City_.customers, where(Customer.class, JoinType.LEFT)
 	  	    .in(Customer_.id, toListOfIds(filterEntity.getCustomers(), new Long[0])));   
	        }  
        }
        return criteria;
    }
    
    public void beforeInsert(City city) {
        validate(city);
    }

    public void beforeUpdate(City city) {
        validate(city);
    }

    @Override
    public void beforeRemove(City city) {
        Criteria<City, City> cityCustomersCriteria = criteria().
            join(City_.customers, where(Customer.class, JoinType.LEFT)
                .eq(Customer_.city, city));
        
        if(count(cityCustomersCriteria) > 0) {
            throw new BusinessException("Cannot remove "+city.getName() + " because it has customers associated.");
        }

    }

    public void validate(City city) {
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
    public List<State> getStatesByName(String query) {
        return criteria(State.class)
               .likeIgnoreCase(State_.name, "%" + query + "%")
               .getResultList();
    }
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Customer> getCustomersByCityId(Long cityId) {
         return getEntityManager().createQuery("select r from Customer r where r.city.id =:id",Customer.class)
                .setParameter("id",cityId)
                .getResultList(); 
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public City findById(Serializable id) {
        TypedQuery<City> findById = getEntityManager().createQuery("select s from City s left join fetch s.customers where s.id = :id", City.class)
            .setParameter("id", id);
        try {
            return findById.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
 
}
