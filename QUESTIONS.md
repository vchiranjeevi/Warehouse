# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
I would consider refactoring the database access layer to ensure consistency across all modules. Right now, some parts of the code base use direct repository calls while others wrap logic in use cases or services. This inconsistency can make maintenance harder and lead to duplicated business rules. My preference would be to standardize on a clean architecture approach: repositories for persistence, domain models for business logic, and use cases for orchestration. This separation makes the code easier to test, reduces coupling, and ensures that business rules are enforced uniformly. I would also ensure that exception handling and transaction boundaries are consistent, since we saw earlier that unhandled exceptions were bubbling up as 500 errors.

2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
Using an OpenAPI spec for Warehouse gives us strong guarantees: the contract is explicit, code can be generated, and clients can rely on the spec. It reduces human error and helps with documentation. The downside is that generated code can sometimes feel rigid or verbose, and small changes require updating the spec and regenerating. For Product and Store, coding endpoints directly gave us flexibility and speed, but at the cost of consistency and documentation. If I had to choose, I would lean toward using OpenAPI for all three domains. It enforces consistency, makes onboarding easier, and ensures that tests and clients align with the same contract. However, for rapid prototyping or internal endpoints, direct coding can still be acceptable.

3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
I would prioritize tests around the business rules first, since they are the core of the system and the source of most potential errors. For example, ensuring that capacity cannot be less than stock, or that business unit codes are unique. These should be covered with unit tests and integration tests. Next, I would focus on endpoint tests to validate that the API behaves correctly (status codes, error messages, serialization). Seeded data tests are useful for regression, but I would avoid over-relying on them and instead create data within tests to guarantee independence. Over time, I would add coverage reports to monitor gaps, and use CI pipelines to enforce that critical paths (create, update, delete, rule violations) are always tested. This balances thoroughness with efficiency, ensuring that the most important rules and endpoints are always validated.
