package coding4world.adminfaces.app.service;

import com.github.adminfaces.persistence.model.Filter;
import com.github.adminfaces.persistence.service.CrudService;
import com.github.adminfaces.template.exception.BusinessException;
import org.apache.deltaspike.data.api.criteria.Criteria;
import coding4world.adminfaces.app.model.State;
import coding4world.adminfaces.app.model.State_;
import coding4world.adminfaces.app.bean.AppLists;
import coding4world.adminfaces.app.repository.StateRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;
import coding4world.adminfaces.app.model.City;   
import coding4world.adminfaces.app.model.City_;   
import coding4world.adminfaces.app.model.Customer;   
import coding4world.adminfaces.app.model.Customer_;   
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import javax.persistence.criteria.JoinType;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import static com.github.adminfaces.template.util.Assert.has;


 
@Stateless
public class StateService extends CrudService<State, Long> implements Serializable {

    @Inject
    protected StateRepository stateRepository;// you can use repositories to extract complex queries from your service

    @Inject
    protected AppLists appLists;

    @Override
    public void afterAll(State state) {
        appLists.clearStates();//invalidate State list cache
    }

    /** 
     * This method is used for (real) pagination and is called by lazy 
     * PrimeFaces datatable on list page
     *
     * @param filter holds restrictions populated on the list page
     * @return a criteria populated with given restrictions 
     */ 
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    protected Criteria<State, State> configRestrictions(Filter<State> filter) {
        Criteria<State, State> criteria = criteria();
        
        if (filter.hasParam(State_.id.getName())) {
            criteria.eq(State_.id, filter.getLongParam(State_.id.getName()));   
        }  
        if (filter.hasParam(State_.name.getName())) {
            criteria.eq(State_.name, (String)filter.getParam(State_.name.getName()));   
        }  
        if (filter.hasParam(State_.abbreviation.getName())) {
            criteria.eq(State_.abbreviation, (String)filter.getParam(State_.abbreviation.getName()));   
        }  
        if (filter.hasParam(State_.cities.getName())) {
            criteria.distinct().join(State_.cities, where(City.class, JoinType.LEFT)
                .in(City_.id, toListOfIds((Set<City>) filter.getParam(State_.cities.getName()), new Long[0])));   
        }  
        if (filter.hasParam(State_.customers.getName())) {
            criteria.distinct().join(State_.customers, where(Customer.class, JoinType.LEFT)
                .in(Customer_.id, toListOfIds((Set<Customer>) filter.getParam(State_.customers.getName()), new Long[0])));   
        }  

        //create restrictions based on filter entity
        if (has(filter.getEntity())) {
            State filterEntity = filter.getEntity();
            
	        if (has(filterEntity.getId())) {
                 criteria.eq(State_.id, filterEntity.getId());   
	        }  
	        if (has(filterEntity.getName())) {
                 criteria.eq(State_.name, filterEntity.getName());   
	        }  
	        if (has(filterEntity.getAbbreviation())) {
                 criteria.eq(State_.abbreviation, filterEntity.getAbbreviation());   
	        }  
	        if (has(filterEntity.getCities())) {
                 criteria.distinct().join(State_.cities, where(City.class, JoinType.LEFT)
 	  	    .in(City_.id, toListOfIds(filterEntity.getCities(), new Long[0])));   
	        }  
	        if (has(filterEntity.getCustomers())) {
                 criteria.distinct().join(State_.customers, where(Customer.class, JoinType.LEFT)
 	  	    .in(Customer_.id, toListOfIds(filterEntity.getCustomers(), new Long[0])));   
	        }  
        }
        return criteria;
    }
    
    public void beforeInsert(State state) {
        validate(state);
    }

    public void beforeUpdate(State state) {
        validate(state);
    }

    @Override
    public void beforeRemove(State state) {
        Criteria<State, State> stateCitiesCriteria = criteria().
            join(State_.cities, where(City.class, JoinType.LEFT)
                .eq(City_.state, state));
        
        if(count(stateCitiesCriteria) > 0) {
            throw new BusinessException("Cannot remove "+state.getName() + " because it has cities associated.");
        }
        Criteria<State, State> stateCustomersCriteria = criteria().
            join(State_.customers, where(Customer.class, JoinType.LEFT)
                .eq(Customer_.state, state));
        
        if(count(stateCustomersCriteria) > 0) {
            throw new BusinessException("Cannot remove "+state.getName() + " because it has customers associated.");
        }

    }

    public void validate(State state) {
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
    public List<City> getCitiesByStateId(Long stateId) {
         return getEntityManager().createQuery("select r from City r where r.state.id =:id",City.class)
                .setParameter("id",stateId)
                .getResultList(); 
    }
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Customer> getCustomersByStateId(Long stateId) {
         return getEntityManager().createQuery("select r from Customer r where r.state.id =:id",Customer.class)
                .setParameter("id",stateId)
                .getResultList(); 
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public State findById(Serializable id) {
        TypedQuery<State> findById = getEntityManager().createQuery("select s from State s left join fetch s.citiesleft join fetch s.customers where s.id = :id", State.class)
            .setParameter("id", id);
        try {
            return findById.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
 
}
