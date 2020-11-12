package coding4world.adminfaces.app.model;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import coding4world.adminfaces.app.model.State;
import javax.persistence.ManyToOne;
import java.util.Set;
import java.util.HashSet;
import coding4world.adminfaces.app.model.Customer;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import com.github.adminfaces.persistence.model.PersistenceEntity;

@Entity
public class City implements Serializable, PersistenceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	private static final long serialVersionUID = 1L;
	@Version
	@Column(name = "version")
	private int version;

	@Column
	@NotNull
	private String name;

	@ManyToOne
	private State state;

	@OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
	private Set<Customer> customers = new HashSet<Customer>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof City)) {
			return false;
		}
		City other = (City) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (id != null)
			result += "id: " + id;
		result += ", version: " + version;
		if (name != null && !name.trim().isEmpty())
			result += ", name: " + name;
		return result;
	}

	public State getState() {
		return this.state;
	}

	public void setState(final State state) {
		this.state = state;
	}

	public Set<Customer> getCustomers() {
		return this.customers;
	}

	public void setCustomers(final Set<Customer> customers) {
		this.customers = customers;
	}
}