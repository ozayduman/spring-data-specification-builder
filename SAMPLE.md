# SPECIFICATION-BUILDER SAMPLE
This section shows how to use [Specification Builder](https://github.com/ozayduman/spring-data-specification-builder)
 library to write type-safe (client-oriented dynamic) queries for search screens in spring data jpa.
The full sample can be found in [spring-data-specification](https://github.com/ozayduman/spring-data-specification) repo.

```
<dependency>
    <groupId>com.github.ozayduman</groupId>
    <artifactId>specification-builder</artifactId>
    <version>0.0.3</version>
</dependency>
```

```
@RestController
@RequestMapping("/customer")
@Transactional
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/query")
    public PageResultDTO query(@RequestBody PageRequestDTO pageRequestDTO){
        return PageResultDTO.from(customerService.query(pageRequestDTO), CustomerMapper.INSTANCE::toDTO);
    }
}
```
Then define as a Service class by injecting CustomerRepository class. In this class create a SpecificationBuilder and 
use bind method to allow whatever fields you want to be queryable. Also create a PageRequestBuilder and use bindSort method
to allow which fields to be sortable as shown below:
```
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Page<Customer> query(PageRequestDTO pageRequestDTO) {
        final Specification<Customer> specification = createSpecification(pageRequestDTO);
        final PageRequest pageRequest = createPageRequest(pageRequestDTO);
        return customerRepository.findAll(specification, pageRequest);
    }

    private Specification<Customer> createSpecification(PageRequestDTO pageRequestDTO) {
        return SpecificationBuilder.<Customer>of(pageRequestDTO)
                .bind("name", Customer_.name)
                .bind("lastName", Customer_.surname)
                .bind("email", Customer_.email)
                .bindJoin("phoneNumber", Customer_.phones, Phone_.number)
                .build();
    }

    private PageRequest createPageRequest(PageRequestDTO pageRequestDTO) {
        return PageRequestDTO.PageRequestBuilder.of(pageRequestDTO)
                .bindSort("name", Customer_.name)
                .bindSort("lastName", Customer_.surname)
                .bindSort("email", Customer_.email)
                .build();
    }
}
```
Finally, define a repository interface that extends PagingAndSortingReporistory and JpaSpecificationExecutor interfaces
as follows:
````
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, JpaSpecificationExecutor<Customer> { }
````
