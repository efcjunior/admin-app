/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package coding4world.adminfaces.app.bean;

import com.github.adminfaces.persistence.bean.CrudMB;
import coding4world.adminfaces.app.model.City;
import coding4world.adminfaces.app.service.CityService;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import javax.inject.Named;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import static com.github.adminfaces.persistence.util.Messages.addDetailMessage;
import static com.github.adminfaces.persistence.util.Messages.getMessage;

import java.util.*;
import coding4world.adminfaces.app.model.State;      
import coding4world.adminfaces.app.model.Customer;   


@Named
@ViewScoped
public class CityFormMB extends CrudMB<City> implements Serializable {

    @Inject
    CityService cityService;

    @Inject
    public void initService() {
        setCrudService(cityService);
    }

    @Override
    public void afterRemove() {
        try {
            super.afterRemove();//adds remove message
            Faces.redirect("city/city-list.xhtml");
            clear(); 
            sessionFilter.clear(CityFormMB.class.getName());//removes filter saved in session for this bean.
        } catch (Exception e) {
            log.log(Level.WARNING, "",e);
        }
    }

    public List<State> completeState(String query) {
        return cityService.getStatesByName(query);
    }
 

}
