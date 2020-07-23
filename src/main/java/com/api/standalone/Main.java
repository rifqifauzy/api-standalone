package com.api.standalone;

import java.util.Locale;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import com.api.standalone.model.Cities;
import com.api.standalone.model.Person;
import com.api.standalone.model.Province;
import com.github.javafaker.Faker;

public class Main {
	
	public static void main(String[] args) {
		Base.open();
		
		/*generate data province if provinces table is empty*/
//		if (Province.count()==0)
//			genDataProvince();
//		LazyList<Province> provinces = Province.findAll().orderBy("name");
//		System.out.println(provinces.toJson(true, "name"));
		
		/*generate data person if persons table is empty*/
//		if (Person.count().intValue()==0)
//			genDataPerson();
		
//		LazyList<Person> persons = Person.findAll().limit(1).orderBy("name");
//		Person person = Person.findById(502);
//		System.out.println(person.toJson(true, "name", "address", "email", "date_of_birth"));
		
		LazyList<Cities> cities = Cities.findAll().orderBy("name");
		System.out.println(cities.toJson(true));
		
		Base.close();
	}

	/**
	 * generate dummy data province
	 * */
	public static void genDataProvince() {
		String provinces = "Jawa Tengah,Jawa Barat,Jawa Timur,Aceh,Sumatra Utara,DKI Jakarta,Lampung,Sumatra Selatan,"
				+ "Sumatra Barat,Bali,Papua";
		for (String prov : provinces.split(",")) {
			Province province = new Province(prov);
			province.saveIt();
		}
	}
	
	/**
	 * generate dummy data person using Java Faker
	 * */
	public static void genDataPerson() {
		for (int i = 1; i <= 100; i++) {
			Faker faker = new Faker(new Locale("in-ID"));
			String firstName = faker.name().firstName();
			String lastName = faker.name().lastName();
			
			Person person = new Person();
			person.setName(firstName.concat(" ").concat(lastName));
			person.setAddress(faker.address().fullAddress());
			person.setDateOfBirth(new java.sql.Date(faker.date().birthday().getTime()));
			person.setEmail(firstName.toLowerCase().concat(lastName.toLowerCase()).concat("@gmail.com"));
			person.saveIt();
		}
	}
	
}
