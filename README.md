# Specification Builder 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ozayduman/specification-builder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ozayduman/specification-builder)

Specification-Builder is a client-oriented dynamic search query library that supports joins among multiple tables in a strongly-type manner for Spring Projects.                                                              
This library simplifies writing type-safe queries for search screens by using `Spring Data JPA`'s `JpaSpecificationExecutor` and `hibernate-jpamodelgen`. 
As you might know foreach query screen you have to pass a specific DTO (Data Transfer Objects) and write specific queries using that DTO.
This leads to boiler-plate, useless, repetitive code. By using this library you can get rid of that kind of code, and write fluent-style dynamic queries driven by client-side easily.

#### FEATURES
* Client-oriented dynamic query generation by using fluent style programming.
* You can use different properties for the client and the server-side. This feature enables us not to expose domain entities to external world directly.
* You can restrict, and open individual properties for query operations. 
* You can use the same property names for both client-side and server-side.
* You can combine the dynamic query generation with your custom specifications.
* Client-side decides to what operations will take place depending on the operands put in the `criteriaDTO` or `pageRequestDTO`. On the client-side you can use the following operators:
  * equal to: `EQ`
  * not equal to: `NOT_EQ`
  * greater than: `GT`
  * greater than or equal to: `GE`
  * less than`LT`
  * less than or equal to : `LE`
  * between : `BT`
  * in : `IN`
  * not in : `NOT_IN`
  * is null: `NULL`
  * is not null: `NOT_NULL`
  * is true: `TRUE`
  * is false: `FALSE`
  * like: `LIKE`
  * not like: `NOT_LIKE`
* You can use all these operators also in joins if needed as well.
#### DOCUMENTATION
* [User Guide](#server-side)
* [Javadoc](https://javadoc.io/doc/com.github.ozayduman/specification-builder/latest/index.html)  
* [Sample Project](https://github.com/ozayduman/spring-data-specification)   
#### HOW TO USE
Just add the following maven dependency to your pom.xml file. 
````
<dependency>
    <groupId>com.github.ozayduman</groupId>
    <artifactId>specification-builder</artifactId>
    <version>0.0.4</version>
</dependency>
````
#### USAGE

#### SERVER-SIDE
by using bind method you can enable properties to be used in dynamic query generation. Client is allowed to use the properties only bound via bind method.
if DTO properties are different from the entity properties then you have to specify it as the first argument of the bind method e.g. `bind("employeeName", Employee_.name)`. Otherwise, you can fell free to omit it e.g. `bind("Employee_.name)`.

![](.README_images/specification1.png)
 ```
final Specification<Employee> specification = SpecificationBuilder.<Employee>of(criteriaDTO)
                .bind("employeeName", Employee_.name)
                .bind("employeeSurname", Employee_.surname)
                .bind("employeeEmail", Employee_.email)
                .bind("employeeBirthDate", Employee_.birthDate)
                .bindJoin("phoneNumber", Employee_.phones, Phone_.number)
                .build();

        var customerFromDB = employeeRepository.findOne(specification)
                .orElseThrow(() -> new NoSuchElementException());
```
If your dto and entity share common names for properties you can simply define as follows:
 ```
final Specification<Employee> specification = SpecificationBuilder.<Employee>of(criteriaDTO)
                .bind(Employee_.name)
                .bind(Employee_.surname)
                .bind(Employee_.email)
                .bind(Employee_.birthDate)
                .bindJoin(Employee_.phones, Phone_.number)
                .build();

        var customerFromDB = employeeRepository.findOne(specification)
                .orElseThrow(() -> new NoSuchElementException());
```
For joins, you should use `bindJoin` instead e.g. `bindJoin("phoneNumber", Employee_.phones, Phone_.number)` or `bindJoin(Employee_.phones, Phone_.number)`
You can add custom specifications by using `bindCustom` method
#### PAGINATION
For returning query results page by page you should pass sort information via `PageRequestDTO` instead of `CriteriaDTO` and then use PageRequestBuilder as follows:
````
  final Specification<Employee> specification = SpecificationBuilder.<Employee>of(pageRequestDTO)
                .bind("employeeName", Employee_.name)
                .bind("employeeSurname", Employee_.surname)
                .bind("employeeEmail", Employee_.email)
                .bind("employeeBirthDate", Employee_.birthDate)
                .bindJoin("phoneNumber", Employee_.phones, Phone_.number)
                .build();

        var pageRequest = PageRequestBuilder.of(pageRequestDTO)
                .bindSort("employeeName", Employee_.name)
                .bindSort("phoneNumber", Phone_.number)
                .build();

  Page<Employee> page = employeeRepository.findAll(specification, pageRequest);

  PageResultDTO pageResultDTO = PageResultDTO.from(page, EmployeeMapper.INSTANCE::toDTO);
````
If you don't want to use map struct library, you can write it explicitly as follows:
````
  PageResultDTO pageResultDTO = PageResultDTO.from(page, e -> {
            EmployeeResponseDTO dto = new EmployeeResponseDTO();
            dto.setName(e.getName());
            dto.setSurname(e.getSurname());
            dto.setEmail(e.getEmail());
            return dto;
        });

````

#### CLIENT-SIDE
On the client side you should pass the property, its value, and operation that will be used in the query generation.   
Notice that some operators take no arguments (e.g. NULL, NOT_NULL, TRUE), some takes single, multiple values or range values as operands.
So, you should follow the constraints of each operator described below:

* `EQ, NOT_EQ, GT, GE, LT; LE` these operators take only one value as an argument:
  ````
  {
    ..
    "operations": [
      {
        "property": "name",
        "operator": "EQ",
        "value": "Alice"
      },
      {
        "property": "age",
        "operator": "GE",
        "value": 18
      }
    ]
  }
  ```` 
* `IN, NOT_IN` these operators take multi-value as an argument:
    ````
    {
      "operations": [
        {
          "property": "customerId",
          "operator": "IN",
          "value": [
            1,
            2,
            3,
            4,
            5
          ]
        }
      ]
    }
    ````
* `BT` this operator takes range of values as an argument:
    ````
    {
      "operations": [
        {
          "property": "age",
          "operator": "BT",
          "value": {
            "low": 18,
            "high": 65
          }
        }
      ]
    }
    ````
* `NULL, NOT_NULL, TRUE, FALSE` these operators take no value as an argument:
    ````
    {
      "operations": [
        {
          "property": "phoneNumber",
          "operator": "NOT_NULL"
        }
      ]
    }
    ````
  
Sort order, requested page, and page size information can be passed as follows:
````
{
  ..
  "sortFields": [
    {
      "property": "name",
      "direction": "ASC"
    },
    {
      "property": "surname",
      "direction": "DESC"
    }
  ],
  "page": 0,
  "size": 10
}
````
#### SAMPLE PROJECT 
There is a [sample project repository](https://github.com/ozayduman/spring-data-specification) that demonstrates usage of specificaiton-builder.
#### HOW TO BUILD
* Requires Java 14
* Executing tests: `./mvn test` (test reports: [./build/reports/tests/test/index.html](./build/reports/tests/test/index.html), code coverage reports: [./build/reports/jacoco/test/html/index.html](./build/reports/jacoco/test/html/index.html))
* Creating jars: `./mvn clean install` (see [./build/libs](./build/libs))
#### HOW TO CONTRIBUTE
[Fork](https://help.github.com/articles/fork-a-repo), and send a [pull request](https://help.github.com/articles/using-pull-requests) and keep your fork in [sync](https://help.github.com/articles/syncing-a-fork/) with the upstream repository.
#### LICENSE
Specification Builder is open source and can be found on GitHub. It is distributed under the Apache 2.0 License.
#### [SAMPLE PROJECT](SAMPLE.md)
