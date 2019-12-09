package service;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "customer", indexes = { 
		     @Index(columnList = "id", name = "customer00", unique = true),
		     @Index(columnList = "customernumber", name = "customer10", unique = true), })
             @NamedQueries({ @NamedQuery(name = Customer.CUSTOMER_GET_ALL, query = "SELECT c FROM Customer c"),
		     @NamedQuery(name = Customer.CUSTOMER_GET_BY_CUSTOMERNUMBER, query = "SELECT c FROM Customer c where c.customernumber = :customernumber"), 
		  }
       )
public class Customer {

	public static final String CUSTOMER_GET_ALL = "customer.getall";
	public static final String CUSTOMER_GET_BY_CUSTOMERNUMBER = "customer.getbycustomernumber";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_generator")
	@SequenceGenerator(name = "customer_generator", sequenceName = "customer_seq")
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@NotEmpty(message = "customernumber must have a value")
	@Size(max = 50, message = "max length for customernumber is 50")
	@Column(name = "customernumber", unique = true)
	private String customernumber;

	@Size(max = 50, message = "max length for name is 50")
	@NotEmpty(message = "name must have a value")
	@Column(name = "name")
	private String name;

	@Version
	@Column(name = "internal_chgnbr")
	private Long internal_chgnbr;

	@Column(name = "changedby")
	@NotNull
	private String changedby;

	public String getCustomernumber() {
		return customernumber;
	}

	public void setCustomernumber(String customernumber) {
		this.customernumber = customernumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChangedby() {
		return changedby;
	}

	public void setChangedby(String changedby) {
		this.changedby = changedby;
	}

	public Long getId() {
		return id;
	}
			
	public Long getInternal_chgnbr() {
		return internal_chgnbr;
	}

	public void setInternal_chgnbr(Long internal_chgnbr) {
		this.internal_chgnbr = internal_chgnbr;
	}

	@Override
	public int hashCode() {
		return Objects.hash(changedby, customernumber, id, internal_chgnbr, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		return Objects.equals(changedby, other.changedby) && Objects.equals(customernumber, other.customernumber)
				&& Objects.equals(id, other.id) && Objects.equals(internal_chgnbr, other.internal_chgnbr)
				&& Objects.equals(name, other.name);
	}

}
