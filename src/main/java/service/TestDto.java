package service;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TestDto {

	@NotNull(message = "Förnamn måste anges")
	@Size(min = 1, max = 50, message = "Förnamn måste vara mellan 1 och 100 antal bokstäver")
	private String firstName;

	@NotNull(message = "Efternamn måste anges")
	@Size(min = 1, max = 50, message = "Efternamn måste vara mellan 1 och 100 antal bokstäver")
	private String lastName;

	@Email
	private String email;

	@Min(value = 18, message = "Ålder får inte vara lägre än 18")
	@Max(value = 150, message = "Ålder får inte vara över än 150")
	private Integer age;
	
	@NotNull(message="Kommentar måste anges")
	private String kommentar;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getKommentar() {
		return kommentar;
	}

	public void setKommentar(String kommentar) {
		this.kommentar = kommentar;
	}

	@Override
	public String toString() {
		return "TestDto [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", age=" + age
				+ ", kommentar=" + kommentar + "]";
	}
	
	
	
	
	

}
