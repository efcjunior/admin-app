package coding4world.adminfaces.app.repository;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import coding4world.adminfaces.app.model.Customer;

@Repository
public interface CustomerRepository extends EntityRepository<Customer,Long> {

}