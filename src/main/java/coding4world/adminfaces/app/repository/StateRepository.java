package coding4world.adminfaces.app.repository;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import coding4world.adminfaces.app.model.State;

@Repository
public interface StateRepository extends EntityRepository<State,Long> {

}