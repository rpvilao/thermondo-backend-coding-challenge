# Backend Senior Coding Challenge 🍿

## Reflections - Testing purposes vs real scenario

The application creates a test and admin users and the tokens to call endpoints are exposed in the logging.

I established to commit around 3 hours to this, everything that is not done or relaxed is properly justified in the
following notes:

1. There's no user password when registering. I would store the password hashed (bcrypt) with salt and a salt column to
   prevent dictionary attacks if the table were somehow accessed.
2. I would handle the database schema creation outside the app with liquibase for example.
3. I implemented a quick opaque token mechanism just to have user flows and authorization.
4. I would implement oauth2 flows (such as user password flow) from the RFC (OAuth2) or have an external service for
   authentication and authorization.
5. Included some parallel processing when enriching using kotlin coroutines.
6. The database schema has no constraints (I would put them if using Liquibase for example).
7. It's an in memory database for the purpose of the project.
8. I would split into different JARs with the following logic: persistence layer, service layer, service API, rest-api,
   authentication module.
9. I create a different representation for the DTOs on the service layer (and not hand the REST DTOs to the data layer).
10. The average rating of the movie won't work with heavy concurrency. I would do this asynchronously or with a
    distributed lock. In the limit I would do this with a direct database update on the field (not with a whole object).
    A regular lock would be out of the question since it wouldn't scale horizontally.
11. The exception handler would be throwing the errors in JSON.
12. There's no login endpoint (I created users with tokens that can be used for testing). In a real scenario I would
    implement a blocking after bruteforce N times mechanism.
13. I would not use the username to be the main identifier for the authenticated principal, but a database ID.
14. I would use UUIDs for the database IDs for better security (not sequential).
15. The API is prepared for versioning using versioning in the path and DTOs.
16. Did not document it, but would do in a real scenario (Javadocs and API docs)
17. The test has two different roles (admin, user) with some endpoints being protected by that (also unit tests).
18. I decided to test the application as a whole and not the services isolated, so they are more kind of integration
    tests.
19. CSRF disabled because it's a REST API based on tokens.
20. The token store is in memory for simplicity, I would do it in an external database (DynamoDB for example).
21. Everything is only in one commit since I usually on the main thread do "feature commits".
22. I would protect the pagination object to have a maximum so calling apps don't kill the application due to lack of memory.
23. I would not expose the framework's Page object just to make sure it wouldn't break in the future if changed.
24. No searching capabilities were implemented.
25. Another more relaxed option for to have the proper rating is to assume the potential error (in a big dataset it won't
    matter) and recalculate the proper number everytime with a cron job for example.

## How to run

```bash
    mvn clean package
    java -jar target/popcorn-time-1.0.0.jar 
    
    # Or with Docker
    docker run -v "$PWD":/tmp/work -w /tmp/work maven:3.9.6-eclipse-temurin-17 mvn clean package
    docker run -v "$PWD":/tmp/work -w /tmp/work maven:3.9.6-eclipse-temurin-17 java -jar target/popcorn-time-1.0.0.jar
```

==========

Welcome to our Movie Rating System Coding Challenge! We appreciate you taking
the time to participate and submit a coding challenge! 🥳

In this challenge, you'll be tasked with designing and implementing a robust
backend system that handles user interactions and provides movie ratings. We
don't want to check coding conventions only; **we want to see your approach
to systems design!**

**⚠️ As a tech-agnostic engineering team, we ask you to pick the technologies
you are most comfortable with and those that will showcase your strongest
performance. 💪**

## ✅ Requirements

- [ ] The backend should expose RESTful endpoints to handle user input and
  return movie ratings.
- [ ] The system should store data in a database. You can use any existing
  dataset or API to populate the initial database.
- [ ] Implement user endpoints to create and view user information.
- [ ] Implement movie endpoints to create and view movie information.
- [ ] Implement a rating system to rate the entertainment value of a movie.
- [ ] Implement a basic profile where users can view their rated movies.
- [ ] Include unit tests to ensure the reliability of your code.
- [ ] Ensure proper error handling and validation of user inputs.

## ✨ Bonus Points

- [ ] Implement authentication and authorization mechanisms for users.
- [ ] Provide documentation for your API endpoints using tools like Swagger.
- [ ] Implement logging to record errors and debug information.
- [ ] Implement caching mechanisms to improve the rating system's performance.
- [ ] Implement CI/CD quality gates.

## 📋 Evaluation Criteria

- **Systems Design:** We want to see your ability to design a flexible and
  extendable system. Apply design patterns and software engineering concepts.
- **Code quality:** Readability, maintainability, and adherence to best
  practices.
- **Functionality:** Does the system meet the requirements? Does it provide
  movie
  ratings?
- **Testing:** Adequate test coverage and thoroughness of testing.
- **Documentation:** Clear documentation for setup, usage, and API endpoints.

## 📐 Submission Guidelines

- Fork this GitHub repository.
- Commit your code regularly with meaningful commit messages.
- Include/Update the README.md file explaining how to set up and run your
  backend, including any dependencies.
- Submit the link to your repository.

## 🗒️ Notes

- You are encouraged to use third-party libraries or frameworks to expedite
  development but be prepared to justify your choices.
- Feel free to reach out if you have any questions or need clarification on the
  requirements.
- Remember to approach the challenge as you would a real-world project, focusing
  on scalability, performance, and reliability.

## 🤔 What if I don't finish?

Part of the exercise is to see what you prioritize first when you have a limited
amount of time. For any unfinished tasks, please do add `TODO` comments to
your code with a short explanation. You will be given an opportunity later to go
into more detail and explain how you would go about finishing those tasks.
