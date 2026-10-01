# mock-api-full

This project use git subtree to include the `mock-api` project as a subdirectory. 

- The `mock-api` project is a Spring Boot application that provides a mock API backend for testing and development purposes.
- The `mock-api-ui` project is a React + Typescript application that provides a UI to manage the mock API.

## Pull changes from the projects

```shell
git subtree pull --prefix=mock-api-backend git@github.com:Joxebus/mock-api.git main --squash
git subtree pull --prefix=mock-api-frontend git@github.com:Joxebus/mock-api-ui.git main --squash
```

## Push changes to the projects

```shell
git subtree push --prefix=mock-api-backend main
git subtree push --prefix=mock-api-frontend main
```