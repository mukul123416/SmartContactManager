package com.example.demo;

import com.example.demo.entities.Employee;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		List<String> list = Arrays.asList("b","a","c","d","e","f");
		List<String> filterData;
		list.stream().forEach(a->{
//			System.out.println(a);
		});
		list.stream().sorted().forEach(a->{
//			System.out.println("sorted"+a);
		});

		String s = list.stream().max((x, y) -> x.compareTo(y)).get();
//		System.out.println(s);

		List<Employee> employeeList = new ArrayList<Employee>();
		Map<String,Integer> map = new HashMap<String,Integer>();
		employeeList.add(new Employee(111,"Mukul Sharma",32,"Female","HR",2011,25000.0));
		employeeList.add(new Employee(122,"Monit Sharma",25,"Male","Sales And Marketing",2015,13500.0));
		employeeList.add(new Employee(133,"Rajesh Sharma",29,"Male","Infrastructure",2012,18000.0));
		employeeList.add(new Employee(144,"Rohan Sharma",28,"Male","Product Development",2014,32500.0));
		employeeList.add(new Employee(155,"Priya Sharma",27,"Female","HR",2013,22700.0));
		employeeList.add(new Employee(166,"Rakesh Sharma",43,"Male","Security And transport",2016,10500.0));
		employeeList.add(new Employee(177,"Himanshu Sharma",35,"Male","Infrastructure",2010,27000.0));
		employeeList.add(new Employee(188,"Aditya Sharma",31,"Male","Product Development",2015,34500.0));
		employeeList.add(new Employee(199,"Hackishes Sharma",24,"Female","Accounts And Finance",2016,11500.0));
		employeeList.add(new Employee(200,"Rocky Sharma",38,"Male","Sales And Marketing",2015,11000.5));

		// Group By before java8
		for(int i=0;i<employeeList.size();i++){
			if(map.containsKey(employeeList.get(i).getGender())){
				map.put(employeeList.get(i).getGender(),map.get(employeeList.get(i).getGender())+1);
			}else {
				map.put(employeeList.get(i).getGender(),1);
			}
		}
//		System.out.println(map);

		// Group By using java8
		Map<String, Long> collect = employeeList.stream().collect(Collectors.groupingBy(Employee::getGender, Collectors.counting()));
		System.out.println(collect);

		Set<String> collect1 = employeeList.stream().map(a -> a.getDepartment()).collect(Collectors.toSet());
		System.out.println(collect1);

		Map<String, Double> collect2 = employeeList.stream().collect(Collectors.groupingBy(Employee::getGender, Collectors.averagingInt(Employee::getAge)));
		System.out.println(collect2);

		Optional<Employee> collect3 = employeeList.stream().collect(Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)));
		System.out.println(collect3.get().getName());

		List<String> collect4 = employeeList.stream().filter(a -> a.getYearOfJoining() > 2015).map(a -> a.getName()).collect(Collectors.toList());
		System.out.println(collect4);

		Map<String, Double> collect5 = employeeList.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.averagingDouble(Employee::getSalary)));
		System.out.println(collect5);

		Map<String, Long> collect6 = employeeList.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
		System.out.println(collect6);

		Optional<Employee> min = employeeList.stream().filter(a -> a.getGender() == "Male" && a.getDepartment() == "Product Development").min(Comparator.comparingInt(Employee::getAge));
		System.out.println(min.get());

		Employee employee = employeeList.stream().min(Comparator.comparingInt(Employee::getYearOfJoining)).get();
		System.out.println(employee);

		Map<String, Long> collect7 = employeeList.stream().filter(a -> a.getDepartment() == "Sales And Marketing").collect(Collectors.groupingBy(Employee::getGender, Collectors.counting()));
		System.out.println(collect7);

		Map<String, Double> collect8 = employeeList.stream().collect(Collectors.groupingBy(Employee::getGender, Collectors.averagingDouble(Employee::getSalary)));
		System.out.println(collect8);



		int threadCount = 0;
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for ( Thread t : threadSet){
			if ( t.getThreadGroup() == Thread.currentThread().getThreadGroup()){
				System.out.println("Thread :"+t+":"+"state:"+t.getState());
				++threadCount;
			}
		}
		System.out.println("Thread count started by Main thread:"+threadCount);
		SpringApplication.run(DemoApplication.class, args);
	}

}
