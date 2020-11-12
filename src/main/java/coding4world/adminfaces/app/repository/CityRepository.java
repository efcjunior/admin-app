package coding4world.adminfaces.app.repository;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import coding4world.adminfaces.app.model.City;

@Repository
public interface CityRepository extends EntityRepository<City,Long> {

}